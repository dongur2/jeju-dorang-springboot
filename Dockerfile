# Docker file


# Docker file's BASE IMAGE: jdk 17 image
FROM openjdk:17-jdk

# Docker 빌드 시 사용할 환경 변수를 정의
ARG JAR_FILE=build/libs/*.jar

# 호스트 파일 시스템에서 Docker 이미지로 파일을 복사; Spring Boot 애플리케이션의 실행 가능한 JAR 파일을 app.jar로 복사
COPY ${JAR_FILE} app.jar
# Docker 컨테이너가 시작될 때 실행할 명령어를 지정
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]