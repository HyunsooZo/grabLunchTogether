## Grab Lunch Together 프로젝트

### 프로젝트 소개

회사 근처 사람들끼리 점심 약속을 잡고 리뷰할 수 있는 기능들을 구현하고</br>
각 기능에 대한 테스트 코드를 작성해 보았습니다.
---

### 기술 스택

- [x] Spring Boot(v2.7.14)<br/>
- [x] REST API
- [x] Spring Data Jpa & Hibernate<br/>
- [x] MariaDB(v11.0.2)<br/>
- [x] Spring Security-JWT(HMAC512)<br/>
- [x] Spring Scheduling<br/>
- [x] Jsoup(v1.15.3)<br/>
- [x] Swagger(v3.0.0)<br/>
- [x] JDK(v11.0.17)
- [x] JavaMailSender
- [x] 외부 API 연동<br/>
  &nbsp;&nbsp; - Geocode API : 공공데이터<br/>
  &nbsp;&nbsp; - SMS 전송 API : 네이버클라우드<br/>
  &nbsp;&nbsp; - OCR API     : 네이버클로바<br/>

---

### Restful Api 설계

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

**User(유저) 관련 api**
- 일반회원가입 : 사용자 일반가입은 회원정보 직접입력을 통해서 가능합니다.
- 간편회원가입 : 사용자 간편가입은 명함사진(OCR진행)과 비밀번호입력을 통해서 가능합니다.
- 로그인 : 비밀번호는 `BCryptPasswordEncoder`를 통해 암호화하여 저장하고 로그인 시 복호화하여 비교 후 
          회원정보와 일치 시 토큰을 발행합니다.
- 회원정보 수정 : 입력된 수정정보로 회원정보를 업데이트 합니다.
- 비밀번호 변경 : 입력된 비밀번호를 암호화 하여 비밀번호를 업데이트합니다.
- 비밀번호 초기화 : 임시비밀번호를 암호화하여 저장 하고 사용자 Email 로 임시비밀번호를 전송합니다.

**Plan(점심약속) 관련 api** 
- 주변회원 탐색 : 사용자는 본인이 설정한 거리 내 회원목록을 조회할 수 있습니다. 
- 점심약속 신청 : 주변회원 리스트에 조회된 회원에게 시간/장소/메뉴/요청메세지를 적어 점심약속신청을 생성할 수 있습니다. (피신청인에게 SMS 전송)
- 점심약속 업데이트 :
    - 점심약속 내용 업데이트 : 점심약속 수락/거절 전 약속신청인은 생성된 점심약속을 수정할 수 있습니다.
    - 점심약속 승낙/거절 : 점심약속을 받은 피신청인은 해당 점심약속을 승낙 또는 거절할 수 있습니다. 
    - 점심약속 일정 취소 : 점심약속시간 전 내 두 사용자는 승낙 상태의 약속을 취소할 수 있습니다. 
- 점심약속 삭제 : 점심약속을 신청을 한 사용자는 승낙/거절 이전의 약속을 약속 시간 1시간 전 이내에 삭제 할 수 있습니다.

**Plan History(약속 히스토리) 관련 api**
- 점심약속 히스토리 조회 : 사용자가 관련되었던 약속의 점심약속 히스토리 목록을 조회할 수 있습니다.
- (등록의 경우 매 분 스케줄러를 통해 `COMPLETED` , `CANCEL`상태이면서 약속시간이 만료된 약속을 히스토리에 저장합니다.)

**User Review(유저 리뷰) 관련 api**
- 리뷰 작성 : 점심약속이 완료된 상대의 프로필에 사용자는 서로에게 별점을 매기고 리뷰를 작성할 수 있습니다.
- 리뷰 조회 : 상대방 사용자의 id로 상대방 사용자에게 달린 리뷰리스트를 조회할 수 있습니다.
- 리뷰 수정 : 본인이 작성한 리뷰에 한하여 리뷰를 수정할 수 있습니다.
- 리뷰 삭제 : 본인이 작성한 리뷰에 한하여 리뷰를 삭제할 수 있습니다.

**Favorite User(즐겨찾는 유저) 관련 api**
- 즐겨찾는 유저 등록 : 조회한 유저목록에서 유저를 선택하여 내 즐겨찾기 목록에 닉네임과 함께 등록합니다.
- 즐겨찾는 유저 조회 : 내가 등록한 즐겨찾는 유저 목록을 조회합니다.
- 즐겨찾는 유저 닉네임 수정 : 내가 등록한 즐겨찾는 유저의 닉네임을 수정할 수 있습니다.
- 즐겨찾는 유저 삭제 : 내가 등록한 즐겨찾는 유저를 목록에서 삭제할 수 있습니다.

**Must-Eat-Spot(맛집) 관련 api**

- 조회 : 크롤링을 통해 저장된 지역주변 대표맛집 목록을 파라미터(지역이름)를 이용하여 조회 합니다.
    - `synchronized`를 사용하여 동기화 -> 크롤링이 진행되는 시점에는 맛집목록 조회 불가합니다.
    - 동시성 문제 방지, 원자성 보장, 데이터 일관성 유지

**BookMark Spot(즐겨찾는 맛집) 관련 api**

- 즐겨찾는 맛집 등록 : 
  - 직접입력 등록 : 조회된 맛집 목록 중 식당을 선택하여 내가 즐겨찾는 맛집으로 등록합니다.
  - 맛집정보로 등록 : 조회한 Must-Eat-Spot(맛집)의 id로 즐겨찾기 맛집을 등록합니다.
- 즐겨찾는 맛집 조회 : 본인이 등록한 맛집 목록을 조회</br>
    - (맛집테이블(FOODIE_SPOT)의 컬럼들은 크롤링 시마다 삭제/생성 되므로 연관관계를 설정 하지 않고 조회된 식당의 모든 컬럼의
      데이터를 즐겨찾기맛집 테이블에 저장)
- 즐겨찾는 맛집 삭제 : 본인이 등록한 맛집 목록을 삭제합니다.

---

### [Schedule]

**점심약속 상태 업데이트 및 히스토리 등재**

- 1분 단위로 스케쥴링 하여 점심약속시간이 지난 `REQUESTED`요청 상태의 점심약속 상태 `EXPIRED`로 업데이트.
- 1분 단위로 스케줄링 하여 점심약속시간이 지난 `ACCEPTED`수락 상태의 점심약속 상태 `COMPLETED`로 업데이트 후 히스토리 등재

**크롤링 - 지역주변 대표맛집 조회**

- 주 1회(매 주 월요일 00:00) [kakao map]에서 주요 도시별 맛집 크롤링하여 저장.
- 맛집 정보(식당이름/평점/대표메뉴/영업시간) 조회 기능 제공.
- 매주 별점/영업여부 등을 업데이트 하기 위해 크롤링 시마다 모든컬럼을 삭제 후 조회된내용 새로등록

---

### [ERD]

![ERD](https://drive.google.com/uc?id=1ygug3YzOmOSk4R3MUAgpUQ_ecEPSPXap)

