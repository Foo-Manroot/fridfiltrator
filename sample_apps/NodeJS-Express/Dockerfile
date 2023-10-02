FROM node:alpine as base

WORKDIR /app

COPY app.js ./
COPY package.json ./
COPY sample_files ./sample_files/

RUN rm -rf node_modules && npm install express

#################
# Add requirements for the implant
# Frida 16.1.4 has problems attaching or spawning to the process
ARG frida_ver=16.0.10
ARG implant_dir=/implant
RUN mkdir "$implant_dir"

# Python is required to install frida-tools
ENV PYTHONUNBUFFERED=1
RUN apk add --update --no-cache python3 && ln -sf python3 /usr/bin/python
RUN python3 -m ensurepip
RUN pip3 install --no-cache --upgrade pip setuptools

# Install dependencies to install the frida-tools wheel from source
RUN apk add --update --no-cache curl libc6-compat gcc musl-dev python3-dev

# First option: run Frida directly from the container
RUN mkdir "${implant_dir}/frida-devkit"
RUN curl -L "https://github.com/frida/frida/releases/download/${frida_ver}/frida-core-devkit-${frida_ver}-linux-x86_64-musl.tar.xz" -o "${implant_dir}/frida-devkit.tar.xz"
RUN tar xvJ -C "${implant_dir}/frida-devkit" -f "${implant_dir}/frida-devkit.tar.xz"
RUN env FRIDA_CORE_DEVKIT="${implant_dir}/frida-devkit" python -m pip install frida-tools

# (alternative option, in case debugging the container from the host is better)
#RUN curl -L "https://github.com/frida/frida/releases/download/${frida_ver}/frida-server-${frida_ver}-linux-x86_64-musl.xz" -o "${implant_dir}/frida-server.xz"
#RUN unxz "${implant_dir}/frida-server.xz"
#RUN chmod +x "${implant_dir}/frida-server"

# Script to run the agent and the intended app
COPY implant/injected-script.js /injected-script.js
COPY implant/start_with_implant.sh /start_with_implant.sh
RUN chmod +x "/start_with_implant.sh"
CMD [ "/bin/sh", "/start_with_implant.sh" ]
###############

#CMD ["node", "./app.js"]
