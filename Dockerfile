FROM openjdk:8-jre-alpine

COPY target/ophzynk-1.0-SNAPSHOT.jar /ophzynk/

ENTRYPOINT ["java", "-cp", "/ophzynk/ophzynk-1.0-SNAPSHOT.jar", "com.chinniehendrix.ophzynk.Ophzynk"]