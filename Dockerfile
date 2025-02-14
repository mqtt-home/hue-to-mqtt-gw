# FROM gcr.io/distroless/nodejs:18
# COPY --from=build /opt/app/dist /opt/app/
#
# CMD ["/opt/app/index.js", "/var/lib/huemqtt/config.json"]

FROM node:22.14-alpine
COPY app/dist /opt/app/
WORKDIR /opt/app/

CMD ["node", "index.js", "/var/lib/huemqtt/config.json"]
