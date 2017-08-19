package com.kichang.util;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;



public class StringUtil {
	public static String replaceChars(String str, String searchstr, String replacestr) {
		String ret = "";
		
		String[] strs = StringUtils.splitByWholeSeparator(str, searchstr);
		
		if (strs.length < 2) {
			ret = str;
		} else if (strs.length == 2) {
			ret = strs[0] + replacestr + strs[1];
		} else if (strs.length > 2) {
			ret = strs[0] + replacestr + strs[1];
			for (int i=2; i< strs.length; i++) {
				ret += replacestr + strs[i];
			}
		}
		
		return ret;
	}

	public static String randomString() {
		UUID uuid = UUID.randomUUID();
		String random = uuid.toString();
		return random;
	}
	
	public static final boolean isEmptyOrWhitespaceOnly(String str) {
		if ((str == null) || (str.length() == 0)) {
			return true;
		}

		int length = str.length();

		for (int i = 0; i < length; ++i) {
			if (!(Character.isWhitespace(str.charAt(i)))) {
				return false;
			}
		}

		return true;
	}
	public static String uni2han (String ustr) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		
		
		char ch = 0;
		
		for (int i= ustr.indexOf("\\u"); i>-1; i=ustr.indexOf("\\u")) {
			ch = (char)Integer.parseInt(ustr.substring(i+2, i+6), 16);
			
			sb.append(ustr.substring(0, i));
			sb.append(String.valueOf(ch));
			ustr = ustr.substring(i+6);
		}
		sb.append(ustr);
		
		return sb.toString();
		
		
	}
	// 유니코드를 한글코드로 변환
	public static String uni2hanOld (String unicodestr) throws UnsupportedEncodingException {
		
		StringBuffer sb = new StringBuffer();
		String[] rawUnicode = unicodestr.split("");
			
		int intVal = 0;
		char[] chVal;
		
		for(int i = 0; i < rawUnicode.length; i++ ) {
			chVal = rawUnicode[i].toCharArray();
			for(int j = 0; j < chVal.length; j++ ) {
				intVal = chVal[j];
				sb.append(chVal[j]);
			}
		}
		return sb.toString();
	}
	
	/*
	 * 비밀번호는 최소한 1개 이상의 숫자, 1개 이상의 특수기호,
	 * 1개이상의 대문자, 1개이상의 소문자 중 3가지를 만족
	 * 입력된 비밀번호와 비교할 문자를 서로 하나하나 대입하여 처리 
	 */
	public static boolean isPasswordCharCheckAceInsu(String password){
		if(password == null || "".equals(password) || password.length() < 8){
			return false;
		}
	
		boolean isNumber = false;
		boolean isSpecialChar = false;
		boolean isLowerChar = false;
		boolean isUpperChar = false;
		
		String validNumber = "0123456789";
		String validSpecialChar = "{}[]|\\~`!@#$^:?'";


		char tempChar;
			 
		for(int i=0;i<password.length();i++){
			tempChar = password.charAt(i);
			
			for(int j=0; j<validNumber.length();j++){
				if(validNumber.charAt(j) == tempChar){
					isNumber = true;
				}
			}
			
			for(int j=0; j<validSpecialChar.length();j++){
				if(validSpecialChar.charAt(j) == tempChar){
					isSpecialChar = true;
				}
			}
			
			if((tempChar >= 'a') && (tempChar <= 'z')){
				isLowerChar = true;
			}
			
			if((tempChar >= 'A') && (tempChar <= 'Z')){
				isUpperChar = true;
			}

		}
		
		int no = 0;
		
		if (isNumber)
			no++;
		if (isSpecialChar)
			no++;
		if (isLowerChar)
			no++;
		if (isUpperChar)
			no++;
		
		return no >= 3;
		
		/*
		if(!(isNumber && isSpecialChar && isLowerChar && isUpperChar)){
			return false;
		}
		*/
		
	}

}
