FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src/ src/
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/target/folha-pagamento-*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]