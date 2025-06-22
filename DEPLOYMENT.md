# 배포 가이드

## 로컬 환경에서 실행

### 1. 필요 사항
- JDK 17 이상
- Gradle 8.x

### 2. 프로젝트 클론 및 빌드
```bash
git clone https://github.com/vincenzo-dev-82/vincenzo-coupon-system-by-java.git
cd vincenzo-coupon-system-by-java

# 실행 권한 부여 (Linux/Mac)
chmod +x gradlew

# 빌드
./gradlew clean build
```

### 3. 애플리케이션 실행
```bash
# Gradle로 실행
./gradlew bootRun

# 또는 JAR 파일로 실행
java -jar build/libs/coupon-system-0.0.1-SNAPSHOT.jar
```

## 프로덕션 환경 설정

### 1. 데이터베이스 변경 (PostgreSQL)

`src/main/resources/application-prod.yml` 파일 생성:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/coupon_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
    
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate
    show-sql: false
    
  h2:
    console:
      enabled: false
      
server:
  port: 8080
  
logging:
  level:
    com.vincenzo.coupon: INFO
    org.springframework.web: WARN
```

### 2. 환경 변수 설정
```bash
export DB_USERNAME=your_db_username
export DB_PASSWORD=your_db_password
export SPRING_PROFILES_ACTIVE=prod
```

### 3. 빌드 및 실행
```bash
./gradlew clean build
java -jar -Dspring.profiles.active=prod build/libs/coupon-system-0.0.1-SNAPSHOT.jar
```

## Docker로 실행

### 1. Dockerfile 생성
```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY build/libs/coupon-system-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### 2. Docker 이미지 빌드 및 실행
```bash
# 빌드
./gradlew clean build
docker build -t coupon-system:latest .

# 실행
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_USERNAME=your_username \
  -e DB_PASSWORD=your_password \
  coupon-system:latest
```

## Docker Compose로 실행

`docker-compose.yml` 파일:

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: coupon_db
      POSTGRES_USER: coupon_user
      POSTGRES_PASSWORD: coupon_pass
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_USERNAME: coupon_user
      DB_PASSWORD: coupon_pass
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/coupon_db
    depends_on:
      - postgres

volumes:
  postgres_data:
```

실행:
```bash
docker-compose up -d
```

## 성능 최적화

### 1. JVM 옵션
```bash
java -jar \
  -Xms512m \
  -Xmx2048m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=100 \
  -Dspring.profiles.active=prod \
  build/libs/coupon-system-0.0.1-SNAPSHOT.jar
```

### 2. 커넥션 풀 설정
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
```

## 모니터링

### Spring Boot Actuator 활성화

`build.gradle`에 추가:
```gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

`application.yml`에 추가:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

### 헬스 체크
```bash
curl http://localhost:8080/actuator/health
```

## 로그 관리

### 파일 기반 로그 설정

`src/main/resources/logback-spring.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>logs/coupon-system.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>logs/coupon-system.%d{yyyy-MM-dd}.log</fileNamePattern>
                <maxHistory>30</maxHistory>
            </rollingPolicy>
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        
        <root level="INFO">
            <appender-ref ref="FILE" />
        </root>
    </springProfile>
</configuration>
```