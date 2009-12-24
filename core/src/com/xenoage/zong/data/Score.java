package com.xenoage.zong.data;

import com.xenoage.util.MathTools;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.controller.ScoreController;
import com.xenoage.zong.data.format.ScoreFormat;
import com.xenoage.zong.data.header.ScoreHeader;
import com.xenoage.zong.data.info.ScoreInfo;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.MusicElement;
import com.xenoage.zong.data.music.Staff;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.util.MeasureColumn;
import com.xenoage.zong.util.exceptions.InvalidScorePositionException;

import java.util.HashMap;
import java.util.List;


/**
 * This class contains a single piece of written music.
 * 
 * @author Andreas Wenger
 */
public class Score
{

	//general information about the score
	ScoreInfo scoreInfo;

	//the default formats of the score
	ScoreFormat scoreFormat;

	//the list of elements that are normally used by all staves
	ScoreHeader scoreHeader;

	//the list of the staves, parts and groups
	StavesList stavesList;
	
	//controller for basic operations on score data
	ScoreController controller;
	
	//other application-dependend meta-information
	HashMap<String, Object> metaData = new HashMap<String, Object>();


	/**
	 * Creates an empty score.
	 */
	public Score()
	{
		this.scoreInfo = new ScoreInfo();
		this.scoreFormat = new ScoreFormat();
		this.scoreHeader = new ScoreHeader();
		this.stavesList = new StavesList(this);
		this.controller = new ScoreController(this);
	}


	/**
	 * Gets general information about the score.
	 */
	public ScoreInfo getScoreInfo()
	{
		return scoreInfo;
	}


	/**
	 * Sets general information about the score.
	 */
	public void setScoreInfo(ScoreInfo scoreInfo)
	{
		this.scoreInfo = scoreInfo;
	}


	/**
	 * Gets the default formats of the score.
	 */
	public ScoreFormat getScoreFormat()
	{
		return scoreFormat;
	}


	/**
	 * Sets the default formats of the score.
	 */
	public void setScoreFormat(ScoreFormat scoreFormat)
	{
		this.scoreFormat = scoreFormat;
	}
	
	
	/**
	 * Adds the given part at the given position in parts.
	 */
	public void addPart(int partIndex, Part part)
	{
		int measures = 0;
		if (stavesList.getStavesCount() > 0)
			measures = stavesList.getStaff(0).getMeasures().size();
		stavesList.addPart(part, partIndex, measures);
	}


	/**
	 * Adds an unnamed part using the default instrument to this score at the
	 * given position in parts. The new part has the given number of staves.
	 */
	public Part addPart(int partIndex, int stavesCount)
	{
		int measures = 0;
		if (stavesList.getStavesCount() > 0)
			measures = stavesList.getStaff(0).getMeasures().size();
		Part part = new Part("Unnamed", null, stavesCount, null);
		stavesList.addPart(part, partIndex, measures);
		return part;
	}


	/**
	 * Gets the list of parts.
	 */
	public List<Part> getParts()
	{
		return stavesList.getParts();
	}


	/**
	 * Gets the staff of the given part with the given part-internal index.
	 */
	public Staff getStaff(int partIndex, int partStaffIndex)
	{
		return stavesList.getStaff(partIndex, partStaffIndex);
	}


	/**
	 * Gets the number of measures of this score.
	 */
	public int getMeasuresCount()
	{
		return stavesList.getMeasuresCount();
	}


	/**
	 * Adds the given number of empty measures at the end of the score.
	 */
	public void addEmptyMeasures(int measuresCount)
	{
		stavesList.addEmptyMeasures(measuresCount);
	}


	/**
	 * Gets the number of the staves.
	 */
	public int getStavesCount()
	{
		return stavesList.getStavesCount();
	}


	/**
	 * Gets the measure of the given index of all staves.
	 */
	public MeasureColumn getMeasureColumn(int index)
	{
		return stavesList.getMeasureColumn(index);
	}


	/**
	 * Gets the barline group the given staff belongs to. At the moment only the
	 * first registered group of the staff is returned. If no group is found,
	 * null is returned.
	 */
	public BarlineGroup getBarlineGroup(int staffIndex)
	{
		return stavesList.getBarlineGroup(staffIndex);
	}


	/**
	 * Gets the number of bracket groups.
	 */
	public int getBracketGroupsCount()
	{
		return stavesList.getBracketGroupsCount();
	}


	/**
	 * Gets the bracket group with the given index.
	 */
	public BracketGroup getBracketGroup(int index)
	{
		return stavesList.getBracketGroup(index);
	}


