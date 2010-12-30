package com.xenoage.zong.core.music.direction;

import com.xenoage.util.font.FontInfo;
import com.xenoage.zong.core.music.format.Position;


/**
 * Direction words, that are not interpreted
 * by this program.
 * 
 * TODO: instead of FontInfo and String, use FormattedText?
 *
 * @author Andreas Wenger
 */
public final class Words
  extends Direction
{
	
	private final String text;
	private final FontInfo fontInfo;
	
	
	public Words(String text, FontInfo fontInfo, Position position)
	{
		super(position);
		this.text = text;
		this.fontInfo = fontInfo;
	}
	
	
	public String getText()
	{
		return text;
	}
	
	
	public FontInfo getFontInfo()
	{
		return fontInfo;
	}
	
  
}
