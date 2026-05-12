package com.ipguard.core.ip;

import java.math.BigInteger;
import java.net.InetAddress;

/** IP 주소 문자열을 정규화하고, v4 | v6 객체로 변환 */
public final class IpParser {

	private IpParser() {}

	/**
	 * 문자열 앞뒤의 공백이나 따옴표(")를 제거
	 * IPv6의 대괄호([])나 IPv4의 포트 번호(:8080)를 제거
	 * IPv6 주소 뒤에 붙는 네트워크 인터페이스 정보(%en0)를 삭제
	 */
	private static String normalize(String raw) {
		String s = raw.trim();
		if (s.isEmpty()) return s;
		if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
			s = s.substring(1, s.length() - 1).trim();
		}
		if (s.startsWith("[")) {
			int end = s.indexOf(']');
			if (end <= 1) return s;
			String remainder = s.substring(end + 1).trim();
			if (!remainder.isEmpty() && !remainder.matches(":[0-9]+")) {
				return s;
			}
			s = s.substring(1, end).trim();
		}
		if (isIpv4WithPort(s)) {
			int idx = s.lastIndexOf(':');
			s = s.substring(0, idx).trim();
		}
		int zone = s.indexOf('%');
		if (zone > 0) s = s.substring(0, zone);
		return s;
	}

	/** IPv4 주소 뒤에 포트 번호가 붙은 형태(예: 192.168.0.1:8080)"인지 확인 */
	private static boolean isIpv4WithPort(String s) {
		if (s == null) return false;
		int idx = s.lastIndexOf(':');
		if (idx <= 0 || idx == s.length() - 1) return false;
		if (s.indexOf(':') != idx) return false;

		String host = s.substring(0, idx);
		String port = s.substring(idx + 1);
		if (!host.contains(".")) return false;

		for (int i = 0; i < port.length(); i++) {
			if (!Character.isDigit(port.charAt(i))) return false;
		}
		return true;
	}

	private static boolean isIpv4Literal(String s) {
		String[] parts = s.split("\\.", -1);
		if (parts.length != 4) return false;

		for (String part : parts) {
			if (part.isEmpty() || part.length() > 3) return false;
			for (int i = 0; i < part.length(); i++) {
				if (!Character.isDigit(part.charAt(i))) return false;
			}
			int value = Integer.parseInt(part);
			if (value < 0 || value > 255) return false;
		}
		return true;
	}

	private static boolean isLikelyIpv6Literal(String s) {
		if (s == null || s.isBlank()) return false;
		if (s.indexOf(':') < 0) return false;

		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			boolean ok =
				(ch >= '0' && ch <= '9') ||
				(ch >= 'a' && ch <= 'f') ||
				(ch >= 'A' && ch <= 'F') ||
				ch == ':' ||
				ch == '.';
			if (!ok) return false;
		}
		return true;
	}

	/** normalize한 문자열로 실제 객체 생성 */
	public static IpAddress parse(String raw) {
		if (raw == null) throw new IllegalArgumentException("ip is null");
		String s = normalize(raw);
		if (s.isEmpty()) throw new IllegalArgumentException("ip is blank");
		if (!isIpv4Literal(s) && !isLikelyIpv6Literal(s)) {
			throw new IllegalArgumentException("ip literal required: " + raw);
		}

		try {
			InetAddress addr = InetAddress.getByName(s);
			byte[] bytes = addr.getAddress();

			if (bytes.length == 4) {
				BigInteger v = new BigInteger(1, bytes);
				return new Ipv4Address(v);
			}
			if (bytes.length == 16) {
				BigInteger v = new BigInteger(1, bytes);
				return new Ipv6Address(v, addr.getHostAddress());
			}
			throw new IllegalArgumentException("unsupported ip length: " + bytes.length);
		} catch (Exception e) {
			throw new IllegalArgumentException("invalid ip: " + raw, e);
		}
	}

}
