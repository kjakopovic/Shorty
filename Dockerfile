FROM openjdk:17-jdk-alpine

RUN apk add --no-cache maven

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

EXPOSE 8000

CMD ["java", "-jar", "shorty-api/target/shorty-api-0.0.1-SNAPSHOT.jar"]
