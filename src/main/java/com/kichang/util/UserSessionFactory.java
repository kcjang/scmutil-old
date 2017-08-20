
package com.kichang.util;


public class UserSessionFactory {
	public static UserSession createUserSession() {
		return new UserSessionImpl();
	}
}
