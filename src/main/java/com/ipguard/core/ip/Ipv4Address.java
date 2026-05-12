package com.ipguard.core.ip;

import java.math.BigInteger;

/** IpAddress 인터페이스를 실제로 구현한 IPv4 전용 클래스 */
public final class Ipv4Address implements IpAddress {
	/** IP 주소를 편하게 계산하기 위해서 (BigInteger) */
	private final BigInteger v128;

	Ipv4Address(BigInteger v128) {
		this.v128 = v128;
	}

	@Override
	public IpFamily family() {return IpFamily.IPV4;}
	@Override
	public BigInteger value128() {return v128;}

	@Override
	public String normalized() {
		long v = v128.longValue();
		return ((v >> 24) & 0xff) + "." + ((v >> 16) & 0xff) + "." + ((v >> 8) & 0xff) + "." + (v & 0xff);
	}
	@Override
	public int compareTo(IpAddress other) {
		if (other == null) return 1;
		int f = this.family().compareTo(other.family());
		if (f != 0) return f;
		return this.v128.compareTo(other.value128());
	}
}
