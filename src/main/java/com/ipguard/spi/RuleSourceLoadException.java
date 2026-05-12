package com.ipguard.spi;

/** Raised when a built-in RuleSource adapter cannot load raw rules from its backing source. */
public final class RuleSourceLoadException extends IllegalStateException {
	private final String sourceDescription;

	public RuleSourceLoadException(String sourceDescription, String detailMessage) {
		super("Failed to load rules from " + sourceDescription + ": " + detailMessage);
		this.sourceDescription = sourceDescription;
	}

	public RuleSourceLoadException(String sourceDescription, Throwable cause) {
		super("Failed to load rules from " + sourceDescription, cause);
		this.sourceDescription = sourceDescription;
	}

	public String sourceDescription() {
		return sourceDescription;
	}
}
