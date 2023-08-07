## 프로젝트 기능 및 설계

### 프로젝트 개요
회사 근처 사람들끼리 점심 약속을 잡고 리뷰할 수 있는 애플리케이션.

---
### [사용기술]
- [x] Spring Boot(v2.7.14)-tomcat(v9.0.78)<br/>
- [x] REST API
- [x] Hibernate<br/>
- [x] MariaDB(v11.0.2)<br/>
- [x] Spring Security-JWT(HMAC512)<br/>
- [x] Spring Scheduling<br/>
- [x] Jsoup(v1.15.3)<br/>
  &nbsp;&nbsp; - (https://map.naver.com/)crawling
- [x] 외부 API 연동<br/>
  &nbsp;&nbsp; - Geocode API : 공공데이터<br/>
  &nbsp;&nbsp; - SMS 전송 API : 네이버클라우드<br/>
  &nbsp;&nbsp; - OCR API     : 네이버클로바<br/>
- [x] Swagger(v3.0.0)<br/>
- [x] JDK(v11.0.17)

---
### [Api]

**회원가입 api**
- 추가 :
  - 직접입력 가입 : 사용자 회원가입은 회원정보 직접입력을 통해 가능
  - OCR(외부api)가입 : 사용자 회원가입은 명함정보 OCR(명함사진업로드)+비밀번호입력을 통해 가능
  - 회원비밀번호는 `BCryptPasswordEncoder`를 통해 암호화하여 저장
- 조회 : 
  - 주변회원 조회(파라미터로 받은 Nkm 반경 내 회원목록 조회)
    - 거리가 가까운순으로 회원정렬 및 회사명으로 검색 가능

**점심약속-조회/생성/수정/삭제 + 승낙/거절/취소 api**
- 생성 : 사용자는 본인의 주변회원 리스트에 조회된 회원에게 시간/장소/메뉴를 적어 점심약속신청 가능.
- 조회 : 사용자는 본인에게 신청된 점심약속 리스트 조회가능.
- 수정 : 
     - 점심약속 내용 수정 : 점심약속 수락/거절 전 약속신청인은 생성된 점심약속 수정가능
     - 점심약속 승낙/거절 : 점심약속을 받은 피신청자는 점심약속을 승낙/거절 가능.
     - 점심약속 일정 취소 : 점심약속시간 전 내 두 사용자는 승낙 상태의 약속을 취소 가능. 
- 삭제 : 점심약속을 신청을 한 사용자는 승낙/거절 이전의 약속을 약속 시간 1시간 전 이내 약속을 삭제 가능.
- 외부 api 연동 : 생성된(신청된) 점심약속 내용을 피신청자에게 문자메세지로 전송

**사용자 평가 api**
- 추가 : 약속상태가 `COMPLETED` or `CANCELLED` 상대의 프로필에 사용자는 서로에게 별점을 매기고 댓글작성 가능
- 조회 : 사용자의 id로 사용자에게 달린 리뷰 조회 가능
- 수정 : 내가 작성한 리뷰에 한하여 수정가능
- 삭제 : 내가 작성한 리뷰에 한하여 삭제가능
- 단 리뷰는 한 상대에게 1개만 작성이 가능. (리뷰수정 or 삭제 후 재리뷰는 가능)

**로그인 api**
- 사용자는 아이디/비밀번호로 로그인 가능
- 비밀번호 복호화 -> 일치 시 토큰 발행

**즐겨찾는 회원 api**
- 추가 : 조회한 유저목록에서 유저를 선택하여 내 즐겨찾기 목록에 닉네임과 함께 추가 
- 조회 : 내가 등록한 즐겨찾는 회원 목록 조회
- 수정 : 내가 등록한 즐겨찾는 회원의 닉네임을 수정할 수 있음
- 삭제 : 내가 등록한 즐겨찾는 회원을 목록에서 삭제

**맛집 목록불러오기 api**
- 조회 : 크롤링을 통해 저장된 지역주변 대표맛집 목록을 파라미터(지역이름)를 이용하여 조회
  - `synchronized`를 사용하여 동기화 -> 크롤링이 진행되는 시점에는 맛집목록 조회 불가  
  - 동시성 문제 방지, 원자성 보장, 데이터 일관성 유지

**즐겨찾는 맛집 api**
- 추가 : 조회된 맛집 목록 중 식당을 선택하여 내가 즐겨찾는 맛집으로 추가 / 또는 직접입력하여 추가 가능
- 조회 : 내가 등록한 맛집 목록을 조회</br>
   - (맛집테이블(FOODIE_SPOT)의 컬럼들은 크롤링 시마다 삭제/생성 되므로 연관관계를 설정 하지 않고 조회된 식당의 모든 컬럼의 데이터를 즐겨찾기맛집 테이블에 저장)

---
### [Schedule]
**스케줄링 - 점심약속 상태 업데이트 및 히스토리 등재**
- 1분 단위로 스케쥴링 하여 점심약속시간이 지난 `REQUESTED`요청 상태의 점심약속 상태 `EXPIRED`로 업데이트.
- 1분 단위로 스케줄링 하여 점심약속시간이 지난 `ACCEPTED`수락 상태의 점심약속 상태 `COMPLETED`로 업데이트 후 히스토리 등재

### [Crawling]
**크롤링 - 지역주변 대표맛집 조회**
- 주 1회(매 주 월요일 00:00) [kakao map]에서 주요 도시별 맛집 크롤링하여 저장.
- 맛집 정보(식당이름/평점/대표메뉴/영업시간) 조회 기능 제공.
- 매주 별점/영업여부 등을 업데이트 하기 위해 크롤링 시마다 모든컬럼을 삭제 후 조회된내용 새로등록

---
### [ERD]
![ERD](https://drive.google.com/uc?id=1nPRMKyCP-aWwXeDBbsBMJ93-RQH1rxRX)

