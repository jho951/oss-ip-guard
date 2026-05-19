# 문서 안내

이 디렉터리는 `ip-guard`를 세 가지 관점으로 설명합니다.

- 코어 엔진이 어떻게 추상화돼 있는지
- 애플리케이션에 직접 붙일 때 어떻게 쓰는지
- 확장, 테스트, 운영 문제를 어떻게 다루는지

## 처음 보는 경우

1. [아키텍처](./architecture.md)
2. [모듈 가이드](./modules.md)
3. [규칙 문법](./rule-syntax.md)

## 직접 통합하는 경우

1. [Servlet 통합 예제](./integration-servlet-filter.md)
2. [Spring 통합 예제](./integration-spring.md)

## 확장하는 경우

1. [확장 가이드](./extension-guide.md)

## 검증하거나 문제를 추적하는 경우

1. [테스트/CI 가이드](./test-and-ci.md)
2. [트러블슈팅](./troubleshooting.md)

## 추천 읽기 순서

- OSS를 직접 사용할 경우: `README` -> `아키텍처` -> `규칙 문법` -> `통합 문서`
- 상위 플랫폼이 감싸서 사용할 경우: `아키텍처` -> `모듈 가이드` -> `규칙 문법`
- 내장 `RuleSources`와 `IpGuards`로 충분하지 않을 때만 `확장 가이드`를 봅니다.
