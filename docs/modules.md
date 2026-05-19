# 모듈 가이드

## 공개 모듈

| Artifact | Responsibility |
| --- | --- |
| `io.github.jho951:ip-guard-core` | strict IP 파싱, ordered rule 파싱/매칭, 결정 엔진, `RuleSource` 계약, `RuleSources`, `IpGuards` 같은 내장 진입 API |

## 패키지별 책임

| Package | Responsibility |
| --- | --- |
| `com.ipguard.spi` | 규칙 원문 공급 계약과 내장 로더 |
| `com.ipguard.core.ip` | IP 파싱, 정규화, IPv4/IPv6 값 객체 |
| `com.ipguard.core.rules` | 규칙 문법 파싱과 매칭 |
| `com.ipguard.core.engine` | 엔진과 진입 팩토리 |
| `com.ipguard.core.decision` | 최소 판정 결과 모델 |

## 공개 API 원칙

- `ip-guard-core`는 외부 프레임워크에 의존하지 않습니다.
- `RuleSource`는 raw text만 제공하고 파싱 책임은 가지지 않습니다.
- `RuleSources`와 `IpGuards`는 채택 편의성을 위한 내장 API입니다.
- 정책 모델은 `RuleAction`, `DecisionReason`, `MatchedRule`처럼 작고 안정적인 타입으로 노출합니다.
- 규칙의 선언 순서는 공개 계약의 일부입니다.

## 소비 방식

### 직접 OSS로 소비하는 경우

- 애플리케이션이 `ip-guard-core`를 직접 의존합니다.
- `IpGuards`와 `IpGuardEngine`을 바로 사용해도 됩니다.
- 이 경우 README의 예제처럼 `com.ipguard.*` 타입을 직접 import하는 것이 정상입니다.

### 상위 플랫폼이 감싸는 경우

- 상위 플랫폼이 `ip-guard-core`를 내부 구현으로 사용합니다.
- 최종 애플리케이션은 가능하면 상위 플랫폼 API만 소비합니다.
- 상위 플랫폼은 규칙 문법을 줄여서 재구현하지 말고 `ip-guard-core`에 위임하는 편이 낫습니다.

권장 경계:

- `ip-guard-core`: `RuleSource`, `RuleSources`, `IpGuards`, `IpGuardEngine`으로 ordered raw rule을 파싱하고 판정합니다.
- 상위 플랫폼 모듈: `List<String>` 규칙과 `RuleAction fallbackAction`을 받아 내부에서 `IpGuardEngine`을 생성하는 facade나 factory를 제공합니다.
- 최종 애플리케이션: 상위 플랫폼 API만 소비하고 `com.ipguard.*` 타입을 직접 import하지 않습니다.

상위 플랫폼이 자체 CIDR matcher로 대체하면 IPv6, range, IPv4 wildcard 같은 `ip-guard` 문법이 줄어들 수 있으므로, 규칙 판정은 가능한 `ip-guard-core`에 위임합니다.
