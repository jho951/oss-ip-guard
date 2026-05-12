# ip-guard

[![Build](https://github.com/jho951/ip-guard/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/ip-guard/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/ip-guard-core?label=maven%20central)](https://central.sonatype.com/search?q=jho951)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](./LICENSE)
[![Tag](https://img.shields.io/github/v/tag/jho951/ip-guard)](https://github.com/jho951/ip-guard/tags)

## 제공 모듈

- `ip-guard-core`: strict IP 파싱, ordered rule 파싱/매칭, 결정 엔진
- `ip-guard-core` 안에 `RuleSource`, `RuleSources`, `IpGuards`, `RuleSourceLoadException` 같은 최소 계약 타입과 진입 API 포함
- 지원 문법: IPv4/IPv6 single, CIDR, range, IPv4 wildcard
- 판정 모델: explicit `allow`/`deny`, first-match semantics, fallback action

## 빠른 시작

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.jho951:ip-guard-core:<version>")
}
```

```java
import com.ipguard.core.engine.IpGuards;
import com.ipguard.core.rules.RuleAction;

var engine = IpGuards.fromString("""
    deny 10.0.0.13
    allow 10.0.0.0/8
    """, RuleAction.DENY);

var decision = engine.decide("10.0.0.42");
boolean allowed = decision.allowed();
```

규칙은 한 줄에 하나씩 작성합니다. 각 줄은 반드시 `allow` 또는 `deny`로 시작해야 하며, 빈 줄은 무시되고, `#` 또는 `//` 뒤의 내용은 주석으로 처리됩니다.

```text
deny 10.0.0.13
allow 10.0.0.0/8
allow 192.168.*.*
deny 2001:db8::/32
```

### `txt` 파일에서 시작

```java
import com.ipguard.core.engine.IpGuards;
import com.ipguard.core.rules.RuleAction;

import java.nio.file.Path;

var engine = IpGuards.fromPath(Path.of("config/ip-rules.txt"), RuleAction.DENY);
```

### classpath resource에서 시작

```java
import com.ipguard.core.engine.IpGuards;
import com.ipguard.core.rules.RuleAction;

var engine = IpGuards.fromResource("rules/ip-rules.txt", RuleAction.DENY);
```

### `fallbackAction`이 결과를 바꾸는 경우

```java
import com.ipguard.core.engine.IpGuards;
import com.ipguard.core.rules.RuleAction;

var engine = IpGuards.fromString("allow 10.0.0.0/8\n", RuleAction.DENY);

engine.decide("10.1.2.3").allowed();     // true
engine.decide("203.0.113.10").allowed(); // false, no rule matched so fallback DENY
```

## 문서

- [docs/readme.md](docs/readme.md)
- [아키텍처](docs/architecture.md)
- [모듈 가이드](docs/modules.md)
- [규칙 문법](docs/rule-syntax.md)
- [확장 가이드](docs/extension-guide.md)
- [Servlet 통합 예제](docs/integration-servlet-filter.md)
- [Spring 통합 예제](docs/integration-spring.md)
- [테스트/CI 가이드](docs/test-and-ci.md)
- [트러블슈팅](docs/troubleshooting.md)
- [기여 가이드](CONTRIBUTING.md)
