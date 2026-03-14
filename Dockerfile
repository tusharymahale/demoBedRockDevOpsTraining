#Single stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/demo-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]


# multi-stage, recommended
## Stage 1: build
#FROM maven:3.9.4-eclipse-temurin-17 AS builder
#WORKDIR /build
#COPY pom.xml .
#COPY src ./src
#RUN mvn -DskipTests package
#
## Stage 2: runtime
#FROM eclipse-temurin:17-jre
#WORKDIR /app
#COPY --from=builder /build/target/demo-0.0.1-SNAPSHOT.jar /app/app.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","/app/app.jar"]