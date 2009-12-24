package com.xenoage.zong.data.music;

import java.util.ArrayList;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Class for a staff of any size.
 * 
 * A vocal staff that is visible throughout the whole
 * score is an instance of this class as well
 * as a small ossia staff that is only displayed
 * over a single measure.
 * 
 * Staves are divided into measures.
 *
 * @author Andreas Wenger
 */
public class Staff
{
	
  //the measures from the beginning to the ending
  //of the score (even the invisible ones)
  private ArrayList<Measure> measures = new ArrayList<Measure>();
  
  //number of lines in this staff
  private int linesCount = 5;
  
  //interline space in this staff, or null for default
  private Float interlineSpace = null;
  
  private Score parentScore;
  
  
  
  /**
   * Creates a new staff for the given {@link Score}.
   */
  public Staff(Score parentScore)
  {
    this.parentScore = parentScore;
  }
  
  
  /**
	 * Returns a deep copy of this {@link Staff}.
	 * This is can by very memory expensive and slow, dependent
	 * of the number of measures in this staff.
	 */
	public Staff deepCopy()
	{
		//create staff
		Staff ret = new Staff(parentScore);
		//copy measures
		DeepCopyCache cache = new DeepCopyCache();
		for (Measure m : this.measures)
		{
			ret.measures.add(m.deepCopy(ret, cache));
		}
		//copy other information
		ret.linesCount = linesCount;
		ret.interlineSpace = interlineSpace;
		return ret;
	}
  
  
  /**
   * Adds the given number of empty measures at the end
   * of the staff.
   */
  public void addEmptyMeasures(int measuresCount)
  {
    for (int i = 0; i < measuresCount; i++)
    {
      measures.add(new Measure(this));
    }
    parentScore.getScoreHeader().updateMeasuresCount(measures.size());
  }
  
  
  /**
   * Gets the list of measures.
   */
  public ArrayList<Measure> getMeasures()
  {
    return measures;
  }
  
  
  /**
   * Gets the parent score of this staff.
   */
  protected Score getParentScore()
  {
    return parentScore;
  }
  
  
  /**
   * Gets the number of lines of this measure in mm.
   */
  public int getLinesCount()
  {
  	return linesCount;
  }
  
  
  /**
   * Gets the interline space of this measure in mm.
   */
  public float getInterlineSpace()
  {
  	if (interlineSpace != null)
  	{
  		//return custom value
  		return interlineSpace;
  	}
  	else
  	{
  		//return default value
  		return parentScore.getScoreFormat().getInterlineSpace();
  	}
  }
  
  
  /**
	 * Gets the global index of this staff.
	 */
	public int getIndex()
	{
		return parentScore.getStaffIndex(this);
	}
  

}
