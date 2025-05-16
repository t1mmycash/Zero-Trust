FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/UserService-*.jar user-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "user-service.jar"]