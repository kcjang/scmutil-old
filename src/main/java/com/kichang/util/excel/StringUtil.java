package com.kichang.util.excel;

import java.util.Stack;



public class StringUtil {
	
	public static String getNamespace(Stack<String> namespace) {
		StringBuffer sb = new StringBuffer();
		for(Object n : namespace.toArray()) {
			sb.append(n).append("::");
		}
		
		return sb.lastIndexOf("::") < 0 ? "" : sb.substring(0, sb.lastIndexOf("::"));
	}
}
