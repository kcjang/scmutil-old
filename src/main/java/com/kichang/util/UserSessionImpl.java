
package com.kichang.util;

import java.util.HashMap;
import java.util.Map;


public class UserSessionImpl implements UserSession {
	private Map propMap = new HashMap();
	
	/* (non-Javadoc)
	 * @see com.kichang.util.UserSession#get(java.lang.String)
	 */
	public Object get(String key) {
		return propMap.get(key);
	}

	/* (non-Javadoc)
	 * @see com.kichang.util.UserSession#set(java.lang.Object, java.lang.Object)
	 */
	public void set(final String key, final Object value) {
		propMap.put(key, value);

	}

	public Map getMap() {
		return propMap;
	}

}
