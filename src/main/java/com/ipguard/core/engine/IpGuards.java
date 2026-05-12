package com.ipguard.core.engine;

import com.ipguard.core.rules.RuleAction;
import com.ipguard.spi.RuleSource;
import com.ipguard.spi.RuleSources;

import java.nio.file.Path;
import java.util.List;

/** Convenience factory entrypoints for building IpGuardEngine instances from common sources. */
public final class IpGuards {
	private IpGuards() {}

	public static IpGuardEngine fromSource(RuleSource ruleSource, RuleAction fallbackAction) {
		return new IpGuardEngine(ruleSource, fallbackAction);
	}

	public static IpGuardEngine fromString(String raw, RuleAction fallbackAction) {
		return fromSource(RuleSources.fromString(raw), fallbackAction);
	}

	public static IpGuardEngine fromLines(List<String> rules, RuleAction fallbackAction) {
		return fromSource(RuleSources.fromLines(rules), fallbackAction);
	}

	public static IpGuardEngine fromPath(Path path, RuleAction fallbackAction) {
		return fromSource(RuleSources.fromPath(path), fallbackAction);
	}

	public static IpGuardEngine fromResource(String resourceName, RuleAction fallbackAction) {
		return fromSource(RuleSources.fromResource(resourceName), fallbackAction);
	}

	public static IpGuardEngine fromResource(ClassLoader classLoader, String resourceName, RuleAction fallbackAction) {
		return fromSource(RuleSources.fromResource(classLoader, resourceName), fallbackAction);
	}
}
