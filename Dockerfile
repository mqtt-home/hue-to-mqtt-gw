FROM node:16.13.2-alpine3.14

# Set working directory
WORKDIR /opt/app

# Copy project files
COPY . .

# Run container as non-root (unprivileged) user
# The node user is provided in the Node.js Alpine base image
RUN chown node -R /opt/app
USER node

# Build project
RUN npm install
CMD ["npm", "start"]
