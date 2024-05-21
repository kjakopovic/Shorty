FROM openjdk:17-jdk-alpine

COPY ./target/asee-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8000
EXPOSE 5432

CMD ["java", "-jar", "app.jar"]