package com.ipguard.core.rules;

import com.ipguard.core.ip.IpFamily;
import com.ipguard.core.ip.IpAddress;

/** IP가 규칙에 맞는지 검사하는 모든 방법"의 공통 규격 */
public interface IpRule {
	IpFamily family();
	RuleKind kind();
	boolean matches(IpAddress ip);
}
