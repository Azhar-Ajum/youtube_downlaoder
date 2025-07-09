# Use Eclipse Temurin JDK 17 (official Java 17 image)
FROM eclipse-temurin:17-jdk

# Install ffmpeg and Python3 + pip
RUN apt-get update && \
    apt-get install -y ffmpeg python3-pip && \
    pip3 install yt-dlp

# Create app directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (to leverage Docker layer caching)
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the project source
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port 8080 (Spring Boot default)
EXPOSE 8080

# Run the JAR
CMD ["java", "-jar", "target/youtube_downloader-0.0.1-SNAPSHOT.jar"]
