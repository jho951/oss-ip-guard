# Module

## 공개 모듈

| Artifact | Responsibility |
| --- | --- |
| `io.github.jho951:ip-guard-core` | strict IP 파싱, ordered rule 파싱/매칭, 결정 엔진, `RuleSource` 계약, 그리고 `RuleSources`/`IpGuards` 같은 built-in 진입 API |

## 공개 API 원칙

- `ip-guard-core`는 외부 프레임워크에 의존하지 않습니다.
- `RuleSource`는 같은 artifact 안의 최소 계약 타입으로 유지합니다.
- `RuleSources`와 `IpGuards`는 채택 편의성을 위한 built-in 진입 API로 유지합니다.
- 정책 모델은 `RuleAction`, `DecisionReason`, `MatchedRule`처럼 작고 안정적인 타입으로 노출합니다.
- 규칙의 선언 순서는 공개 계약의 일부입니다.

## 상위 모듈과의 관계

`ip-guard`는 1계층 엔진이므로, 상위 플랫폼 모듈이 존재하는 환경에서는 최종 애플리케이션이 `ip-guard-core`를 직접 조립하지 않는 구성을 권장합니다.

권장 경계:

- `ip-guard-core`: `RuleSource`, `RuleSources`, `IpGuards`, `IpGuardEngine`으로 ordered raw rule을 파싱하고 판정합니다.
- 상위 플랫폼 모듈: `List<String>` 규칙과 `RuleAction fallbackAction`을 받아 내부에서 `IpGuardEngine`을 생성하는 facade나 factory를 제공합니다.
- 최종 애플리케이션: 상위 플랫폼 API만 소비하고 `com.ipguard.*` 타입을 직접 import하지 않습니다.

상위 플랫폼이 자체 CIDR matcher로 대체하면 IPv6, range, IPv4 wildcard 같은 `ip-guard` 문법이 줄어들 수 있으므로, 규칙 판정은 가능한 `ip-guard-core`에 위임합니다.
