package com.kichang.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConvExcel {
	Log logger = LogFactory.getLog(ConvExcel.class);
	public String convCsv(List list,Class clas,String[] titles, String[] vars ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		StringBuffer csv = new StringBuffer();
		List<Method> methods = new ArrayList<Method>();
		List results = new ArrayList();


		for(int i=0; i< vars.length; i++) {
			methods.add(clas.getMethod(makeGetMethod(vars[i]), (Class[]) null));
		}
		
		for (int i=0; i<list.size(); i++) {
			Object[] r = new Object[methods.size()];
			Object obj = list.get(i);
			for (int j=0; j<methods.size(); j++) {
				Object ret = ((Method)methods.get(j)).invoke(obj,(Object[]) null);
				r[j] = ret;
			}
			results.add(r);
		}
		
		for (int i=0; i<titles.length; i++) {
			csv.append(titles[i]).append("	");
		}
		csv.append("\r\n");
		
		for (int i=0; i<results.size(); i++) {
			Object[] r = (Object[])results.get(i);
			for (int j=0; j<r.length; j++) {
				csv.append(r[j]);
				csv.append("	");
			}
			csv.append("\r\n");
		}
		return csv.toString();
	}
	
	private String makeGetMethod(String var) {
		return "get" + var.substring(0,1).toUpperCase() + var.substring(1);
	}
	
	public String saveCsv(String body,String root) {
		String tempFileName = Converter.dateToStringhyphen(new Date()) + "_" +
								FileUtil.getTimeInMillis()+Math.random()+".xls";

		File csv = new File(root+"csv/"+tempFileName);
		try {
			FileOutputStream out = new FileOutputStream(csv);
			out.write(body.getBytes());
			out.close();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}

		return tempFileName;
	}
	
}
