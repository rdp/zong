package com.xenoage.zong.io.musicxml.in.readers;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.lang.Tuple2.t;
import static com.xenoage.util.math.Fraction._0;
import static com.xenoage.zong.core.music.MP.mp;
import static com.xenoage.zong.core.music.MP.mp0;
import static com.xenoage.zong.core.music.beam.Beam.beam;

import com.xenoage.pdlib.PMap;
import com.xenoage.pdlib.PVector;
import com.xenoage.util.Range;
import com.xenoage.util.enums.VSide;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.Attachable;
import com.xenoage.zong.core.music.ColumnElement;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.MeasureElement;
import com.xenoage.zong.core.music.MusicContext;
import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.core.music.Part;
import com.xenoage.zong.core.music.Pitch;
import com.xenoage.zong.core.music.StavesList;
import com.xenoage.zong.core.music.VoiceElement;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.curvedline.CurvedLine;
import com.xenoage.zong.core.music.curvedline.CurvedLineWaypoint;
import com.xenoage.zong.core.music.curvedline.CurvedLine.Type;
import com.xenoage.zong.core.music.direction.Wedge;
import com.xenoage.zong.core.music.util.BeatInterval;
import com.xenoage.zong.io.musicxml.in.util.MusicReaderException;
import com.xenoage.zong.io.musicxml.in.util.OpenCurvedLine;
import com.xenoage.zong.io.musicxml.in.util.OpenElements;
import com.xenoage.zong.io.score.ScoreController;
import com.xenoage.zong.io.score.selections.Cursor;
import com.xenoage.zong.util.exceptions.MeasureFullException;


/**
 * This class stores the current context when
 * reading a MusicXML 1.1 document.
 * 
 * Example for context variables are the
 * current divisions value or open ties.
 *
 * @author Andreas Wenger
 */
public final class MusicReaderContext
{
	
	private final Cursor cursor;
	private final int divisions;
	
  //current system and page index. only valid if system breaks and page breaks
  //are used continuously
  private final int systemIndex;
  private final int pageIndex;
  
  //maps MusicXML staff-/voice-element to our voice:
  //musicXMLVoice == voiceMappings.get(musicXMLStaff).get(scoreVoice)
  private final PVector<PVector<String>> voiceMappings; 
  
  private final OpenElements openElements;
  
  private MusicReaderSettings settings;


	public MusicReaderContext(Score score, MusicReaderSettings settings)
  {
  	this(new Cursor(score, mp0, false), 1, 0, 0, new PVector<PVector<String>>(),
  		new OpenElements(), settings);
  }
	

  private MusicReaderContext(Cursor cursor, int divisions, int systemIndex,
		int pageIndex, PVector<PVector<String>> voiceMappings, OpenElements openElements,
		MusicReaderSettings settings)
	{
		this.cursor = cursor;
		this.divisions = divisions;
		this.systemIndex = systemIndex;
		this.pageIndex = pageIndex;
		this.voiceMappings = voiceMappings;
		this.openElements = openElements;
		this.settings = settings;
	}


