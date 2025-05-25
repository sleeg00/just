
# JUST - 익명 고민 상담

<p align="center">
주변 사람에게는 말 못할 고민을 털어 놓을 수 있는 공간, Just  <br>

## 🚪 프로젝트 소개
> **Just Application은 익명성을 강조한 SNS입니다. 다소 공격적인 댓글과 선정적인 성향의 SNS보다 사람들이 편하게 느끼고 그런 생태계를 만들고자 모든 사용자가 노력하기를 바랍니다. 해당 어플리케이션은 익명성을 지키면서 SNS를 재미있고 따듯하게 즐기수 있습니다!**

<br><br>
## 🔧 기술 스택
**언어**  
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)

**프레임워크 & 라이브러리**  
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)

**데이터베이스**  
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)

**모니터링**  
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white)
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white)
![Pinpoint APM](https://img.shields.io/badge/Pinpoint_APM-0080FF?style=for-the-badge&logoColor=white)

**자동화 배포**  
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)


  <br><br>
## ⚙️    아키텍쳐 설계 

### 💎 백엔드 아키텍쳐 

<img width="1592" alt="스크린샷 2025-03-16 오후 12 44 28" src="https://github.com/user-attachments/assets/8dcfc5a9-d64d-4fd7-ad58-2ce0a91478da" /><br><br>


### 💎 ERD
![Copy of Just](https://github.com/inje-megabrain/JUST-be/assets/96710732/653e7f58-802c-465c-af1d-25ffb38f93e4) <br><br><br><br>




## 📺 화면 구성
<table>
  <tr>
    <td><img width="350" alt="메인 페이지" src="https://github.com/inje-megabrain/JUST-be/assets/96710732/847ce9a8-c58c-4413-940a-c53badedf7db"></td>
    <td><img width="350" alt="댓글 페이지" src="https://github.com/inje-megabrain/JUST-be/assets/96710732/3ae14c73-1605-45b9-8ce1-ee4c3fa4aaa4"></td>
    <td><img width="350" alt="글 작성 페이지" src="https://github.com/inje-megabrain/JUST-be/assets/96710732/79c941cc-9c0b-424e-b204-9d592e827978"></td>
    <td><img width="350" alt="태그 생성 페이지" src="https://github.com/inje-megabrain/JUST-be/assets/96710732/184ec1ff-9c8a-4148-ad90-4e3999eafdbd"></td>
  </tr>
</table>
<br><br><br><br>


## 📦 맡은 역할
| 구분                     | 상세 내용                                                                   | 효과                          |
| ---------------------- | ----------------------------------------------------------------------- | --------------------------- |
| **🗂️ ERD & 요구사항 정의**  | • 전체 도메인 모델링<br>• ERD 작성 ↔ 이해 관계자 리뷰 문서화                                | 개발 범위 명확화 & 테이블 변경 빈도 ↓     |
| **🛠️ CRUD API 설계·개발** | • Spring Boot 기반 게시글 CRUD·검색 API 구현•  | 게시글 관련 CRUD 기능 설계 및 개발 |
| **🚀 CI/CD 파이프라인**     | • GitHub Actions + Docker Compose 배포 워크플로 설계<br>• git-secret으로 환경변수 암호화 | 코드 푸시 → 배포 자동화(평균 3 분)      |
| **📊 실시간 모니터링**        | • Grafana·Prometheus·Pinpoint APM 도입<br>• JVM/DB/Queue 메트릭 대시보드 구축      | 시스템 장애/병목 지점 파악 가능      |
| **🔍 인덱스 튜닝**          | • EXPLAIN 분석 → Full Scan 구간 식별<br>• 역값 ASC 인덱스 적용                       | 게시글 리스트 API 응답 25 % 단축      |
| **🗡️ N+1 제거**         | • JPA Fetch Join + Batch Size 설정<br>• 추가 쿼리 90 건 → 1 건 축소               | 요청 당 DB RT 40 ms ↓          |
| **⚙️ 대량 쓰기 최적화**       | • RabbitMQ 비동기 큐 + 배치 처리<br>• Lock/커넥션 경합 해소                            | 1 분 33 K TPS, 평균 240 ms 유지  |
| **🔐 인증 로직 개선**        | • 커스텀 `HandlerMethodArgumentResolver`<br>• JWT → 컨트롤러 자동 주입             | 인증 코드 중복 제거 (모듈 결합도↓)       |
| **🧩 중복 쿼리 리팩터링**      | • Template Method + Hook 패턴 적용<br>• QueryDSL 중복 로직 65 % 제거              | 유지보수 시간 ↓ / 신규 정렬 확장 용이     |





