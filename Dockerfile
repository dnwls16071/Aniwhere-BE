FROM openjdk:17-jdk

COPY build/libs/*SNAPSHOT.jar aniwhere.jar

ENTRYPOINT ["java", "-jar", "/aniwhere.jar"]