# Architecture

`ip-guard`는 1계층 OSS IP 접근 제어 엔진입니다.

## 모듈

- 공개 artifact는 `ip-guard-core` 하나입니다.
- `RuleSource` 같은 최소 계약 타입도 같은 artifact 안에 포함됩니다.
- 내부 패키지는 `core`, `spi`처럼 역할별로 나뉘지만, 빌드와 배포는 단일 모듈로 유지합니다.

## 핵심 정책 모델

- 각 규칙은 반드시 `allow` 또는 `deny` action을 가져야 합니다.
- 규칙은 선언된 순서대로 평가됩니다.
- 첫 번째 매칭 규칙이 최종 판정을 결정합니다.
- 어떤 규칙도 매칭되지 않으면 엔진 생성 시 지정한 `fallbackAction`을 사용합니다.
- 입력이 IP literal이 아니면 항상 `INVALID_IP`로 거부합니다.

## 경계 원칙

- 핵심 판정 로직은 단일 `ip-guard-core` artifact 안에 둡니다.
- `RuleSource`는 raw text만 반환하고 파싱 책임을 가지지 않습니다.
- `RuleSources`는 파일, 문자열, classpath resource 같은 일반적인 로딩 경로를 위한 built-in adapter입니다.
- `Decision`은 `action`, `reason`, `normalizedIp`, `matchedRule`만 노출하는 최소 공통 모델을 유지합니다.
- 상위 플랫폼 모듈이 `ip-guard`를 감싸는 경우, 최종 애플리케이션은 `com.ipguard.*` 타입보다 상위 플랫폼의 facade나 factory를 우선 사용합니다.
- `ip-guard` 고유 문법은 상위 플랫폼 API에서도 손실 없이 전달되어야 합니다.

## 동작 흐름

1. `RuleSource` 또는 `RuleSources`가 규칙 원문을 제공합니다.
2. `RuleParser`가 각 줄을 `allow|deny + expression` 규칙으로 컴파일합니다.
3. `IpGuardEngine`이 입력 IP를 strict literal 기준으로 정규화하고 파싱합니다.
4. 같은 IP 패밀리의 규칙을 선언 순서대로 평가합니다.
5. 첫 번째 매칭 규칙이 있으면 그 rule action으로 결정하고, 없으면 `fallbackAction`으로 결정합니다.

## 규칙 문법

`ip-guard-core`는 다음 rule expression을 지원합니다.

- Single: 단일 IPv4/IPv6 주소
- CIDR: IPv4/IPv6 CIDR 대역
- Range: 같은 패밀리의 시작 IP와 끝 IP
- IPv4 wildcard: `192.168.*.*`처럼 뒤쪽 octet을 `*`로 표현하는 문법

상세한 예시와 제약은 [규칙 문법](./rule-syntax.md)을 봅니다.

## 상위 플랫폼 통합 기준

상위 플랫폼 모듈이 `ip-guard`를 2계층 API로 노출할 때는 다음 형태를 권장합니다.

- 소비자는 ordered raw rule 목록과 `fallbackAction`만 전달합니다.
- 상위 플랫폼 내부에서 `RuleSource`와 `IpGuardEngine`을 생성합니다.
- 소비자 애플리케이션은 `com.ipguard.*` 패키지를 직접 import하지 않습니다.
- IPv6, CIDR, range, IPv4 wildcard 같은 `ip-guard` 문법을 별도 축소 구현으로 대체하지 않습니다.

예시:

```java
PlatformIpGuardFacade.fromRules(rules, RuleAction.DENY);
```
