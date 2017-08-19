package com.kichang.util;

import java.util.Map;

public interface UserSession {

	public final static String USER = "USER";
	public final static String USER_ID = "USER_ID";
	public final static String LEVEL_CODE = "LEVEL_CODE";
	public final static String USER_NAME = "USER_NAME";
	public final static String LOGIN_CLASS = "LOGIN_CLASS";
	public final static String CHIST_CODE = "CHIST_CODE";
	public final static String CLUB_MENU = "CLUB_MENU";
	
	public final static String AUTH = "AUTH";
	/*
	public final static String MENU0 = "MENU0";
	public final static String MENU1 = "MENU1";
	public final static String MENU2 = "MENU2";
	public final static String MENU3 = "MENU3";
	public final static String MENU4 = "MENU4";
	public final static String MENU5 = "MENU5";
	*/
	public final static String TYPE = "TYPE";
	
	public void set(String key, Object value);
	public Object get(String key);
	public Map getMap();
}
