package com.kichang.util.excel;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class KCSVWriter implements KWriter {
	OutputStream os;
	Writer writer; 

	@Override
	public void create(OutputStream os) throws IOException {
		this.os = os;
		writer = new OutputStreamWriter(os);
		
	}

	@Override
	public void close() {
		try {
			writer.close();
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
			writer.write(buf.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
