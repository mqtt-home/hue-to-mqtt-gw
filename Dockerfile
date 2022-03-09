FROM node:17.6.0-alpine3.14

# Set working directory
WORKDIR /opt/app

# Copy project files
COPY ./app .

# Run container as non-root (unprivileged) user
# The node user is provided in the Node.js Alpine base image
RUN chown node -R /opt/app
USER node

# Build project
RUN npm install
RUN npm run build

CMD ["node", "dist/index.js", "/var/lib/huemqtt/config.json"]
