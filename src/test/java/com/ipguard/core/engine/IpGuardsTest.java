package com.ipguard.core.engine;

import com.ipguard.core.rules.RuleAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IpGuardsTest {

	@TempDir
	Path tempDir;

	@Test
	void from_string_creates_engine_for_inline_rules() {
		IpGuardEngine engine = IpGuards.fromString("""
			deny 10.0.0.13
			allow 10.0.0.0/8
			""", RuleAction.DENY);

		assertTrue(engine.decide("10.0.0.42").allowed());
		assertFalse(engine.decide("10.0.0.13").allowed());
	}

	@Test
	void from_lines_creates_engine_for_rule_lists() {
		IpGuardEngine engine = IpGuards.fromLines(
			List.of("allow 127.0.0.1", "deny 10.0.0.0/8"),
			RuleAction.DENY
		);

		assertTrue(engine.decide("127.0.0.1").allowed());
		assertFalse(engine.decide("10.1.2.3").allowed());
	}

	@Test
	void from_path_creates_engine_for_rule_files() throws IOException {
		Path rulesFile = tempDir.resolve("ip-rules.txt");
		Files.writeString(rulesFile, "allow 203.0.113.10\n", StandardCharsets.UTF_8);

		IpGuardEngine engine = IpGuards.fromPath(rulesFile, RuleAction.DENY);

		assertTrue(engine.decide("203.0.113.10").allowed());
		assertFalse(engine.decide("203.0.113.11").allowed());
	}

	@Test
	void from_resource_creates_engine_for_classpath_rules() {
		IpGuardEngine engine = IpGuards.fromResource("rules/allow-local.txt", RuleAction.DENY);

		assertTrue(engine.decide("127.0.0.1").allowed());
		assertFalse(engine.decide("127.0.0.2").allowed());
	}
}
