![header](https://capsule-render.vercel.app/api?type=waving&color=B43E62&height=300&section=header&text=JavaFeed&fontColor=ffffff&fontSize=90)

# SpartaNbCamp-Javafeed-TestCode-pp
[Personal Project] <br>
5월에 진행한 `Javafeed` 팀 프로젝트에 이어서 테스트 코드를 작성하여 실습하는 개인 프로젝트 레포지토리 입니다.
<br> 팀 프로젝트에서 생성한 `Entity` / `Controller` / `Service` 에 대한 테스트를 진행하고,<br>
`AOP`를 활용해 API가 호출될 때마다 요청에 대한 값을 로그에 출력 시킵니다.
- `AOP`
- `단위 테스트`
- `통합 테스트`

<br>


### Git Commit Convention
작성 예시
```
git commit -m "[Test] - [필수 2 - DTO, Entity 테스트 추가하기] - NewsfeedRequestDto 테스트 코드 추가 #2"
```
<br>

1. Git Convention
  - `Add` : 새로운 파일 추가
  - `Fix` : 버그 수정에 대한 커밋
  - `Test` : 테스트 코드 수정에 대한 커밋

<br>

2. Branch 전략
  - `main` : 개발 완료 및 배포를 위한 브랜치

<br>

### Test Result - Entity
1. `UserTest` - `단위`
<img width="1318" alt="image" src="https://github.com/YeahyunKim/SpartaNbCamp-Javafeed-TestCode-pp/assets/132278619/5a2f6cc0-a070-4652-bb33-d6eb57ac27f0">

<br>
<br>
<br>

2. `NewsfeedTest` - `단위`
<img width="1316" alt="image" src="https://github.com/YeahyunKim/SpartaNbCamp-Javafeed-TestCode-pp/assets/132278619/30bb92a9-abf5-4106-96c3-df99a7009a52">

<br>
<br>
<br>

3. `CommentTest` - `단위`
<img width="1315" alt="image" src="https://github.com/YeahyunKim/SpartaNbCamp-Javafeed-TestCode-pp/assets/132278619/69815579-7f4f-4429-95cb-683265a96736">

<br>
<br>
<br>
<br>

### Test Result - Controller
1. `UserControllerTest` - `단위`
<img width="1314" alt="image" src="https://github.com/YeahyunKim/SpartaNbCamp-Javafeed-TestCode-pp/assets/132278619/b62661bd-98f5-491f-9d89-f8f933fcf1ef">

<br>
<br>
<br>

2. `NewsfeedControllerTest` - `단위`
<img width="1316" alt="image" src="https://github.com/YeahyunKim/SpartaNbCamp-Javafeed-TestCode-pp/assets/132278619/a1a15a5d-6d6a-4fea-b15f-863d96851202">

<br>
<br>
<br>

3. `CommetControllerTest` - `단위`
<img width="1322" alt="image" src="https://github.com/YeahyunKim/SpartaNbCamp-Javafeed-TestCode-pp/assets/132278619/a48acf74-cdd8-4365-bc85-a4a78e35f2e6">

<br>
<br>
<br>
<br>

### Test Result - Service
1. `UserServiceTest` - `단위`
<img width="1317" alt="image" src="https://github.com/YeahyunKim/SpartaNbCamp-Javafeed-TestCode-pp/assets/132278619/bf0e9783-0a20-4f4f-9af1-8d30752b6dee">

<br>
<br>
<br>

2. `NewsfeedServiceTest` - `단위`
<img width="1315" alt="image" src="https://github.com/YeahyunKim/SpartaNbCamp-Javafeed-TestCode-pp/assets/132278619/2fbcf91e-51de-422f-bdeb-dcb33b59b9d6">

<br>
<br>
<br>

3. `CommentServiceTest` - `단위`
<img width="1317" alt="image" src="https://github.com/YeahyunKim/SpartaNbCamp-Javafeed-TestCode-pp/assets/132278619/f81d8f89-2d90-40f0-a457-16bb90d85362">

<br>
<br>
<br>
<br>

### Test Result - ServiceIntegration
1. `UserServiceTest` - `통합`
<img width="1314" alt="image" src="https://github.com/YeahyunKim/SpartaNbCamp-Javafeed-TestCode-pp/assets/132278619/45209d8f-b965-4016-bc03-56346fb671fa">

<br>
<br>
<br>
