FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive

RUN sed -i 's/ports.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    sed -i 's/archive.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    apt-get update && \
    apt-get install -y openjdk-21-jdk

WORKDIR /app
COPY target/sdcs-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]
