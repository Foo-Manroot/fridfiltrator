import db_connector/db_sqlite
import net
import std/[asyncnet, asyncdispatch, nativesockets]
import strformat
import strutils

import std/os

const
    packet_size = 256
    port = Port 9999
    address = "127.0.0.1"
    db_name = "events.db"


type MSG_TYPE {.pure.} = enum
    POLLING         = '\x00'
    IMPLANT_EVENT   = '\x01'



let db = open(db_name, "", "", "")
# We start from scratch every run, discarding previous events
db.exec(sql"DROP TABLE IF EXISTS events")
db.exec(sql"""CREATE TABLE events (
    timestamp INT NOT NULL,
    data BLOB NOT NULL
)""")


proc handle_implant_event(data: string, db: DbConn) {.async.} =
    try:
        # Data needs to be converted to seq[byte] to be interpreted as
        # binary by bindParams
        let data_blob = newSeq[byte](data.len)
        copyMem(
            data_blob[0].unsafeAddr,
            data[0].unsafeAddr,
            data.len
        )

        # We have to use a prepared statement to insert binary data into the database
        let preparedStmt = db.prepare("""INSERT INTO events (timestamp, data)
                    VALUES (unixepoch("now"), ?)"""
        )
        preparedStmt.bindParams(data_blob)

        if db.tryExec(preparedStmt):

            preparedStmt.finalize()
            echo "Data successfully inserted into the DB"

        else:
            echo "Error executing prepared statement :("

    except:
        echo "Error trying to insert an event into the DB: ", getCurrentExceptionMsg()


proc handle_polling(
        msg: tuple[
              data: string
            , address: string
            , port: Port
        ]
        , db: DbConn
        , socket: AsyncSocket
    ) {.async.} =

    #[ Structure of the `data` packet:
        1 Byte -> 0x00 (MSG_TYPE.POLLING)
        4 Bytes -> 32-bit unsigned integer to store the cutoff timestamp
    ]#
    var response: string

    if len(msg.data) != 5:

        response = "Bruh, I need at least a timestamp or something"
        await socket.sendTo(address = msg.address, port = msg.port, data = response)

    else:
        # Why the fuck did someone decide to use a "string" for socket communication??
        # Mate, this language sucks...
        let timestamp = ntohl(
            (
                cast[ptr uint32](
                    msg.data[1].addr
                )
            )[]
        )
        echo &"Requested timestamp: {timestamp}"

        for row in db.fastRows(sql"SELECT * FROM events WHERE timestamp >= ?", timestamp):
            # x = @[<timestamp>, <data>]
            echo repr(row)
            response = $row

            # More stupid conversions because this language sucks
            let ts_uint32 = htonl(row[0].parseInt.uint32)
            var ts_bytes: string = "\x00\x00\x00\x00"
            ts_bytes[3] = char((ts_uint32 and 0xff000000'u32) shr 24)
            ts_bytes[2] = char((ts_uint32 and 0x00ff0000'u32) shr 16)
            ts_bytes[1] = char((ts_uint32 and 0x0000ff00'u32) shr  8)
            ts_bytes[0] = char( ts_uint32 and 0x000000ff'u32        )

            let data_len_uint32 = htonl(row[1].len.uint32)
            var data_len_bytes: string = "\x00\x00\x00\x00"
            data_len_bytes[3] = char((data_len_uint32 and 0xff000000'u32) shr 24)
            data_len_bytes[2] = char((data_len_uint32 and 0x00ff0000'u32) shr 16)
            data_len_bytes[1] = char((data_len_uint32 and 0x0000ff00'u32) shr  8)
            data_len_bytes[0] = char( data_len_uint32 and 0x000000ff'u32        )

#            echo "repr(ts_bytes) => " & repr(ts_bytes)
            response = ts_bytes & data_len_bytes & row[1]
#            echo "repr(response) => " & repr(response)

            await socket.sendTo(address = msg.address, port = msg.port, data = response)

        # EOD
        await socket.sendTo(address = msg.address, port = msg.port, data = "\x00")


proc handle_connection(
        msg: tuple[
              data: string
            , address: string
            , port: Port
        ]
        , socket: AsyncSocket
    ) {.async.} =

    let data = msg.data

    echo "======= NEW PACKET RECEIVED ========"
    echo &"message data: {repr(data)}"
    echo &"from addr: {msg.address}"
    echo &"with port: {msg.port.int}"


    if len(data) <= 0:
        return

    var msg_type: MSG_TYPE

    try:
        msg_type = data[0].MSG_TYPE
    except:
        echo &"Unknown message type: 0x{data[0].int8:02x}"
        return

    case msg_type:
        of MSG_TYPE.POLLING:
            echo "The type was POLLING"
            asyncCheck handle_polling(msg, db, socket)

        of MSG_TYPE.IMPLANT_EVENT:
            echo "The type was IMPLANT_EVENT"
            asyncCheck handle_implant_event(data[1..^1], db)


proc serve(socket: AsyncSocket) {.async.} =
    while true:
        let msg = waitfor socket.recvFrom(packet_size)
        asyncCheck handle_connection(msg, socket)


var socket = newAsyncSocket(sockType = SOCK_DGRAM, protocol = IPPROTO_UDP)
socket.bindAddr(port = port, address = address)

echo &"Now accepting connections on udp://{address}:{port}"

asyncCheck serve(socket)
runForever()

