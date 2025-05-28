FROM openjdk:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-slim-buster AS deploy

ENV TZ=America/Lima
ENV JAR_NAME=com.marcas-0.0.1-SNAPSHOT.jar
WORKDIR /app

# Copiamos el JAR compilado desde la etapa de construcción
COPY --from=builder /app/target/$JAR_NAME ./app.jar

# Instalación de fontconfig, libfreetype6 (para reportes excel)
#RUN apt-get update && apt-get install -y fontconfig libfreetype6 && apt-get clean

# Afinación para contenedor
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["java", "-jar", "app.jar"]