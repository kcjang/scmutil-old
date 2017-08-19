package com.kichang.util.excel;

import com.kichang.util.FileUtil;

public class ExcelUtil {
	private final static String[] EXTS = {"xls","xlsx"};
	public static boolean isExcelFile(String filename) {
		boolean ret = false;
		String ext = FileUtil.getExt(filename);
		for (String exc : EXTS) {
			if (exc.equalsIgnoreCase(ext))
				ret = true;
		}
		return ret;
	}
	
	
	
}
