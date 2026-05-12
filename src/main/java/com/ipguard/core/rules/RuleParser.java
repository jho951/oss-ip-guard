package com.ipguard.core.rules;

import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpParser;
import com.ipguard.core.ip.IpRange;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RuleParser {
	private static final Pattern DIRECTIVE = Pattern.compile("(?i)^(allow|deny)\\s+(.+)$");

	private RuleParser() {}

	public static List<CompiledRule> parse(String raw) {
		if (raw == null || raw.isBlank()) return List.of();

		List<CompiledRule> out = new ArrayList<>();
		String[] lines = raw.split("\\R");

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			String stripped = stripComment(line).trim();
			if (stripped.isEmpty()) continue;
			out.add(parseOne(i + 1, line, stripped));
		}
		return List.copyOf(out);
	}

	private static String stripComment(String line) {
		int hash = line.indexOf('#');
		int sl = line.indexOf("//");
		int cut = -1;
		if (hash >= 0) cut = hash;
		if (sl >= 0) cut = (cut < 0) ? sl : Math.min(cut, sl);
		return cut < 0 ? line : line.substring(0, cut);
	}

	private static CompiledRule parseOne(int lineNumber, String rawLine, String stripped) {
		Matcher matcher = DIRECTIVE.matcher(stripped);
		if (!matcher.matches()) {
			throw new RuleParseException(
				lineNumber,
				rawLine,
				"rule action is required. Use 'allow <expr>' or 'deny <expr>'."
			);
		}

		RuleAction action = RuleAction.fromKeyword(matcher.group(1));
		String expression = matcher.group(2).trim();
		if (expression.isEmpty()) {
			throw new RuleParseException(lineNumber, rawLine, "rule expression is empty");
		}

		return new CompiledRule(lineNumber, expression, action, parseExpression(lineNumber, rawLine, expression));
	}

	private static IpRule parseExpression(int lineNumber, String rawLine, String expression) {
		if (looksLikeIpv4Wildcard(expression)) {
			String cidr = ipv4WildcardToCidr(lineNumber, rawLine, expression);
			int slash = cidr.indexOf('/');
			IpAddress ip = parseIp(lineNumber, rawLine, cidr.substring(0, slash).trim());
			int prefix = Integer.parseInt(cidr.substring(slash + 1).trim());
			return new Ipv4WildcardRule(ip, prefix);
		}

		int slash = expression.indexOf('/');
		if (slash > 0) {
			try {
				IpAddress ip = parseIp(lineNumber, rawLine, expression.substring(0, slash).trim());
				int prefix = Integer.parseInt(expression.substring(slash + 1).trim());
				return new CidrIpRule(ip.family(), prefix, ip.value128());
			} catch (NumberFormatException e) {
				throw new RuleParseException(lineNumber, rawLine, "CIDR prefix must be a valid integer", e);
			} catch (IllegalArgumentException e) {
				throw new RuleParseException(lineNumber, rawLine, e.getMessage(), e);
			}
		}

		int dash = expression.indexOf('-');
		if (dash > 0) {
			IpAddress start = parseIp(lineNumber, rawLine, expression.substring(0, dash).trim());
			IpAddress end = parseIp(lineNumber, rawLine, expression.substring(dash + 1).trim());

			if (start.family() != end.family()) {
				throw new RuleParseException(lineNumber, rawLine, "range family mismatch: " + expression);
			}

			try {
				return new RangeIpRule(new IpRange(start.family(), start.value128(), end.value128()));
			} catch (IllegalArgumentException e) {
				throw new RuleParseException(lineNumber, rawLine, e.getMessage(), e);
			}
		}

		return new SingleIpRule(parseIp(lineNumber, rawLine, expression));
	}

	private static boolean looksLikeIpv4Wildcard(String s) {
		if (s == null) return false;
		String t = s.trim();
		return t.matches("(?:(?:\\d{1,3}|\\*)\\.){3}(?:\\d{1,3}|\\*)")
			&& t.indexOf('*') >= 0;
	}

	private static IpAddress parseIp(int lineNumber, String rawLine, String rawIp) {
		try {
			return IpParser.parse(rawIp);
		} catch (IllegalArgumentException e) {
			throw new RuleParseException(lineNumber, rawLine, e.getMessage(), e);
		}
	}

	private static String ipv4WildcardToCidr(int lineNumber, String rawLine, String s) {
		String[] parts = s.split("\\.");
		if (parts.length != 4) throw new RuleParseException(lineNumber, rawLine, "invalid wildcard: " + s);

		int fixed = 0;
		for (String p : parts) {
			if ("*".equals(p)) break;

			int v;
			try {
				v = Integer.parseInt(p);
			} catch (Exception e) {
				throw new RuleParseException(lineNumber, rawLine, "invalid wildcard: " + s, e);
			}
			if (v < 0 || v > 255) {
				throw new RuleParseException(lineNumber, rawLine, "invalid wildcard: " + s);
			}

			fixed++;
		}

		for (int i = fixed; i < 4; i++) {
			if (!"*".equals(parts[i])) {
				throw new RuleParseException(lineNumber, rawLine, "wildcard must be contiguous: " + s);
			}
		}

		if (fixed == 0) throw new RuleParseException(lineNumber, rawLine, "too broad wildcard: " + s);

		int prefix = fixed * 8;

		StringBuilder ip = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			if (i > 0) ip.append('.');
			if (i < fixed) ip.append(parts[i]);
			else ip.append('0');
		}
		return ip + "/" + prefix;
	}
}
