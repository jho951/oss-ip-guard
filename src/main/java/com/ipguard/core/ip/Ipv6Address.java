package com.ipguard.core.ip;

import java.math.BigInteger;

/** IpAddress 인터페이스를 실제로 구현한 IPv6 전용 클래스 */
public final class Ipv6Address implements IpAddress {
	/** 128비트 크기의 IPv6 주소를 계산 */
	private final BigInteger v128;
	private final String normalized;

	Ipv6Address(BigInteger v128, String normalized) {
		this.v128 = v128;
		this.normalized = normalized;
	}

	@Override
	public IpFamily family() {return IpFamily.IPV6;}

	@Override
	public BigInteger value128() {return v128;}

	@Override
	public String normalized() {return normalized;}

	/** Ipv6는 복잡해서 파싱 단계에서 미리 만든 문자열을 저장해둠 */
	@Override
	public int compareTo(IpAddress other) {
		if (other == null) return 1;
		int f = this.family().compareTo(other.family());
		if (f != 0) return f;
		return this.v128.compareTo(other.value128());
	}
}
