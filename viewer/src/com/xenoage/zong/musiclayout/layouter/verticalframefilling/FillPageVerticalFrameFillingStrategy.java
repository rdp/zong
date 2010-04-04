package com.xenoage.zong.musiclayout.layouter.verticalframefilling;

import com.xenoage.zong.core.Score;
import com.xenoage.zong.musiclayout.FrameArrangement;
import com.xenoage.zong.musiclayout.SystemArrangement;


/**
 * This vertical frame filling strategy
 * increases the distance of the systems
 * of the given {@link FrameArrangement} so that the
 * vertical space is completely used
 * and returns the result.
 * 
 * @author Andreas Wenger
 */
public class FillPageVerticalFrameFillingStrategy
  implements VerticalFrameFillingStrategy
{
	
	private static FillPageVerticalFrameFillingStrategy instance = null;
	
	
	public static FillPageVerticalFrameFillingStrategy getInstance()
	{
		if (instance == null)
			instance = new FillPageVerticalFrameFillingStrategy();
		return instance;
	}
	
	
	private FillPageVerticalFrameFillingStrategy()
	{
	}
	
  
  public FrameArrangement computeFrameArrangement(FrameArrangement frameArr, Score score)
	{
		FrameArrangement ret = frameArr;
    //if there is no or only one system, do nothing
    if (frameArr.getSystemsCount() > 1)
    {
	    //compute remaining space
	    SystemArrangement lastSystem = frameArr.getSystem(frameArr.getSystemsCount() - 1);
	    float lastSystemEndY = lastSystem.getOffsetY() + lastSystem.getHeight();
	    float remainingSpace = frameArr.getUsableSize().height - lastSystemEndY;
	    //compute additional space between the systems
	    float additionalSpace = remainingSpace / (frameArr.getSystemsCount() - 1);
	    //compute new y-offsets
	    SystemArrangement[] systemArrs = new SystemArrangement[frameArr.getSystemsCount()];
	    for (int i = 0; i < frameArr.getSystemsCount(); i++)
	    {
	      SystemArrangement system = frameArr.getSystem(i);
	      systemArrs[i] = system.changeOffsetY(system.getOffsetY() + i * additionalSpace);
	    }
	    ret = new FrameArrangement(systemArrs, frameArr.getUsableSize());
    }
    return ret;
	}

	
}
