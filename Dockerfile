FROM openjdk:17
EXPOSE 8080

WORKDIR /app
COPY build/libs/*.jar /app/eccomerce.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "eccomerce.jar"]