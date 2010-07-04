package com.xenoage.zong.musiclayout.layouter.verticalframefilling;

import static com.xenoage.zong.core.music.MP.atStaff;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.format.SystemLayout;
import com.xenoage.zong.core.music.Staff;
import com.xenoage.zong.io.score.ScoreController;
import com.xenoage.zong.musiclayout.FrameArrangement;
import com.xenoage.zong.musiclayout.SystemArrangement;
import com.xenoage.zong.musiclayout.spacing.ColumnSpacing;


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
    if (frameArr.getSystems().size() > 0)
    {
    	SystemArrangement lastSystem = frameArr.getSystems().getLast();
    	offsetY = lastSystem.getOffsetY() + lastSystem.getHeight();
    	remainingSpace -= offsetY;
    }
    
    //compute height of an additional system
    SystemLayout defaultSystemLayout = score.getScoreFormat().getSystemLayout();
    float defaultSystemDistance = defaultSystemLayout.getSystemDistance();
    float defaultMargin = defaultSystemLayout.getSystemMarginLeft() + defaultSystemLayout.getSystemMarginRight();
    SystemArrangement newSystem = createEmptySystem(score, usableSize.width, 0);
    float newSystemHeight = defaultSystemDistance + newSystem.getHeight();
    
    //add as many additional empty staves as possible
    int newSystemsCount = (int) (remainingSpace / newSystemHeight);
    if (newSystemsCount > 0)
    {
    	//otherwise add the empty systems
    	PVector<SystemArrangement> newSystems = frameArr.getSystems();
    	for (int i = frameArr.getSystems().size() - 1; i < newSystemsCount; i++)
    	{
    		newSystems = newSystems.plus(createEmptySystem(
    			score, usableSize.width - defaultMargin, offsetY + defaultSystemDistance));
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
  		staffHeights[iStaff] = (staff.getLinesCount() - 1) *
  			ScoreController.getInterlineSpace(score, atStaff(iStaff));
  	}
  	//compute staff distances 
  	for (int iStaff = 1; iStaff < staffHeights.length; iStaff++)
  	{
  		staffDistances[iStaff - 1] = score.getScoreFormat().getStaffLayoutNotNull(iStaff).getStaffDistance();
  	}
  	//create and returns system
  	SystemLayout defaultSystemLayout = score.getScoreFormat().getSystemLayout();
  	return new SystemArrangement(-1, -1, new PVector<ColumnSpacing>(),
  		defaultSystemLayout.getSystemMarginLeft(), defaultSystemLayout.getSystemMarginRight(),
  		width, staffHeights, staffDistances, offsetY);
  }
  
  

}
