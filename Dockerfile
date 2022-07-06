FROM openjdk:11-jre-slim
EXPOSE 8080
COPY target/*.jar app.jar
CMD ["java","-Duser.timezone=UTC", "-Dapp.name=starling-coding-challenge", "-jar","app.jar"]
