package com.xenoage.util.xml;

import java.io.ByteArrayInputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;


/**
 * Entity resolver that does just nothing.
 * 
 * TODO: Isn't there a better way to do this?
 *
 * @author Andreas Wenger
 */
public class NoResolver
  implements EntityResolver
{
  
  
  private static InputSource in = new InputSource(new ByteArrayInputStream(
    "<?xml version='1.0' encoding='UTF-8'?>".getBytes()));

  public InputSource resolveEntity(String publicId, String systemId)
  {
    return in;
  }

}