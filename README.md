# Spring Plus

Spring Boot 심화 과제 - 트러블슈팅 및 기능 구현

---

## 사용 기술

- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Data JPA / QueryDSL
- JWT
- MySQL

---

## 구현 내용

| 레벨 | 분류 | 내용 |
|------|------|------|
| Lv.1 | 트러블슈팅 | `@Transactional(readOnly = true)` 설정으로 인한 INSERT 오류 수정 |
| Lv.2 | 기능 추가 | User 닉네임 추가 및 JWT 클레임에 포함 |
| Lv.3 | 기능 개선 | JPQL 동적 쿼리 - weather / 수정일 기간 검색 |
| Lv.4 | 테스트 | 컨트롤러 단위 테스트 수정 (`@WebMvcTest`) |
| Lv.5 | 트러블슈팅 | AOP 포인트컷 표현식 수정 (`@Before`) |
| Lv.6 | 기능 개선 | JPA Cascade - 할 일 생성 시 작성자 자동 담당자 등록 |
| Lv.7 | 성능 개선 | N+1 문제 해결 (`JOIN FETCH`) |
| Lv.8 | 리팩토링 | JPQL → QueryDSL 전환 |
| Lv.9 | 기능 추가 | Spring Security 도입 (JWT 필터 기반 인증) |
| Lv.10 | 도전 | QueryDSL Projections + 동적 검색 + 페이징 |
| Lv.11 | 도전 | 트랜잭션 전파(`REQUIRES_NEW`) - 매니저 등록 로그 독립 저장 |

---

## 주요 구현 상세

**Lv.8 - QueryDSL Custom Repository**

`TodoRepositoryCustom` 인터페이스와 `TodoRepositoryCustomImpl`로 분리해 `JpaRepository`와 함께 사용했다. `fetchJoin()`으로 N+1 문제를 방지했다.

**Lv.10 - 검색 API**

`@QueryProjection`으로 필요한 필드(제목, 담당자 수, 댓글 수)만 DTO로 직접 매핑했다. `BooleanExpression`을 활용해 null 조건은 WHERE절에서 자동 제외되도록 처리했다.

**Lv.11 - 로그 트랜잭션 분리**

`LogService.saveLog()`에 `Propagation.REQUIRES_NEW`를 적용해 매니저 등록 트랜잭션이 롤백되더라도 로그는 독립적으로 커밋되도록 구현했다.