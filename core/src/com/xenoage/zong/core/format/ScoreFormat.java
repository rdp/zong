package com.xenoage.zong.core.format;

import static com.xenoage.util.NullTools.throwNullArg;

import java.awt.Font;

import com.xenoage.pdlib.PVector;


/**
 * Default formats for a score.
 *
 * @author Andreas Wenger
 */
public final class ScoreFormat
{
	
	public static final Font DEFAULT_FONT = new Font("Times New Roman", Font.PLAIN, 10);

  private final float interlineSpace;
  private final float topSystemDistance;
  private final SystemLayout systemLayout;
  private final PVector<StaffLayout> staffLayouts;
  private final StaffLayout staffLayoutOther;
  private final Font lyricFont;
  private final MeasureNumbering measureNumbering;
  
  //default: Rastralgröße Nr. 4
  public static ScoreFormat defaultValue = new ScoreFormat(1.6f, 
  	15, SystemLayout.defaultValue, null, StaffLayout.defaultValue, 
  	DEFAULT_FONT, MeasureNumbering.System);
  
  
  /**
   * Creates a new {@link ScoreFormat}.
   * @param interlineSpace     space between two staff lines ("Rastralgröße") in mm
   * @param topSystemDistance  default distance between the first line of the top system
   *                           to the top page margin in mm
   * @param systemLayout       default system layout information
   * @param staffLayouts       default staff layout information (may also be or contain null)
   * @param staffLayoutOther   staff layout for staves not found in staffLayouts
   * @param lyricFont          default lyric font
   * @param measureNumbering   measure numbering style
   */
  public ScoreFormat(float interlineSpace, float topSystemDistance,
  	SystemLayout systemLayout, PVector<StaffLayout> staffLayouts, StaffLayout staffLayoutOther,
  	Font lyricFont, MeasureNumbering measureNumbering)
  {
  	this.interlineSpace = interlineSpace;
  	this.topSystemDistance = topSystemDistance;
  	this.systemLayout = systemLayout;
  	this.staffLayouts = staffLayouts;
  	this.staffLayoutOther = staffLayoutOther;
  	this.lyricFont = lyricFont;
  	this.measureNumbering = measureNumbering;
  }


  /**
   * Gets the default interline space in mm.
   */
  public float getInterlineSpace()
  {
    return interlineSpace;
  }
  
  
  /**
   * Sets the default interline space in mm.
   */
  public ScoreFormat withInterlineSpace(float interlineSpace)
  {
    return new ScoreFormat(interlineSpace, topSystemDistance, systemLayout,
    	staffLayouts, staffLayoutOther, lyricFont, measureNumbering);
  }
  
  
  /**
   * Gets the default distance between the first line of the
   * top system to the top page margin in mm.
   */
  public float getTopSystemDistance()
  {
    return topSystemDistance;
  }
  
  
  /**
   * Sets the default distance between the first line of the
   * top system to the top page margin in mm.
   */
  public ScoreFormat withTopSystemDistance(float topSystemDistance)
  {
    return new ScoreFormat(interlineSpace, topSystemDistance, systemLayout,
    	staffLayouts, staffLayoutOther, lyricFont, measureNumbering);
  }
  
  
  /**
   * Gets the default layout information for systems.
   */
  public SystemLayout getSystemLayout()
  {
    return systemLayout;
  }
  
  
  /**
   * Sets the default layout information for systems.
   */
  public ScoreFormat withSystemLayout(SystemLayout systemLayout)
  {
    return new ScoreFormat(interlineSpace, topSystemDistance, systemLayout,
    	staffLayouts, staffLayoutOther, lyricFont, measureNumbering);
  }
  
  
  /**
   * Gets the default layout information for the given staff, or null if undefined.
   */
  public StaffLayout getStaffLayout(int staff)
  {
  	if (staffLayouts != null && staff < staffLayouts.size())
  	{
  		return staffLayouts.get(staff);
  	}
  	else
  	{
  		return null;
  	}
  }
  
  
  /**
   * Sets the default layout information for the given staff, or null if undefined.
   */
  public ScoreFormat withStaffLayout(int staff, StaffLayout staffLayout)
  {
  	PVector<StaffLayout> staffLayouts = this.staffLayouts.with(staff, staffLayout);
    return new ScoreFormat(interlineSpace, topSystemDistance, systemLayout,
    	staffLayouts, staffLayoutOther, lyricFont, measureNumbering);
  }
  
  
  /**
	 * Gets the default layout information for staves
	 * which have no own default layout.
	 */
	public StaffLayout getStaffLayoutOther()
	{
	  return staffLayoutOther;
	}
	
	
	/**
   * Sets the default layout information for staves
	 * which have no own default layout.
   */
  public ScoreFormat withStaffLayoutOther(StaffLayout staffLayoutOther)
  {
    return new ScoreFormat(interlineSpace, topSystemDistance, systemLayout,
    	staffLayouts, staffLayoutOther, lyricFont, measureNumbering);
  }
	
	
	/**
   * Gets the default layout information for the given staff, or
   * the default information for all other staves if unknown.
   * Thus, null is never returned.
   */
  public StaffLayout getStaffLayoutNotNull(int staff)
  {
  	StaffLayout ret = getStaffLayout(staff);
  	return (ret != null ? ret : getStaffLayoutOther());
  }


	/**
	 * Gets the default font used for lyrics.
	 * This is always a valid font, and never null.
	 */
	public Font getLyricFont()
	{
		return lyricFont;
	}
	
	
	/**
   * Sets the default font used for lyrics.
	 * Must not be null.
   */
  public ScoreFormat withLyricFont(Font lyricFont)
  {
  	throwNullArg(lyricFont);
    return new ScoreFormat(interlineSpace, topSystemDistance, systemLayout,
    	staffLayouts, staffLayoutOther, lyricFont, measureNumbering);
  }


	/**
	 * Gets the style of measure numbering.
	 */
	public MeasureNumbering getMeasureNumbering()
	{
		return measureNumbering;
	}
	
	
	/**
   * Sets the style of measure numbering.
   */
  public ScoreFormat withMeasureNumbering(MeasureNumbering measureNumbering)
  {
  	throwNullArg(lyricFont);
    return new ScoreFormat(interlineSpace, topSystemDistance, systemLayout,
    	staffLayouts, staffLayoutOther, lyricFont, measureNumbering);
  }
 
  
}
