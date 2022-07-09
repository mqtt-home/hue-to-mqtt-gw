# FROM gcr.io/distroless/nodejs:18
# COPY --from=build /opt/app/dist /opt/app/
#
# CMD ["/opt/app/index.js", "/var/lib/huemqtt/config.json"]

FROM node:18.5-alpine
COPY app/dist /opt/app/

CMD ["node", "dist/index.js", "/var/lib/huemqtt/config.json"]
