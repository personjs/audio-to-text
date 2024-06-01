FROM maven:latest as builder

WORKDIR /app
COPY . .
RUN mvn clean package

# Use Python 3.8 official Docker image as base
FROM python:3.8-slim

# Install necessary dependencies for Java
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget \
    && rm -rf /var/lib/apt/lists/*

RUN wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.deb \
    && dpkg -i jdk-21_linux-x64_bin.deb

# Set environment variables for Java
ENV JAVA_HOME /opt/java/jdk-21
ENV PATH $JAVA_HOME/bin:$PATH

RUN pip install SpeechRecognition pocketsphinx moviepy

# Set working directory
WORKDIR /app

# Copy application files to the container
COPY --from=builder /app/target/*.jar app.jar
COPY src/main/python .

VOLUME /app/uploads
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]