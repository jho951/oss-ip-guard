package com.ipguard.core.ip;

/** IP 주소 종류 목록 */
public enum IpFamily {
	/** `192.168.0.1` 같이 4개의 숫자로 이루어진 방식 (32비트) */
	IPV4,
	/** `2001:0db8:85a3...` 같이 길고 복잡한 방식 (128비트) */
	IPV6,
}
