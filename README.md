# 제주도랑(Jeju-dorang) 
<img src="images/logo.png" alt='jeju-dorang main logo' />

<p>제주도랑은 사용자들이 편리하게 소통할 수 있는 커뮤니티를 제공합니다. 커뮤니티는 '모임'과 '잡담' 두 개의 게시판으로 구성되는데, '모임' 게시판은 사용자들이 각자의 여행에 함께할 동행 파트너를 구하는, 제주도랑의 주 기능을 제공하는 공간입니다. 혼자 간 여행에서 2인 식사부터 가능한 식당에 결국 가지 못했을 때 우울했던, 관광 체험 상품을 혼자 신청하기는 머쓱했던 경험으로 편하고 자유롭게 동행을 구하는 공간이 있으면 좋겠다고 생각되어 구현하게 된 게시판입니다. 이 게시판에서는 여행 전체를 함께 할 동행을 찾거나, 특정 액티비티를 함께 즐길 동행을 구하거나, 식사를 함께 할 동행을 찾는 등 기간이나 활동에 구애받지 않고 다양한 동행 파트너를 구할 수 있습니다. 글 작성 형식에 제한을 두지 않아 사용자들이 자유롭게 동행을 구할 수 있도록 했습니다. 여행 합류를 원하는 사용자는 해당 게시글에 댓글을 작성하고, 게시글 작성자는 발생한 새 댓글 알림을 통해 이를 확인하고 카카오톡 오픈 채팅 링크 같은 연락 수단을 대댓글로 작성하는 것 같은 방법으로 합류가 이루어지게 됩니다. 댓글 작성자에게도 똑같이 새 대댓글 알림을 발생시켜 원활한 확인을 가능하게 했습니다. 게시글 작성자는 원하는 동행을 모두 구했을 경우 게시글의 모집중 버튼을 모집완료 버튼으로 변경해 상태를 표시할 수 있습니다.</p>

<p>잡담 게시판은 사용자들이 자유롭게 이야기를 나누고, 제주도 여행에 대한 정보를 공유하는 공간입니다. 다녀온 관광지, 식당에 대한 후기나 현재 날씨 혹은 여행 계획에 대한 조언 요청 같은 여행에 대한 정보를 활발하게 나눌 수 있는 게시판을 생각하고 구현하게 되었습니다.</p>

<p>부가적으로, 제주도랑은 제주도 공공데이터를 활용하여 제주도 여행지들을 소개합니다. 여행 계획을 세울 때나 여행 중 방문할 관광지나 식당을 찾을 때 효율적이고 편리하게 사용할 수 있는 여행지 데이터베이스가 있으면 좋겠다는 요지에서 만들게 된 기능입니다. 주 기능인 동행 파트너를 구하는 게시판과 여행지 정보를 모아볼 수 있는 게시판이 함께 있으면 긍정적인 시너지 효과를 낼 수 있을 것이라고 생각했습니다. 여행할 때 주로 찾게 되는 쇼핑, 관광, 식당 카테고리로 구분하여 여행지 목록을 정렬해 사용자에게 보여주도록 구현했습니다. 여행 계획에 도움이 되도록 여행지의 위치, 간단한 설명, 관련 태그를 표시했습니다. 또한 사용자는 북마크 기능을 활용하여 가고자 하는 여행지를 저장해두고 마이페이지에서 한 눈에 확인할 수 있습니다.</p>

<br>

### 🗓️ 개발 기간
2023.12.28 ~ 2024.03

<br>

### 🛠 기술
- IDE

    <img src="https://img.shields.io/badge/IntelliJ-000000?style=for-the-badge&logo=intellijidea&logoColor=white"/>
    
- Frontend
  
    <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=HTML5&logoColor=white"/>
    <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=CSS3&logoColor=white"/>
    <img src="https://img.shields.io/badge/Bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white"/>
    <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=white"/>
    <img src="https://img.shields.io/badge/Thymeleaf 3.1.2-005F0F?style=for-the-badge&logo=Thymeleaf&logoColor=white"/>
- Backend

    <img src="https://img.shields.io/badge/Java 17-gray?style=for-the-badge&logo=openjdk&logoColor=white"/>
    <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"/>
    <img src="https://img.shields.io/badge/Spring Boot 3.2.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>

  - DataBase

    <img src="https://img.shields.io/badge/MariaDB 11.2.2-003545?style=for-the-badge&logo=mariadb&logoColor=white"/>
    <img src="https://img.shields.io/badge/Redis 7.2.4-DC382D?style=for-the-badge&logo=redis&logoColor=white"/>
    <img src="https://img.shields.io/badge/amazon s3-569A31?style=for-the-badge&logo=amazons3&logoColor=white"/>
    <img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=for-the-badge&logo=Java&logoColor=white"/>

  - Security / Authentication & Authorization

    <img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"/>
    <img src="https://img.shields.io/badge/json web tokens-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"/>
    <img src="https://img.shields.io/badge/oauth2.0-4285F4?style=for-the-badge&logo=oauth2.0&logoColor=white"/>
  
<br>

### 🌐 2024.03.05 배포
<img src="https://img.shields.io/badge/amazon ec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"/> <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"/>
<img src="https://img.shields.io/badge/github actions-181717?style=for-the-badge&logo=github&logoColor=white"/>

http://43.201.67.46:8080/

GitHub Actions를 사용하여 CI/CD 파이프라인을 구축하고, EC2 인스턴스 내에서 MariaDB, Redis, 스프링부트 프로젝트 Docker Container를 실행하는 방식으로 배포했습니다.

<br>

### ERD
<img src="images/erd.png" alt="erd" />

<br>

### System Architecture
<img src="images/architecture.png" alt="system architecture" />


<br>

# 설치
### Project Clone
```bash
~$ git clone https://github.com/dongur2/jeju-dorang-springboot.git
```
- `application.yml` 파일 생성 후 변수 설정

<br>

### MariaDB 11.2.2
#### Window

MariaDB 설치

https://mariadb.org/download/?t=mariadb&p=mariadb&r=10.11.7&os=windows&cpu=x86_64&pkg=zip&m=blendbyte

데이터베이스 생성
```bash
~$ create database jeju_dorang;
```
  
#### Mac
  
Homebrew 업데이트
```bash
~$ brew update
```

MariaDB 설치
```bash
~$ brew install mariadb
```

Homebrew로 MariaDB 실행
```bash
~$ brew services start mariadb
```

MariaDB 접속
```bash
mariadb
```

데이터베이스 생성
```bash
create database jeju_dorang;
```

<br>

### Redis 7.2.4
#### Window
Redis 설치

https://github.com/microsoftarchive/redis/releases

redis-cli.exe로 Redis 실행

Redis 비밀번호 설정
```bash
vi /etc/redis/redis.conf
```



#### Mac

Redis 설치
```bash
~$ brew install redis
```

Redis를 background로 실행
```bash
~$ brew services start redis
```

Redis 비밀번호 설정
```bash
vi /etc/redis/redis.conf
```

- `requirepass {비밀번호}`

<br>

### 프로젝트 실행 후 권한 데이터 생성
```bash
insert into role(name) values('ADMIN');
insert into role(name) values('USER');
```

<br>

# 변경 로그
