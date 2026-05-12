package com.ipguard.core.decision;

/** Why the engine produced the final decision. */
public enum DecisionReason {
	MATCHED_RULE,
	FALLBACK,
	INVALID_IP
}
