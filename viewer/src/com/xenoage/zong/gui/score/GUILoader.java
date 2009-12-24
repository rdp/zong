package com.xenoage.zong.gui.score;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xenoage.util.xml.XMLReader;
import com.xenoage.util.io.IO;


/**
 * Loads a score panel GUI from a given XML file
 * into a given {@link GUIManager}.
 * 
 * @author Andreas Wenger
 */
public class GUILoader
{
	
	/**
	 * Loads the elements from the given XML file into the
	 * given {@link GUIManager}.
	 */
	public void loadFromXML(String filepath)
		throws IOException
	{
		//load XML document
		InputStream in = IO.openDataFile(filepath);
    Document xmlDoc;
    try
    {
      xmlDoc = XMLReader.readFile(in);
    }
    catch (Exception ex)
    {
      throw new IOException(ex.getMessage());
    }
    //root element must be "gui"
    Element root = XMLReader.root(xmlDoc);
    if (!root.getNodeName().equals("gui"))
    {
    	throw new IOException("No \"gui\" root element");
    }
    //read elements
    List<Element> list = XMLReader.elements(root);
    for (Element e : list)
    {
    	//TODO
    	//continue writing the loader when the whole GUI is working.
    }
	}
	
	
	/**
	 * Adds the given elements to the given GUIContainer.
	 */
	

}
