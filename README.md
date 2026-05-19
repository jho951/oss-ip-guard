# ip-guard

[![Build](https://github.com/jho951/oss-ip-guard/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/oss-ip-guard/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/ip-guard-core?label=maven%20central)](https://central.sonatype.com/search?q=jho951)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](./LICENSE)
[![Tag](https://img.shields.io/github/v/tag/jho951/oss-ip-guard)](https://github.com/jho951/oss-ip-guard/tags)

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

## 문서

- [문서 인덱스](docs/readme.md)