	/**
	 * Gets list of elements that are normally used in all staves, like time
	 * signatures and key signatures.
	 */
	public ScoreHeader getScoreHeader()
	{
		return scoreHeader;
	}


	/**
	 * Returns the number of Divisions of a quarter note. This is needed for
	 * Midi and MusicXML Export.
	 */
	public int computeDivisions()
	{
		int actualdivision = 4;
		for (int i = 0; i < stavesList.getStavesCount(); i++)
		{
			Staff staff = stavesList.getStaff(i);
			for (Measure measure : staff.getMeasures())
			{
				for (Voice voice : measure.getVoices())
				{
					for (MusicElement me : voice.getElements())
					{
						if (me.getDuration() != null)
						{
							actualdivision = MathTools.lcm(actualdivision, me.getDuration().getDenominator());
						}
					}
				}
			}
		}
		return actualdivision / 4;
	}
	
	
	/**
	 * Gets the {@link Staff} at the given global index.
	 */
	public Staff getStaff(int staffIndex)
		throws InvalidScorePositionException
	{
		if (staffIndex >= 0 && staffIndex <= stavesList.staves.size())
		{
			return stavesList.staves.get(staffIndex);
		}
		else
		{
			throw new InvalidScorePositionException(new ScorePosition(staffIndex, -1, null, -1));
		}
	}
	
	
	/**
	 * Gets the {@link Measure} with the given index at the staff with the given
	 * global index.
	 */
	public Measure getMeasure(int staffIndex, int measureIndex)
		throws InvalidScorePositionException
	{
		Staff staff = getStaff(staffIndex);
		if (measureIndex >= 0 && measureIndex <= staff.getMeasures().size())
		{
			return staff.getMeasures().get(measureIndex);
		}
		else
		{
			throw new InvalidScorePositionException(new ScorePosition(staffIndex, measureIndex, null, -1));
		}
	}
	
	
	/**
	 * Gets the {@link Voice} with the given index within the measure with the
	 * given index in the staff with the given global index.
	 */
	public Voice getVoice(int staffIndex, int measureIndex, int voiceIndex)
		throws InvalidScorePositionException
	{
		Measure measure = getMeasure(staffIndex, measureIndex);
		if (voiceIndex >= 0 && voiceIndex <= measure.getVoices().size())
		{
			return measure.getVoices().get(voiceIndex);
		}
		else
		{
			throw new InvalidScorePositionException(new ScorePosition(staffIndex, measureIndex, null, voiceIndex));
		}
	}
	
	
	/**
	 * Gets the global index of the given {@link Staff}, or -1 if the staff
	 * is not part of this score.
	 */
	public int getStaffIndex(Staff staff)
	{
		return stavesList.getStaffIndex(staff);
	}
	
	
	/**
	 * Gets the {@link ScoreController} of this score, which provides
	 * basic operations on the score data.
	 */
	public ScoreController getController()
	{
		return controller;
	}
	
	
	/**
   * Gets the start index of the given part.
   */
  public int getPartStartIndex(Part part)
  {
  	return stavesList.getPartStartIndex(part);
  }
  
  
  /**
   * Gets the end index of the given part.
   */
  public int getPartEndIndex(Part part)
  {
  	return stavesList.getPartEndIndex(part);
  }
  
  
  /**
   * Sets a new staves list for this score.
   * This is needed when the instrumentation was changed.
   */
  public void setStavesList(StavesList stavesList)
  {
  	this.stavesList = stavesList;
  }
  
  
  public StavesList getStavesList()
  {
  	return stavesList;
  }
  
  
  /**
   * Gets the filled beats for each measure column, that
   * means, the first beat in each column where the is no music
   * element following any more.
   */
  public Fraction[] getFilledBeats()
  {
  	Fraction[] ret = new Fraction[stavesList.getMeasuresCount()];
  	for (int iMeasure = 0; iMeasure < stavesList.getMeasuresCount(); iMeasure++)
  	{
	  	Fraction maxBeat = Fraction._0;
	  	for (Staff staff : stavesList.getStaves())
	  	{
	  		Fraction beat = staff.getMeasures().get(iMeasure).getFilledBeats();
	  		if (beat.compareTo(maxBeat) > 0)
	  			maxBeat = beat;
	  	}
	  	ret[iMeasure] = maxBeat;
  	}
  	return ret;
  }
  
  
  /**
   * Adds the given meta-data information.
   */
  public void addMetaData(String id, Object value)
  {
  	metaData.put(id, value);
  }
  
  
  /**
   * Gets the given meta-data information, or null.
   */
  public Object getMetaData(String id)
  {
  	return metaData.get(id);
  }


}
