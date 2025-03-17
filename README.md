
# JUST - 익명 고민 상담

<p align="center">
주변 사람에게는 말 못할 고민을 털어 놓을 수 있는 공간, Just  <br>

## 🚪 프로젝트 소개
> **Just Application은 익명성을 강조한 SNS입니다. 다소 공격적인 댓글과 선정적인 성향의 SNS보다 사람들이 편하게 느끼고 그런 생태계를 만들고자 모든 사용자가 노력하기를 바랍니다. 해당 어플리케이션은 익명성을 지키면서 SNS를 재미있고 따듯하게 즐기수 있습니다!**

<br><br>
## 🔧 기술 스택
언어 <br>
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)<br><br>
프레임워크 & 라이브러리 <br>
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)<br><br>
데이터베이스 <br>
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)<br><br>
자동화 배포 <br>
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)  <br><br><br>

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


## 📦 맡은 역할과 성능 최적화

### 🔹 대량 요청 처리 최적화 (비동기 큐 활용)
문제점 파악
- API 쓰기 요청 증가 시 성능 저하 문제 발견 → **Pinpoint APM**으로 병목 분석

해결 과정 탐색
- **DB 커넥션 획득 과정에서 병목 발생** → 벌크 쿼리 고려했지만, **재시도 로직과 비동기 API 필요성** 확인

해결 방안 탐색
- **Redis Pub/Sub → 메시지 유실 위험, Stream → 메모리 사용 부담**

해결
- **RabbitMQ 기반 비동기 배치 처리 도입 → 1분 동안 33,000건 부하 테스트, 평균 응답 시간 240ms 유지**

### 🔹 N+1 문제 해결
문제점 파악
- **조히쿼리에서 응답속도 저하 확인 -> JPA N+1 문제 확인**

해결 과정 탐색 및 해결
- **JPA fetch join 활용**하여 연관 엔티티 한 번의 쿼리로 조회,  **배치 크기 조정 (Batch Size 설정)**으로 추가적인 쿼리 최적화

<br><br>
### 🔹 인덱스 최적화 (역순 스캔 문제 해결)
문제점 파악
- API에서 **게시글 생성 시간 60만건의 풀 테이블 스캔 발생-> 역순 인덱스로 사용**

해결 과정 탐색
- **Real MySQL 분석 결과, 역순 스캔보다 정순 스캔이 잠금 과정에서 성능 우위**

해결
- **생성 시간을 음수 변환 후 정순 스캔 적용 → API 응답 시간 160ms → 120ms (25% 개선)**

<br><br>
### 🔹 캐시 최적화 (CAS 연산 활용)
문제점 파악
- 로컬 캐시에서 `synchronized` 블록으로 인해 **경합(lock) 문제 발생**

해결 과정 탐색 및 해결
-  **CAS(Compare-And-Swap) 연산을 활용해 락 없이 안전하게 캐시 갱신**  
 






