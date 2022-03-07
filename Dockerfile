FROM openjdk:8-jdk-alpine
EXPOSE 8080

ADD . /build
WORKDIR /build

RUN ./gradlew clean build

ENTRYPOINT ["java", "-jar", "/build/build/libs/rock-paper-scissors-1.0-SNAPSHOT.jar"]