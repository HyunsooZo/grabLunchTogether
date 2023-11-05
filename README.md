## Grab Lunch Together 프로젝트

### 목차
- [프로젝트 소개](#프로젝트-소개)
- [기술 스택](#기술-스택)
- [API 명세서](#API-명세서)
- [주요 기능](#주요-기능)
- [프로젝트 구조](#프로젝트-구조)
- [ERD](#ERD)

### 프로젝트 소개

사용자들의 위치기반으로 회사 근처 사람들끼리 점심 약속을 잡고 <br>
상대방에 대해 리뷰할 수 있는 기능들을 구현하고 각 기능에 대한 테스트 코드를 작성했습니다.

---

### 기술 스택

<img src="https://img.shields.io/badge/java-007396?&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/spring-6DB33F?&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/Spring boot-6DB33F?&logo=Spring boot&logoColor=white"> <img src="https://img.shields.io/badge/gradle-02303A?&logo=gradle&logoColor=white">
<br>
<img src="https://img.shields.io/badge/Spring Security-6DB33F?&logo=Spring Security&logoColor=white"> 
<img src="https://img.shields.io/badge/Json web tokens-000000?&logo=Json web tokens&logoColor=white">
<br>
<img src="https://img.shields.io/badge/MariaDB-003545?&logo=mariaDB&logoColor=white"> 
<img src="https://img.shields.io/badge/redis-DC382D?&logo=redis&logoColor=white"> 
<img src="https://img.shields.io/badge/Spring JPA-6DB33F?&logo=Spring JPA&logoColor=white"> 
<img src="https://img.shields.io/badge/SMTP-CC0000?&logo=Gmail&logoColor=white">
<br>
<image src="https://img.shields.io/badge/Docker-2496ED?&logo=Docker&logoColor=white">
<img src="https://img.shields.io/badge/aws-232F3E?&logo=amazonaws&logoColor=white"> 
<img src="https://img.shields.io/badge/ec2-FF9900?&logo=amazonec2&logoColor=white"> 
<img src="https://img.shields.io/badge/rds-527FFF?&logo=amazonrds&logoColor=white"> 
<img src="https://img.shields.io/badge/S3-569A31?&logo=amazons3&logoColor=white"> 
<img src="https://img.shields.io/badge/Jenkins-2088FF?&logo=Jenkins&logoColor=white" alt="actions">
<br>
<img src="https://img.shields.io/badge/intellijidea-000000?&logo=intellijidea&logoColor=white"> 
<img src="https://img.shields.io/badge/postman-FF6C37?&logo=postman&logoColor=white"> 
<img src="https://img.shields.io/badge/swagger-85EA2D?&logo=swagger&logoColor=white">
<br>
<img src="https://img.shields.io/badge/Geocode API:공공데이터-007396?&logoColor=white">
<img src="https://img.shields.io/badge/SMS 전송 API:네이버클라우드-1122222?&logoColor=white">
<img src="https://img.shields.io/badge/OCR API:네이버클로바-1122222?&logoColor=white">

---

### API 명세서

[스웨거 API 문서](http://13.209.169.74:8080/swagger-ui/index.html#/)

| URL                                     | Http Method | description          |
|-----------------------------------------|-------------|----------------------|
| /api/users/signup                       | `POST`      | 사용자 회원가입             |
| /api/users/signup/ocr                   | `POST`      | 사용자 간편회원가입(명함OCR)    |
| /api/users/login                        | `POST`      | 사용자 로그인              |
| /api/users/password/reset               | `POST`      | 사용자 비밀번호초기화          |
| /api/users/password/change              | `PATCH`     | 사용자 비밀번호 변경          |
| /api/users/edit                         | `PATCH`     | 사용자 정보 수정            |
| /api/plans/distance/{kilometer}         | `GET`       | 주변회원찾기               |
| /api/plans/received                     | `GET`       | 내가 받은 점심약속요청 조회      |
| /api/plans/requested                    | `GET`       | 요청한 점심약속 조회하기        |
| /api/plans/user/{accepterId}            | `POST`      | 점심약속 요청하기            |
| /api/plans/{planId}/accept/{acceptCode} | `PATCH`     | 점심약속 수락/거절 하기        |
| /api/plans/{planId}/edit                | `PATCH`     | 점심약속 수정하기            |
| /api/plans/{planId}/cancel              | `PATCH`     | 점심약속 취소하기            |
| /api/plans/{planId}                     | `DELETE`    | 점심약속 삭제하기            |
| /api/planhistorys                       | `GET`       | 사용자 점심약속 히스토리 조회     |
| /api/reviews/planhistory/{planHistoryId} | `POST`      | 약속했던 상대방에게 리뷰등록      |
| /api/reviews/{userReviewId}             | `PUT`       | 내가 작성한 리뷰 삭제         | 
| /api/reviews/{userReviewId}             | `DELETE`    | 내가 작성한 리뷰 수정         |
| /api/reviews/user/{targetUserId}        | `GET`       | 사용자에 대한 리뷰목록 조회      |
| /api/must-eat-places/{city}             | `GET`       | 도시별 맛집목록 불러오기        |
| /api/bookmark-spots                     | `POST`      | 맛집 즐겨찾기 등록(직접입력)     |
| /api/bookmark-spots/must-eat-place/{mustEatPlaceId} | `POST`      | 맛집 즐겨찾기 등록(맛집목록정보사용) |
| /api/bookmark-spots                     | `GET`       | 즐겨찾기 맛집 조회           |
| /api/bookmark-spots/{bookmarkSpotId}    | `DELETE`    | 즐겨찾기 맛집 삭제           |
| /api/favorite-users/user/{targetId}     | `POST`      | 즐겨찾는 친구 추가하기         |
| /api/favorite-users                     | `GET`       | 즐겨찾는 친구 조회하기         |
| /api/favorite-users/{favoriteUserId}    | `PATCH`     | 즐겨찾는 친구 정보 수정        |
| /api/favorite-users/{favoriteUserId}    | `DELETE`    | 즐겨찾는 친구 정보 삭제        |

### 주요 기능
<details>
<summary><strong>User(사용자) 관련 api</strong></summary>
  
- 일반회원가입 : 사용자 일반가입은 회원정보 직접입력을 통해서 가능합니다.
- 간편회원가입 : 사용자 간편가입은 명함사진(OCR진행)과 비밀번호입력을 통해서 가능합니다.
- 로그인 : 비밀번호는 `BCryptPasswordEncoder`를 통해 암호화하여 저장하고 로그인 시 복호화하여 비교 후 사용자정보와 일치 시 토큰을 발행합니다.
- 회원정보 수정 : 입력된 수정정보로 사용자정보를 업데이트 합니다.
- 비밀번호 변경 : 입력된 비밀번호를 암호화 하여 비밀번호를 업데이트합니다.
- 비밀번호 초기화 : 임시비밀번호를 암호화하여 저장 하고 사용자 Email 로 임시비밀번호를 전송합니다.
  
</details>

<details>
<summary><Strong>Plan(점심약속) 관련 api</Strong></summary>

- 주변회원 탐색 : 사용자는 본인이 설정한 거리 내 회원목록을 조회할 수 있습니다. 
- 점심약속 신청 : 
    - 조회된 사용자에게 일정(시간/장소/메뉴/요청메세지)을 적어 점심약속 신청을 생성할 수 있습니다. 
    - 피신청인에게는 생성된 점심약속을 요약하여 SMS로 전송
- 점심약속 업데이트 :
    - 점심약속 수정 : 점심약속 신청에 응답 전, 신청인은 생성된 점심약속 일정을 수정할 수 있습니다.
    - 점심약속 응답 : 피신청인은 해당 점심약속을 수락 또는 거절할 수 있습니다.
    - 점심약속 취소 : 수락 상태의 점심약속 시간 전 신청인/피신청인은 약속을 취소할 수 있습니다. 
- 점심약속 삭제 : 신청인은 점심약속 시간이 1시간 이상 남은 미응답 상태인 약속을 삭제 할 수 있습니다.
</details>

<details>
<summary><Strong>Plan History(약속 히스토리) 관련 api</Strong></summary>
  
- 점심약속 히스토리 조회 : 사용자가 신청하거나 수락한 점심약속 히스토리 목록을 조회할 수 있습니다.
- (매 분 스케줄링을 통해 `COMPLETED` ,`CANCEL`상태이면서 일정이 지난 점심약속을 히스토리에 등록)
</details>

<details>
<summary><Strong>User Review(사용자 리뷰) 관련 api</Strong></summary>
  
- 리뷰 작성 : 점심약속이 완료된 상대방의 프로필에 서로 별점과 리뷰를 작성할 수 있습니다.
- 리뷰 조회 : 다른사용자의 id로 사용자에게 등록 된 리뷰리스트를 조회할 수 있습니다.
- 리뷰 수정 : 본인이 작성한 리뷰를 수정할 수 있습니다.
- 리뷰 삭제 : 본인이 작성한 리뷰를 삭제할 수 있습니다.
</details>

<details>
<summary><Strong>Favorite User(즐겨찾는 사용자) 관련 api</Strong></summary>
  
- 즐겨찾는 사용자 등록 : 조회한 사용자목록에서 사용자를 선택하여 내 즐겨찾기 목록에 닉네임과 함께 등록합니다.
- 즐겨찾는 사용자 조회 : 내가 등록한 즐겨찾는 사용자 목록을 조회합니다.
- 즐겨찾는 사용자 닉네임 수정 : 내가 등록한 즐겨찾는 사용자의 닉네임을 수정할 수 있습니다.
- 즐겨찾는 사용자 삭제 : 내가 등록한 즐겨찾는 사용자를 목록에서 삭제할 수 있습니다.
</details>

<details>
<summary><Strong>Must-Eat-Spot(맛집) 관련 api</Strong></summary>

- 조회 : 크롤링을 통해 저장된 지역주변 대표맛집 목록을 파라미터(지역이름)를 이용하여 조회 합니다.
    - `synchronized`를 사용하여 동기화 -> 크롤링이 진행되는 시점에는 맛집목록 조회 불가합니다.
    - 동시성 문제 방지, 원자성 보장, 데이터 일관성 

</details>

<details>
<summary><Strong>BookMark Spot(즐겨찾는 맛집) 관련 api</Strong></summary>

- 즐겨찾는 맛집 등록 : 
  - 직접입력 등록 : 조회된 맛집 목록 중 식당을 선택하여 내가 즐겨찾는 맛집으로 등록합니다.
  - 맛집정보로 등록 : 조회한 Must-Eat-Spot(맛집)의 id로 즐겨찾기 맛집을 등록합니다.
- 즐겨찾는 맛집 조회 : 본인이 등록한 맛집 목록을 조회</br>
    - (맛집테이블(FOODIE_SPOT)의 컬럼들은 크롤링 시마다 삭제/생성 되므로 연관관계를 설정 하지 않고 조회된 식당의 모든 컬럼의
      데이터를 즐겨찾기맛집 테이블에 저장)
- 즐겨찾는 맛집 삭제 : 본인이 등록한 맛집 목록을 삭제합니다.

</details>

<details>
<summary><Strong>점심약속 상태 업데이트 및 히스토리 저장</Strong></summary>

- 1분 단위로 스케쥴링 하여 일정이 지난 `REQUESTED`요청 상태의 점심약속 상태 `EXPIRED`로 업데이트.
- 1분 단위로 스케줄링 하여 일정이 지난 `ACCEPTED`수락 상태의 점심약속 상태 `COMPLETED`로 업데이트 후 히스토리 등록

</details>
<details>
<summary><Strong>지역주변 대표맛집 저장</Strong></summary>

- 주 1회(매 주 월요일 00:00) [kakao map]에서 주요 도시별 맛집 크롤링하여 저장.
- 맛집 정보(식당이름/평점/대표메뉴/영업시간) 조회 기능 제공.
- 매주 별점/영업여부 등을 업데이트 하기 위해 크롤링 시마다 모든컬럼을 삭제 후 조회된내용 새로등록

</details>

---

### 프로젝트 구조

![Sys Arch](https://drive.google.com/uc?id=17XC_OfKZTq4ziU13OvhDruV5MW83rTTP)

### ERD

![ERD](https://drive.google.com/uc?id=1jxn3J1ZP1WE9K9ChXXeIBMiM_Bl7MAk8)

