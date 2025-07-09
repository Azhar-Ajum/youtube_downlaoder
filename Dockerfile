# Use Eclipse Temurin base image for Java 17
FROM eclipse-temurin:17-jdk

# Install ffmpeg and yt-dlp
RUN apt-get update \
    && apt-get install -y ffmpeg python3-pip \
    && pip3 install yt-dlp \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven Wrapper and pom.xml first to leverage Docker cache
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# Download dependencies
RUN chmod +x mvnw \
    && ./mvnw dependency:go-offline

# Copy the rest of your source code
COPY src ./src

# Package the app
RUN ./mvnw package -DskipTests

# Copy the generated jar to run
COPY target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java","-jar","/app/app.jar"]
