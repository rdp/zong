package com.xenoage.zong.core;

import com.xenoage.pdlib.PMap;
import com.xenoage.zong.core.format.ScoreFormat;
import com.xenoage.zong.core.header.ScoreHeader;
import com.xenoage.zong.core.info.ScoreInfo;
import com.xenoage.zong.core.music.Globals;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.Measure;
import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.core.music.Part;
import com.xenoage.zong.core.music.Staff;
import com.xenoage.zong.core.music.StavesList;
import com.xenoage.zong.core.music.Voice;


/**
 * This class contains a single piece of written music.
 * 
 * @author Andreas Wenger
 */
public final class Score
{

	//general information
	private final ScoreInfo scoreInfo;
	private final ScoreFormat scoreFormat;
	
	//musical data
	private final ScoreHeader scoreHeader;
	private final StavesList stavesList;
	private final Globals globals;
	
	//additional meta information
	private final PMap<String, Object> metaData;
	
	private static final Score empty =
		new Score(ScoreInfo.empty(), ScoreFormat.getDefault(),
			ScoreHeader.empty(), StavesList.empty(), Globals.empty(),
			new PMap<String, Object>());


	/**
	 * Creates a new {@link Score}.
	 * @param scoreInfo    general information about the score
	 * @param scoreFormat  the default formats of the score
	 * @param scoreHeader  elements that are shared by all staves
	 * @param globals      global information like slurs and beams
	 * @param metaData     other application-dependend meta-information
	 *                     e.g. used to store page layout information, which is not part
	 *                     of the musical score in this project
	 */
	public Score(ScoreInfo scoreInfo, ScoreFormat scoreFormat, ScoreHeader scoreHeader,
		StavesList stavesList, Globals globals, PMap<String, Object> metaData)
	{
		this.scoreInfo = scoreInfo;
		this.scoreFormat = scoreFormat;
		this.scoreHeader = scoreHeader;
		this.stavesList = stavesList;
		this.globals = globals;
		this.metaData = metaData;
	}
	
	
	/**
	 * Gets an empty {@link Score}.
	 */
	public static Score empty()
	{
		return empty;
	}
	
	
	/**
	 * Gets the musical position of the given element, or returns
	 * null if unknown.
	 */
	public MP getMP(MusicElement element)
	{
		return globals.getMP(element);
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
	public Score withScoreInfo(ScoreInfo scoreInfo)
	{
		return new Score(scoreInfo, scoreFormat, scoreHeader, stavesList, globals, metaData);
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
	public Score withScoreFormat(ScoreFormat scoreFormat)
	{
		return new Score(scoreInfo, scoreFormat, scoreHeader, stavesList, globals, metaData);
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
	 * Sets the {@link ScoreHeader} of this score.
	 */
	public Score withScoreHeader(ScoreHeader scoreHeader)
	{
		return new Score(scoreInfo, scoreFormat, scoreHeader, stavesList, globals, metaData);
	}
	
	
	/**
	 * Gets the list of staves, parts and groups.
	 */
	public StavesList getStavesList()
  {
  	return stavesList;
  }
	
	
	/**
	 * Sets the list of staves, parts and groups.
	 */
	public Score withStavesList(StavesList stavesList)
  {
  	return new Score(scoreInfo, scoreFormat, scoreHeader, stavesList, globals, metaData);
  }
	
	
	/**
	 * Gets the number of staves.
	 */
	public int getStavesCount()
  {
  	return stavesList.getStavesCount();
  }
	
	
	/**
	 * Gets the global information like slurs and beams.
	 */
	public Globals getGlobals()
	{
		return globals;
	}
	
	
	/**
	 * Sets the global information like slurs and beams.
	 */
	public Score withGlobals(Globals globals)
	{
		return new Score(scoreInfo, scoreFormat, scoreHeader, stavesList, globals, metaData);
	}
	
	
	/**
   * Gets the meta-data information.
   */
  public PMap<String, Object> getMetaData()
  {
  	return metaData;
  }


	/**
	 * Adds the given number of empty measures at the end of the score.
	 */
	public Score plusEmptyMeasures(int measuresCount)
	{
		StavesList stavesList = this.stavesList.plusEmptyMeasures(measuresCount);
		ScoreHeader scoreHeader = this.scoreHeader.plusEmptyMeasures(measuresCount);
		return new Score(scoreInfo, scoreFormat, scoreHeader, stavesList,
			globals, metaData);
	}
  

  /**
   * Adds the given meta-data information.
   */
  public Score putMetaData(String id, Object value)
  {
  	PMap<String, Object> metaData = this.metaData.plus(id, value);
  	return new Score(scoreInfo, scoreFormat, scoreHeader, stavesList,
  		globals, metaData);
  }
  
  
  /**
	 * Gets the measure at the given {@link MP}.
	 */
	public Measure getMeasure(MP mp)
	{
		return stavesList.getMeasure(mp);
	}
	
	
	/**
	 * Gets the number of measures.
	 */
	public int getMeasuresCount()
	{
		return scoreHeader.getColumnHeaders().size();
	}
	
	
	/**
	 * Gets the staff with the given index.
	 */
	public Staff getStaff(int staffIndex)
	{
		return stavesList.getStaff(staffIndex);
	}
	
	
	/**
	 * Gets the staff at the given {@link MP}.
	 */
	public Staff getStaff(MP mp)
	{
		return stavesList.getStaff(mp);
	}
	
	
	/**
	 * Gets the voice at the given {@link MP}.
	 */
	public Voice getVoice(MP mp)
	{
		return stavesList.getVoice(mp);
	}
	
	
	/**
	 * Replaces the measure at the given {@link MP}.
	 * All affected elements (slurs, beams, ...) will automatically be removed
	 * from the score, so the returned score is guaranteed to be in a
	 * consistent state.
	 */
	public Score withMeasure(MP mp, Measure measure)
	{
		Measure oldMeasure = getMeasure(mp);
		Globals globals = this.globals;
		globals = globals.minusMeasure(oldMeasure);
		globals = globals.plusMeasure(mp, measure);
		return withMeasureUnchecked(mp, measure, globals);
	}
	
	
	/**
	 * Replaces the measure at the given {@link MP}.
	 * Unlike {@link Score#withMeasure(MP, Measure)}, this method does
	 * not remove obsolete elements but just replaces the measure, but therefore
	 * the updated list of globals must be given.
	 */
	public Score withMeasureUnchecked(MP mp, Measure measure, Globals globals)
	{
		Staff staff = getStaff(mp).withMeasure(mp.getMeasure(), measure);
		return withStaffUnchecked(mp, staff, globals);
	}
	
	
	/**
	 * Replaces the staff at the given {@link MP}.
	 * All affected elements (slurs, beams, ...) will automatically be removed
	 * from the score, so the returned score is guaranteed to be in a
	 * consistent state.
	 */
	public Score withStaff(MP mp, Staff staff)
	{
		Staff oldStaff = getStaff(mp);
		Globals globals = this.globals;
		globals = globals.minusStaff(oldStaff);
		globals = globals.plusStaff(mp, staff);
		return withStaffUnchecked(mp, staff, globals);
	}
	
	
	/**
	 * Replaces the staff at the given {@link MP}.
	 * Unlike {@link Score#withStaff(MP, Staff)}, this method does
	 * not remove obsolete elements but just replaces the measure, but therefore
	 * the updated list of globals must be given.
	 */
	private Score withStaffUnchecked(MP mp, Staff staff, Globals globals)
	{
		StavesList stavesList = this.stavesList.withStaff(mp.getStaff(), staff);
		return new Score(scoreInfo, scoreFormat, scoreHeader, stavesList, globals, metaData);
	}
	
	
	/**
	 * Replaces the voice at the given {@link MP}.
	 * All affected elements (slurs, beams, ...) will automatically be removed
	 * from the score, so the returned score is guaranteed to be in a
	 * consistent state.
	 */
	public Score withVoice(MP mp, Voice voice)
	{
		Voice oldVoice = getVoice(mp);
		Globals globals = this.globals;
		globals = globals.minusVoice(oldVoice);
		globals = globals.plusVoice(mp, voice);
		return withVoiceUnchecked(mp, voice, globals);
	}
	
	
	/**
	 * Replaces the voice at the given {@link MP}.
	 * Unlike {@link Score#withVoice(MP, Voice)}, this method does
	 * not remove obsolete elements but just replaces the measure, but therefore
	 * the updated list of globals must be given.
	 */
	public Score withVoiceUnchecked(MP mp, Voice voice, Globals globals)
	{
		Measure measure = getMeasure(mp).withVoice(mp.getVoice(), voice);
		return withMeasureUnchecked(mp, measure, globals);
	}

	
	/**
	 * Adds the given part at the end of the other parts.
	 * The corresponding staves are created, too.
	 */
	public Score plusPart(Part part)
	{
		StavesList stavesList = this.stavesList;
		int measures = 0;
		if (stavesList.getStavesCount() > 0)
			measures = stavesList.getStaff(0).getMeasures().size();
		stavesList = stavesList.plusPart(part, measures);
		return new Score(scoreInfo, scoreFormat, scoreHeader, stavesList,
			globals, metaData);
	}

}
