package com.ipguard.spi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleSourcesTest {

	@TempDir
	Path tempDir;

	@Test
	void from_string_returns_raw_text_as_is() {
		RuleSource source = RuleSources.fromString("allow 127.0.0.1\n");

		assertEquals("allow 127.0.0.1\n", source.loadRaw());
	}

	@Test
	void from_lines_joins_lines_with_newlines() {
		RuleSource source = RuleSources.fromLines(List.of("allow 127.0.0.1", "deny 10.0.0.0/8"));

		assertEquals("allow 127.0.0.1\ndeny 10.0.0.0/8\n", source.loadRaw());
	}

	@Test
	void from_path_reads_utf8_rule_text() throws IOException {
		Path rulesFile = tempDir.resolve("ip-rules.txt");
		Files.writeString(rulesFile, "allow 127.0.0.1\n", StandardCharsets.UTF_8);

		RuleSource source = RuleSources.fromPath(rulesFile);

		assertEquals("allow 127.0.0.1\n", source.loadRaw());
	}

	@Test
	void from_path_wraps_io_failures_with_source_context() {
		RuleSource source = RuleSources.fromPath(tempDir.resolve("missing-rules.txt"));

		RuleSourceLoadException ex = assertThrows(RuleSourceLoadException.class, source::loadRaw);

		assertTrue(ex.sourceDescription().contains("missing-rules.txt"));
		assertTrue(ex.getMessage().contains("Failed to load rules from path"));
	}

	@Test
	void from_resource_reads_classpath_text() {
		RuleSource source = RuleSources.fromResource("/rules/allow-local.txt");

		assertEquals("allow 127.0.0.1\n", source.loadRaw());
	}

	@Test
	void from_resource_reports_missing_resource_name() {
		RuleSource source = RuleSources.fromResource("rules/missing.txt");

		RuleSourceLoadException ex = assertThrows(RuleSourceLoadException.class, source::loadRaw);

		assertEquals("classpath resource 'rules/missing.txt'", ex.sourceDescription());
		assertTrue(ex.getMessage().contains("resource was not found"));
	}
}
