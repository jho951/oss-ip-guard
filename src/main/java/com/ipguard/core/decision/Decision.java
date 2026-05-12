package com.ipguard.core.decision;

import com.ipguard.core.rules.RuleAction;

import java.util.Optional;

/** Final policy decision returned by the IP guard engine. */
public final class Decision {
	private final RuleAction action;
	private final DecisionReason reason;
	private final String normalizedIp;
	private final MatchedRule matchedRule;

	private Decision(RuleAction action, DecisionReason reason, String normalizedIp, MatchedRule matchedRule) {
		this.action = action;
		this.reason = reason;
		this.normalizedIp = normalizedIp;
		this.matchedRule = matchedRule;
	}

	public static Decision matched(String normalizedIp, MatchedRule matchedRule) {
		if (matchedRule == null) {
			throw new IllegalArgumentException("matchedRule is required");
		}
		return new Decision(matchedRule.action(), DecisionReason.MATCHED_RULE, normalizedIp, matchedRule);
	}

	public static Decision fallback(String normalizedIp, RuleAction fallbackAction) {
		if (fallbackAction == null) {
			throw new IllegalArgumentException("fallbackAction is required");
		}
		return new Decision(fallbackAction, DecisionReason.FALLBACK, normalizedIp, null);
	}

	public static Decision invalidIp() {
		return new Decision(RuleAction.DENY, DecisionReason.INVALID_IP, null, null);
	}

	public boolean allowed() {
		return action == RuleAction.ALLOW;
	}

	public RuleAction action() {
		return action;
	}

	public DecisionReason reason() {
		return reason;
	}

	public String normalizedIp() {
		return normalizedIp;
	}

	public Optional<MatchedRule> matchedRule() {
		return Optional.ofNullable(matchedRule);
	}
}
