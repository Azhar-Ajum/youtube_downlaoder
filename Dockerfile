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

# Copy ALL project files
COPY . .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Package app (skip tests)
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java","-jar","target/youtube_downloader-0.0.1-SNAPSHOT.jar"]
