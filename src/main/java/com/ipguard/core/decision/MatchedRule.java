package com.ipguard.core.decision;

import com.ipguard.core.rules.RuleAction;
import com.ipguard.core.rules.RuleKind;

/** Metadata for the ordered rule that produced the final decision. */
public final class MatchedRule {
	private final int lineNumber;
	private final RuleAction action;
	private final RuleKind kind;
	private final String expression;

	public MatchedRule(int lineNumber, RuleAction action, RuleKind kind, String expression) {
		this.lineNumber = lineNumber;
		this.action = action;
		this.kind = kind;
		this.expression = expression;
	}

	public int lineNumber() {
		return lineNumber;
	}

	public RuleAction action() {
		return action;
	}

	public RuleKind kind() {
		return kind;
	}

	public String expression() {
		return expression;
	}
}
