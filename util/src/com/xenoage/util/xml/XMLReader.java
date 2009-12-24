package com.xenoage.util.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.*;
import org.xml.sax.SAXException;


/**
 * This class contains some helper functions
 * to parse XML files and read values
 * out of XML documents with JAXP.
 *
 * @author Andreas Wenger
 */
public class XMLReader
{

  
  /**
   * Reads and returns the XML document at the given path.
   */
  public static Document readFile(String file)
    throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    builder.setEntityResolver(new NoResolver());
    return builder.parse(file);
  }
  
  
  /**
   * Reads and returns the XML document at the given input stream.
   */
  public static Document readFile(InputStream stream)
    throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    builder.setEntityResolver(new NoResolver());
    return builder.parse(stream);
  }
  
  
  /**
   * Returns a XMLStreamReader for the XML document at the given path.
   */
  public static XMLStreamReader createXMLStreamReader(String file)
  	throws IOException, XMLStreamException
	{
  	return createXMLStreamReader(new FileInputStream(file)); 
	}
  
  
  /**
   * Returns a XMLStreamReader for the XML document at the given input stream.
   */
  public static XMLStreamReader createXMLStreamReader(InputStream stream)
  	throws IOException, XMLStreamException
	{ 
  	XMLInputFactory factory = XMLInputFactory.newInstance(); 
  	//disable DTD resolver
  	factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
  	XMLStreamReader reader = factory.createXMLStreamReader(stream);
  	return reader;
	}
  
  
  /**
   * Gets the root element of the given document.
   */
  public static Element root(Document doc)
  {
    return doc.getDocumentElement();
  }
  
  
  /**
   * Gets the text of the given element, or "".
   */
  public static String text(Element element)
  {
    if (element == null)
      return "";
    else
      return element.getTextContent();
  }
  
  
  /**
   * Gets the trimmed text of the given element, or "".
   */
  public static String textTrim(Element element)
  {
    if (element == null)
      return "";
    else
      return element.getTextContent().trim();
  }
  
  
  /**
   * Reads and returns the value of the attribute with the
   * given name of the given element, or null if not found.
   */
  public static String attribute(Element element, String name)
  {
  	/* OBSOLETE
    if (element == null) return null;
    NamedNodeMap attributes = element.getAttributes();
    if (attributes == null) return null;
    Node value = attributes.getNamedItem(name);
    if (value == null) return null;
    return value.getTextContent(); */
  	String ret = attributeNotNull(element, name);
  	return (ret.length() > 0 ? ret : null);
  }
  
  
  /**
   * Reads and returns the value of the attribute with the
   * given name of the given element, or "" if not found.
   */
  public static String attributeNotNull(Element element, String name)
  {
  	/* OBSOLETE
    String ret = attribute(element, name);
    if (ret == null)
      ret = "";
    return ret; */
  	return element.getAttribute(name);
  }
  
  
  /**
   * Gets the first child element of the given node with
   * the given name, or null if not found.
   */
  public static Element element(Node parent, String name)
  {
  	for(Node node = parent.getFirstChild(); node != null; node = node.getNextSibling())
    {
      if (node.getNodeName().equals(name))
        return (Element) node;
    }
    return null;
  }
  
  
  /**
   * Gets the a list of all child elements of the given node with
   * the given name. If no such elements are found, the returned
   * list is empty. 
   */
  public static LinkedList<Element> elements(Node parent, String name)
  {
    LinkedList<Element> ret = new LinkedList<Element>();
    for(Node node = parent.getFirstChild(); node != null; node = node.getNextSibling())
    {
      if (node.getNodeName().equals(name))
        ret.add((Element) node);
    }
    return ret;
  }
  
  
  /**
   * Gets the a list of all child elements of the given node.
   * If no such elements are found, the returned list is empty. 
   */
  public static List<Element> elements(Node parent)
  {
  	LinkedList<Element> ret = new LinkedList<Element>();
  	for(Node node = parent.getFirstChild(); node != null; node = node.getNextSibling())
    {
      if (node.getNodeType() == Node.ELEMENT_NODE)
        ret.add((Element) node);
    }
    return ret;
  }
  
  
  /**
   * Gets the text of the given child element with the given name
   * of the given parent element, or null if it does not exist.
   */
  public static String elementText(Element parentElement, String elementName)
  {
    Element e = element(parentElement, elementName);
    if (e == null)
      return null;
    else
      return text(e);
  }
  
  
  /**
   * Gets the text of the given child element with the given name
   * of the given parent element, or "", even if it does not exist.
   */
  public static String elementTextNotNull(Element parentElement, String elementName)
  {
    return text(element(parentElement, elementName));
  }


}