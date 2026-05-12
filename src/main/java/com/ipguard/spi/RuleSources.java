package com.ipguard.spi;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Built-in RuleSource factories for common raw rule loading scenarios. */
public final class RuleSources {
	private RuleSources() {}

	public static RuleSource fromString(String raw) {
		return () -> raw;
	}

	public static RuleSource fromLines(List<String> rules) {
		List<String> copy = copyRules(rules);
		return () -> joinLines(copy);
	}

	public static RuleSource fromPath(Path path) {
		Path target = requirePath(path);
		return () -> readPath(target);
	}

	public static RuleSource fromResource(String resourceName) {
		return fromResource(defaultClassLoader(), resourceName);
	}

	public static RuleSource fromResource(ClassLoader classLoader, String resourceName) {
		ClassLoader loader = requireClassLoader(classLoader);
		String normalized = normalizeResourceName(resourceName);
		return () -> readResource(loader, normalized);
	}

	private static List<String> copyRules(List<String> rules) {
		if (rules == null || rules.isEmpty()) {
			return List.of();
		}

		List<String> copy = new ArrayList<>(rules);
		return Collections.unmodifiableList(copy);
	}

	private static String joinLines(List<String> rules) {
		if (rules.isEmpty()) {
			return "";
		}

		StringBuilder raw = new StringBuilder();
		for (String rule : rules) {
			if (rule != null) {
				raw.append(rule);
			}
			raw.append('\n');
		}
		return raw.toString();
	}

	private static Path requirePath(Path path) {
		if (path == null) {
			throw new IllegalArgumentException("path is required");
		}
		return path;
	}

	private static ClassLoader requireClassLoader(ClassLoader classLoader) {
		if (classLoader == null) {
			throw new IllegalArgumentException("classLoader is required");
		}
		return classLoader;
	}

	private static String normalizeResourceName(String resourceName) {
		if (resourceName == null || resourceName.isBlank()) {
			throw new IllegalArgumentException("resourceName is required");
		}

		String normalized = resourceName.trim();
		while (normalized.startsWith("/")) {
			normalized = normalized.substring(1);
		}

		if (normalized.isEmpty()) {
			throw new IllegalArgumentException("resourceName is required");
		}

		return normalized;
	}

	private static String readPath(Path path) {
		try {
			return Files.readString(path, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuleSourceLoadException("path '" + path + "'", e);
		}
	}

	private static String readResource(ClassLoader classLoader, String resourceName) {
		try (InputStream in = classLoader.getResourceAsStream(resourceName)) {
			if (in == null) {
				throw new RuleSourceLoadException(
					"classpath resource '" + resourceName + "'",
					"resource was not found"
				);
			}
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuleSourceLoadException("classpath resource '" + resourceName + "'", e);
		}
	}

	private static ClassLoader defaultClassLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return (loader != null) ? loader : RuleSources.class.getClassLoader();
	}
}
