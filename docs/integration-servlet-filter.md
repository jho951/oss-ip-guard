# Servlet Integration

`ip-guard-core`는 servlet API를 직접 의존하지 않으므로, 상위 웹 계층에서 요청 IP를 추출한 뒤 엔진에 전달하는 형태로 붙입니다.

## 기본 흐름

1. 애플리케이션 시작 시 `IpGuards.fromPath(...)` 또는 `IpGuards.fromResource(...)`로 엔진을 생성합니다.
2. filter에서 실제 클라이언트 IP 문자열을 추출합니다.
3. `engine.decide(clientIp)`로 판정합니다.
4. `decision.allowed()`가 `false`면 `403`을 반환합니다.

## 예시

```java
import com.ipguard.core.engine.IpGuardEngine;
import com.ipguard.core.engine.IpGuards;
import com.ipguard.core.rules.RuleAction;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Path;

public final class IpGuardFilter extends HttpFilter {
	private final IpGuardEngine engine =
		IpGuards.fromPath(Path.of("config/ip-rules.txt"), RuleAction.DENY);

	@Override
	protected void doFilter(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain chain
	) throws IOException, ServletException {
		String clientIp = request.getRemoteAddr();
		var decision = engine.decide(clientIp);

		if (!decision.allowed()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		chain.doFilter(request, response);
	}
}
```

## 주의할 점

- `X-Forwarded-For`, `Forwarded`, trusted proxy 정책은 이 라이브러리 바깥 책임입니다.
- `ip-guard-core`에는 hostname 허용이나 DNS lookup 정책이 없습니다.
- 운영 중 디버깅이 필요하면 `Decision.reason()`과 `matchedRule()`을 같이 기록합니다.
