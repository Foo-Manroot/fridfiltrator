#!/bin/sh

implant_dir=/implant
app_dir=/app

# (alternative option, in case debugging the container from the host is better)
#"${implant_dir}/frida-server" -l 0.0.0.0:3344 -D
#printf 'Frida implant running on 0.0.0.0:3344\n'



## The original app had the following instructions:
# WORKDIR /app
# CMD [ "node", "./app.js" ]
cd "$app_dir"
frida -l /injected-script.js -f "$(command -v node)" ./app.js
#node ./app.js
