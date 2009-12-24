package com.xenoage.zong.musiclayout.layouter.verticalframefilling;

import com.xenoage.util.math.Size2f;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.format.SystemLayout;
import com.xenoage.zong.data.music.Staff;
import com.xenoage.zong.musiclayout.FrameArrangement;
import com.xenoage.zong.musiclayout.SystemArrangement;
import com.xenoage.zong.musiclayout.spacing.MeasureColumnSpacing;


/**
 * This vertical frame filling strategy
 * adds empty systems to the current frame
 * until the page is filled and returns
 * the result.
 * 
 * @author Andreas Wenger
 */
public class EmptySystemsVerticalFrameFillingStrategy
  implements VerticalFrameFillingStrategy
{
	
	private static EmptySystemsVerticalFrameFillingStrategy instance = null;
	
	
	public static EmptySystemsVerticalFrameFillingStrategy getInstance()
	{
		if (instance == null)
			instance = new EmptySystemsVerticalFrameFillingStrategy();
		return instance;
	}
	
	
	private EmptySystemsVerticalFrameFillingStrategy()
	{
	}
	
  
  /**
   * Fill frame with empty systems.
   */
  public FrameArrangement computeFrameArrangement(FrameArrangement frameArr, Score score)
  {
  	Size2f usableSize = frameArr.getUsableSize();
  	FrameArrangement ret = frameArr;
  	
    //compute remaining space
  	float remainingSpace = usableSize.height;
  	float offsetY = 0;
    if (frameArr.getSystemsCount() > 0)
    {
    	SystemArrangement lastSystem = frameArr.getSystem(frameArr.getSystemsCount() - 1);
    	offsetY = lastSystem.getOffsetY() + lastSystem.getHeight();
    	remainingSpace -= offsetY;
    }
    
    //compute height of an additional system
    SystemLayout defaultSystemLayout = score.getScoreFormat().getDefaultSystemLayout();
    float defaultSystemDistance = defaultSystemLayout.getSystemDistance();
    float defaultMargin = defaultSystemLayout.getSystemMarginLeft() + defaultSystemLayout.getSystemMarginRight();
    SystemArrangement newSystem = createEmptySystem(score, usableSize.width, 0);
    float newSystemHeight = defaultSystemDistance + newSystem.getHeight();
    
    //add as many additional empty staves as possible
    int newSystemsCount = (int) (remainingSpace / newSystemHeight);
    if (newSystemsCount > 0)
    {
    	//otherwise add the empty systems
    	SystemArrangement[] newSystems = new SystemArrangement[frameArr.getSystemsCount() + newSystemsCount];
    	for (int i = 0; i < frameArr.getSystemsCount(); i++)
    	{
    		newSystems[i] = frameArr.getSystem(i);
    	}
    	for (int i = 0; i < newSystemsCount; i++)
    	{
    		newSystems[frameArr.getSystemsCount() + i] = createEmptySystem(
    			score, usableSize.width - defaultMargin, offsetY + defaultSystemDistance);
    		offsetY += newSystemHeight;
    	}
    	ret = new FrameArrangement(newSystems, frameArr.getUsableSize());
    }
    
    return ret;
  }
  
  
  /**
   * Creates an additional system for the given {@link Score} with
   * the given width and y-offset in mm and returns it.
   */
  private SystemArrangement createEmptySystem(Score score, float width, float offsetY)
  {
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
  		staffDistances[iStaff - 1] = score.getScoreFormat().getDefaultStaffLayoutNotNull(iStaff).getStaffDistance();
  	}
  	//create and returns system
  	SystemLayout defaultSystemLayout = score.getScoreFormat().getDefaultSystemLayout();
  	return new SystemArrangement(-1, -1, new MeasureColumnSpacing[0],
  		defaultSystemLayout.getSystemMarginLeft(), defaultSystemLayout.getSystemMarginRight(),
  		width, staffHeights, staffDistances, offsetY);
  }
  
  

}
