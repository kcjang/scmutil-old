package com.kichang.util.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class PoiExcelWriter implements KWriter {
	Logger logger = Logger.getLogger(PoiExcelWriter.class);
	
	final static int MAX_ROW = 60000;
	OutputStream out;
	SXSSFWorkbook wb = null;
	Sheet lastSheet;

	String[] heads;
	
	@Override
	public void setWorkbook(String filename) throws IOException {
		filename = filename + ".xlsx";
		out = new FileOutputStream(filename);
		
	}
	
	@Override
	public void setWorkbook(OutputStream output) throws IOException {
		out = output;
		
	}


	@Override
	public void flush() {

		try {
			((SXSSFSheet)lastSheet).flushRows(100);
		} catch (IOException e) {
			System.err.println("Excel Flush Error : " + e.getMessage());
		}

		
		int nSheet = wb.getNumberOfSheets();
		
		for (int i = 0; i < nSheet; i++) {
			Sheet sheet = wb.getSheetAt(i);
			
			//shiftRightColumns(sheet, 2, 1);
			
			sheet.setColumnWidth(0, 38 * 256);
			sheet.setColumnWidth(1, 38 * 256);
			sheet.setColumnWidth(2, 38 * 256);
			sheet.setColumnWidth(3, 10 * 256);
			sheet.setColumnWidth(4, 38 * 256);
			
			int top = 0;
			int bottom = sheet.getLastRowNum();
			int left = 0;
			int right = heads.length-1;
			
			CellRangeAddress range = new CellRangeAddress(top, bottom, left, right);
			sheet.setAutoFilter(range);
			sheet.createFreezePane(0, 1, 0, 1);
		}

		try {
			 wb.write(out);
			 out.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null)
				try {
					out.close();
					wb.dispose();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	

	private void shiftRightColumns(Sheet sheet, int column, int moveLen) {
		int firstRow = sheet.getFirstRowNum();
		int lastRow = sheet.getLastRowNum();
		
		for(int i=firstRow; i<lastRow; i++) {
			Row row = sheet.getRow(i);
			
			if (row == null)
				continue;
			
			int lastColumn = row.getLastCellNum();
			for(int j=lastColumn; j<column; j--) {
				Cell cell = row.getCell(j);
				Cell right = row.getCell(j+1);
				right.setCellValue(cell.getStringCellValue());
			}
		}
	}

	

	@Override
	public void createHead(String[] heads) {
		this.heads = heads;
		if (wb == null)
			wb = new SXSSFWorkbook(100);
		
		int numSheet = wb.getNumberOfSheets();
		
		lastSheet = wb.createSheet(String.valueOf(numSheet + 1));
		
		
		CellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(HSSFColor.GREY_80_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		Font font = wb.createFont();
		font.setColor(HSSFColor.WHITE.index);
		style.setFont(font);

		
		Row row = lastSheet.createRow(0);
		for(int cellnum = 0; cellnum < heads.length; cellnum++) {
			Cell cell = row.createCell(cellnum);
			cell.setCellValue(heads[cellnum]);
			cell.setCellStyle(style);
		}
		
	}

	@Override
	public void addRow(String[] datas) {
		int lastRow = lastSheet.getLastRowNum();
		if (lastRow > MAX_ROW) {
			createHead(heads);
			lastRow = 0;
		}
		
		Row row = lastSheet.createRow(lastRow+1);
		for(int cellnum = 0; cellnum < datas.length; cellnum++) {
			Cell cell = row.createCell(cellnum);
			cell.setCellValue(datas[cellnum]);
		}
		
		if (lastRow % 100 == 0) {
			try {
				((SXSSFSheet)lastSheet).flushRows(100);
			} catch (IOException e) {
				System.err.println("Excel Flush Error : " + e.getMessage());
			}
		}
		

	}



}
