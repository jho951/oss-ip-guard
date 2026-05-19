# 아키텍처

## 책임

### 공개 모듈
- `ip-guard-core`
  - 공개 artifact는 하나이며, strict IP 파싱, ordered rule 파싱/매칭, 결정 엔진을 함께 제공합니다.
  - `RuleSource`, `RuleSources`, `IpGuards`, `IpGuardEngine`, `Decision` 같은 공개 계약과 진입 API를 포함합니다.
  - 지원 문법은 IPv4/IPv6 single, CIDR, range, IPv4 wildcard입니다.

### 규칙 입력 계층
- `RuleSource`
  - 규칙 원문을 제공하는 최소 SPI입니다.
  - raw text만 반환하며, rule parsing 책임은 갖지 않습니다.
- `RuleSources`
  - 문자열, `List<String>`, 파일, classpath resource 같은 일반적인 입력 경로를 위한 built-in adapter입니다.
  - 소비 애플리케이션이 별도 로더를 만들지 않아도 바로 엔진을 구성할 수 있게 합니다.

### 규칙 파싱 및 매칭 계층
- `RuleParser`
  - 각 줄을 `allow|deny + expression` 구조로 해석하고 컴파일합니다.
  - 주석 제거, line-aware parse error, wildcard/CIDR/range 같은 문법 해석을 담당합니다.
- `RuleSet`
  - 컴파일된 규칙을 IPv4/IPv6 패밀리별로 보관합니다.
  - 선언 순서를 유지한 채 first-match 방식으로 평가합니다.

### 판정 계층
- `IpGuardEngine`
  - 입력 IP를 정규화하고 strict literal 기준으로 파싱합니다.
  - 파싱된 IP에 대해 해당 패밀리 규칙만 평가하고 최종 allow/deny 결정을 반환합니다.
- `Decision`
  - 최종 결과를 `action`, `reason`, `normalizedIp`, `matchedRule` 중심의 최소 모델로 제공합니다.
  - 규칙 매칭, fallback, invalid input을 외부에서 구분할 수 있게 합니다.

### 소비 경계
- 직접 OSS로 사용하는 경우
  - 애플리케이션이 `ip-guard-core`를 직접 의존하고 `IpGuards`나 `IpGuardEngine`을 바로 사용할 수 있습니다.
- 상위 플랫폼이 감싸는 경우
  - 상위 모듈이 내부에서 `ip-guard-core`를 사용하고, 최종 애플리케이션에는 facade나 factory만 노출하는 구성을 권장합니다.
  - 이 경우에도 `ip-guard`의 규칙 문법은 가능한 한 축소 없이 그대로 전달하는 편이 안전합니다.

## 원칙

### 판정 책임 집중
- 규칙 판정 집중: `ip-guard`는 ordered allow/deny 결정에만 집중합니다.
- 웹 계층 분리: 요청 객체에서 클라이언트 IP를 추출하는 책임은 애플리케이션 또는 상위 통합 계층에 둡니다.
- 프록시 정책 외부화: `X-Forwarded-For`, `Forwarded`, trusted proxy 정책은 코어 바깥에서 결정합니다.

### 결정 모델의 단순성
- 명시적 action: 모든 규칙은 반드시 `allow` 또는 `deny`를 가져야 합니다.
- 선언 순서 보존: 규칙 순서는 공개 계약의 일부이며, 첫 번째 매칭 규칙이 최종 결과를 결정합니다.
- fallback 고정: 어떤 규칙도 매칭되지 않으면 엔진 생성 시 지정한 `fallbackAction`을 사용합니다.
- invalid 입력 차단: 입력이 IP literal이 아니면 항상 `INVALID_IP`로 거부합니다.

### 추상화 경계의 분리
- 로딩과 파싱 분리: `RuleSource`는 규칙 원문만 제공하고, 파싱은 `RuleParser`가 담당합니다.
- 파싱과 판정 분리: 규칙 해석은 파서에서 끝내고, 최종 평가 순서는 `RuleSet`과 `IpGuardEngine`이 담당합니다.
- 결과 모델 최소화: 외부에는 판정에 필요한 최소 metadata만 노출하고 내부 매칭 구현은 감춥니다.

### 환경 비의존성
- 프레임워크 비의존: Spring, Servlet, WebFlux 같은 특정 웹 프레임워크 통합을 코어에 포함하지 않습니다.
- 운영 정책 분리: 규칙 파일 reload 전략, 로깅 정책, 헤더 신뢰 기준은 상위 애플리케이션 책임으로 둡니다.
- 네트워크 해석 배제: hostname 허용 정책이나 DNS lookup을 판정 모델에 섞지 않습니다.

### 문법 보존과 확장성
- 지원 문법 유지: single, CIDR, range, IPv4 wildcard를 코어 문법으로 유지합니다.
- 축소 구현 지양: 상위 플랫폼이 자체 matcher로 일부 문법만 재구현하는 방식은 피합니다.
- 확장 지점 명확화: 새 입력 소스는 `RuleSource`, 새 규칙 문법은 `RuleParser`와 `IpRule` 계층에서 확장합니다.

## 동작 흐름

```text
RuleSource / RuleSources
        -> raw rule text
        -> RuleParser
        -> RuleSet
        -> IpGuardEngine
        -> Decision
```

1. `RuleSource` 또는 `RuleSources`가 규칙 원문을 제공합니다.
2. `RuleParser`가 각 줄을 `allow|deny + expression` 규칙으로 컴파일합니다.
3. `IpGuardEngine`이 입력 IP를 strict literal 기준으로 정규화하고 파싱합니다.
4. `RuleSet`이 같은 IP 패밀리의 규칙을 선언 순서대로 평가합니다.
5. 첫 번째 매칭 규칙이 있으면 그 action으로 결정하고, 없으면 `fallbackAction`으로 결정합니다.

## 참고

- 규칙 문법 상세는 [규칙 문법](./rule-syntax.md)을 봅니다.
- 확장 방법은 [확장 가이드](./extension-guide.md)를 봅니다.
- 직접 통합 예시는 [Servlet 통합](./integration-servlet-filter.md), [Spring 통합](./integration-spring.md)을 봅니다.
