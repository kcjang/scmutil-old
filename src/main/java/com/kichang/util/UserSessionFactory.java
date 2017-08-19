
package com.kichang.util;

/**
 * @author 
 * @since 2006. 11. 07
 */
public class UserSessionFactory {
	public static UserSession createUserSession() {
		return new UserSessionImpl();
	}
}
