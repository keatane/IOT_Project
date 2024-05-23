FROM docker.io/nodered/node-red:3.1.9-18-minimal

# Copy package.json to the WORKDIR so npm builds all
# of your added nodes modules for Node-RED
WORKDIR /data
COPY --chown=1000:1000 package.json /data
RUN npm install --unsafe-perm --no-update-notifier --no-fund --only=production
WORKDIR /usr/src/node-red

# Copy _your_ Node-RED project files into place
# NOTE: This will only work if you DO NOT later mount /data as an external volume.
#       If you need to use an external volume for persistence then
#       copy your settings and flows files to that volume instead.
COPY --chown=1000:1000 settings.js /data/settings.js
# COPY flows_cred.json /data/flows_cred.json
COPY --chown=1000:1000 flows.json /data/flows.json
