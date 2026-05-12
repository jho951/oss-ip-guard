package com.ipguard.core.rules;

/** Raised when a raw rule line cannot be parsed into a supported rule. */
public final class RuleParseException extends IllegalArgumentException {
	private final int lineNumber;
	private final String rawLine;

	public RuleParseException(int lineNumber, String rawLine, String message) {
		super("Invalid rule at line " + lineNumber + ": " + message);
		this.lineNumber = lineNumber;
		this.rawLine = rawLine;
	}

	public RuleParseException(int lineNumber, String rawLine, String message, Throwable cause) {
		super("Invalid rule at line " + lineNumber + ": " + message, cause);
		this.lineNumber = lineNumber;
		this.rawLine = rawLine;
	}

	public int lineNumber() {
		return lineNumber;
	}

	public String rawLine() {
		return rawLine;
	}
}
