FROM openjdk:17

WORKDIR /app
VOLUME /datamart

COPY target/QueryEngine.jar .

CMD ["java", "-jar", "/app/QueryEngine.jar", "/datamart"]