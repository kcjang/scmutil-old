package com.kichang.util.xml;

import java.util.Stack;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.kichang.util.excel.StringUtil;

public class HierarchyXMLEventReader implements XMLEventReader {
	private XMLEventReader reader;
	
	private Stack<String> namespace = new Stack<String>();
	String currentName = null;

	public HierarchyXMLEventReader(XMLEventReader reader) {
		this.reader = reader;
	}

	@Override
	public Object next() {
		return reader.next();
	}

	@Override
	public void remove() {
		reader.remove();
		
	}

	@Override
	public XMLEvent nextEvent() throws XMLStreamException {
		XMLEvent event = reader.nextEvent();
		
		if (event.isStartElement()) {
			StartElement startElement = event.asStartElement();
			String localPart = startElement.getName().getLocalPart();
			namespace.push(localPart);
			currentName = StringUtil.getNamespace(namespace);
		} else if (event.isEndElement()) {
			EndElement endElement = event.asEndElement();
			String localPart = endElement.getName().getLocalPart();
			currentName = StringUtil.getNamespace(namespace);
			namespace.pop();
		}
		return event;
	}
	
	public String getCurrentName() {
		return currentName;
	}

	@Override
	public boolean hasNext() {
		return reader.hasNext();
	}

	@Override
	public XMLEvent peek() throws XMLStreamException {
		return reader.peek();
	}

	@Override
	public String getElementText() throws XMLStreamException {
		return reader.getElementText();
	}

	@Override
	public XMLEvent nextTag() throws XMLStreamException {
		return reader.nextTag();
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return reader.getProperty(name);
	}

	@Override
	public void close() throws XMLStreamException {
		reader.close();
		
	}
	
	
	
}
