package com.xenoage.zong.musiclayout.layouter.arrangement;

import java.util.ArrayList;
import java.util.LinkedList;


import com.xenoage.util.lang.Tuple3;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.format.MeasureLayout;
import com.xenoage.zong.data.format.ScoreFormat;
import com.xenoage.zong.data.format.StaffLayout;
import com.xenoage.zong.data.format.SystemLayout;
import com.xenoage.zong.data.header.ScoreHeader;
import com.xenoage.zong.data.music.Staff;
import com.xenoage.zong.data.music.layout.SystemBreak;
import com.xenoage.zong.musiclayout.SystemArrangement;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterContext;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.cache.NotationsCache;
import com.xenoage.zong.musiclayout.layouter.cache.VoiceSpacingsCache;
import com.xenoage.zong.musiclayout.layouter.measurecolumnspacing.MeasureColumnSpacingStrategy;
import com.xenoage.zong.musiclayout.spacing.MeasureColumnSpacing;
import com.xenoage.zong.util.ArrayTools;


/**
 * The system arrangement strategy arranges
 * a list of measure columns into a {@link SystemArrangement}.
 * 
 * The systems will not be stretched to its full possible width.
 * This can be done by another strategy.
 * 
 * This strategy also regards forced and prohibited system breaks
 * and custom system margins (left, right) and staff margins.
 * 
 * @author Andreas Wenger
 */
