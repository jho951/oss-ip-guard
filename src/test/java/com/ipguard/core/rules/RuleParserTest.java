package com.ipguard.core.rules;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RuleParserTest {

	@Test
	void explicit_action_is_required() {
		RuleParseException ex = assertThrows(
			RuleParseException.class,
			() -> RuleParser.parse("127.0.0.1\n")
		);

		assertEquals(1, ex.lineNumber());
		assertEquals("127.0.0.1", ex.rawLine());
	}

	@Test
	void invalid_rule_keeps_source_line_information() {
		RuleParseException ex = assertThrows(
			RuleParseException.class,
			() -> RuleParser.parse("allow 10.*.1.*\n")
		);

		assertEquals(1, ex.lineNumber());
		assertEquals("allow 10.*.1.*", ex.rawLine());
	}

	@Test
	void invalid_cidr_prefix_keeps_source_line_information() {
		RuleParseException ex = assertThrows(
			RuleParseException.class,
			() -> RuleParser.parse("allow 10.0.0.0/nope\n")
		);

		assertEquals(1, ex.lineNumber());
		assertEquals("allow 10.0.0.0/nope", ex.rawLine());
	}

	@Test
	void descending_range_keeps_source_line_information() {
		RuleParseException ex = assertThrows(
			RuleParseException.class,
			() -> RuleParser.parse("allow 192.168.0.10-192.168.0.1\n")
		);

		assertEquals(1, ex.lineNumber());
		assertEquals("allow 192.168.0.10-192.168.0.1", ex.rawLine());
	}
}
