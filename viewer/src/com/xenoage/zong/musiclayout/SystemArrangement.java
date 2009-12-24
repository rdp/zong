package com.xenoage.zong.musiclayout;


import com.xenoage.zong.musiclayout.spacing.MeasureColumnSpacing;
import com.xenoage.zong.util.ArrayTools;


/**
 * A system arrangement is the horizontal and
 * vertical spacing of a system.
 * 
 * It contains the indices of the first and last
 * measure of the system, the widths of all
 * measure columns, the width of the system
 * (which may be longer than the width used
 * by the measures), the distances between the
 * staves and the vertical offset of the system.
 *
 * @author Andreas Wenger
 */
public final class SystemArrangement
{

	//index of the first and last measure
	private final int startMeasureIndex;
	private final int endMeasureIndex;
	
	//list of measure column spacings
	//this will often contain references to the measure column spacings
	//that were computed before, but it can also store new measure column spacings
	//that were created because for example a leading spacing was added
	private final MeasureColumnSpacing[] measureColumnSpacings;
	
	//left and right margin of the system in mm
	private final float marginLeft;
	private final float marginRight;

	//width of the system in mm (may be longer than the summed up widths
	//of the measure columns, e.g. to create empty staves)
	private final float systemWidth;

	//heights and distances of the staves in mm
	private final float[] staffHeights; //0..#staves
	private final float[] staffDistances; //0..(#staves-1)
	private final float totalHeight;
	
	//vertical offset of the system in mm
	private final float offsetY;


	/**
	 * Creates a SystemArrangement.
	 * @param startMeasureIndex      index of the first measure in this system, or -1 if none
	 * @param endMeasureIndex        index of the last measure in this system, or -1 if none
	 * @param measureColumnSpacings  list of the layouts of the measure columns in this system
	 * @param marginLeft             left margin of the system in mm
	 * @param marginRight            right margin of the system in mm
	 * @param systemWidth            width of the system in mm (may be longer than the used width) without left margin
	 * @param staffHeights           the heights of each staff in mm
	 * @param staffDistances         the distances of the staves in mm (one less then the number of staves)
	 * @param offsetY                vertical offset of the system in mm
	 */
	public SystemArrangement(int startMeasureIndex, int endMeasureIndex,
		MeasureColumnSpacing[] measureColumnSpacings, float marginLeft, float marginRight, float systemWidth,
		float[] staffHeights, float[] staffDistances, float offsetY)
	{
		if (staffHeights.length != staffDistances.length + 1)
		{
			throw new IllegalArgumentException("There must be one more staff height that staff distance");
		}
		this.startMeasureIndex = startMeasureIndex;
		this.endMeasureIndex = endMeasureIndex;
		this.measureColumnSpacings = measureColumnSpacings;
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.systemWidth = systemWidth;
		this.staffHeights = staffHeights;
		this.staffDistances = staffDistances;
		this.totalHeight = ArrayTools.sum(staffHeights) + ArrayTools.sum(staffDistances);
		this.offsetY = offsetY;
	}


	/**
	 * Gets the total height of this system in mm.
	 */
	public float getHeight()
	{
		return totalHeight;
	}


	/**
	 * Gets the index of the first measure of the system.
	 */
	public int getStartMeasureIndex()
	{
		return startMeasureIndex;
	}


	/**
	 * Gets the index of the last measure of the system.
	 */
	public int getEndMeasureIndex()
	{
		return endMeasureIndex;
	}


	/**
	 * Gets the height of the staff with the given index.
	 */
	public float getStaffHeight(int index)
	{
		return staffHeights[index];
	}
	
	
	/**
	 * Gets the distance between the previous and the given staff.
	 */
	public float getStaffDistance(int index)
	{
		return (index > 0 ? staffDistances[index - 1] : 0);
	}
	
	
	/**
	 * Gets the left margin of the system in mm.
	 */
	public float getMarginLeft()
	{
		return marginLeft;
	}
	
	
	/**
	 * Gets the right margin of the system in mm.
	 */
	public float getMarginRight()
	{
		return marginRight;
	}
	
	
	/**
	 * Gets the width of the system (without the horizontal offset).
	 * It may not be equal to the used width. To get the used
	 * width, call getUsedWidth. 
	 */
	public float getWidth()
	{
		return systemWidth;
	}
	
	
	/**
	 * Gets the used width of the system.
	 */
	public float getUsedWidth()
	{
		float ret = 0;
		for (int i = 0; i < measureColumnSpacings.length; i++)
		{
			ret += measureColumnSpacings[i].getWidth();
		}
		return ret;
	}


	/**
	 * Gets the list of the layouts of the measure columns
	 * in this system.
	 * 
	 * TIDY: not return array, but element by given index
	 */
	public MeasureColumnSpacing[] getMeasureColumnSpacings()
	{
		return measureColumnSpacings;
	}
	
	
	/**
   * Gets the vertical offset of the system in mm,
   * relative to the top 
   */
  public float getOffsetY()
  {
    return offsetY;
  }
  
  
  /**
   * Sets the width of the system in mm and returns
   * the changed system arrangement.
   */
  public SystemArrangement changeSystemWidth(float systemWidth)
  {
    return new SystemArrangement(startMeasureIndex, endMeasureIndex, measureColumnSpacings,
    	marginLeft, marginRight, systemWidth, staffHeights, staffDistances, offsetY);
  }
  
  
  /**
   * Sets the vertical offset of the system in mm and returns
   * the changed system arrangement.
   */
  public SystemArrangement changeOffsetY(float offsetY)
  {
    return new SystemArrangement(startMeasureIndex, endMeasureIndex, measureColumnSpacings,
    	marginLeft, marginRight, systemWidth, staffHeights, staffDistances, offsetY);
  }
  
  
  /**
   * Sets the measure column spacings and width of the system in mm and returns
   * the changed system arrangement.
   */
  public SystemArrangement changeSpacings(MeasureColumnSpacing[] measureColumnSpacings, float systemWidth)
  {
    return new SystemArrangement(startMeasureIndex, endMeasureIndex, measureColumnSpacings,
    	marginLeft, marginRight, systemWidth, staffHeights, staffDistances, offsetY);
  }
  

}
