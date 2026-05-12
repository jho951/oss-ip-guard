package com.ipguard.core.rules;

import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpFamily;

public final class SingleIpRule implements IpRule {

	private final IpAddress target;
	private final IpFamily family;

	public SingleIpRule(IpAddress target) {
		this.target = target;
		this.family = target.family();
	}

	@Override
	public IpFamily family() {
		return family;
	}

	@Override
	public RuleKind kind() {
		return RuleKind.SINGLE;
	}

	@Override
	public boolean matches(IpAddress ip) {
		if (ip == null || ip.family() != family) return false;
		return target.value128().equals(ip.value128());
	}
}
