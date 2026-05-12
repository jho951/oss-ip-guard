package com.ipguard.core.rules;

/** Supported rule expression forms. */
public enum RuleKind {
	SINGLE,
	CIDR,
	RANGE,
	IPV4_WILDCARD
}
