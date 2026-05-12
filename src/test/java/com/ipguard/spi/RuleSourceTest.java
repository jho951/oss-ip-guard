package com.ipguard.spi;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class RuleSourceTest {

	@Test
	void ruleSource_exposes_single_loadRaw_contract() {
		Method[] declaredMethods = RuleSource.class.getDeclaredMethods();

		assertThat(RuleSource.class.isInterface()).isTrue();
		assertThat(declaredMethods).hasSize(1);
		assertThat(declaredMethods[0].getName()).isEqualTo("loadRaw");
		assertThat(declaredMethods[0].getReturnType()).isEqualTo(String.class);
	}

	@Test
	void ruleSource_implementation_returns_raw_text_as_is() {
		RuleSource source = () -> "127.0.0.1\n2001:db8::1\n";

		assertThat(source.loadRaw()).isEqualTo("127.0.0.1\n2001:db8::1\n");
	}
}
