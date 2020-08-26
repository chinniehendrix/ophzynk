FROM openjdk:8-jre-alpine

COPY target/ophzynk-1.0-SNAPSHOT-shaded.jar /ophzynk/

COPY health.sh /ophzynk/
RUN chmod +x /ophzynk/health.sh

ENTRYPOINT ["java", "-jar", "/ophzynk/ophzynk-1.0-SNAPSHOT-shaded.jar"]