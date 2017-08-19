package com.kichang.util.fortify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.StringUtils;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.kichang.util.excel.StringUtil;


public class TransKisa {

	Workbook 	workbook 	= null;
	WritableWorkbook	writableWorkbook = null;
	
	
	int bigcol 	= 1;		// second column
	int smallcol 	= 2;			// third column
	int outcol 	= 20;
	
	public TransKisa() {}
	
	public TransKisa(int bigcol, int smallcol, int outcol) {
		this.bigcol 	= bigcol;
		this.smallcol = smallcol;
		this.outcol 	= outcol;
	}
	
	public void init(InputStream in, OutputStream out) throws BiffException, IOException {
		WorkbookSettings s = new WorkbookSettings();  
		s.setUseTemporaryFileDuringWrite(true);  
		
		System.out.println("Read Workbook ...............");
		workbook 		= Workbook.getWorkbook(in);
		System.out.println("Copy Workbook ...............");
		writableWorkbook = Workbook.createWorkbook(out, workbook,s);
	}
	
	public String trans(Map<String, String> mapping) throws FileNotFoundException {

		
		Sheet[] sheets = workbook.getSheets();
		System.out.println("Start Translating ...............");
		try {
			for(int i=0; i<sheets.length; i++) {
				Sheet sheet = sheets[i];
				System.out.println("Processing ........ sheet - " + sheet.getName());
				WritableSheet writableSheet = writableWorkbook.getSheet(i);
				int rows = sheet.getRows();
				for (int row = 0; row<rows; row++) {
					String big = sheet.getCell(bigcol, row).getContents();
					String small = sheet.getCell(smallcol, row).getContents();
					String category = big;

					if (!StringUtils.isBlank(small)) {
						category = big + ": " + small;
					}

					String kisa = mapping.get(category);

					if (kisa != null && !kisa.trim().isEmpty()) {
						WritableCell cell;
						Label l = new Label(outcol, row, kisa);
						cell = (WritableCell) l;
						writableSheet.addCell(cell);
					}
				}
			}
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			try {
				System.out.println("Writing Excel File ...............");
				writableWorkbook.write();
				System.out.println("Close Excel File ...............");
				writableWorkbook.close();
				workbook.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		return "";
	}
	
	public static String usage() {
		StringBuffer sb = new StringBuffer();
		sb.append("Usage : java -jar TransCate.jar [excel source] [excel output] [B no] [S no] [K no]").append("\n");
		sb.append("     B no : Big Category Column no.").append("\n");
		sb.append("     S no : Small Category Column no.").append("\n");
		sb.append("     K no : KISA Category Column no.").append("\n");
		
		return sb.toString();
	}

	public static void main(String[] args) throws BiffException, IOException {
		if (args.length < 5) {
			System.out.println(usage());
			return;
		}
		
		int bno = Integer.parseInt(args[2]);
		int sno = Integer.parseInt(args[3]);
		int kno = Integer.parseInt(args[4]);
		
		TransKisa trans = new TransKisa(bno, sno, kno);
		System.out.println("Initializing ...............");
		trans.init( new FileInputStream(args[0]),new FileOutputStream(args[1]));
		System.out.println("Read Mapping File ...............");
		Map<String, String> mapping = trans.readMapping(trans.getClass().getResourceAsStream("/KISA_metadata.xml"));
		System.out.println("Mapping Size : " + mapping.size());
		trans.trans(mapping);
	}

	public Map<String, String> readMapping(File metaxml) {
		InputStream is = null;
		Map<String, String> ret = null;
		try {
			is = new FileInputStream(metaxml);
			ret = readMapping(is);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return ret;
	}
	public static Map<String, String> readMapping(InputStream inputStream) throws FileNotFoundException {
		Map<String,String> mapping = new HashMap<String, String>();
		XMLInputFactory f = XMLInputFactory.newInstance();
		XMLEventReader eventReader = null;
		try {
			eventReader = f.createXMLEventReader(inputStream);
			Stack<String> namespace = new Stack<String>();
			
			String internalCategory = null, externalCategory = null;

			while(eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					String name = startElement.getName().getLocalPart();
					namespace.push(name);
				}
				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();
					String name = endElement.getName().getLocalPart();
					namespace.pop();
				}
				
				if (event.isStartElement()) {
					if (event.asStartElement().getName().getLocalPart().equals("Mapping") 
							&& StringUtil.getNamespace(namespace).equals("ExternalMetadataPack::ExternalList::Mapping")) {
						
						internalCategory = null;
						externalCategory = null;
						StartElement startElement = event.asStartElement();
					}
				}
				
				if (event.isEndElement()) {
					if (event.asEndElement().getName().getLocalPart().equals("Mapping") 
							&& StringUtil.getNamespace(namespace).equals("ExternalMetadataPack::ExternalList")
							&& internalCategory != null && externalCategory != null) {
						mapping.put(internalCategory, externalCategory);
					}
						
				}
				
				if (event.isStartElement()) {
					if (event.asStartElement().getName().getLocalPart().equals("InternalCategory") 
							&& StringUtil.getNamespace(namespace).equals("ExternalMetadataPack::ExternalList::Mapping::InternalCategory")) {
						event = eventReader.nextEvent();
						internalCategory = event.asCharacters().getData();
					}
				}
				
				if (event.isStartElement()) {
					if (event.asStartElement().getName().getLocalPart().equals("ExternalCategory") 
							&& StringUtil.getNamespace(namespace).equals("ExternalMetadataPack::ExternalList::Mapping::ExternalCategory")) {
						event = eventReader.nextEvent();
						externalCategory = event.asCharacters().getData();
					}
				}
			
			} // end while
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} finally {
			try {
				if (eventReader != null)
					eventReader.close();
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
		return mapping;
	}

}
