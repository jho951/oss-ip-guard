package com.ipguard.core.ip;

import java.math.BigInteger;

/**
 * IPv4와 IPv6 주소를 하나의 규격으로 다루기 위한 추상화
 * Comparable를 통해 IP 주소끼리 선후 관계를 비교
 */
public interface IpAddress extends Comparable<IpAddress> {
	/** IPv4인지 IPv6인지 종류 판별 */
	IpFamily family();
	/** IP 주소를 128비트 숫자(BigInteger)로 변환한 값 */
	BigInteger value128();
	/** 주소의 형식을 표준형 문자열 변환한 값 */
	String normalized();
}
