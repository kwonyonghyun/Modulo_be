FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:NewSize=128m", "-XX:MaxNewSize=128m", "-XX:MaxHeapSize=96m", "-XX:InitialHeapSize=96m", "-jar", "app.jar"]