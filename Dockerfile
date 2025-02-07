FROM amazoncorretto:21.0.5

WORKDIR /app

COPY . .

RUN ./gradlew clean build

FROM amazoncorretto:21.0.5

WORKDIR /app

COPY --from=0 /app/build/libs/*.jar app.jar

CMD ["java", "-jar", "app.jar"]