	public Score getScore()
  {
  	return cursor.getScore();
  }
  
  
  public MusicReaderContext withScore(Score score)
  {
  	Cursor cursor = this.cursor.withScore(score);
  	return new MusicReaderContext(cursor, divisions, systemIndex, pageIndex,
  		voiceMappings, openElements, settings);
  }
  
  
  public int getDivisions()
  {
    return divisions;
  }

  
  public MusicReaderContext withDivisions(int divisions)
  {
  	return new MusicReaderContext(cursor, divisions, systemIndex, pageIndex,
  		voiceMappings, openElements, settings);
  }
  
  
  /**
	 * Moves the current beat within the current part and measure.
	 */
	public MusicReaderContext moveCurrentBeat(Fraction beat)
	{
		MP mp = this.cursor.getMP();
		Cursor cursor = this.cursor.withMP(mp.withBeat(mp.getBeat().add(beat)));
		return new MusicReaderContext(cursor, divisions, systemIndex, pageIndex,
  		voiceMappings, openElements, settings);
	}
	
	
  public MP getMP()
  {
    return cursor.getMP();
  }
  
  
  /**
	 * Gets the current system index.
	 * The value is only valid if system breaks and page breaks
	 * are used continuously.
	 */
	public int getSystemIndex()
	{
		return systemIndex;
	}

	
	/**
	 * Increments the current system index.
	 */
	public MusicReaderContext incSystemIndex()
	{
		return new MusicReaderContext(cursor, divisions, systemIndex + 1, pageIndex,
  		voiceMappings, openElements, settings);
	}

	
	/**
	 * Gets the current page index.
	 * The value is only valid if system breaks and page breaks
	 * are used continuously.
	 */
	public int getPageIndex()
	{
		return pageIndex;
	}

	
	/**
	 * Increments the current page index.
	 */
	public MusicReaderContext incPageIndex()
	{
		return new MusicReaderContext(cursor, divisions, systemIndex, pageIndex + 1,
  		voiceMappings, openElements, settings);
	}
	
	
	/**
   * Gets the global value of a tenth in mm. 
   */
  public float getTenthMm()
  {
    return getScore().getScoreFormat().getInterlineSpace() / 10;
  }
  
  
  /**
   * Gets the indices of the staves of the current part.
   */
  public Range getPartStavesIndices()
  {
  	StavesList stavesList = getScore().getStavesList();
  	Part part = stavesList.getPartByStaffIndex(cursor.getMP().getStaff());
    return stavesList.getPartStaffIndices(part);
  }
  
  
  /**
   * Gets the number of lines of the staff with the given index, relative
   * to the current part.
   */
  public int getStaffLinesCount(int staffIndexInPart)
  {
  	MP mp = cursor.getMP().withStaff(getPartStavesIndices().getStart() + staffIndexInPart);
  	return getScore().getStaff(mp).getLinesCount();
  }
  
  
  /**
   * Call this method when a new part begins.
   */
  public MusicReaderContext beginNewPart(int partIndex)
  {
  	Part part = getScore().getStavesList().getParts().get(partIndex);
  	MP mp = mp(getScore().getStavesList().getPartStaffIndices(part).getStart(), 0, 0, _0);
  	Cursor cursor = this.cursor.withMP(mp);
  	PVector<PVector<String>> voiceMappings = new PVector<PVector<String>>();
  	for (int i = 0; i < part.getStavesCount(); i++)
  		voiceMappings = voiceMappings.plus(new PVector<String>());
  	return new MusicReaderContext(cursor, divisions, 0, 0,
  		voiceMappings, new OpenElements(), settings);
  }
  
  
  /**
	 * Call this method when a new measure begins.
	 */
	public MusicReaderContext beginNewMeasure(int measureIndex)
	{
		Cursor cursor = this.cursor.withMP(
			mp(this.cursor.getMP().getStaff(), measureIndex, 0, _0));
		return new MusicReaderContext(cursor, divisions, systemIndex, pageIndex,
  		voiceMappings, openElements, settings);
	}

  
  /**
   * Opens a new beam with the given number.
   */
  public MusicReaderContext openBeam(int number)
  {
    checkNumber1to6(number);
    PVector<PVector<Chord>> openBeams = openElements.getOpenBeams();
    if (openBeams.get(number-1) != null)
    {
    	//error: this beam is already open
    	if (!settings.ignoreErrors)
    	{
    		throw new MusicReaderException("Beam " + number + " already open", this);
    	}
    }
    //open beam
  	openBeams = openBeams.with(number-1, new PVector<Chord>());
    return new MusicReaderContext(cursor, divisions, systemIndex, pageIndex,
  		voiceMappings, openElements.withOpenBeams(openBeams), settings);
  }
  
  
  /**
   * Adds the given chord to the open beam
   * with the given number.
   */
  public MusicReaderContext addBeamChord(Chord chord, int number)
  {
  	checkNumber1to6(number);
  	PVector<PVector<Chord>> openBeams = openElements.getOpenBeams();
    openBeams = openBeams.with(number-1, openBeams.get(number-1).plus(chord));
    return new MusicReaderContext(cursor, divisions, systemIndex, pageIndex,
  		voiceMappings, openElements.withOpenBeams(openBeams), settings);
  }
  
  
  /**
   * Closes the open beam with the given number
   * and returns its chords.
   */
  public Tuple2<MusicReaderContext, PVector<Chord>> closeBeam(int number)
  {
  	checkNumber1to6(number);
  	PVector<PVector<Chord>> openBeams = openElements.getOpenBeams();
  	PVector<Chord> ret = openBeams.get(number-1);
  	openBeams = openBeams.with(number-1, null);
  	MusicReaderContext context =
  		new MusicReaderContext(cursor, divisions, systemIndex, pageIndex,
  			voiceMappings, openElements.withOpenBeams(openBeams), settings);
    return t(context, ret);
  }
  
  
  /**
   * Sets a waypoint for a slur or tie with the given number.
   * When all required waypoints of the beam are set, the curved line is created.
   * 
   * For tied elements without a number, use openUnnumberedTied instead.
   */
  public MusicReaderContext registerCurvedLine(Type type,
  	CurvedLineWaypoint.Type wpType, int number, CurvedLineWaypoint clwp, VSide side)
  {
  	checkNumber1to6(number);
  	//ignore continue waypoints at the moment
  	if (wpType == CurvedLineWaypoint.Type.Continue)
  		return this;
  	boolean start = (wpType == CurvedLineWaypoint.Type.Start);
  	boolean stop = !start;
  	PVector<OpenCurvedLine> openCLs = (type == Type.Slur ?
  		openElements.getOpenSlurs() : openElements.getOpenTies());
  	//get open curved line, or create it
  	OpenCurvedLine openCL = openCLs.get(number-1);
  	if (openCL == null)
  	{
  		openCL = new OpenCurvedLine();
  		openCL.type = type;
  	}
  	//this point must not already be set
  	if ((start && openCL.start != null) || (stop && openCL.stop != null))
  		throw new MusicReaderException(wpType + " waypoint already set for " + type + " " + number, this);
  	OpenCurvedLine.Waypoint wp = new OpenCurvedLine.Waypoint();
  	wp.wp = clwp;
  	wp.side = side;
  	if (start)
  		openCL.start = wp;
  	else
  		openCL.stop = wp;
  	//if complete, write it now, otherwise remember it
  	MusicReaderContext ret = this;
  	if (openCL.start != null && openCL.stop != null)
  	{
  		ret = ret.createCurvedLine(openCL);
  		openCLs = openCLs.with(number-1, null);
  	}
  	else
  	{
  		openCLs = openCLs.with(number-1, openCL);
  	}
  	return new MusicReaderContext(ret.cursor, ret.divisions, ret.systemIndex, ret.pageIndex,
  		ret.voiceMappings, (type == Type.Slur ?
  			ret.openElements.withOpenSlurs(openCLs) : ret.openElements.withOpenTies(openCLs)),
  		ret.settings);
  }
  
  
  /**
   * Creates and writes a curved line from the given {@link OpenCurvedLine} object.
   */
  private MusicReaderContext createCurvedLine(OpenCurvedLine openCurvedLine)
  {
  	if (openCurvedLine.start != null && openCurvedLine.stop != null)
  	{
			CurvedLine cl = new CurvedLine(openCurvedLine.type,
				pvec(openCurvedLine.start.wp, openCurvedLine.stop.wp), openCurvedLine.start.side);
			return writeCurvedLine(cl);
  	}
  	return this;
  }
  
  
  /**
   * Sets a start waypoint for a tied element without a number.
   */
  public MusicReaderContext openUnnumberedTied(Pitch pitch,
  	CurvedLineWaypoint clwp, VSide side)
  {
  	PMap<Pitch, OpenCurvedLine> openUnnumberedTies =
  		openElements.getOpenUnnumberedTies();
  	OpenCurvedLine openCL = new OpenCurvedLine();
  	openCL.type = Type.Tie;
  	openCL.start = new OpenCurvedLine.Waypoint();
  	openCL.start.wp = clwp;
  	openCL.start.side = side;
  	openUnnumberedTies = openUnnumberedTies.plus(pitch, openCL);
  	return new MusicReaderContext(cursor, divisions, systemIndex, pageIndex,
  		voiceMappings, openElements.withOpenUnnumberedTies(openUnnumberedTies), settings);
  }
  
  
  /**
   * Closes the a tied element without a number.
   */
  public MusicReaderContext closeUnnumberedTied(Pitch pitch, CurvedLineWaypoint stopWP, VSide side)
  {
  	PMap<Pitch, OpenCurvedLine> openUnnumberedTies =
  		openElements.getOpenUnnumberedTies();
  	OpenCurvedLine openCL = openUnnumberedTies.get(pitch);
  	openUnnumberedTies = openUnnumberedTies.minus(pitch);
  	openCL.stop = new OpenCurvedLine.Waypoint();
  	openCL.stop.wp = stopWP;
  	openCL.stop.side = side;
  	MusicReaderContext ret = this;
  	ret = ret.createCurvedLine(openCL);
  	return new MusicReaderContext(ret.cursor, ret.divisions, ret.systemIndex, ret.pageIndex,
  		ret.voiceMappings, ret.openElements.withOpenUnnumberedTies(openUnnumberedTies), ret.settings);
  }
  
  
  /**
   * Checks if the given beam/slur/tie number is valid,
   * i.e. between 1 and 6. Otherwise an
   * IllegalArgumentException is thrown.
   */
  private void checkNumber1to6(int number)
  {
    if (number < 1 || number > 6)
    {
      throw new IllegalArgumentException("Number must be between 1 and 6.");
    }
  }
  
  
  /**
   * Sets the beginning of a wedge with the given number.
   */
  public MusicReaderContext openWedge(int number, Wedge wedge)
  {
  	checkNumber1to6(number);
  	PVector<Wedge> openWedges = openElements.getOpenWedges().with(number-1, wedge);
  	return new MusicReaderContext(cursor, divisions, systemIndex, pageIndex,
  		voiceMappings, openElements.withOpenWedges(openWedges), settings);
  }
  
  
  /**
   * Closes the wedge with the given number and returns it.
   */
  public Tuple2<MusicReaderContext, Wedge> closeWedge(int number)
  {
  	checkNumber1to6(number);
  	Wedge ret = openElements.getOpenWedges().get(number-1);
  	PVector<Wedge> openWedges = openElements.getOpenWedges().with(number-1, null);
  	MusicReaderContext context =
  		new MusicReaderContext(cursor, divisions, systemIndex, pageIndex,
  			voiceMappings, openElements.withOpenWedges(openWedges), settings);
    return t(context, ret);
  }
  
  
  /**
   * Gets the {@link MusicContext} at the current position at the
   * staff with the given part-intern index.
   */
  public MusicContext getMusicContext(int staffIndexInPart)
  {
  	MP mp = cursor.getMP().withStaff(getPartStavesIndices().getStart() + staffIndexInPart);
  	return ScoreController.getMusicContext(getScore(), mp,
  		BeatInterval.BeforeOrAt, BeatInterval.Before);
  }

	
	/**
	 * Gets the voice index for the given MusicXML voice and MusicXML staff.
	 * This is needed, because voices are defined for staves in Score, while
	 * voices are defined for parts in MusicXML.
	 * @param mxlStaff  0-based staff index, found in staff-element minus 1
	 * @param mxlVoice  voice id, found in voice-element
	 */
	public Tuple2<MusicReaderContext, Integer> getVoice(int mxlStaff, String mxlVoice)
	{
		try
		{
			//gets the voices list for the given staff
			PVector<String> voices = voiceMappings.get(mxlStaff);
			//look for the given MusicXML voice
			for (int scoreVoice = 0; scoreVoice < voices.size(); scoreVoice++)
			{
				if (voices.get(scoreVoice).equals(mxlVoice))
					return t(this, scoreVoice);
			}
			//if not existent yet, we have to create it
			voices = voices.plus(mxlVoice);
			MusicReaderContext context =
				new MusicReaderContext(cursor, divisions, systemIndex, pageIndex,
		  		voiceMappings.with(mxlStaff, voices), openElements, settings);
			return t(context, voices.size() - 1);
		}
		catch (IndexOutOfBoundsException ex)
		{
			throw new RuntimeException("MusicXML staff " + mxlStaff + " and voice \"" + mxlVoice +
				"\" are invalid for the current position. Enough staves defined in attributes?");
		}
	}
	
	
	/**
   * Writes the given {@link ColumnElement} at the
   * current measure and current beat.
   */
  public MusicReaderContext writeColumnElement(ColumnElement element)
  {
  	return withScore(ScoreController.writeColumnElement(getScore(), getMP(), element));
  }
  
	
	/**
   * Writes the given {@link MeasureElement} at the given staff (index relative to first
   * staff in current part), current measure and current beat.
   */
  public MusicReaderContext writeMeasureElement(MeasureElement element, int staffIndexInPart)
  {
  	int staffIndex = getPartStavesIndices().getStart() + staffIndexInPart;
  	MP pos = getMP().withStaff(staffIndex);
  	return withScore(ScoreController.writeMeasureElement(getScore(), pos, element));
  }
  
  
  /**
   * Writes the given {@link VoiceElement} to the current position
   * without moving the cursor forward.
   */
  public MusicReaderContext writeVoiceElement(VoiceElement element, int staffIndexInPart, int voice)
  {
  	Cursor cursor = this.cursor.withMP(getMP().withStaff(
  		getPartStavesIndices().getStart() + staffIndexInPart).withVoice(voice));
  	try
  	{
  		cursor = cursor.write(element);
  	}
  	catch (MeasureFullException ex)
  	{
  		if (!settings.ignoreErrors)
  			throw new MusicReaderException(ex, this);
  	}
  	return new MusicReaderContext(cursor, divisions, systemIndex, pageIndex,
  		voiceMappings, openElements, settings);
  }
  
  
  /**
   * Moves the cursor forward by the given duration.
   */
  public MusicReaderContext moveCursorForward(Fraction duration)
  {
  	MP oldMP = cursor.getMP();
  	Cursor cursor = this.cursor.withMP(oldMP.withBeat(oldMP.getBeat().add(duration)));
  	return new MusicReaderContext(cursor, divisions, systemIndex, pageIndex,
  		voiceMappings, openElements, settings);
  }
  
  
  /**
   * Creates a beam for the given chords.
   */
  public MusicReaderContext writeBeam(PVector<Chord> chords)
  {
  	return withScore(ScoreController.writeBeam(getScore(), beam(chords)));
  }
  
  
  /**
   * Adds a curved line.
   */
  public MusicReaderContext writeCurvedLine(CurvedLine cl)
  {
  	return withScore(ScoreController.writeCurvedLine(getScore(), cl));
  }
  
  
  /**
   * Writes an attachment to the given element.
   */
  public MusicReaderContext writeAttachment(MusicElement anchor, Attachable attachment)
  {
  	return withScore(getScore().withGlobals(
  		getScore().getGlobals().plusAttachment(anchor, attachment)));
  }
  
  
  /**
   * Replaces the given old chord by the given new one.
   * It must have the same duration like the chord which was already there.
	 * If the old chord had a beam, slur, directions or lyrics, they will be used
	 * again.
   */
  public MusicReaderContext replaceChord(Chord oldChord, Chord newChord)
  {
  	return withScore(ScoreController.replaceChord(getScore(), oldChord, newChord));
  }
	
	
  /**
	 * Replaces the chord at the given position by the given chord.
	 * It must have the same duration like the chord which was already there.
	 * If the old chord had a beam, slur, directions or lyrics, they will be used
	 * again.
	 */
	public MusicReaderContext replaceChord(MP mp, Chord chord)
	{
		return withScore(ScoreController.replaceChord(getScore(), mp, chord));
	}
	
	
	public MusicReaderSettings getSettings()
	{
		return settings;
	}
	
  
	@Override public String toString()
	{
		return "cursor at " + cursor.getMP() + ", system " + systemIndex + ", page " + pageIndex;
	}
  

}
