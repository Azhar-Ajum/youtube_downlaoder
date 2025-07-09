# Stage 1: Build
FROM eclipse-temurin:17-jdk as build

WORKDIR /app

# Copy Maven wrapper and pom.xml first (to cache dependencies)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the code
COPY src src

# Package the application
RUN ./mvnw package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk

# Install dependencies
USER root
RUN apt-get update \
 && apt-get install -y ffmpeg python3-pip \
 && pip3 install yt-dlp

RUN useradd -ms /bin/bash appuser
USER appuser

WORKDIR /app

# Copy jar from the build stage
COPY --from=build /app/target/youtube_downloader-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
