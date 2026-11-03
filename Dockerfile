# syntax=docker/dockerfile:1.7

FROM amazoncorretto:21.0.5 AS build

WORKDIR /workspace

COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY src ./src
COPY fonts ./fonts

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon build

RUN jdeps \
        --ignore-missing-deps \
        --multi-release 21 \
        --print-module-deps \
        build/libs/demoji-all.jar > /tmp/modules && \
    jlink \
        --add-modules "$(cat /tmp/modules),jdk.charsets,jdk.crypto.ec" \
        --compress=zip-6 \
        --no-header-files \
        --no-man-pages \
        --output /jre

FROM debian:bookworm-slim AS runtime

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        ca-certificates \
        fontconfig \
        libfreetype6 && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd --gid 10001 app && \
    useradd --uid 10001 --gid app --home-dir /app --shell /usr/sbin/nologin --no-create-home app

WORKDIR /app

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=build /jre "${JAVA_HOME}"
COPY --from=build --chown=10001:10001 /workspace/fonts ./fonts
COPY --from=build --chown=10001:10001 /workspace/build/libs/demoji-all.jar ./app.jar

USER 10001:10001

ENTRYPOINT ["java", "-jar", "app.jar"]
