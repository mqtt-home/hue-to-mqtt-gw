# ---- Prod ----
FROM openjdk:18-jdk-alpine
RUN mkdir /opt/app
WORKDIR /opt/app
COPY src/de.rnd7.huemqtt/target/huemqtt.jar .
COPY src/logback.xml .

CMD java -jar ./huemqtt.jar /var/lib/huemqtt/config.json
