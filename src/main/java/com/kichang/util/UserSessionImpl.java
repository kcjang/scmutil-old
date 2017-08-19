/**
 * ======================================================================
 *  �� ����Ʈ������ ���α׷����۱� �� ��Ÿ ���������� 
 *  (��)������ũ�� �ֽ��ϴ�.
 *  ���� �� ����Ʈ������ �ҹ��̿� �ÿ��� ���� ��ǻ�����α׷���ȣ��
 *  ��Ÿ �������ǹ��ɿ� ��õ� �Ρ������ å���� ������ �� �ֽ��ϴ�.
 *  ������ �� ����Ʈ��� ����� ���� ��Ÿ ������ ���� �Ͽ�
 *  ���������������� ��Ÿ ��ü�� ������� ����ϴ� ��� ���ù��ɿ�
 *  ���� �Ρ������ å���� ���� �˴ϴ�.
 * ======================================================================
 */
package com.kichang.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ���â
 * @since 2006. 11. 07
 */
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
