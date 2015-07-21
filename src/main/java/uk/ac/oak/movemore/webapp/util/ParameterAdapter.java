package uk.ac.oak.movemore.webapp.util;

import javax.xml.bind.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;
import javax.xml.parsers.*;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ParameterAdapter extends XmlAdapter<org.w3c.dom.Element, Parameter> {
	 private ClassLoader classLoader;
	    private DocumentBuilder documentBuilder;
	    private JAXBContext jaxbContext;
	 
	    public ParameterAdapter() {
	        classLoader = Thread.currentThread().getContextClassLoader();
	    }
	 
	    public ParameterAdapter(JAXBContext jaxbContext) {
	        this();
	        this.jaxbContext = jaxbContext;
	    }
	 
	    private DocumentBuilder getDocumentBuilder() throws Exception {
	        // Lazy load the DocumentBuilder as it is not used for unmarshalling.
	        if (null == documentBuilder) {
	            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            documentBuilder = dbf.newDocumentBuilder();
	        }
	        return documentBuilder;
	    }
	 
	    private JAXBContext getJAXBContext(Class<?> type) throws Exception {
	        if (null == jaxbContext) {
	            // A JAXBContext was not set, so create a new one based  on the type.
	            return JAXBContext.newInstance(type);
	        }
	        return jaxbContext;
	    }
	 
	    @Override
	    public Element marshal(Parameter parameter) throws Exception {
	        if (null == parameter) {
	            return null;
	        }
	 
	        // 1. Build the JAXBElement to wrap the instance of Parameter.
	        QName rootElement = new QName(parameter.getName());
	        Object value = parameter.getValue();
	        Class<?> type = value.getClass();
	        JAXBElement jaxbElement = new JAXBElement(rootElement, type, value);
	 
	        // 2.  Marshal the JAXBElement to a DOM element.
	        Document document = getDocumentBuilder().newDocument();
	        Marshaller marshaller = getJAXBContext(type).createMarshaller();
	        marshaller.marshal(jaxbElement, document);
	        Element element = document.getDocumentElement();
	 
	        // 3.  Set the type attribute based on the value's type.
	        element.setAttribute("type", type.getName());
	        return element;
	    }
	 
	    @Override
	    public Parameter unmarshal(Element element) throws Exception {
	        if (null == element) {
	            return null;
	        }
	 
	        // 1. Determine the values type from the type attribute.
	        Class<?> type = classLoader.loadClass(element.getAttribute("type"));
	 
	        // 2. Unmarshal the element based on the value's type.
	        DOMSource source = new DOMSource(element);
	        Unmarshaller unmarshaller = getJAXBContext(type).createUnmarshaller();
	        JAXBElement jaxbElement = unmarshaller.unmarshal(source, type);
	 
	        // 3. Build the instance of Parameter
	        Parameter parameter = new Parameter();
	        parameter.setName(element.getLocalName());
	        parameter.setValue(jaxbElement.getValue());
	        return parameter;
	    }
	 

}
