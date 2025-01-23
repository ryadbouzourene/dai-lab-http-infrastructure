FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/*with-dependencies.jar app.jar
EXPOSE 80
ENTRYPOINT ["java", "-jar", "app.jar"]