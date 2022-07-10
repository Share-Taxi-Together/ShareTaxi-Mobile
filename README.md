# Share Taxi Together
한국공학대학교 택시 합승 어플리케이션  

<!-- [팀 노션 바로가기](https://adhesive-carpet-0f4.notion.site/MenuCanvas-a950308ec6d049c6acbd171f420bbe7c) -->
<!--
  - [0. 팀원](#0-팀원)
  - [1. 소개](#1-소개)
  - [2. 시스템 수행 시나리오](#2-시스템수행시나리오)
  - [3. 아키텍처](#3-아키텍처)
  - [4. 기술스택](#4-기술스택)
  - [5.팀원](#5-팀원)
  - [6. Git 규칙](#6-Git 규칙)
-->
  




## 1. 소개
 
에브리타임에서 택시 같이탈 인원을 구하는 게시물은 꾸준히 올라오지만 다른게시물에 해당 게시물이 묻혀 결국 혼자 택시를 타고오는 상황이 발생한다. 
이러한 상황을 해결하기 위해 택시합승인원을 구하는 어플리케이션을 제작하고자 한다. 한국공학대학교 학생들을 대상으로 다음과 같은 기능을 제공한다.  

- 회원가입시 학교메일 인증을 통해 한국공학대 학생들만 이용할 수 있도록 함
- 택시 탑승 목록 제공 / 택시 합승 신청
- 원하는 장소가 리스트에 없다면 장소 추가
- 같이 합승할 인원들과 채팅
- 점수제도 도입을 통한 노쇼방지
<img width="300px" src="https://user-images.githubusercontent.com/52478817/178130499-ddf15cf7-fc45-4dd0-bb3e-6726b0a23534.png">


## 2. 시스템 수행 시나리오
<img src="/images/시스템수행시나리오.png">  


## 3. 아키텍처
<img src="/images/architecture.png">  

## 4. 기술 스택

|분류|기술
|---|-----|
|Client|<img src="https://img.shields.io/badge/Android-3DDC84?style=flat&logo=Android&logoColor=black"> <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=black">
|BackEnd|<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=Spring Boot&logoColor=white"> |
|Database|<img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat&logo=Firebase&logoColor=white"> |
|Etc|<img src="https://img.shields.io/badge/GitHub-181717?style=flat&logo=GitHub&logoColor=white"> <img src="https://img.shields.io/badge/Figma-F24E1E?style=flat&logo=Figma&logoColor=white"> |

  

## 5. 팀원

| Name    | 이지윤                                  | 윤채림                                      |
| ------- | -------------------------------------- | ------------------------------------------- | 
| Profile |                                | <img width="200px" src="https://avatars.githubusercontent.com/u/87295524?v=4" />                             |
| role    | PM, DB, Frontend                | Frontend, Backend                                  |
| Github  | [@wldsbs](https://github.com/wldsbs) | [@crimii112](https://github.com/crimii112) |

  
## 6. Git 규칙

### 커밋 메시지
```
- feat : 새로운 기능 추가
- fix : 버그 수정
- docs : 문서 수정
- refactor : 코드 리팩토링
```

### 브랜치
**Master branch**

- 배포 가능한 상태만을 관리하는 브랜치
  - 새로운 Feature가 추가되었을때 1.0 → 2.0 과 같이 정수 버전 릴리즈
  - 새로운 기능이 추가되지 않고 버그 및 디자인 수정의 경우 1.0 → 1.1 과 같이 소수점 버전 릴리즈

**Develop branch**

- 다음에 배포할 것을 개발하는 브랜치
- develop 브랜치는 통합 브랜치의 역할을 하고, 평소에는 이 브랜치를 기반으로 개발 진행

**Feature branch**

- 기능 개발하는 브랜치이며, develop 브랜치로부터 분기
- 이름은 feature/(front/back)/(기능명)로 설정
- 본인의 작업이 완료되면 master 브랜치로 pull request 작성
- feature 브랜치는 그 기능을 다 완성할때까지 유지하고, 다 완성되면 Develop 브랜치로 merge

### 이슈
 - Issue에 Todo 기능 작성 및 Project 목록에 추가
 - 버그 또는 의문점이 생기면 Issue에 작성
 
 ### 프로젝트
  - 스프린트 기간은 2주를 넘지 않음
  - 최대한 스프린트 기간에 맞춰서 개발 완료할 수 있도록 계획 작성
  - 스프린트 시작하는 **화요일** 회의!
