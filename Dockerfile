FROM eclipse-temurin:17-jdk

# Install dependencies
USER root
RUN apt-get update \
 && apt-get install -y ffmpeg python3-pip \
 && pip3 install yt-dlp

# Create a non-root user to run the app
RUN useradd -ms /bin/bash appuser
USER appuser

# Set working directory
WORKDIR /app

# Copy the JAR
COPY target/youtube_downloader-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the app
ENTRYPOINT ["java","-jar","/app/app.jar"]
