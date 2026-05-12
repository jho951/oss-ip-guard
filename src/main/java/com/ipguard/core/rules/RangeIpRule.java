package com.ipguard.core.rules;

import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpFamily;
import com.ipguard.core.ip.IpRange;

/** 특정 IP 범위(Range)를 기준으로 접속을 허용하거나 차단할지 결정하는 규칙 */
public final class RangeIpRule implements IpRule {
	/** IP 주소의 시작과 끝 범위(Range) */
	private final IpRange range;

	public RangeIpRule(IpRange range) {
		this.range = range;
	}

	@Override
	public IpFamily family() {return range.family();}
	@Override
	public RuleKind kind() {return RuleKind.RANGE;}
	@Override
	public boolean matches(IpAddress ip) {return range.contains(ip);}
}
