package com.xenoage.zong.io.musicxml.in;

import com.xenoage.util.font.FontInfo;


/**
 * This class contain some default values for MusicXML import.
 * 
 * @author Andreas Wenger
 */
class MxlDefaults
{
	
	private FontInfo wordFontInfo = new FontInfo(12f);
	private FontInfo lyricFontInfo = new FontInfo(10f);
	
	
	public FontInfo getWordFontInfo()
	{
		return wordFontInfo;
	}
	
	
	public void setWordFontInfo(FontInfo wordFontInfo)
	{
		this.wordFontInfo = wordFontInfo;
	}
	
	
	public FontInfo getLyricFontInfo()
	{
		return lyricFontInfo;
	}
	
	
	public void setLyricFontInfo(FontInfo lyricFontInfo)
	{
		this.lyricFontInfo = lyricFontInfo;
	}
	

}
