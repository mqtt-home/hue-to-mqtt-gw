# ---- Prod ----
FROM openjdk:8-jdk-alpine
RUN mkdir /opt/app
WORKDIR /opt/app
COPY src/de.rnd7.cupsmqtt/target/cupsmqtt.jar .
COPY src/logback.xml .

CMD java -jar ./cupsmqtt.jar /var/lib/cupsmqtt/config.json
