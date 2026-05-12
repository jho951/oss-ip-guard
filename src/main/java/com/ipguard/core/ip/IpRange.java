package com.ipguard.core.ip;

import java.math.BigInteger;

/** IP 주소의 시작과 끝 범위(Range)를 정의하고, 특정 IP가 그 사이에 들어오는지 판단하는 범위 검사기 */
public final class IpRange {
	/** IPv4인지 IPv6인지 종류 판별 */
	private final IpFamily family;
	/** 시작 IP를 숫자로 바꾼 값 (예: 192.168.0.1 -> 3232235521)*/
	private final BigInteger start;
	/** 끝 IP를 숫자로 바꾼 값 (예: 192.168.0.5 -> 3232235525)*/
	private final BigInteger end;

	public IpRange(IpFamily family, BigInteger start, BigInteger end) {
		if (family == null) throw new IllegalArgumentException("family required");
		if (start == null || end == null) throw new IllegalArgumentException("start/end required");
		if (start.compareTo(end) > 0) throw new IllegalArgumentException("start must be <= end");
		this.family = family;
		this.start = start;
		this.end = end;
	}

	public IpFamily family() {return family;}

	public boolean contains(IpAddress ip) {
		if (ip == null || ip.family() != family) return false;
		var v = ip.value128();
		return v.compareTo(start) >= 0 && v.compareTo(end) <= 0;
	}
}