# 확장 가이드

먼저 전제부터 분명히 합니다. `ip-guard`는 대부분 "확장"보다 "조합"으로 사용하는 쪽이 단순합니다.

## 먼저 확인할 것

커스텀 로딩이 필요하지 않다면 `RuleSource`를 직접 구현하기 전에 내장 어댑터를 먼저 검토합니다.

- `RuleSources.fromString(...)`
- `RuleSources.fromLines(...)`
- `RuleSources.fromPath(...)`
- `RuleSources.fromResource(...)`

애플리케이션에서 바로 엔진이 필요하면 `IpGuards`를 통해 같은 입력 경로로 바로 `IpGuardEngine`을 생성할 수 있습니다.

## `RuleSource`를 직접 구현해야 하는 경우

아래처럼 규칙 원문이 파일이나 resource가 아닌 다른 저장소에 있을 때만 `RuleSource` 구현을 고려합니다.

- 데이터베이스
- 원격 설정 저장소
- 암호화된 설정 파일
- 사내 설정 시스템

핵심 원칙:

- `RuleSource`는 raw text만 반환합니다.
- rule parsing이나 매칭 책임을 `RuleSource` 안으로 넣지 않습니다.
- 가능하면 `allow|deny + expression` 형태의 원문을 그대로 반환합니다.

## 새 규칙 타입 추가

새 IP 규칙 타입을 추가할 때는 `ip-guard-core` 안에서 판정 모델부터 추가합니다.

- 규칙은 `IpRule` 구현으로 표현합니다.
- 각 규칙은 `family()`와 `kind()`를 명확히 가져야 합니다.
- 선언 순서를 바꾸지 않고도 평가할 수 있어야 합니다.
- 파싱 책임은 `RuleParser`에 둡니다.
- 새 규칙이 기존 규칙과 충돌하지 않는지 테스트를 추가합니다.

## 새 문법 추가

룰 텍스트 문법을 확장할 때는 다음 순서를 따릅니다.

1. `RuleParser`에서 `allow|deny + expression` 구조 안에 새 expression을 인식합니다.
2. `IpGuardEngine`이 새 규칙을 ordered evaluation에 포함하는지 확인합니다.
3. `MatchedRule.kind`와 `Decision` metadata가 기대대로 채워지는지 검증합니다.
4. line number가 포함된 parse error가 유지되는지 확인합니다.

권장 원칙:

- 문법은 가능한 단순하게 유지합니다.
- 같은 의미를 여러 표기법으로 허용하지 않습니다.
- 모호한 입력은 거부합니다.
- hostname이나 서비스 전용 식별자를 코어 문법에 섞지 않습니다.

## 새 판정 이유 추가

판정 결과를 더 자세히 설명해야 하면 `DecisionReason`을 확장합니다.

- reason은 사람이 읽기 쉬워야 합니다.
- reason은 구현 세부사항보다 판정 경로를 설명해야 합니다.
- 새 reason 추가 시 `Decision` 생성 경로와 테스트를 함께 갱신합니다.

## 검증

새 확장 추가 후에는 최소한 아래를 확인합니다.

- 모듈 간 컴파일이 깨지지 않는지
- rule ordering semantics가 바뀌지 않는지
- invalid IP가 우회 허용되지 않는지
- 입력/출력 모델이 불필요하게 복잡해지지 않는지
