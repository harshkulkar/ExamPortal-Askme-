package com.hcoders.portal.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

	public static void main(String[] args) {
		BCryptPasswordEncoder pc = new BCryptPasswordEncoder();
		System.out.println(pc.encode("123"));
	}
}
