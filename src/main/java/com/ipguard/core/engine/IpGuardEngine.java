package com.ipguard.core.engine;

import com.ipguard.core.decision.Decision;
import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpParser;
import com.ipguard.core.rules.RuleAction;
import com.ipguard.core.rules.RuleParser;
import com.ipguard.core.rules.RuleSet;
import com.ipguard.spi.RuleSource;

import java.util.List;

/** Framework-neutral ordered allow/deny engine for IP access-control decisions. */
public final class IpGuardEngine {
	private final RuleSet rules;
	private final RuleAction fallbackAction;

	public IpGuardEngine(RuleSource ruleSource, RuleAction fallbackAction) {
		String raw = (ruleSource == null) ? "" : ruleSource.loadRaw();
		this.fallbackAction = requireAction(fallbackAction);
		this.rules = new RuleSet(RuleParser.parse(raw));
	}

	public static IpGuardEngine fromRules(List<String> rules, RuleAction fallbackAction) {
		return new IpGuardEngine(() -> joinLines(rules), fallbackAction);
	}

	public Decision decide(String rawIp) {
		final IpAddress ip;
		try {
			ip = IpParser.parse(rawIp);
		} catch (IllegalArgumentException e) {
			return Decision.invalidIp();
		}

		return rules.evaluate(ip, fallbackAction);
	}

	private static RuleAction requireAction(RuleAction action) {
		if (action == null) {
			throw new IllegalArgumentException("fallbackAction is required");
		}
		return action;
	}

	private static String joinLines(List<String> rules) {
		if (rules == null || rules.isEmpty()) {
			return "";
		}

		StringBuilder raw = new StringBuilder();
		for (String rule : rules) {
			if (rule != null) {
				raw.append(rule);
			}
			raw.append('\n');
		}
		return raw.toString();
	}
}
