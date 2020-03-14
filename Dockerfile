FROM openjdk:8-alpine

COPY target/uberjar/landizer.jar /landizer/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/landizer/app.jar"]
