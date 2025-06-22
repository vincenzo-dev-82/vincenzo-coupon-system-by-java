FROM openjdk:17-jdk-slim

# 애플리케이션 포트
EXPOSE 8080

# 애플리케이션 JAR 파일 복사
ARG JAR_FILE=build/libs/coupon-system-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 실행
ENTRYPOINT ["java","-jar","/app.jar"]