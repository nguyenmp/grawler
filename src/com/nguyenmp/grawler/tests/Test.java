package com.nguyenmp.grawler.tests;

import java.io.IOException;
import com.nguyenmp.grawler.*;

public class Test {
	public static void main(String[] args) throws HttpClientFactory.SSHException, IOException {
		String username = args[0];
		String password = args[1];

		GoldSession session = Grawler.login(username, password);
	}
}
