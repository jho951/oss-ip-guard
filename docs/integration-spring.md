# Spring 통합

이 문서는 `ip-guard-core`를 애플리케이션이 직접 의존하는 경우를 기준으로 설명합니다.

Spring에서도 역할은 같습니다. 요청 IP 추출은 웹 계층에서 처리하고, `ip-guard-core`는 순수 판정만 담당하게 둡니다.

## Bean 구성 예시

```java
import com.ipguard.core.engine.IpGuardEngine;
import com.ipguard.core.engine.IpGuards;
import com.ipguard.core.rules.RuleAction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
class IpGuardConfig {

	@Bean
	IpGuardEngine ipGuardEngine() {
		return IpGuards.fromPath(Path.of("config/ip-rules.txt"), RuleAction.DENY);
	}
}
```

## OncePerRequestFilter 예시

```java
import com.ipguard.core.engine.IpGuardEngine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public final class IpGuardFilter extends OncePerRequestFilter {
	private final IpGuardEngine engine;

	public IpGuardFilter(IpGuardEngine engine) {
		this.engine = engine;
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String clientIp = request.getRemoteAddr();
		var decision = engine.decide(clientIp);

		if (!decision.allowed()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		filterChain.doFilter(request, response);
	}
}
```

## 책임 분리

- Spring Bean은 엔진 생성과 수명주기를 관리합니다.
- Filter는 request에서 최종 클라이언트 IP 문자열을 추출합니다.
- `ip-guard-core`는 그 문자열만 받아 allow/deny와 metadata를 반환합니다.

## 운영 가이드

- reverse proxy 앞단이 있으면 trusted proxy 정책부터 정한 뒤 최종 client IP를 만들어 엔진에 전달합니다.
- 규칙 파일 교체와 reload 전략은 상위 애플리케이션에서 관리합니다.
- 판정 로그에는 `normalizedIp`, `reason`, `matchedRule.lineNumber()` 정도만 남겨도 운영 분석에 충분합니다.
