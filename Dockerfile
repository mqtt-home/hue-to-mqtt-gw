FROM node:18.5-alpine as build

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

FROM gcr.io/distroless/nodejs:18
COPY --from=build /opt/app/dist /opt/app/

CMD ["/opt/app/index.js", "/var/lib/huemqtt/config.json"]
