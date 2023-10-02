#!/bin/sh

SCRIPT_DIR="$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

export FLASK_APP=whatever
export FLASK_ENV=development
cd "$SCRIPT_DIR"
flask run
