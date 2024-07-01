FROM openjdk:17-jdk-alpine

RUN apk add --no-cache maven

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

EXPOSE 8000
EXPOSE 5432

CMD ["java", "-jar", "target/asee-0.0.1-SNAPSHOT.jar"]
