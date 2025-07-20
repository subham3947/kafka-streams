FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
CMD ["sh", "-c", "sleep 10 && java -jar /app.jar"]
