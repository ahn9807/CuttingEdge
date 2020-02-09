# CuttingEdge
Week two project of KAIST Immersion camp

### 우리 어플의 대략 적인 개요
---
1. 유저가 로그인을 하여 자신의 정보를 데이터베이스에 등록
2. 유저는 자신의 출발지와 목적지를 설정함
3. 현재 생성된 같이 타는 목록에 들어간뒤 마음에 드는 시간이 없으면 새로 생성
4. 새로 생성할 경우, 같이 타는 사람이 올때 까지 기다려야함
5. 누군가 같이 타도록 설정할 경우 푸쉬 알람과 함께 어디어디로 오라는 메세지 전송

#### 주의 사항
1. 목적지는 학교별로 선택하게 된다. 즉 KAIST는 단하나 혹은 여러개의 목적지중 하나를 선택하여야 한다. 또한 학교가 목적지가 될 수도 있다.
2. 인증은 페이스북 인증과 이메일 인증을 하여야한다. 이메일 인증은 학교 학생임을 인증하여, 보다 안전하게 탈수 있도록 하는 수단이다.
3. 가입시 id는 중복 체크를 해야 한다. 

### 네트워크 구현해야 할일
---
- [x] 페이스북과 email인증을 통해서 저장된 user의 정보 데이터 베이스에 등록
- [x] 유저에게 세션값을 제공하여, 세션로그인 구현 => sharedPreference를 이용하여 구현한다. 
- [x] 저장된 비밀번호는 암호화 하여 보안을 구현 
- [ ] 유저가 쿼리한 위치 정보를 추출하여 데이터베이스에 등록
- [ ] 현재 상태를 가지다가 적절한 친구가 생성되면 해당하는 유저들에게 전달

### 네트워크 API
우선 세션 로그인을 할 건지 아니면 일반 패스워드 로그인을 할 건지에 따라서 사용해야 하는 메소드가 달라진다.
반드시 다음 두개의 함수를 이용해서 session을 확보한 생태로 Network Manager 메소드를 사용해야 한다. 
####클라이언트 ####
##### 로그인 관련 함수 #####
```java
public boolean Connect()
public boolean Disconnect()
public boolean IsConnected()

public void Login(final Context context, UserData userData, final NetworkListener callback)
public void Logout(Context context, UserData userData, NetworkListener callback) 
public void Signup(final Context context, final UserData userData, String method, final NetworkListener callback)
public void ChangeUserData(Context context, UserData userData, final NetworkListener callback)

public void GetCurrentState(final NetworkListener callback)

public void MakeNewGroup(Context context, AlgorithmData group, final NetworkListener callback)
public void JoinGroup(Context context, AlgorithmData group, final NetworkListener callback)
public void GetGroupInformation(Context context, AlgorithmData group, final NetworkListener callback)
public void ExitGroup(Context context, AlgorithmData group, final NetworkListener callback)

public static String BitmapToString(Bitmap bitmapPicture)
public static String BitmapToString(Bitmap bitmapPicture)
public static Bitmap StringToBitmap(String bitmapString)
```
#### 서버 ####
```javascript
socket.on('client_login',json(UserModel)) 일반 로그인 함수 아이디와 패스워드 필요
socket.on('client_login_facebook',json(UserModel)) 페이스북 로그인 함수 아이디와 페이스북 토큰이 필요 만약 회원가입 되어 있지 않으면 자동으로 회원가입까지 처리
socket.on('client_singup',json(UserModel)) 회원가입 함수 아이디와 패스워드 필요
```
#### 데이터 베이스 관련 함수 ####

### 데이터 베이스 구조
---
#### 컬렉션 구조
1. 유저 전체의 정보를 저장할 컬렉션을 만든다. 이 데이터 베이스는 차후에 로그인이라든지 유저정보 가져오는데 사용한다.
2. 학교별로 컬렉션을 만든다. 이 컬렉션은 알고리즘을 돌리는 곳에서 사용한다.
### 도큐먼트 구조
1. 유저 정보

| key      | value                      |
|----------|----------------------------|
| id       | String                     |
| password | String 암호화됨            |
| name     | String                     |
| school   | String KAIST, GIST ...     |
| email    | String                     |
| gender   | String Man Woman Undefined |
| phone    | String ###########         |
| fbToken  | String                     |
| jsonWebToken | String                 |

2. 알고리즘 데이터 

| key                 |                 value                 |
|---------------------|:-------------------------------------:|
| id                  | [[String]] (same from user collection)|
| departureDateFrom   | String format year:month:day:hour:min |
| departureDateTo     | same                                  |
| deaparureLocation   |            String ex)KAIST            |
| destinationLocation |          String ex)st_dajeon          |

### SharedPreference 구조
1. network (session)


### 기능 ###
#### 클라이언트에서 주는 것 #####
##### 첫 화면 #####
1. 내가 지금 첫 화면이다
2. 학교

##### 두번째 화면 #####
1. 내가 Join 을 할건지 아니면 새로 방을 팔건지
2. 조인을 한다면 어떤 방에 조인 할 건지
3. 방을 판다면 어떤 조건으로 할 건지

#### 서버에서 주는 것####
##### 첫 화면 #####
1. 지금 현재 상태에 대한 모든 정보 (id 빼고)
2. 모든 정보를 주기전에 현재 시간이 만료된 방은 주지 않는다. 

##### 두번쨰 화면 #####
1. 조인 결과
2. 방 파졌는지 결과

##### 세번째 화면 #####
1. 현재 방의 정보
2. 방 나가기
3. 채팅

##### 기타 #####
1. 시간이 되면 알려줘야되 


### Memo ###

const chatroomSchema = mongoose.Schema({ 
    id:'String',
    member:[String], //id of each memebers
    message:[{nickname:String, date:{type:Date, default:Date.now}, message:'String'}]
})
채팅룸의 id 는 그와 상응하는 algorithmData 의 id와 일치시킨다. 






























