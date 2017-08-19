package com.kichang.util.xml;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class XMLFilterEntityImpl extends XMLFilterImpl implements LexicalHandler {

	private String currentEntity = null;
	
	public XMLFilterEntityImpl(XMLReader reader)
	    throws SAXNotRecognizedException, SAXNotSupportedException {
		super(reader);
		setProperty("http://xml.org/sax/properties/lexical-handler", this);
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
	    throws SAXException {
		if (currentEntity == null) {
		    super.characters(ch, start, length);
		    return;
		}
		
		String entity = "&" + currentEntity + ";";
		super.characters(entity.toCharArray(), 0, entity.length());
		currentEntity = null;
	}
	
	@Override
	public void startEntity(String name) throws SAXException {
		currentEntity = name;
	}
	
	@Override
	public void endEntity(String name) throws SAXException {
	}
	
	@Override
	public void startDTD(String name, String publicId, String systemId)
	    throws SAXException {
	}
	
	@Override
	public void endDTD() throws SAXException {
	}
	
	@Override
	public void startCDATA() throws SAXException {
	}
	
	@Override
	public void endCDATA() throws SAXException {
	}
	
	@Override
	public void comment(char[] ch, int start, int length) throws SAXException {
	}
}