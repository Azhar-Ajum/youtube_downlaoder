# Stage 1: Build the JAR
FROM eclipse-temurin:17-jdk as build

WORKDIR /app

# Copy Maven wrapper and give permission
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build the JAR
RUN ./mvnw package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:17-jdk

USER root

# Install ffmpeg + yt-dlp
RUN apt-get update \
 && apt-get install -y ffmpeg python3-pip \
 && pip3 install yt-dlp

RUN useradd -ms /bin/bash appuser
USER appuser

WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/youtube_downloader-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
