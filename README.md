# Share Taxi Together

### 택시 합승 어플리케이션  

<!-- [팀 노션 바로가기](https://adhesive-carpet-0f4.notion.site/MenuCanvas-a950308ec6d049c6acbd171f420bbe7c) -->

  - [0. 팀원](#0-팀원)
  - [1. 소개](#1-소개)
  - [2. 커밋규칙 & 브랜치](#2-커밋규칙&브랜치)
  <!--- 3. 아키텍처 -->
  

## 0. 팀원

| Name    | 이지윤                                  | 윤채림                                      |
| :------- | :---------------------------------------- | :---------------------------------------- | 
| Profile |                                | <img width="200px" src="https://avatars.githubusercontent.com/u/87295524?v=4" />                             |
| role    | PM, Database, Frontend                | Frontend, Backend                                  |
| Github  | [@wldsbs](https://github.com/wldsbs) | [@crimii112](https://github.com/crimii112) |


## 1. 소개

**한국공학대학교 학생들을 위한 택시 합승 어플리케이션**  
에브리타임에서 택시 같이탈 인원 구하는 게시물은 꾸준히 올라오지만 다른게시물에 해당 게시물이 묻혀 결국 혼자 택시를 타고오는 상황이 발생한다. 
우리는 이런 상황을 해결하기 위해 택시합승인원을 구하는 어플리케이션을 제작하고자 한다. 대상은 한국공학대학교 학생들로 선정하였으며 
회원가입시 학교메일 인증을 통해 한국공학대 학생들만 이용할 수 있도록 할 예정이다.  
<img width="300px" src="https://user-images.githubusercontent.com/52478817/178130499-ddf15cf7-fc45-4dd0-bb3e-6726b0a23534.png">


## 2. 커밋규칙&브랜치

```
- feat : 기능 (새로운 기능)
- fix : 버그 (버그 수정)
- docs : 문서 (문서 추가, 삭제, 수정)
```

**Master branch**

- 배포 가능한 상태만을 관리하는 브랜치
  - 새로운 Feature가 추가되었을때 1.0 → 2.0 과 같이 정수 버전 릴리즈
  - 새로운 기능이 추가되지 않고 버그 및 디자인 수정의 경우 1.0 → 1.1 과 같이 소수점 버전 릴리즈

**Develop branch**

- 다음에 배포할 것을 개발하는 브랜치
- develop 브랜치는 통합 브랜치의 역할을 하고, 평소에는 이 브랜치를 기반으로 개발 진행

**Feature branch**

- 기능 개발하는 브랜치이며, develop 브랜치로부터 분기
- feature 브랜치는 그 기능을 다 완성할때까지 유지하고, 다 완성되면 Develop 브랜치로 merge

