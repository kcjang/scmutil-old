package com.kichang.util.excel;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import jxl.Workbook;

public class KCSVWriter implements KWriter {
	String filename;
	OutputStream writer; 

	@Override
	public void setWorkbook(String filename) throws IOException {
		this.filename = filename + ".csv";
		writer = new FileOutputStream(filename);
		
	}
	

	@Override
	public void setWorkbook(OutputStream output) throws IOException {
		writer = output;
	}

	@Override
	public void flush() {
		try {
			writer.flush();
			//writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void createHead(String[] heads) {
		addRow(heads);
	}

	@Override
	public void addRow(String[] datas) {
		StringBuffer buf = new StringBuffer();
		for(int i=0; i<datas.length; i++) {
			if (i > 0)
				buf.append("\t");
			buf.append(datas[i]);
		}
		buf.append("\n");
		try {
			writer.write(buf.toString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
}
