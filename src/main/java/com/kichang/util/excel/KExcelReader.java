package com.kichang.util.excel;

import java.io.File;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class KExcelReader {
	String 	filename 		= null;
	Workbook 	workbook 	= null;
	Sheet 		sheet 			= null;
	
	public KExcelReader(String filename) throws BiffException, IOException {
		this.filename = filename;
		workbook = Workbook.getWorkbook(new File(filename));
		sheet = workbook.getSheet("취약점");
	}
}
