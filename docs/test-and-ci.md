# 테스트/CI 가이드

## 로컬 테스트 실행

전체 빌드:

```bash
./gradlew clean build
```

전체 테스트:

```bash
./gradlew test
```

## 현재 테스트 범위

- `ip-guard-core`
  - `IpGuardsTest`
    - string/list/path/resource factory entrypoints
  - `IpGuardEngineTest`
    - ordered match semantics
    - fallback action
    - strict hostname rejection
    - IPv4 wildcard metadata
    - IPv6 normalization
  - `RuleParserTest`
    - explicit action requirement
    - line-aware parse errors
    - invalid CIDR and descending range edge cases
  - `RuleSourceTest`
    - single-method raw text contract
  - `RuleSourcesTest`
    - string/list/path/resource adapters
    - source load failure reporting

## GitHub Actions

현재 워크플로우 파일:

- `.github/workflows/build.yml`
- `.github/workflows/publish.yml`

## 기본 반영 흐름

1. `main`에서 작업 브랜치를 만듭니다.
2. 로컬에서 `./gradlew test`로 먼저 확인합니다.
3. 브랜치를 push하고 `main` 대상으로 PR을 엽니다.
4. `build.yml`이 통과한 뒤 merge합니다.
5. release가 필요할 때만 태그를 push해 `publish.yml`을 실행합니다.

### `build.yml`

- 트리거: `main` 대상 PR, `main` push
- 수행: `./gradlew clean test --no-daemon --stacktrace`

### `publish.yml`

- 트리거: `v*` 태그 push
- 수행:
  1. `./gradlew test --no-daemon --stacktrace`
  2. `./gradlew -Prelease_version="$VERSION" publishAggregationToCentralPortal --no-daemon --stacktrace`
  3. Central Portal에 배포

## 참고

CI와 문서는 소스 트리 기준으로 설명합니다. generated build 산출물은 문서 기준이 아닙니다.
`main` 직접 push가 가능한 권한이 있더라도 기본 운영은 PR 경유를 기준으로 맞춥니다.
