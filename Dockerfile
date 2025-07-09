# Use Eclipse Temurin base image for Java 17
FROM eclipse-temurin:17-jdk

# Install ffmpeg and python3 + venv
RUN apt-get update \
    && apt-get install -y ffmpeg python3 python3-venv

# Create venv for yt-dlp
RUN python3 -m venv /opt/yt-dlp-venv

# Activate venv and install yt-dlp
RUN /opt/yt-dlp-venv/bin/pip install --upgrade pip \
    && /opt/yt-dlp-venv/bin/pip install yt-dlp

# Make sure yt-dlp from venv is on PATH
ENV PATH="/opt/yt-dlp-venv/bin:$PATH"

# Set work directory
WORKDIR /app

# Copy Maven Wrapper & pom.xml first for Docker cache
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# Prepare dependencies
RUN chmod +x mvnw \
    && ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Package app
RUN ./mvnw package -DskipTests

# Copy built jar
COPY target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run
ENTRYPOINT ["java","-jar","/app/app.jar"]
