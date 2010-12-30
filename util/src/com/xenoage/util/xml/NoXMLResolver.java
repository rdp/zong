package com.xenoage.util.xml;

import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;


/**
 * XML resolver that does just nothing.
 * 
 * TODO: Isn't there a better way to do this?
 *
 * @author Andreas Wenger
 */
public class NoXMLResolver
  implements XMLResolver
{

	@Override public Object resolveEntity(String publicID, String systemID, String baseURI,
		String namespace) throws XMLStreamException
	{
		return null;
	}

}