package com.xenoage.zong.io.musicxml.in;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import com.xenoage.util.Range;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.MusicContext;
import com.xenoage.zong.core.music.Pitch;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.curvedline.CurvedLineWaypoint;
import com.xenoage.zong.core.music.curvedline.CurvedLine.Type;
import com.xenoage.zong.core.music.direction.Wedge;
import com.xenoage.util.enums.VSide;
import com.xenoage.util.exceptions.InvalidFormatException;


/**
 * This class stores the current context when
 * reading a MusicXML 1.1 document.
 * 
 * Example for context variables are the
 * current divisions value or open ties.
 *
 * @author Andreas Wenger
 */
class MxlScoreDataContext
{
	
	private Score score;
	private final MxlScoreFormat scoreFormat;
	
  private int divisions = 1;
  
  private int partIndex = 0;
  private Range partStavesIndices = null;
  private int measureIndex = 0;
  
  //MusicXML uses a sequential format with a cursor.
  //we have to keep track about the cursor position,
  //which is within a part and a measure.
  private Fraction currentBeat = null; //current beat within current staff and measure
  
  //the current musical context for each staff
  private MusicContext[] currentMusicContext = null;
  
  //maps MusicXML staff-/voice-element to Score voice:
  //musicXMLVoice == voiceMappings.get(musicXMLStaff).get(scoreVoice)
  private ArrayList<ArrayList<Integer>> voiceMappings = null; 
  
  private List<LinkedList<com.xenoage.zong.core.music.chord.Chord>> openBeams;
  private List<Tuple2<CurvedLineWaypoint, VSide>> openSlurs;
  private List<Tuple2<CurvedLineWaypoint, VSide>> openTies;
  private Hashtable<Pitch, Tuple2<CurvedLineWaypoint, VSide>> openUnnumberedTies;
  private List<Wedge> openWedges;
  
  //current system and page index. only valid if system breaks and page breaks
  //are used continuously
  private int currentSystemIndex = 0;
  private int currentPageIndex = 0;
  
  
  /**
   * Creates a new context.
   * @param score        the score to start with
   * @param scoreFormat  the format of the score
   */
  public MxlScoreDataContext(Score score, MxlScoreFormat scoreFormat)
  {
  	this.score = score;
  	this.scoreFormat = scoreFormat;
    //6 levels of beaming
    openBeams = new ArrayList<LinkedList<Chord>>(6);
    for (int i = 0; i < 6; i++)
    {
      openBeams.add(null);
    }
    //6 slots each for slurs, beams and wedges
    openSlurs = new ArrayList<Tuple2<CurvedLineWaypoint, VSide>>(6);
    for (int i = 0; i < 6; i++) openSlurs.add(null);
    openTies = new ArrayList<Tuple2<CurvedLineWaypoint, VSide>>(6);
    for (int i = 0; i < 6; i++) openTies.add(null);
    openWedges = Arrays.asList(new Wedge[6]);
    //infinite number of unnumbered tied elements, identified by pitch
    openUnnumberedTies = new Hashtable<Pitch, Tuple2<CurvedLineWaypoint, VSide>>();
  }
  
  
  public Score getScore()
  {
  	return score;
  }
  
  
  public void setScore(Score score)
  {
  	this.score = score;
  }
  
  
  public MxlScoreFormat getScoreFormat()
  {
  	return scoreFormat;
  }
  
  
  /**
   * Call this method when a new part begins.
   */
  public void beginNewPart(int partIndex, int partFirstStaffIndex, int partLastStaffIndex)
  {
    this.partIndex = partIndex;
    this.partStavesIndices = new Range(partFirstStaffIndex, partLastStaffIndex);
    this.measureIndex = 0;
    this.currentBeat = null;
    this.currentMusicContext = new MusicContext[partStavesIndices.getCount()];
		for (int i = 0; i < partStavesIndices.getCount(); i++)
    {
    	this.currentMusicContext[i] = new MusicContext();
    }
    this.voiceMappings = null;
    this.currentSystemIndex = 0;
    this.currentPageIndex = 0;
  }
  
  
  /**
	 * Call this method when a new measure begins.
	 */
	public void beginNewMeasure()
	{
		this.currentBeat = Fraction._0;
		//context stays the same
		this.voiceMappings = new ArrayList<ArrayList<Integer>>(partStavesIndices.getCount());
    for (int i = 0; i < partStavesIndices.getCount(); i++)
    {
    	this.voiceMappings.add(new ArrayList<Integer>(4)); //4 voices are enough in most cases
    }
	}

  
  /**
   * Gets the current divisions value.
   */
  public int getDivisions()
  {
    return divisions;
  }

  
  /**
   * Sets the current divisions value.
   */
  public void setDivisions(int divisions)
  {
    this.divisions = divisions;
  }
  
  
  /**
   * Gets the global value of a tenth in mm. 
   */
  public float getTenthMm()
  {
    return scoreFormat.getTenthMm();
  }


  /**
   * Gets the index of the current measure.
   */
  public int getMeasureIndex()
  {
    return measureIndex;
  }


  /**
   * Sets the index of the current measure.
   */
  public void setMeasureIndex(int measureNumber)
  {
    this.measureIndex = measureNumber;
  }
  
  
  /**
   * Increments the index of the current measure.
   */
  public void incMeasureNumber()
  {
    this.measureIndex++;
  }


  /**
   * Gets the index of the currently edited part.
   */
  public int getPartIndex()
  {
    return partIndex;
  }


