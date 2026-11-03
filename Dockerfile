FROM eclipse-temurin:21-jre-noble

RUN groupadd --gid 10001 app && \
    useradd --uid 10001 --gid app --home-dir /app --shell /usr/sbin/nologin --no-create-home app

WORKDIR /app

COPY --chown=10001:10001 fonts ./fonts
COPY --chown=10001:10001 build/libs/demoji-all.jar ./app.jar

USER 10001:10001

ENTRYPOINT ["java", "-jar", "app.jar"]
