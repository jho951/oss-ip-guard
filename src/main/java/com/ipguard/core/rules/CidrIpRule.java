package com.ipguard.core.rules;

import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpFamily;

import java.math.BigInteger;

/** CIDR(Classless Inter-Domain Routing) 표기법으로 특정 IP 대역을 필터링하는 네트워크 범위 검사기 */
public final class CidrIpRule implements IpRule {
	/** IP 주소 종류 목록 */
	private final IpFamily family;
	/** /24나 /64 처럼 앞에서부터 몇 비트까지를 "동네 주소"로 쓸 것인지 결정하는 숫자 */
	private final int prefixLen;
	/** 앞에서 정한 prefixLen 만큼은 1로 채우고, 나머지는 0으로 채운 숫자 */
	private final BigInteger mask;
	/** 기준이 되는 IP와 마스크를 결합해 만든 **"이 동네의 대표 주소" */
	private final BigInteger network;

	/** 128비트 공간에서 왼쪽부터 prefixLen 만큼만 1로 채우는 작업을 수행 */
	public CidrIpRule(IpFamily family, int prefixLen, BigInteger ipValue128) {
		this.family = family;
		this.prefixLen = prefixLen;
		int bits = (family == IpFamily.IPV4) ? 32 : 128;
		int max = bits;

		if (prefixLen < 0 || prefixLen > max) throw new IllegalArgumentException("invalid prefixLen: " + prefixLen);

		this.mask = prefixLen == 0
			? BigInteger.ZERO
			: BigInteger.ONE.shiftLeft(bits).subtract(BigInteger.ONE).xor(
			BigInteger.ONE.shiftLeft(bits - prefixLen).subtract(BigInteger.ONE)
		);

		this.network = ipValue128.and(mask);
	}

	@Override
	public IpFamily family() {
		return family;
	}

	@Override
	public RuleKind kind() {
		return RuleKind.CIDR;
	}

	@Override
	public boolean matches(IpAddress ip) {
		if (ip == null || ip.family() != family) return false;
		return ip.value128().and(mask).equals(network);
	}
}
