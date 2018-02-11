package com.kichang.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public abstract class PrintUtil {
	static Log logger = LogFactory.getLog(PrintUtil.class);
	
	public static void printList(List list) {
		printSet(list);
	}
	
	public static void printListDetail(List list) {
		printSet2(list,true);
	}
	
	public static void printSet(Collection set) {
		printSet2(set, false);
	}
	private static void printSet2(Collection set, boolean isDetail) {
		int i=1;
		Iterator itr = set.iterator();
		//System.out.println("--------------------------------------------");
		while(itr.hasNext()) {
			Object obj = itr.next();
			String desc = null;
			try {
				desc = isDetail ? getDesc(obj) : obj.toString();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(),e);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(),e);
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(),e);
			}
				
			//System.out.println("(" + (i++) + ") : " +desc + ")");
		}
		//System.out.println("--------------------------------------------");
	}
	
	/*
	
	public static void printPageable(Pageable pageable) {
		StringBuffer buf = new StringBuffer();
		buf.append("( [s]" + pageable.getStart() + ",[M]" + pageable.getPageCount());
		buf.append(",[T]" + pageable.getTotalSize() + " )");
		//System.out.println("--------------------------------------------");
		//System.out.println(buf.toString());
		printSet(pageable.getList());
		
	}
	*/
	
	public static String getDesc(Object obj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		StringBuffer buf = new StringBuffer();
		Class clas = obj.getClass();
		Method[] methods = clas.getMethods();
		buf.append("[" + clas.getName() + ",");
		for (int i=0; i<methods.length; i++) {
			Method method = methods[i];
			if (method.getName().substring(0, 3).equals("get") 
					&& method.getModifiers() == Modifier.PUBLIC
					&& method.getParameterTypes().length == 0) {
				buf.append(method.getName()+"("+method.invoke(obj, new ArrayList().toArray()).toString()+")");
			}
			if (i<methods.length-1)
				buf.append(",");
		}
		buf.append("]");
		
		
		return buf.toString();
	}
	
	
	
	public static Map getProperties(Object object) {
		Map map = new HashMap();
		StringBuffer buffer = new StringBuffer();
		
		Class clas = object.getClass();
		Method[] methods = clas.getMethods();
		
		for (int i=0; i<methods.length; i++) {
			Method method = methods[i];
			if (
					method.getName().substring(0, 3).equals("get") 
					&& method.getModifiers() == Modifier.PUBLIC
					&& method.getParameterTypes().length == 0
				) 
			{
				char[] names = method.getName().substring(3).toCharArray();
				names[0] = Character.toLowerCase(names[0]);
				try {
					Object value = method.invoke(object);
					if (value instanceof Collection)
						continue;
					map.put(new String(names), method.invoke(object));
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}
			
			}
		}
		return map;
	}
	
}
