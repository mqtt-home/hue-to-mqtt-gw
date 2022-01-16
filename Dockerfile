FROM node:16.13.0-alpine3.14

# Set working directory
WORKDIR /opt/app

# Copy package.json and package-lock.json before other files
# Utilise Docker cache to save re-installing dependencies if unchanged
COPY ./package*.json ./

# Install dependencies
RUN npm install --production

# Copy project file
COPY hue-to-mqtt .

# Run container as non-root (unprivileged) user
# The node user is provided in the Node.js Alpine base image
RUN chown node -R /opt/app
USER node

# Build project
RUN npm install
CMD ["npm", "start"]