public class SystemArrangementStrategy
	implements ScoreLayouterStrategy
{
	
	//used strategies
	private final MeasureColumnSpacingStrategy measureColumnSpacingStrategy;
	
	
	/**
	 * Creates a new {@link SystemArrangementStrategy}.
	 */
	public SystemArrangementStrategy(MeasureColumnSpacingStrategy measureColumnSpacingStrategy)
	{
		this.measureColumnSpacingStrategy = measureColumnSpacingStrategy;
	}
  
	
	/**
	 * Arranges an optimum number of measures in a system,
   * beginning at the given measure,
   * and returns this system.
   * If there is not enough space left on the
   * current frame to create a system, null is returned.
	 * @param startMeasure   the index of the measure to start with
	 * @param usableSize     the usable size within the score frame
	 * @param offsetY        the vertical offset of the system in mm
	 * @param systemIndex    the global system index (over all frames!)
	 * @param measureColumnSpacings  a list of all measure column spacings without
	 *                       leading spacings
	 * @param notations      the already computed notations. this cache is modified when
   *                       a leading spacing is created, because the notations needed
   *                       for the leading spacing are added.
	 * @param lc             the context of the layouter
	 */
  public SystemArrangement computeSystemArrangement(int startMeasure, Size2f usableSize,
  	float offsetY, int systemIndex, ArrayList<MeasureColumnSpacing> measureColumnSpacings,
  	NotationsCache notations, ScoreLayouterContext lc)
  {
  	
  	//test if there is enough height for the system
  	Score score = lc.getScore();
  	ScoreFormat scoreFormat = score.getScoreFormat();
  	ScoreHeader scoreHeader = score.getScoreHeader();
  	float[] staffHeights = new float[score.getStavesCount()];
  	float[] staffDistances = new float[score.getStavesCount() - 1];
  	//compute staff heights
  	for (int iStaff = 0; iStaff < staffHeights.length; iStaff++)
  	{
  		Staff staff = score.getStaff(iStaff);
  		staffHeights[iStaff] = (staff.getLinesCount() - 1) * staff.getInterlineSpace();
  	}
  	//compute staff distances
  	for (int iStaff = 1; iStaff < staffHeights.length; iStaff++)
  	{
  		StaffLayout staffLayout = scoreHeader.getStaffLayout(systemIndex, iStaff);
  		if (staffLayout != null && staffLayout.getStaffDistance() != null)
  		{
  			//use custom staff distance
  			staffDistances[iStaff - 1] = staffLayout.getStaffDistance();
  		}
  		else
  		{
  			//use default staff distance
  			staffDistances[iStaff - 1] = score.getScoreFormat().getDefaultStaffLayoutNotNull(iStaff).getStaffDistance();
  		}
  	}
  	//enough space?
  	if (offsetY + ArrayTools.sum(staffHeights) +
  		ArrayTools.sum(staffDistances) > usableSize.height)
  	{
  		//not enough space
  		return null;
  	}
  	
  	//compute the usable width for the system
  	float systemLeftMargin = getLeftMargin(systemIndex, scoreFormat, scoreHeader);
  	float systemRightMargin = getRightMargin(systemIndex, scoreFormat, scoreHeader);
  	float usableWidth = usableSize.width - systemLeftMargin - systemRightMargin;
    
    //append system measure-by-measure, until there is no space any more
    //or until there are no measures left
  	int measuresCount = score.getMeasuresCount();
    LinkedList<MeasureColumnSpacing> systemMCSs = new LinkedList<MeasureColumnSpacing>();
    float usedWidth = 0;
    int currentMeasure;
    while (startMeasure + systemMCSs.size() < measuresCount)
    {
    	currentMeasure = startMeasure + systemMCSs.size();
      
      //decide if to add a leading spacing to the current measure or not
    	MeasureColumnSpacing currentMCS;
    	NotationsCache leadingNotations = null; //add leading notations to given cache only if
    	                                        //measure column with leading spacing is really used
      if (systemMCSs.size() == 0)
      { 
      	//first measure within this system: add leading elements (clef, time sig.)
      	Tuple3<MeasureColumnSpacing, VoiceSpacingsCache, NotationsCache> mcsData =
      		measureColumnSpacingStrategy.computeMeasureColumnSpacing(
      			score.getMeasureColumn(currentMeasure), true /* leading! */, notations,
      			score.getScoreHeader().getMeasureColumnHeader(currentMeasure), lc);
      	currentMCS = mcsData.get1();
      	leadingNotations = mcsData.get3();
      }
      else
      {
      	//otherwise: use the optimal spacing
      	currentMCS = measureColumnSpacings.get(currentMeasure);
      }
      
      //try to add this measure to the current system. if there is no space left for
      //it, don't use it, and we are finished.
      if (!append(currentMCS, currentMeasure, usableWidth, usedWidth,
      	score.getScoreHeader(), systemMCSs.size() == 0))
      {
        break;
      }
      else
      {
      	usedWidth += currentMCS.getWidth();
      	systemMCSs.add(currentMCS);
      	if (leadingNotations != null)
      		notations.setAll(leadingNotations);
      }
    }
    
    //we are finished
    if (systemMCSs.size() == 0)
    {
      return null; //not enough space for the system on this area
    }
    else
    {
      SystemArrangement ret = new SystemArrangement(startMeasure, startMeasure + systemMCSs.size() - 1,
      	ArrayTools.toMeasureColumnSpacingArray(systemMCSs), systemLeftMargin, systemRightMargin, usedWidth, 
      	staffHeights, staffDistances, offsetY);
      return ret;
    }
    
  }
  
  
  /**
   * Appends the given measure to the currently computed system, if possible.
   * It is not tested if there is enough vertical space, this must be done before.
   * @param measure       the spacing of the measure to append
   * @param measureIndex  the index of the measure to append
   * @param params        the current variables (can be changed within this method)
   * @return true, if there was enough space for the measure, so
   *    it was appended. false, if there was not enough
   *    space left, and it was not appended.
   */
  public boolean append(MeasureColumnSpacing measure, int measureIndex,
  	float usableWidth, float usedWidth, ScoreHeader scoreHeader, boolean firstMeasureInSystem)
  {
  	
  	//if a line break is forced, do it (but one measure is always allowed)
  	MeasureLayout layout = scoreHeader.getMeasureLayout(measureIndex);
  	if (layout.systemBreak == SystemBreak.NewSystem && !firstMeasureInSystem)
  		return false;
  	
  	//if a line break is prohibited, force the measure to be within this system
  	boolean force = (layout.systemBreak == SystemBreak.NoNewSystem);
  	
    //enough horizontal space?
    float remainingWidth = usableWidth - usedWidth;
    if (remainingWidth < measure.getWidth() && !force)
      return false;
    
    //ok, append the measure
    return true;
  }
	
	
	/**
	 * Returns the left margin of the given system (global index) in mm.
	 */
	private float getLeftMargin(int systemIndex, ScoreFormat scoreFormat, ScoreHeader scoreHeader)
	{
		SystemLayout systemLayout = scoreHeader.getSystemLayout(systemIndex);
		if (systemLayout != null && systemLayout.getSystemMarginLeft() != null)
		{
			//use custom system margin
			return systemLayout.getSystemMarginLeft();
		}
		else
		{
			//use default system margin
			return scoreFormat.getDefaultSystemLayout().getSystemMarginLeft();
		}
	}
	
	
	/**
	 * Returns the right margin of the given system (global index) in mm.
	 */
	private float getRightMargin(int systemIndex, ScoreFormat scoreFormat, ScoreHeader scoreHeader)
	{
		SystemLayout systemLayout = scoreHeader.getSystemLayout(systemIndex);
		if (systemLayout != null && systemLayout.getSystemMarginRight() != null)
		{
			//use custom system margin
			return systemLayout.getSystemMarginRight();
		}
		else
		{
			//use default system margin
			return scoreFormat.getDefaultSystemLayout().getSystemMarginRight();
		}
	}


}
