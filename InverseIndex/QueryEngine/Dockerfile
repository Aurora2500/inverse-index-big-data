FROM openjdk:17

WORKDIR /app
VOLUME /datalake
VOLUME /datamart

COPY target/QueryEngine.jar .

CMD ["java", "-jar", "/app/QueryEngine.jar", "/datalake", "/datamart"]