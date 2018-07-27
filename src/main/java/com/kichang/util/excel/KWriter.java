package com.kichang.util.excel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.Font;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public interface KWriter {
	public void setWorkbook(String filename) throws IOException;
	
	public void setWorkbook(OutputStream output) throws IOException;
	
	public void flush();
	
	public void createHead(String[] heads);
	
	public void addRow(String[] datas);
	
}
