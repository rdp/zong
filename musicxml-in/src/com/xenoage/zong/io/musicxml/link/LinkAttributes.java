package com.xenoage.zong.io.musicxml.link;


/**
 * The <code>%link-attributes</code> entity from MusicXML.
 * Currently, only the href attribute is supported.
 * 
 * @author Andreas Wenger
 */
public class LinkAttributes
{
	
	private final String href;
	
	
	public LinkAttributes(String href)
	{
		this.href = href;
	}
	
	
	public String getHref()
	{
		return href;
	}

}
