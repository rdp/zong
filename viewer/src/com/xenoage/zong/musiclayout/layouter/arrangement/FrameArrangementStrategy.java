package com.xenoage.zong.musiclayout.layouter.arrangement;

import java.util.ArrayList;
import java.util.LinkedList;


import com.xenoage.util.math.Size2f;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.format.ScoreFormat;
import com.xenoage.zong.core.format.SystemLayout;
import com.xenoage.zong.core.header.ScoreHeader;
import com.xenoage.zong.musiclayout.FrameArrangement;
import com.xenoage.zong.musiclayout.SystemArrangement;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterContext;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.cache.NotationsCache;
import com.xenoage.zong.musiclayout.spacing.MeasureColumnSpacing;
import com.xenoage.zong.util.ArrayTools;


/**
 * A frame arrangement strategy arranges a list of
 * measure columns on a frame by filling it with systems.
 * 
 * The systems are not stretched or moved
 * horizontally or vertically. This can be
 * done by other strategies.
 * 
 * This strategy also regards custom system distances.
 * 
 * @author Andreas Wenger
 */
public class FrameArrangementStrategy
	implements ScoreLayouterStrategy
{
	
	//used strategies
	private final SystemArrangementStrategy systemArrangementStrategy;
	
	
	/**
	 * Creates a new {@link FrameArrangementStrategy}.
	 */
	public FrameArrangementStrategy(SystemArrangementStrategy systemArrangementStrategy)
	{
		this.systemArrangementStrategy = systemArrangementStrategy;
	}

	
	/**
	 * Arranges the given measure column spacings beginning at the
	 * given index to a {@link FrameArrangement}.
	 */
	public FrameArrangement computeFrameArrangement(int startMeasure,
		int startSystem, Size2f usableSize, ArrayList<MeasureColumnSpacing> measureColumnSpacings,
  	NotationsCache notations, ScoreLayouterContext lc)
	{
		Score score = lc.getScore();
		ScoreFormat scoreFormat = score.getScoreFormat();
		ScoreHeader scoreHeader = score.getScoreHeader();
    
    int measuresCount = score.getMeasuresCount();
    int measureIndex = startMeasure;
    int systemIndex = startSystem;
    
    //top margin
    float offsetY = getTopSystemDistance(systemIndex, scoreFormat, scoreHeader);

    //append systems to the frame
    LinkedList<SystemArrangement> systems = new LinkedList<SystemArrangement>();
    while (measureIndex < measuresCount)
    {
      //try to create system on this frame
      SystemArrangement system = systemArrangementStrategy.computeSystemArrangement(
      	measureIndex, usableSize, offsetY, systemIndex, measureColumnSpacings, notations, lc);

      //was there enough place for this system?
      if (system != null)
      {
      	//yes, there is enough place
      	systems.add(system);
      	//update offset and start measure index for next system
      	//add height of this system
      	offsetY += system.getHeight();  
      	//add system distance of the following system
      	offsetY += getSystemDistance(systemIndex + 1, scoreFormat, scoreHeader);
      	//increase indexes
      	systemIndex++;
        measureIndex = system.getEndMeasureIndex() + 1;
      }
      else
      {
      	break;
      }
    }
    
    return new FrameArrangement(ArrayTools.toSystemArrangementArray(systems), usableSize);
	}
	
	
	/**
	 * Returns the top system distance (distance between top margin of the frame and
	 * first staff line of the first system within the frame) for the given
	 * system (global index) in mm.
	 */
	private float getTopSystemDistance(int systemIndex, ScoreFormat scoreFormat, ScoreHeader scoreHeader)
	{
		SystemLayout systemLayout = scoreHeader.getSystemLayout(systemIndex);
		if (systemLayout != null)
		{
			//use custom top system distance
			return systemLayout.getSystemDistance();
		}
		else
		{
			//use default distance
			return scoreFormat.getTopSystemDistance();
		}
	}
	
	
	/**
	 * Returns the system distance (distance between last staff line of the previous
	 * system and first staff line of the following given system (global index) in mm.
	 */
	private float getSystemDistance(int systemIndex, ScoreFormat scoreFormat, ScoreHeader scoreHeader)
	{
		SystemLayout systemLayout = scoreHeader.getSystemLayout(systemIndex);
		if (systemLayout != null)
		{
			//use custom system distance
			return systemLayout.getSystemDistance();
		}
		else
		{
			//use default system distance
			return scoreFormat.getSystemLayout().getSystemDistance();
		}
	}

}
