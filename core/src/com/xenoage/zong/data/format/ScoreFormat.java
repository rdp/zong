package com.xenoage.zong.data.format;

import java.awt.Font;
import java.util.ArrayList;


/**
 * Default formats for a score.
 *
 * @author Andreas Wenger
 */
public class ScoreFormat
{
	
	public static Font DEFAULT_FONT = new Font("Times New Roman", Font.PLAIN, 10);
  
  //space between two staff lines ("Rastralgröße") in mm
  private float interlineSpace = 1.6f; //default: Rastralgröße Nr. 4
  
  //default distance between the first line of the top system to the top page margin in mm
  private float defaultTopSystemDistance = 15;
  
  //default system layout information
  private SystemLayout defaultSystemLayout = SystemLayout.createDefaultSystemLayout();
  
  //default staff layout information
  private ArrayList<StaffLayout> defaultStaffLayouts = new ArrayList<StaffLayout>();
  private StaffLayout defaultStaffLayoutOther = StaffLayout.createDefaultStaffLayout();
  
  //default lyric font
  private Font defaultLyricFont = DEFAULT_FONT;
  
  //measure numbering
  private MeasureNumbering measureNumbering = MeasureNumbering.System;
  
  
  /**
   * Creates a default score format.
   */
  public ScoreFormat()
  {
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
  public void setInterlineSpace(float interlineSpace)
  {
    this.interlineSpace = interlineSpace;
  }
  
  
  /**
   * Gets the default distance between the first line of the
   * top system to the top page margin in mm.
   */
  public float getDefaultTopSystemDistance()
  {
    return defaultTopSystemDistance;
  }
  
  
  /**
   * Sets the default distance between the first line of the
   * top system to the top page margin in mm.
   */
  public void setDefaultTopSystemDistance(float defaultTopSystemDistance)
  {
    this.defaultTopSystemDistance = defaultTopSystemDistance;
  }
  
  
  /**
   * Gets the default layout information for systems.
   */
  public SystemLayout getDefaultSystemLayout()
  {
    return defaultSystemLayout;
  }
  
  
  /**
   * Sets the default layout information for systems.
   */
  public void setDefaultSystemLayout(SystemLayout defaultSystemLayout)
  {
    this.defaultSystemLayout = defaultSystemLayout;
  }
  
  
  /**
   * Gets the default layout information for the given staff, or null if undefined
   */
  public StaffLayout getDefaultStaffLayout(int staff)
  {
  	if (staff < defaultStaffLayouts.size())
  	{
  		return defaultStaffLayouts.get(staff);
  	}
  	else
  	{
  		return null;
  	}
  }
  
  
  /**
   * Sets the default layout information for the given staff.
   */
  public void setDefaultStaffLayout(int staff, StaffLayout defaultStaffLayout)
  {
  	while (staff >= defaultStaffLayouts.size())
  	{
  		this.defaultStaffLayouts.add(null);
  	}
    this.defaultStaffLayouts.set(staff, defaultStaffLayout);
  }
  
  
  /**
	 * Gets the default layout information for staves
	 * which have no own default layout.
	 */
	public StaffLayout getDefaultStaffLayoutOther()
	{
	  return defaultStaffLayoutOther;
	}


	/**
	 * Sets the default layout information for staves
	 * which have no own default layout.
	 */
	public void setDefaultStaffLayoutOther(StaffLayout defaultStaffLayoutOther)
	{
	  this.defaultStaffLayoutOther = defaultStaffLayoutOther;
	}
	
	
	/**
   * Gets the default layout information for the given staff, or
   * the default information for all other staves if unknown.
   * Thus, null is never returned.
   */
  public StaffLayout getDefaultStaffLayoutNotNull(int staff)
  {
  	StaffLayout ret = getDefaultStaffLayout(staff);
  	return (ret != null ? ret : getDefaultStaffLayoutOther());
  }


	/**
	 * Gets the default font used for lyrics.
	 * This is always a valid font, and never null.
	 */
	public Font getDefaultLyricFont()
	{
		return defaultLyricFont;
	}


	/**
	 * Sets the default font used for lyrics.
	 */
	public void setDefaultLyricFont(Font defaultLyricFont)
	{
		if (defaultLyricFont == null)
			throw new IllegalArgumentException("Default font may not be null");
		this.defaultLyricFont = defaultLyricFont;
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
	public void setMeasureNumbering(MeasureNumbering measureNumbering)
	{
		this.measureNumbering = measureNumbering;
	}
 
  
}