  /**
   * Gets the indices of the staves of the current part.
   */
  public Range getPartStavesIndices()
  {
    return partStavesIndices;
  }
  
  
  /**
   * Opens a new beam with the given number.
   */
  public void openBeam(int number)
  {
    checkNumber1to6(number);
    openBeams.set(number-1, new LinkedList<Chord>());
  }
  
  
  /**
   * Adds the given chord to the open beam
   * with the given number.
   */
  public void addBeamChord(Chord chord, int number)
  {
  	checkNumber1to6(number);
    openBeams.get(number-1).add(chord);
  }
  
  
  /**
   * Closes the open beam with the given number
   * and returns its chords.
   */
  public LinkedList<Chord> closeBeam(int number)
  {
  	checkNumber1to6(number);
    LinkedList<Chord> ret = openBeams.get(number-1);
    openBeams.set(number-1, null);
    return ret;
  }
  
  
  /**
   * Sets a start waypoint for a slur or tie with the given number.
   * For tied elements without a number, use openUnnumberedTied instead.
   */
  public void openCurvedLine(Type type, int number, CurvedLineWaypoint clwp, VSide side)
  {
  	checkNumber1to6(number);
  	List<Tuple2<CurvedLineWaypoint, VSide>> list = (type == Type.Slur ? openSlurs : openTies);
  	list.set(number-1, new Tuple2<CurvedLineWaypoint, VSide>(clwp, side));
  }
  
  
  /**
   * Closes the slur or tie with the given number and returns the
   * cached start waypoint.
   * For tied elements without a number, use closeUnnumberedTied instead.
   */
  public Tuple2<CurvedLineWaypoint, VSide> closeCurvedLine(Type type, int number)
  {
  	checkNumber1to6(number);
  	Tuple2<CurvedLineWaypoint, VSide> ret = null;
  	List<Tuple2<CurvedLineWaypoint, VSide>> list = (type == Type.Slur ? openSlurs : openTies);
  	ret = list.get(number-1);
  	list.set(number-1, null);
    return ret;
  }
  
  
  /**
   * Sets a start waypoint for a tied element without a number.
   */
  public void openUnnumberedTied(Pitch pitch, CurvedLineWaypoint clwp, VSide side)
  {
  	openUnnumberedTies.put(pitch, new Tuple2<CurvedLineWaypoint, VSide>(clwp, side));
  }
  
  
  /**
   * Closes the a tied element without a number.
   */
  public Tuple2<CurvedLineWaypoint, VSide> closeUnnumberedTied(Pitch pitch)
  {
    return openUnnumberedTies.remove(pitch);
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
  public void openWedge(int number, Wedge wedge)
  {
  	checkNumber1to6(number);
    openWedges.set(number-1, wedge);
  }
  
  
  /**
   * Closes the wedge with the given number and returns it.
   */
  public Wedge closeWedge(int number)
  {
  	checkNumber1to6(number);
  	Wedge ret = openWedges.get(number-1);
  	openWedges.set(number-1, null);
    return ret;
  }


	/**
	 * Gets the current beat within the current part and measure.
	 */
	public Fraction getCurrentBeat()
	{
	  return currentBeat;
	}


	/**
	 * Moves the current beat within the current part and measure.
	 */
	public void moveCurrentBeat(Fraction beat)
	{
	  this.currentBeat = this.currentBeat.add(beat);
	}
	
	
	/**
	 * Gets the voice index for the given MusicXML voice and MusicXML staff.
	 * This is needed, because voices are defined for staves in Score, while
	 * voices are defined for parts in MusicXML.
	 * @param mxlStaff  0-based staff index, found in staff-element minus 1
	 * @param mxlVoice  0-based voice index within part, found in voice-element minus 1
	 */
	public int getVoice(int mxlStaff, int mxlVoice)
		throws InvalidFormatException
	{
		try
		{
			//gets the voices list for the given staff
			ArrayList<Integer> voices = voiceMappings.get(mxlStaff);
			//look for the given MusicXML voice
			for (int scoreVoice = 0; scoreVoice < voices.size(); scoreVoice++)
			{
				if (voices.get(scoreVoice) == mxlVoice)
					return scoreVoice;
			}
			//if not existent yet, we have to create it
			voices.add(mxlVoice);
			return voices.size() - 1;
		}
		catch (IndexOutOfBoundsException ex)
		{
			throw new InvalidFormatException("MusicXML staff " + mxlStaff + " and voice " + mxlVoice +
				" are invalid for the current position. Enough staves defined in attributes?");
		}
	}
	
	
	/**
	 * Gets the current {@link MusicContext} for the given staff (index relative
	 * to index of first staff within the current part).
	 * Update this context whenever the clef, key signature and other
	 * relevant values change.
	 */
	public MusicContext getCurrentMusicContext(int staff)
	{
		return currentMusicContext[staff];
	}

	
	/**
	 * Gets the current system index.
	 * The value is only valid if system breaks and page breaks
	 * are used continuously.
	 */
	public int getCurrentSystemIndex()
	{
		return currentSystemIndex;
	}

	
	/**
	 * Increments the current system index.
	 */
	public void incCurrentSystemIndex()
	{
		this.currentSystemIndex++;
	}

	
	/**
	 * Gets the current page index.
	 * The value is only valid if system breaks and page breaks
	 * are used continuously.
	 */
	public int getCurrentPageIndex()
	{
		return currentPageIndex;
	}

	
	/**
	 * Increments the current page index.
	 */
	public void incCurrentPageIndex()
	{
		this.currentPageIndex++;
	}
	
	
	@Override public String toString()
	{
		return "part " + partIndex + ", measure " + measureIndex + ", beat " + currentBeat +
			", system " + currentSystemIndex + ", page " + currentPageIndex;
	}
  

}
