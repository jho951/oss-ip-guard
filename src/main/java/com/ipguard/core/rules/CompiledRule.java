package com.ipguard.core.rules;

import com.ipguard.core.decision.MatchedRule;
import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpFamily;

/** Internal compiled representation of one ordered rule line. */
final class CompiledRule {
	private final int lineNumber;
	private final String expression;
	private final RuleAction action;
	private final IpRule rule;

	CompiledRule(int lineNumber, String expression, RuleAction action, IpRule rule) {
		this.lineNumber = lineNumber;
		this.expression = expression;
		this.action = action;
		this.rule = rule;
	}

	int lineNumber() {
		return lineNumber;
	}

	RuleAction action() {
		return action;
	}

	IpFamily family() {
		return rule.family();
	}

	boolean matches(IpAddress ip) {
		return rule.matches(ip);
	}

	MatchedRule toMatchedRule() {
		return new MatchedRule(lineNumber, action, rule.kind(), expression);
	}
}
