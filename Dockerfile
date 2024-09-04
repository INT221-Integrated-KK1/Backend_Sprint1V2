FROM openjdk:17-jdk-alpine AS builder
WORKDIR app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-alpine AS runner
COPY --from=builder /app/target/*.jar api.jar
ENTRYPOINT ["java","-jar","api.jar"]




