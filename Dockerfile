FROM openjdk:11-jre-slim

WORKDIR /app

COPY ./target/classes/asee/asee/PraksaAseeApplication.class app.jar

CMD ["java", "-jar", "app.jar"]