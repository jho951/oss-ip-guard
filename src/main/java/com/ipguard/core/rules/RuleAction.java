package com.ipguard.core.rules;

/** Rule outcome applied when a rule matches or when fallback policy is used. */
public enum RuleAction {
	ALLOW,
	DENY;

	public static RuleAction fromKeyword(String keyword) {
		if (keyword == null) {
			throw new IllegalArgumentException("rule action keyword is required");
		}

		return switch (keyword.trim().toLowerCase()) {
			case "allow" -> ALLOW;
			case "deny" -> DENY;
			default -> throw new IllegalArgumentException("unsupported rule action: " + keyword);
		};
	}
}
