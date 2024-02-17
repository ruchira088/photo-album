FROM eclipse-temurin:21-jre

LABEL org.opencontainers.image.source https://github.com/ruchira088/photo-album

WORKDIR /opt/photo-album

ADD target/web-app.jar web-app.jar

EXPOSE 8080

CMD ["java", "-jar", "/opt/photo-album/web-app.jar"]