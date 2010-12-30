package com.xenoage.zong.core.header;

import com.xenoage.pdlib.PVector;
import com.xenoage.zong.core.format.Break;
import com.xenoage.zong.core.format.StaffLayout;
import com.xenoage.zong.core.format.SystemLayout;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.layout.SystemBreak;
import com.xenoage.zong.util.exceptions.IllegalMPException;


/**
 * This class contains general information
 * about the musical data in a score.
 * 
 * It contains a list of elements that are used in all staves.
 * Such elements are key signatures, time signatures and
 * tempo changes for example.
 * 
 * There is also layout information, like system and
 * page breaks and system and staff distances.
 *
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public final class ScoreHeader
{
  
	private final PVector<SystemLayout> systemLayouts;
  private final PVector<ColumnHeader> columnHeaders;
  
  private static final ScoreHeader empty =
  	new ScoreHeader(new PVector<SystemLayout>(), new PVector<ColumnHeader>());
  
  
  /**
   * Creates a new {@link ScoreHeader}.
   * @param systemLayouts   system information (may contain null)
   * @param columnHeaders   measure column information. may contain null, but
   *                        must have the same size as the number of measures in
   *                        the parent score.
   */
  public ScoreHeader(PVector<SystemLayout> systemLayouts,
  	PVector<ColumnHeader> columnHeaders)
  {
  	this.systemLayouts = systemLayouts;
  	this.columnHeaders = columnHeaders;
  }
  
  
  /**
   * Gets an empty {@link ScoreHeader}.
   */
  public static ScoreHeader empty()
  {
  	return empty;
  }
  
  
  /**
   * Gets layout information for the staff with the given index
   * within the system with the given index, or null if undefined.
   */
  public StaffLayout getStaffLayout(int systemIndex, int staffIndex)
  {
  	SystemLayout systemLayout = getSystemLayout(systemIndex);
  	if (systemLayout != null)
  	{
  		return systemLayout.getStaffLayout(staffIndex);
  	}
  	else
  	{
  		return null;
  	}
  }
  
  
  /**
   * Gets layout information for the system with the given index,
   * or null if undefined.
   */
  public SystemLayout getSystemLayout(int systemIndex)
  {
  	if (systemIndex >= 0 && systemIndex < systemLayouts.size())
  	{
  		return systemLayouts.get(systemIndex);
  	}
  	else
  	{
  		return null;
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
  		Break br = columnHeaders.get(i).getBreak();
  		if (br.getSystemBreak() == SystemBreak.NewSystem)
  			ret++;
  	}
  	return ret;
  }
  
  
  /**
   * Sets the {@link SystemLayout} with the given index.
   */
  public ScoreHeader withSystemLayout(int systemIndex, SystemLayout systemLayout)
  {
  	PVector<SystemLayout> systemLayouts = this.systemLayouts.with(systemIndex, systemLayout);
  	return new ScoreHeader(systemLayouts, columnHeaders);
  }
  
  
  /**
   * Gets the {@link ColumnHeader} at the given index.
   * Never returns null.
   */
  public ColumnHeader getColumnHeader(int measureIndex)
  {
  	if (measureIndex >= columnHeaders.size())
  	{
  		throw new IllegalMPException(MP.atMeasure(measureIndex));
  	}
  	ColumnHeader ret = columnHeaders.get(measureIndex);
  	return (ret != null ? ret : ColumnHeader.empty());
  }
  
  
  /**
   * Gets the {@link ColumnHeader}s.
   */
  public PVector<ColumnHeader> getColumnHeaders()
  {
  	return columnHeaders;
  }
  
  
  /**
   * Sets the {@link SystemLayout} with the given index.
   */
  public ScoreHeader withMeasureColumnHeader(ColumnHeader columnHeader,
  	int measureIndex)
  {
  	PVector<ColumnHeader> columnHeaders =
  		this.columnHeaders.with(measureIndex, columnHeader);
  	return new ScoreHeader(systemLayouts, columnHeaders);
  }
  
  
  /**
   * Adds the given number of empty measure columns.
   */
  public ScoreHeader plusEmptyMeasures(int measuresCount)
  {
    PVector<ColumnHeader> measureColumnHeaders = this.columnHeaders;
    for (int i = 0; i < measuresCount; i++)
    {
    	measureColumnHeaders = measureColumnHeaders.plus(null);
    }
    return new ScoreHeader(systemLayouts, measureColumnHeaders);
  }
  

}
