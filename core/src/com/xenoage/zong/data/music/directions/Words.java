package com.xenoage.zong.data.music.directions;

import com.xenoage.util.font.FontInfo;
import com.xenoage.zong.data.music.Direction;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.format.Position;
import com.xenoage.zong.data.music.util.DeepCopyCache;


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
	
	
	@Override public Direction deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
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
