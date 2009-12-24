package com.xenoage.zong.io.musicxml.in;

import com.xenoage.zong.data.instrument.Instrument;
import com.xenoage.util.exceptions.InvalidFormatException;

import java.util.List;

import proxymusic.ScorePart;


/**
 * This class reads a score-part element from a partwise
 * MusicXML 2.0 document.
 * 
 * The instrument elements are already combined
 * for easier later use.
 * 
 * There is also additional information like the number
 * of needed staves and measures.
 *
 * @author Andreas Wenger
 */
class MxlPartInfo
{
  
	private final ScorePart mxlScorePart;
  private final List<Instrument> instruments;
  
  private int maxStaves;
  private int measuresCount;

  
  public MxlPartInfo(ScorePart mxlScorePart)
  	throws InvalidFormatException
  {
    this.mxlScorePart = mxlScorePart;
    MxlInstruments mxlInstruments = new MxlInstruments(mxlScorePart);
    this.instruments = mxlInstruments.getInstruments();
    this.maxStaves = 1;
    this.measuresCount = 0;
  }
  
  
  /**
   * Gets the MusicXML {@link ScorePart} belonging to this part.
   */
  public ScorePart getMxlScorePart()
  {
  	return mxlScorePart;
  }


	/**
   * Gets the instruments used by this part.
   */
  public List<Instrument> getInstruments()
  {
    return instruments;
  }
  

  /**
   * Gets the maximum number of staves the part
   * uses in the whole score.
   */
  public int getMaxStaves()
  {
    return maxStaves;
  }


  /**
   * Sets the maximum number of staves the part
   * uses in the whole score.
   */
  public void setMaxStaves(int maxStaves)
  {
    this.maxStaves = maxStaves;
  }


  /**
   * Gets the number of measures
   * used by this part.
   */
  public int getMeasuresCount()
  {
    return measuresCount;
  }


  /**
   * Sets the number of measures
   * used by this part.
   */
  public void setMeasuresCount(int measuresCount)
  {
    this.measuresCount = measuresCount;
  }

}
