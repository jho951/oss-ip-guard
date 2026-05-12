package com.ipguard.core.engine;

import com.ipguard.core.decision.DecisionReason;
import com.ipguard.core.rules.RuleAction;
import com.ipguard.core.rules.RuleKind;
import com.ipguard.spi.RuleSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IpGuardEngineTest {

	@Test
	void first_matching_rule_wins() {
		RuleSource src = () -> """
			allow 192.168.0.10
			deny 192.168.0.0/24
			""";
		IpGuardEngine engine = new IpGuardEngine(src, RuleAction.DENY);

		var decision = engine.decide("192.168.0.10");
		var matchedRule = decision.matchedRule().orElseThrow();

		assertTrue(decision.allowed());
		assertEquals(RuleAction.ALLOW, decision.action());
		assertEquals(DecisionReason.MATCHED_RULE, decision.reason());
		assertEquals("192.168.0.10", decision.normalizedIp());
		assertEquals(1, matchedRule.lineNumber());
		assertEquals(RuleAction.ALLOW, matchedRule.action());
		assertEquals(RuleKind.SINGLE, matchedRule.kind());
		assertEquals("192.168.0.10", matchedRule.expression());
	}

	@Test
	void broader_rule_can_block_when_it_comes_first() {
		RuleSource src = () -> """
			deny 192.168.0.0/24
			allow 192.168.0.10
			""";
		IpGuardEngine engine = new IpGuardEngine(src, RuleAction.ALLOW);

		var decision = engine.decide("192.168.0.10");
		var matchedRule = decision.matchedRule().orElseThrow();

		assertFalse(decision.allowed());
		assertEquals(RuleAction.DENY, decision.action());
		assertEquals(DecisionReason.MATCHED_RULE, decision.reason());
		assertEquals(1, matchedRule.lineNumber());
		assertEquals(RuleKind.CIDR, matchedRule.kind());
	}

	@Test
	void fallback_action_is_used_when_no_rule_matches() {
		RuleSource src = () -> "allow 10.0.0.0/8\n";
		IpGuardEngine engine = new IpGuardEngine(src, RuleAction.DENY);

		var decision = engine.decide("192.168.0.10");

		assertFalse(decision.allowed());
		assertEquals(RuleAction.DENY, decision.action());
		assertEquals(DecisionReason.FALLBACK, decision.reason());
		assertEquals("192.168.0.10", decision.normalizedIp());
		assertTrue(decision.matchedRule().isEmpty());
	}

	@Test
	void from_rules_factory_supports_list_input() {
		IpGuardEngine engine = IpGuardEngine.fromRules(
			List.of("deny 10.0.0.0/8", "allow 10.10.10.10"),
			RuleAction.ALLOW
		);

		var decision = engine.decide("203.0.113.10");

		assertTrue(decision.allowed());
		assertEquals(RuleAction.ALLOW, decision.action());
		assertEquals(DecisionReason.FALLBACK, decision.reason());
	}

	@Test
	void wildcard_rule_reports_kind() {
		RuleSource src = () -> "allow 192.168.*.*\n";
		IpGuardEngine engine = new IpGuardEngine(src, RuleAction.DENY);

		var decision = engine.decide("192.168.1.20");
		var matchedRule = decision.matchedRule().orElseThrow();

		assertTrue(decision.allowed());
		assertEquals(RuleKind.IPV4_WILDCARD, matchedRule.kind());
		assertEquals("192.168.*.*", matchedRule.expression());
	}

	@Test
	void bracket_ipv6_with_port_is_supported() {
		RuleSource src = () -> "allow 2001:db8::1\n";
		IpGuardEngine engine = new IpGuardEngine(src, RuleAction.DENY);

		var decision = engine.decide("[2001:db8::1]:443");

		assertTrue(decision.allowed());
		assertEquals("2001:db8:0:0:0:0:0:1", decision.normalizedIp());
	}

	@Test
	void hostname_inputs_are_rejected() {
		RuleSource src = () -> "allow 127.0.0.1\n";
		IpGuardEngine engine = new IpGuardEngine(src, RuleAction.ALLOW);

		var decision = engine.decide("localhost");

		assertFalse(decision.allowed());
		assertEquals(RuleAction.DENY, decision.action());
		assertEquals(DecisionReason.INVALID_IP, decision.reason());
		assertTrue(decision.matchedRule().isEmpty());
	}
}
