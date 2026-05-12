package com.ipguard.spi;

/** Core가 사용하는 규칙 원문 공급 인터페이스. */
public interface RuleSource {

	String loadRaw();
}
