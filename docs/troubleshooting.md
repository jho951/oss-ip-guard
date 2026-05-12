# Troubleshooting

`ip-guard`의 1계층 내부에서 자주 만나는 문제와 확인 순서입니다.

## 1. 규칙이 적용되지 않는다

- 규칙 원문이 비어 있거나 전부 주석/공백인지 확인합니다.
- 각 줄이 `allow <expr>` 또는 `deny <expr>` 형태인지 확인합니다.
- 규칙 순서가 의도와 맞는지 확인합니다. 첫 번째 매칭 규칙이 즉시 적용됩니다.
- 주석 뒤의 내용은 파싱되지 않으므로 실제 규칙이 잘려 있지 않은지 확인합니다.

## 2. 파일이나 classpath resource에서 규칙을 읽지 못한다

- `IpGuards.fromPath(...)` 또는 `RuleSources.fromPath(...)`를 쓴다면 파일 경로와 UTF-8 내용을 먼저 확인합니다.
- `IpGuards.fromResource(...)` 또는 `RuleSources.fromResource(...)`를 쓴다면 resource 이름이 classpath 기준인지 확인합니다.
- 이 단계에서 실패하면 `RuleSourceLoadException`이 발생합니다.
- leading `/`가 붙은 resource 이름도 허용되지만, 실제 경로가 존재하는지 다시 확인합니다.

## 3. 특정 IP가 계속 차단된다

- `RuleSource`가 기대한 규칙 원문을 반환하는지 확인합니다.
- 입력 IP가 `IpParser` 기준으로 정상 파싱되는지 확인합니다.
- `Decision.reason()`이 `MATCHED_RULE`인지 `FALLBACK`인지 먼저 구분합니다.
- `matchedRule.lineNumber()`와 `matchedRule.expression()`으로 어떤 규칙이 먼저 먹혔는지 확인합니다.

## 4. `INVALID_IP`가 나온다

- 입력 값이 `null` 또는 blank인지 확인합니다.
- 따옴표, 대괄호, 포트, zone 표기 때문에 주소가 의도와 다르게 들어왔는지 확인합니다.
- 입력이 hostname이 아닌지 확인합니다. `localhost`, `example.com`은 거부됩니다.
- 테스트에서는 `IpParser.parse(...)`를 직접 호출해 실패 원인을 좁힙니다.

## 5. 규칙 파싱이 실패한다

- 규칙 앞에 `allow` 또는 `deny`가 있는지 확인합니다.
- `Single` 형식은 단일 IP만 넣어야 합니다.
- `CIDR` 형식은 주소와 prefix가 함께 있어야 합니다.
- `Range` 형식은 시작 IP와 끝 IP가 같은 패밀리여야 합니다.
- `IPv4 wildcard`는 `192.168.*.*`처럼 연속된 `*`만 허용합니다.
- 전체 문법 예시는 [규칙 문법](./rule-syntax.md)을 확인합니다.
- `RuleParseException`의 `lineNumber()`와 `rawLine()`으로 실패 지점을 바로 확인합니다.

## 6. IPv4 wildcard가 기대와 다르게 동작한다

- wildcard는 IPv4에서만 지원됩니다.
- wildcard는 왼쪽부터 고정된 옥텟 다음에만 `*`가 올 수 있습니다.
- `192.168.*.*`는 허용되지만 `192.*.1.*`는 거부됩니다.
- wildcard는 내부적으로 CIDR로 변환되지만, 판정 metadata에서는 `IPV4_WILDCARD`로 남습니다.

## 7. CIDR 또는 Range가 기대와 다르게 동작한다

- CIDR prefix가 IP 패밀리와 맞는지 확인합니다.
- Range의 시작 IP와 끝 IP가 같은 패밀리인지 확인합니다.
- Range의 시작값이 끝값보다 크지 않은지 확인합니다.
- 입력 IP와 규칙 IP가 같은 IPv4/IPv6 계열인지 확인합니다.

## 8. fallback 결과가 나온다

- 매칭되는 규칙이 하나도 없다는 뜻입니다.
- `fallbackAction`이 `ALLOW`인지 `DENY`인지 먼저 확인합니다.
- 규칙이 비어 있어도, 규칙이 있지만 none-match여도 reason은 모두 `FALLBACK`입니다.

## 9. 판정 결과는 맞는데 metadata가 기대와 다르다

- 허용/거부 자체는 `Decision.action()`으로 확인합니다.
- 판정 경로는 `Decision.reason()`으로 확인합니다.
- 규칙 기반 판정이면 `matchedRule`이 존재하고, fallback 또는 invalid input이면 비어 있습니다.
- `normalizedIp`는 입력 원문이 아니라 엔진이 사용한 정규화 결과입니다.

## 10. 먼저 확인할 파일

- [`src/main/java/com/ipguard/core/engine/IpGuardEngine.java`](../src/main/java/com/ipguard/core/engine/IpGuardEngine.java)
- [`src/main/java/com/ipguard/core/engine/IpGuards.java`](../src/main/java/com/ipguard/core/engine/IpGuards.java)
- [`src/main/java/com/ipguard/core/rules/RuleParser.java`](../src/main/java/com/ipguard/core/rules/RuleParser.java)
- [`src/main/java/com/ipguard/core/ip/IpParser.java`](../src/main/java/com/ipguard/core/ip/IpParser.java)
- [`src/main/java/com/ipguard/core/rules/RuleSet.java`](../src/main/java/com/ipguard/core/rules/RuleSet.java)
- [`src/main/java/com/ipguard/core/decision/Decision.java`](../src/main/java/com/ipguard/core/decision/Decision.java)
- [`src/main/java/com/ipguard/spi/RuleSources.java`](../src/main/java/com/ipguard/spi/RuleSources.java)
