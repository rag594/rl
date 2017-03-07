FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/play.jar /play/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/play/app.jar"]
