package com.xenoage.zong.io.musicxml.in;

import com.xenoage.util.math.Size2f;
import com.xenoage.zong.core.format.LayoutFormat;
import com.xenoage.zong.core.format.PageFormat;
import com.xenoage.zong.core.format.PageMargins;
import com.xenoage.zong.core.format.ScoreFormat;
import com.xenoage.zong.core.format.LayoutFormat.Side;
import com.xenoage.util.exceptions.InvalidFormatException;

import java.awt.Font;
import java.math.BigDecimal;
import java.util.List;

import proxymusic.Defaults;
import proxymusic.EmptyFont;
import proxymusic.LyricFont;
import proxymusic.MarginType;
import proxymusic.PageLayout;
import proxymusic.Scaling;
import proxymusic.ScorePartwise;
import proxymusic.SystemLayout;


/**
 * This class reads the default format of a score
 * from a MusicXML 2.0 document.
 *
 * @author Andreas Wenger
 */
class MxlScoreFormat
{
  
	private float tenthMm = 0.16f;
  private LayoutFormat pageLayoutFormat;
  private ScoreFormat scoreFormat;
  private MxlDefaults defaults;
  
  
  /**
   * Reads the default format of a score
   * from the given MusicXML document.
   */
  public MxlScoreFormat(ScorePartwise doc)
    throws InvalidFormatException
  {
    pageLayoutFormat = new LayoutFormat();
    scoreFormat = ScoreFormat.getDefault();
    defaults = new MxlDefaults();
    
    //defaults
    Defaults mxlDefaults = doc.getDefaults();
    if (mxlDefaults != null)
    {
      readScaling(mxlDefaults);
      tenthMm = scoreFormat.getInterlineSpace() / 10;
      
      //page layout
      readPageLayout(mxlDefaults, tenthMm);
      
      //system layout
      SystemLayout mxlSystemLayout = mxlDefaults.getSystemLayout();
      if (mxlSystemLayout != null)
      {
      	MxlSystemLayout sl = new MxlSystemLayout(mxlSystemLayout, tenthMm);
      	scoreFormat = scoreFormat.withSystemLayout(sl.getSystemLayout()); 
      	Float topSystemDistance = sl.getTopSystemDistance();
      	if (topSystemDistance != null)
      	{
      		scoreFormat = scoreFormat.withTopSystemDistance(topSystemDistance.floatValue());
      	}
      }
      
      //staff layouts
      List<proxymusic.StaffLayout> mxlStaffLayoutList = mxlDefaults.getStaffLayout();
      for (proxymusic.StaffLayout mxlStaffLayout : mxlStaffLayoutList)
      {
      	MxlStaffLayout sl = new MxlStaffLayout(mxlStaffLayout, tenthMm);
      	if (sl.getNumber() == null)
      	{
      		scoreFormat = scoreFormat.withStaffLayoutOther(sl.getStaffLayout());
      	}
      	else
      	{
      		scoreFormat = scoreFormat.withStaffLayout(sl.getNumber() - 1, sl.getStaffLayout());
      	}
      }
      
      //read default word font
      EmptyFont mxlEF = mxlDefaults.getWordFont();
      if (mxlEF != null)
      {
	      MxlFontInfo fi = new MxlFontInfo(mxlEF);
	      defaults.setWordFontInfo(fi.getFontInfo());
      }
      
      //read default lyrics font
      //(TODO: support more than one font)
      List<LyricFont> mxlLFList = mxlDefaults.getLyricFont();
      for (LyricFont xmlLF : mxlLFList)
      {
      	MxlFontInfo fi = new MxlFontInfo(xmlLF);
      	defaults.setLyricFontInfo(fi.getFontInfo());
      	break; //at the moment only one lyric font is supported
      }
      
      //apply lyrics font
      Font defaultLyricFont = defaults.getLyricFontInfo().createFont(
      	scoreFormat.getLyricFont());
      scoreFormat.withLyricFont(defaultLyricFont);
    } 
  }


  /**
   * Reads the scaling block.
   */
  private void readScaling(Defaults mxlDefaults)
    throws InvalidFormatException
  {
    //scaling
    Scaling mxlScaling = mxlDefaults.getScaling();
    if (mxlScaling != null)
    {
      float millimeters = mxlScaling.getMillimeters().floatValue();
      float tenths = mxlScaling.getTenths().floatValue();
      if (tenths <= 0)
        throw new InvalidFormatException("Element \"tenths\" must be greater than 0");
      //compute default interline space
      scoreFormat = scoreFormat.withInterlineSpace(millimeters * 10 / tenths);
    }
  }
  
  
  /**
   * Reads the page-layout-block.
   */
  private void readPageLayout(Defaults mxlDefaults, float tenthsMm)
  	throws InvalidFormatException
  {
    PageLayout mxlPageLayout = mxlDefaults.getPageLayout();
    if (mxlPageLayout != null)
    {
      Size2f size = new Size2f(100, 100);
      PageMargins pageMarginsLeft = new PageMargins();
      PageMargins pageMarginsRight = new PageMargins();
      
      //page-width and page-height
      BigDecimal mxlPageWidth = mxlPageLayout.getPageWidth();
      BigDecimal mxlPageHeight = mxlPageLayout.getPageHeight();
      if (mxlPageWidth != null && mxlPageHeight != null)
      {
        size = new Size2f(tenthsMm * mxlPageWidth.floatValue(), tenthsMm * mxlPageHeight.floatValue());
      }

      //page-margins
      List<proxymusic.PageMargins> mxlPageMarginsList = mxlPageLayout.getPageMargins();
      for (proxymusic.PageMargins mxlPageMargins : mxlPageMarginsList)
      {
        PageMargins pageMargins = new PageMargins(
        	tenthsMm * mxlPageMargins.getLeftMargin().floatValue(),
        	tenthsMm * mxlPageMargins.getRightMargin().floatValue(),
        	tenthsMm * mxlPageMargins.getTopMargin().floatValue(),
        	tenthsMm * mxlPageMargins.getBottomMargin().floatValue());
        //left, right page or both? default: both
        MarginType mxlMarginType = mxlPageMargins.getType();
        if (mxlMarginType == null || mxlMarginType == MarginType.BOTH)
        {
        	pageMarginsLeft = pageMargins;
        	pageMarginsRight = pageMargins;
        }
        else if (mxlMarginType == MarginType.ODD)
        {
          pageMarginsRight = pageMargins;
        }
        else if (mxlMarginType == MarginType.EVEN)
        {
          pageMarginsLeft = pageMargins;
        }
      }
      
      //apply format
      pageLayoutFormat.setPageFormat(Side.Left, new PageFormat(size, pageMarginsLeft));
      pageLayoutFormat.setPageFormat(Side.Right, new PageFormat(size, pageMarginsRight));
      
    }
  }
  
  
  /**
   * Gets the value of a tenth in mm.
   */
  public float getTenthMm()
  {
  	return tenthMm;
  }
  
  
  /**
   * Gets the score format.
   */
  public ScoreFormat getScoreFormat()
  {
    return scoreFormat;
  }
  
  
  /**
   * Gets the page layout format.
   */
  public LayoutFormat getPageFormat()
  {
    return pageLayoutFormat;
  }
  
  
  /**
   * Gets the other default values, like fonts.
   */
  public MxlDefaults getDefaults()
  {
  	return defaults;
  }

}
