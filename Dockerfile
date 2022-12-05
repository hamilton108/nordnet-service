FROM openjdk:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/nordnet-service-0.0.1-SNAPSHOT-standalone.jar /nordnet-service/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/nordnet-service/app.jar"]
