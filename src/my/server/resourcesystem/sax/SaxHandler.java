package my.server.resourcesystem.sax;

import my.server.resourcesystem.reflection.ReflectionHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxHandler extends DefaultHandler {

	private static final String CLASSNAME = "class";
	private String element = null; 
	private Object object = null;

    @Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals(CLASSNAME)) {
            String className = attributes.getValue("type");
            object = ReflectionHelper.createInstance(className);
		} else {
            element = qName;
		}	
	}

    @Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		element = null;
	}

    @Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (element != null) {
			String value = new String(ch, start, length);
			ReflectionHelper.setFieldValue(object, element, value);
		}
	}
	
	public Object getObject() {
		return object;
	}
}
