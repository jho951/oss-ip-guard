package com.ipguard.core.rules;

import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpFamily;

/** Dedicated rule type for IPv4 wildcard expressions such as {@code 192.168.*.*}. */
public final class Ipv4WildcardRule implements IpRule {
	private final CidrIpRule delegate;

	public Ipv4WildcardRule(IpAddress baseAddress, int prefixLen) {
		this.delegate = new CidrIpRule(IpFamily.IPV4, prefixLen, baseAddress.value128());
	}

	@Override
	public IpFamily family() {
		return IpFamily.IPV4;
	}

	@Override
	public RuleKind kind() {
		return RuleKind.IPV4_WILDCARD;
	}

	@Override
	public boolean matches(IpAddress ip) {
		return delegate.matches(ip);
	}
}
