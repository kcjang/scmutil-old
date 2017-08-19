package com.kichang.util.excel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.kichang.util.ConvExcel;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.biff.FontRecord;
import jxl.format.Colour;
import jxl.format.Font;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class KExcelWriter implements KWriter {
	Logger logger = Logger.getLogger(KExcelWriter.class);
	
	final static int MAX_ROW = 60000;

	WritableWorkbook workbook;
	WritableSheet sheet;

	String[] heads;
	
	int index = 0;
	
	public void create(OutputStream os) throws IOException {
		WorkbookSettings ws = new WorkbookSettings();
		ws.setUseTemporaryFileDuringWrite(true);
		workbook = Workbook.createWorkbook(os,ws);
		sheet = workbook.createSheet("취약점_" + index, index);
	}
	
	private WritableSheet getCurrentSheet() {
		int rows = sheet.getRows();
		
		if (rows >= MAX_ROW) {
			createHead(heads);
		}
		return sheet;
	}
	
	public void close() {
		try {
			workbook.write();
			workbook.close();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} catch (WriteException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public void createHead(String[] heads) {
		
		logger.info("Create Head : " + index);
		this.heads = heads;
		if (index > 0) {
			sheet = workbook.createSheet("취약점_" + index, index);
		}
		index++;
		try {
			for(int i=0; i<heads.length; i++) {
				Label label = new Label(i,0,heads[i]);
				WritableCellFormat cf = new WritableCellFormat();
				cf.setBackground(Colour.DARK_RED2);
				Font font = cf.getFont();
				label.setCellFormat(cf);
				sheet.addCell(label);
			}
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		}
	}
	
	public void addRow(String[] datas) {
		int row = getCurrentSheet().getRows();
		for(int i=0; i<datas.length; i++) {
			Label label = new Label(i,row,datas[i]);
			try {
				sheet.addCell(label);
				//System.out.print(label.getContents() + "\t");

			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(),e);
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(),e);
			}
		}
		//System.out.println("");
	}


}
