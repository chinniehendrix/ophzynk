FROM openjdk:8-jre-alpine

COPY target/ophzynk-1.0-SNAPSHOT.jar /ophzynk/

COPY health.sh /ophzynk/
RUN chmod +x /ophzynk/health.sh

ENTRYPOINT ["java", "-cp", "/ophzynk/ophzynk-1.0-SNAPSHOT.jar", "com.chinniehendrix.ophzynk.Ophzynk"]