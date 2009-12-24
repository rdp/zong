package com.xenoage.zong.data.header;

import java.util.ArrayList;

import com.xenoage.zong.data.format.MeasureLayout;
import com.xenoage.zong.data.format.StaffLayout;
import com.xenoage.zong.data.format.SystemLayout;
import com.xenoage.zong.data.music.layout.SystemBreak;


/**
 * This class contains general information
 * about the musical data in a score.
 * 
 * It contains a list of elements
 * that are normally used in all staves.
 * Such elements are key signatures,
 * time signatures and tempo changes for example.
 * 
 * Notice, that these elements are only hints,
 * that can be used for instance when a new staff
 * is created. The actual score contains its
 * own copies of these elements (or not),
 * only they are valid.
 * 
 * There is also layout information, like system and
 * page breaks and system and staff distances.
 *
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class ScoreHeader
{
  
	//layout information for systems
	private ArrayList<SystemLayout> systemLayouts = new ArrayList<SystemLayout>();
	
	//layout information for staves
	private ArrayList<ArrayList<StaffLayout>> staffLayouts = new ArrayList<ArrayList<StaffLayout>>();
  
  //layout information for measures
  private ArrayList<MeasureLayout> measureLayouts = new ArrayList<MeasureLayout>();
  
  //musical information for measures
  private ArrayList<MeasureColumnHeader> measureColumnHeaders = new ArrayList<MeasureColumnHeader>();
  
  
  /**
   * Call this method, whenever measures have been added to the end of a score.
   */
  public void updateMeasuresCount(int totalMeasuresCount)
  {
  	//add layout information for each measure
  	for (int i = measureLayouts.size(); i < totalMeasuresCount; i++)
  	{
  		measureLayouts.add(new MeasureLayout());
  		measureColumnHeaders.add(new MeasureColumnHeader());
  	}
  }
  
  
  /**
   * Gets layout information for the system with the given index,
   * or null if undefined.
   */
  public SystemLayout getSystemLayout(int systemIndex)
  {
  	if (systemIndex >= 0 && systemIndex < systemLayouts.size())
  		return systemLayouts.get(systemIndex);
  	else
  		return null;
  }
  
  
  /**
   * Sets layout information for the system with the given index,
   * or null if undefined.
   */
  public void setSystemLayout(int systemIndex, SystemLayout systemLayout)
  {
  	if (systemIndex >= 0)
  	{
  		while (systemIndex >= systemLayouts.size())
  		{
  			systemLayouts.add(null);
  		}
  		systemLayouts.set(systemIndex, systemLayout);
  	}
  }
  
  
  /**
   * Gets layout information for the staff with the given (global) index
   * within the system with the given index, or null if undefined.
   */
  public StaffLayout getStaffLayout(int systemIndex, int staffIndex)
  {
  	if (systemIndex >= 0 && systemIndex < staffLayouts.size())
  	{
  		ArrayList<StaffLayout> staves = staffLayouts.get(systemIndex);
  		if (staves != null && staffIndex >= 0 && staffIndex < staves.size())
    	{
    		return staves.get(staffIndex);
    	}
  	}
  	return null;
  }
  
  
  /**
   * Sets layout information for the staff with the given (global) index
   * within the system with the given index, or null if undefined.
   */
  public void setStaffLayout(int systemIndex, int staffIndex, StaffLayout staffLayout)
  {
  	if (systemIndex >= 0)
  	{
  		while (systemIndex >= staffLayouts.size())
  		{
  			staffLayouts.add(null);
  		}
  		if (staffLayouts.get(systemIndex) == null)
  		{
  			staffLayouts.set(systemIndex, new ArrayList<StaffLayout>());
  		}
  		ArrayList<StaffLayout> staves = staffLayouts.get(systemIndex);
  		while (staffIndex >= staves.size())
  		{
  			staves.add(null);
  		}
  		staves.set(staffIndex, staffLayout);
  	}
  }
  
  
  /**
   * Gets the system index of the measure with the given index.
   * Only forced system breaks are considered.
   * TODO: performance
   */
  public int getSystemIndex(int measureIndex)
  {
  	int ret = 0;
  	for (int i = 0; i <= measureIndex; i++)
  	{
  		MeasureLayout ml = getMeasureLayout(i);
  		if (ml.systemBreak == SystemBreak.NewSystem)
  			ret++;
  	}
  	return ret;
  }
  
  
  /**
   * Gets layout information for the measure with the given index.
   */
  public MeasureLayout getMeasureLayout(int measureIndex)
  {
  	return measureLayouts.get(measureIndex);
  }
  
  
  /**
   * Gets the MeasureHeader with the given index.
   */
  public MeasureColumnHeader getMeasureColumnHeader(int measureIndex)
  {
  	return measureColumnHeaders.get(measureIndex);
  }
  

}
