FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/OrderService-*.jar order-service.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "order-service.jar"]