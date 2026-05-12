# Rule Syntax

`ip-guard-core`는 규칙 원문을 줄 단위로 파싱합니다. 각 줄은 하나의 규칙이어야 하며, 빈 줄은 무시됩니다.

`#` 또는 `//` 뒤의 내용은 주석으로 처리됩니다.

```text
allow 127.0.0.1      # localhost
deny 192.168.0.0/16  // private subnet block
```

## 핵심 규칙

- 각 줄은 반드시 `allow` 또는 `deny`로 시작해야 합니다.
- 첫 번째 매칭 규칙이 최종 판정을 결정합니다.
- 어떤 규칙도 매칭되지 않으면 `fallbackAction`이 적용됩니다.
- 규칙 파싱 실패 시 `RuleParseException`이 line number와 raw line을 함께 제공합니다.

## 지원 문법

| Expression | IPv4 | IPv6 | 예시 |
| --- | --- | --- | --- |
| Single | 지원 | 지원 | `allow 127.0.0.1`, `deny 2001:db8::1` |
| CIDR | 지원 | 지원 | `allow 192.168.0.0/16`, `deny 2001:db8::/32` |
| Range | 지원 | 지원 | `allow 192.168.0.1-192.168.0.255`, `deny 2001:db8::1-2001:db8::f` |
| Wildcard | 지원 | 미지원 | `allow 192.168.*.*` |

상위 플랫폼 모듈이 `ip-guard`를 감싸는 경우에도 이 문법은 축소 없이 전달되어야 합니다.

## Single

단일 IP 주소와 정확히 일치합니다.

```text
allow 127.0.0.1
deny 2001:db8::1
```

## CIDR

주소와 prefix를 `/`로 구분합니다. prefix 범위는 IP 패밀리에 맞아야 합니다.

```text
allow 192.168.0.0/16
deny 2001:db8::/32
```

IPv4는 `0`부터 `32`, IPv6는 `0`부터 `128`까지의 prefix를 사용합니다.

## Range

시작 IP와 끝 IP를 `-`로 구분합니다.

```text
allow 192.168.0.1-192.168.0.255
deny 2001:db8::1-2001:db8::f
```

시작 IP와 끝 IP는 같은 패밀리여야 합니다. IPv4와 IPv6를 섞은 range는 거부됩니다.

## IPv4 Wildcard

IPv4 wildcard는 고정된 앞쪽 octet 뒤에 연속된 `*`를 붙이는 문법입니다. 내부적으로 CIDR 규칙으로 변환되지만 `MatchedRule.kind`는 `IPV4_WILDCARD`로 유지됩니다.

```text
allow 10.*.*.*
deny 192.168.*.*
```

허용되는 wildcard:

```text
allow 10.*.*.*
allow 192.168.*.*
deny 192.168.1.*
```

거부되는 wildcard:

```text
allow *.*.*.*
deny 10.*
allow 10.*.*
deny 192.168.*
allow 10.*.1.*
deny 192.*.1.*
allow 300.1.*.*
```

제약:

- wildcard는 IPv4에서만 지원됩니다.
- wildcard도 IPv4 주소처럼 4개 octet을 모두 작성해야 합니다.
- `*`는 뒤쪽에 연속해서만 올 수 있습니다.
- 모든 octet이 `*`인 규칙은 너무 넓어서 거부됩니다.
- 숫자 octet은 `0`부터 `255` 사이여야 합니다.

## 순서 예시

아래 예시는 같은 입력 IP에 대해 결과가 달라집니다.

```text
deny 192.168.0.0/24
allow 192.168.0.10
```

`192.168.0.10`은 첫 번째 규칙에서 이미 차단됩니다.

```text
allow 192.168.0.10
deny 192.168.0.0/24
```

이 경우 `192.168.0.10`은 첫 번째 규칙에서 허용됩니다.

## 입력 IP 정규화

요청 IP는 `IpParser` 기준으로 정규화됩니다.

```text
[2001:db8::1]:443
fe80::1%en0
127.0.0.1:8080
```

`[IPv6]:port` 형식은 대괄호 안의 IPv6 주소를 사용합니다. IPv6 zone 표기는 `%` 뒤를 제거합니다. IPv4 `host:port` 형식은 host 부분만 사용합니다.

## strict IP literal 정책

- hostname은 허용하지 않습니다.
- `localhost`, `example.com` 같은 입력은 `INVALID_IP`로 거부됩니다.
- 코어 엔진은 DNS lookup을 정책 일부로 취급하지 않습니다.
