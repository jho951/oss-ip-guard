package com.ipguard.core.rules;

import com.ipguard.core.decision.Decision;
import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpFamily;

import java.util.ArrayList;
import java.util.List;

public final class RuleSet {
	private final List<CompiledRule> v4;
	private final List<CompiledRule> v6;

	public RuleSet(List<CompiledRule> rules) {
		List<CompiledRule> a = new ArrayList<>();
		List<CompiledRule> b = new ArrayList<>();

		if (rules != null) {
			for (CompiledRule r : rules) {
				if (r == null) continue;
				if (r.family() == IpFamily.IPV4) {
					a.add(r);
				} else {
					b.add(r);
				}
			}
		}

		this.v4 = List.copyOf(a);
		this.v6 = List.copyOf(b);
	}

	public boolean isEmpty() {
		return v4.isEmpty() && v6.isEmpty();
	}

	public Decision evaluate(IpAddress ip, RuleAction fallbackAction) {
		if (ip == null) {
			throw new IllegalArgumentException("ip is required");
		}

		List<CompiledRule> list =
			(ip.family() == IpFamily.IPV4) ? v4 : v6;

		for (CompiledRule rule : list) {
			if (rule.matches(ip)) {
				return Decision.matched(ip.normalized(), rule.toMatchedRule());
			}
		}

		return Decision.fallback(ip.normalized(), fallbackAction);
	}
}
