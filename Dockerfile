FROM eclipse-temurin:21
COPY MediaOrganizer-1.0.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java","-jar", "./MediaOrganizer-1.0.jar"]
