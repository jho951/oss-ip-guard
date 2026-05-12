# Docs

이 디렉터리는 `ip-guard`의 구현, 모듈 구조, 확장, 테스트, 트러블슈팅 문서를 모아둡니다.

## 먼저 읽기

### 시작할때 

1. [아키텍처](./architecture.md)
2. [모듈 가이드](./modules.md)
3. [규칙 문법](./rule-syntax.md)
4. [extension 가이드](./extension-guide.md)
5. [Servlet 통합 예제](./integration-servlet-filter.md)
6. [Spring 통합 예제](./integration-spring.md)

### 문제를 만났을 때

1. [트러블슈팅](./troubleshooting.md)

### 모듈과 테스트

1. [테스트/CI 가이드](./test-and-ci.md)

## 읽는 순서

- 공개 설정 계약은 `oss-contract` 저장소를 봅니다.
- 처음 보는 사람은 `아키텍처`와 `모듈 가이드`부터 읽습니다.
- 지원하는 IP 규칙 형식은 `규칙 문법`을 봅니다.
- 내장 `RuleSources`와 `IpGuards`로 해결되지 않는 경우에만 `RuleSource`를 직접 구현하고, 그때는 `extension 가이드`를 먼저 봅니다.
- 테스트와 CI는 `테스트/CI 가이드`를 봅니다.
- 운영 중 문제는 `트러블슈팅`을 봅니다.
