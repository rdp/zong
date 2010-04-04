package com.xenoage.zong.io.musicxml.in;

import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.util.iterators.It.it;
import static com.xenoage.util.math.Fraction._0;
import static com.xenoage.zong.core.music.MP.atMeasure;
import static com.xenoage.zong.core.music.MP.atStaff;
import static com.xenoage.zong.core.music.MP.mp;
import static com.xenoage.zong.core.music.barline.Barline.createBackwardRepeatBarline;
import static com.xenoage.zong.core.music.barline.Barline.createBarline;
import static com.xenoage.zong.core.music.barline.Barline.createForwardRepeatBarline;
import static com.xenoage.zong.core.music.util.BeatInterval.BeforeOrAt;
import static com.xenoage.zong.core.music.util.VoiceElementSelection.Last;
import static com.xenoage.zong.core.music.util.VoiceElementSide.Start;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import proxymusic.Attributes;
import proxymusic.Backup;
import proxymusic.BackwardForward;
import proxymusic.Barline;
import proxymusic.Forward;
import proxymusic.Repeat;
import proxymusic.RightLeftMiddle;
import proxymusic.ScorePartwise;
import proxymusic.YesNo;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.Parser;
import com.xenoage.util.iterators.It;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.format.Break;
import com.xenoage.zong.core.format.StaffLayout;
import com.xenoage.zong.core.format.SystemLayout;
import com.xenoage.zong.core.header.ScoreHeader;
import com.xenoage.zong.core.music.Attachable;
import com.xenoage.zong.core.music.ColumnElement;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.Measure;
import com.xenoage.zong.core.music.MeasureElement;
import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.core.music.Staff;
import com.xenoage.zong.core.music.Voice;
import com.xenoage.zong.core.music.VoiceElement;
import com.xenoage.zong.core.music.barline.BarlineStyle;
import com.xenoage.zong.core.music.beam.Beam;
import com.xenoage.zong.core.music.beam.BeamWaypoint;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.clef.Clef;
import com.xenoage.zong.core.music.clef.ClefType;
import com.xenoage.zong.core.music.direction.Tempo;
import com.xenoage.zong.core.music.key.Key;
import com.xenoage.zong.core.music.key.TraditionalKey;
import com.xenoage.zong.core.music.layout.PageBreak;
import com.xenoage.zong.core.music.layout.SystemBreak;
import com.xenoage.zong.core.music.rest.Rest;
import com.xenoage.zong.core.music.time.NormalTime;
import com.xenoage.zong.core.music.time.SenzaMisura;
import com.xenoage.zong.core.music.time.Time;
import com.xenoage.zong.core.music.util.BeatE;
import com.xenoage.zong.core.music.util.BeatInterval;
import com.xenoage.zong.core.music.util.VoiceElementSelection;
import com.xenoage.zong.core.music.util.VoiceElementSide;
import com.xenoage.zong.io.score.ScoreController;
import com.xenoage.zong.io.score.ScoreInputOptions;
import com.xenoage.zong.io.score.selections.Cursor;
import com.xenoage.util.error.ErrorProcessing;
import com.xenoage.util.exceptions.InvalidFormatException;
import com.xenoage.zong.util.exceptions.MeasureFullException;


/**
 * This class reads the actual musical contents of
 * the given partwise MusicXML 2.0 document into a {@link ScoreInput}.
 * 
 * TODO: at the end, find common time signatures
 * and key signatures and add them to {@link ScoreHeader}.
 * 
 * If possible, this reader works with the voice-element
 * to separate voices. TODO: if not existent or
 * used unreliably within a measure, implement this algorithm: 
 * http://archive.mail-list.com/musicxml/msg01673.html
 * 
 * TODO: Connect chords over staves, if they have the same
 * voice element but different staff element.
 *
 * @author Andreas Wenger
 */
class MxlScoreData
{

  
  /**
   * Reads the given MusicXML document and returns the score.
   */
  public static Score read(ScorePartwise doc, Score score, MxlScoreFormat mxlScoreFormat,
  	ErrorProcessing err)
    throws InvalidFormatException
  {
  	MxlScoreDataContext context = new MxlScoreDataContext(score, mxlScoreFormat); 
    
    //read the parts
    int staffIndexOffset = 0;
    It<proxymusic.ScorePartwise.Part> mxlParts = 
    	new It<proxymusic.ScorePartwise.Part>(doc.getPart());
    for (proxymusic.ScorePartwise.Part mxlPart : mxlParts)
    {
      //clear part-dependent context values
      int stavesCount = score.getStavesList().getParts().get(mxlParts.getIndex()).getStavesCount();
      context.beginNewPart(mxlParts.getIndex(), staffIndexOffset, staffIndexOffset + stavesCount - 1);
      staffIndexOffset += stavesCount;
      //read the measures
      for (proxymusic.ScorePartwise.Part.Measure mxlMeasure : mxlPart.getMeasure())
      {
        try
        {
        	readMeasure(context, mxlMeasure, err);
        }
        catch (Exception ex)
        {
          throw new InvalidFormatException("Error at " + context.toString(), ex);
        }
        context.incMeasureNumber();
    	}
    }
    
    //go through the whole score, and fill empty measures (that means, measures where
    //voice 0 has no single VoiceElement) with rests
    Fraction measureDuration = new Fraction(1, 4);
    for (int iStaff = 0; iStaff < context.getScore().getStavesCount(); iStaff++)
    {
    	Staff staff = context.getScore().getStaff(atStaff(iStaff));
    	for (int iMeasure = 0; iMeasure < staff.getMeasures().size(); iMeasure++)
    	{
    		Measure measure = staff.getMeasures().get(iMeasure);
    		Time newTime = context.getScore().getScoreHeader().getColumnHeader(iMeasure).getTime();
    		if (newTime != null)
    		{
    			//time signature has changed
    			if (newTime instanceof NormalTime)
    			{
    				measureDuration = ((NormalTime) newTime).getBeatsPerMeasure();
    			}
    			else
    			{
    				measureDuration = new Fraction(1, 4); //default: 1/4
    			}
    		}
    		Voice voice0 = measure.getVoices().get(0);
    		if (voice0.isEmpty())
    		{
    			//TODO: "whole rests" or split. currently, also 3/4 rests are possible
    			context.setScore(ScoreController.writeVoiceElement(
    				context.getScore(), mp(iStaff, iMeasure, 0, _0), new Rest(measureDuration)));
    		}
    	}
    }
    
    return context.getScore();
  }
  
  
  /**
   * Reads the given measure element and uses the current context.
   * The context may be changed by this method.
   */
  @SuppressWarnings("unchecked")
  private static void readMeasure(MxlScoreDataContext context,
  	proxymusic.ScorePartwise.Part.Measure mxlMeasure, ErrorProcessing err)
  	throws InvalidFormatException, MeasureFullException
  {
    //begin a new measure
    context.beginNewMeasure();
    //list all elements
    MxlMusicData musicData = new MxlMusicData(mxlMeasure);
    for(Tuple2<MxlMusicData.Type, ? extends Object> item : musicData.getItems())
    {
    	Object data = item.get2();
    	switch (item.get1())
    	{
    		case Chord:
    			MxlChord.readChord(context, (List<proxymusic.Note>) data, this, err);
    			break;
    		case Attributes:
    			readAttributes(context, (Attributes) data);
          break;
    		case Backup:
    			readBackup(context, (Backup) data);
    			break;
    		case Forward:
    			readForward(context, (Forward) data);
    			break;
    		case Print:
    			readPrint(context, (proxymusic.Print) data);
    			break;
    		case Direction:
    			MxlDirection.readDirection(context, (proxymusic.Direction) data, this);
    			break;
    		case Barline:
    			readBarline(context, (Barline) data);
    	}
    }
  }
  
  
  /**
   * Reads the given attributes element and uses the current context.
   * The context may be changed by this method.
   */
  private static void readAttributes(MxlScoreDataContext context,
  	Attributes mxlAttributes)
  {
  	
  	//divisions
  	BigDecimal mxlDivisions = mxlAttributes.getDivisions();
	  if (mxlDivisions != null)
	  {
	    //change divisions value
	    int divisions = mxlDivisions.intValue(); //we allow only integer values!
	    context.setDivisions(divisions);
	  }
	  
	  //key signatures
	  for (proxymusic.Key mxlKey : it(mxlAttributes.getKey()))
	  {
	  	//change key signature
      //only the fifths element is supported
      BigInteger mxlFifths = mxlKey.getFifths();
      if (mxlFifths != null)
      {
        //write to all staves within the current part (TODO: attribute "number" for single staves)
        for (int staff = 0; staff < context.getPartStavesIndices().getCount(); staff++)
        {
        	Key key = new TraditionalKey(mxlFifths.intValue());
        	writeColumnElement(context, key, staff);
        	context.getCurrentMusicContext(staff).setKey(key);
        }
      }
	  }
	  
	  //time signatures
	  for (proxymusic.Time mxlTime : it(mxlAttributes.getTime()))
	  {
	  	//change time signature
	  	Time time = null;
      if (mxlTime.getSenzaMisura() != null)
      {
        //senza misura
        time = new SenzaMisura();
      }
      else
      {
      	//at the moment we read only one beats/beat-type
      	//currently we accept only integers > 0
      	//TODO: fix proxymusic to make the following more convenient
      	String sBeats = null;
      	String sBeatType = null;
      	for (JAXBElement<String> e : mxlTime.getBeatsAndBeatType())
      	{
      		String name = e.getName().getLocalPart();
      		if (name.equals("beats"))
      			sBeats = e.getValue();
      		else if (name.equals("beat-type"))
      			sBeatType = e.getValue();
      		if (sBeats != null && sBeatType != null)
      			break;
      	}
        Integer beats = Parser.parseIntegerNull(sBeats);
        Integer beatType = Parser.parseIntegerNull(sBeatType);
        if (beats != null && beatType != null)
        {
        	time = new NormalTime(beats, beatType);
        }
      }
      //add to all staves
      if (time != null)
      {
      	//write to all staves within the current part (TODO: attribute "number" for single staves)
      	for (int staff = 0; staff < context.getPartStavesIndices().getCount(); staff++)
        {
        	writeNoVoiceElement(time, staff);
        }
      }
    }
	  
	  //clefs
	  for (proxymusic.Clef mxlClef : it(mxlAttributes.getClef()))
	  {
      //change clef
	  	Clef clef = null;
      switch (mxlClef.getSign())
      {
      	/* TODO case C:
      		clef = new Clef(ClefType.C); break; */
      	case F:
      		clef = new Clef(ClefType.F); break;
      	case G:
      		clef = new Clef(ClefType.G); break;
      	/* TODO case NONE:
      		clef = new Clef(ClefType.C); break;
      	case PERCUSSION:
      		clef = new Clef(ClefType.C); break;
      	case TAB:
      		clef = new Clef(ClefType.C); break; */
      }
      //staff (called "number" in MusicXML), first staff is default
      int staff = notNull(mxlClef.getNumber(), 1).intValue() - 1;
      //add to staff
      if (clef != null)
      {
      	writeNoVoiceElement(clef, staff);
      	context.getCurrentMusicContext(staff).setClef(clef);
      }
    }
	  
	  //transposition changes
	  if (mxlAttributes.getTranspose() != null)
	  {
	  	proxymusic.Transpose mxlTranspose = mxlAttributes.getTranspose();
	  	int chromatic = mxlTranspose.getChromatic().intValue();
	  	Transpose transpose = new Transpose(chromatic);
	  	//write to all staves of this part
	  	for (int staff = 0; staff < context.getPartStavesIndices().getCount(); staff++)
	  	{
  			writeNoVoiceElement(transpose, staff);
      }
    }
  
  }
  
  
  /**
   * Reads the given backup element.
   */
  private static void readBackup(MxlScoreDataContext context, Backup mxlBackup)
  	throws InvalidFormatException
  {
    //duration
    Fraction duration = readDuration(context, mxlBackup.getDuration()).invert();
    //move cursor
    context.moveCurrentBeat(duration);
  }
  
  
  /**
   * Reads the given forward element.
   */
  private static void readForward(MxlScoreDataContext context, Forward mxlForward)
    throws InvalidFormatException
  {
    //duration
    Fraction duration = readDuration(context, mxlForward.getDuration());
    //move cursor
    context.moveCurrentBeat(duration);
  }

  
  /**
   * Returns the duration as a {@link Fraction} from the given duration element.
   */
  public static Fraction readDuration(MxlScoreDataContext context, BigDecimal mxlDuration)
  	throws InvalidFormatException
  {
  	int duration = mxlDuration.intValue(); //we allow only integer values
    if (duration == 0)
    {
      throw new InvalidFormatException("Element has a duration of 0.");
    }
    return new Fraction(duration, 4 * context.getDivisions());
  }
  
  
  /**
   * Reads the given {@link proxymusic.Print} element.
   */
  private void readPrint(MxlScoreDataContext context, proxymusic.Print mxlPrint)
  {
  	//system and page break
  	YesNo mxlNewSystem = mxlPrint.getNewSystem();
  	SystemBreak systemBreak = (mxlNewSystem == null ? null :
  		(mxlNewSystem == YesNo.YES ? SystemBreak.NewSystem : SystemBreak.NoNewSystem));
  	YesNo mxlNewPage = mxlPrint.getNewPage();
  	PageBreak pageBreak = (mxlNewPage == null ? null :
  		(mxlNewPage == YesNo.YES ? PageBreak.NewPage : PageBreak.NoNewPage));
  	context.setScore(ScoreController.writeColumnElement(context.getScore(),
  		atMeasure(context.getMeasureIndex()), new Break(pageBreak, systemBreak)));
  	
  	//we assume that custom system layout information is just used in combination with
  	//forced system/page breaks. so we ignore system-layout elements which are not combined with system/page breaks.
  	//the first measure of a score is also ok.
  	if (context.getMeasureIndex() == 0 || systemBreak == SystemBreak.NewSystem ||
  		pageBreak == PageBreak.NewPage)
  	{
  		
  		//first page or new page?
  		boolean isPageBreak = pageBreak == PageBreak.NewPage;
  		boolean isPageStarted = (context.getMeasureIndex() == 0 || isPageBreak);
			if (isPageBreak)
			{
				//increment page index
				context.incCurrentPageIndex();
			}
  		
  		//first system or new system?
			boolean isSystemBreak = isPageBreak || systemBreak == SystemBreak.NewSystem;
  		if (isSystemBreak)
  		{
  			//increment system index 
  			context.incCurrentSystemIndex();
  		}	

  		//read system layout, if there
  		MxlSystemLayout mxlSystemLayout = null;
  		if (mxlPrint.getSystemLayout() != null)
  		{
  			mxlSystemLayout = new MxlSystemLayout(mxlPrint.getSystemLayout(),
    			context.getTenthMm());
  			SystemLayout systemLayout = mxlSystemLayout.getSystemLayout();
  			
  			//for first systems on a page, use top-system-distance
  			if (isPageStarted)
  			{
  				systemLayout = systemLayout.withSystemDistance(mxlSystemLayout.getTopSystemDistance());
  			}
  			
  			//apply values
  			ScoreHeader scoreHeader =	context.getScore().getScoreHeader().withSystemLayout(
  				context.getCurrentSystemIndex(), systemLayout);
  			context.setScore(context.getScore().withScoreHeader(scoreHeader));
  		}
  		
  	}
  	
  	//staff layouts
  	It<proxymusic.StaffLayout> mxlStaffLayouts = it(mxlPrint.getStaffLayout());
		for (proxymusic.StaffLayout mxlStaffLayout : mxlStaffLayouts)
		{
			int staffIndex = notNull(mxlStaffLayout.getNumber(), 1).intValue() - 1;
			context.setScore(ScoreController.withStaffLayout(context.getScore(),
				context.getCurrentSystemIndex(),
				context.getPartStavesIndices().getStart() + staffIndex,
				readStaffLayout(context, mxlStaffLayout)));
		}
		
  }
  
  
  /**
   * Reads the given barline element.
   * Currently only left and right barlines are supported.
   */
  private void readBarline(MxlScoreDataContext context, Barline mxlBarline)
    throws InvalidFormatException
  {
  	RightLeftMiddle location = notNull(mxlBarline.getLocation(), RightLeftMiddle.RIGHT);
  	Repeat repeat = mxlBarline.getRepeat();
  	ScoreHeader header = context.getScore().getScoreHeader();
  	int measureIndex = context.getMeasureIndex();
  	BarlineStyle style = Util.getBarlineStyle(mxlBarline.getBarStyle());
  	if (repeat != null)
  	{
  		//repeat barline
	    if (location == RightLeftMiddle.LEFT)
	    {
	    	//left barline
	    	if (repeat.getDirection() == BackwardForward.FORWARD)
	    	{
	    		style = notNull(style, BarlineStyle.HeavyLight);
	    		context.setScore(ScoreController.writeColumnStartBarline(context.getScore(),
	    			measureIndex, createForwardRepeatBarline(style)));
	    	}
	    }
	    else if (location == RightLeftMiddle.RIGHT)
	    {
	    	//right barline
	    	if (repeat.getDirection() == BackwardForward.BACKWARD)
	    	{
	    		style = notNull(style, BarlineStyle.LightHeavy);
	    		int times = notNull(repeat.getTimes(), 1).intValue();
	    		context.setScore(ScoreController.writeColumnEndBarline(context.getScore(),
	    			measureIndex, createBackwardRepeatBarline(style, times)));
	    	}
	    }
  	}
  	else
  	{
  		//regular barline
  		style = notNull(style, BarlineStyle.Regular);
  		if (location == RightLeftMiddle.LEFT)
	    {
  			//left barline
  			context.setScore(ScoreController.writeColumnStartBarline(context.getScore(),
    			measureIndex, createBarline(style)));
	    }
	    else if (location == RightLeftMiddle.RIGHT)
	    {
	    	//right barline
	    	context.setScore(ScoreController.writeColumnEndBarline(context.getScore(),
    			measureIndex, createBarline(style)));
	    }
  	}
  }
  
  
  /**
   * Reads the given {@link proxymusic.Print} element and returns it.
   */
  private StaffLayout readStaffLayout(MxlScoreDataContext context,
  	proxymusic.StaffLayout mxlStaffLayout)
  {
  	StaffLayout ret = StaffLayout.getDefault();

		//staff-distance
  	BigDecimal mxlStaffDistance = mxlStaffLayout.getStaffDistance();
		if (mxlStaffDistance != null)
			ret = ret.withStaffDistance(mxlStaffDistance.floatValue() * context.getTenthMm());
		
		return ret;
  }
  
  
  /**
   * Writes the given {@link VoiceElement} to the current measure.
   * The given staff (index within part) and voice is used.
   */
  static void writeVoiceElement(MxlScoreDataContext context,
  	VoiceElement element, int staff, int voice)
  {
    context.setScore(ScoreController.writeVoiceElement(context.getScore(),
    	mp(context.getPartStavesIndices().getStart() + staff,
  			context.getMeasureIndex(), voice, context.getCurrentBeat()), element));
    //we move the "cursor" forward, that is
    //needed when writing time/key signatures and so on.
    if (element.getDuration() != null) 
      context.moveCurrentBeat(element.getDuration());
  }
  
  
  /**
   * Writes the given {@link MeasureElement} at the given staff (index relative to first
   * staff in current part), current measure and current beat.
   */
  static void writeMeasureElement(MxlScoreDataContext context,
  	MeasureElement element, int staff)
  {
  	int staffIndex = context.getPartStavesIndices().getStart() + staff;
  	int measureIndex = context.getMeasureIndex();
  	Fraction beat = context.getCurrentBeat();
  	MP pos = mp(staffIndex, measureIndex, 0, beat);
  	context.setScore(ScoreController.writeMeasureElement(
  		context.getScore(), pos, element));
  }
  
  
  /**
   * Writes the given {@link ColumnElement} at the
   * current measure and current beat.
   */
  static void writeColumnElement(MxlScoreDataContext context,
  	ColumnElement element)
  {
  	int measureIndex = context.getMeasureIndex();
  	Fraction beat = context.getCurrentBeat();
  	MP pos = mp(0, measureIndex, 0, beat);
  	context.setScore(ScoreController.writeColumnElement(
  		context.getScore(), pos, element));
  }
  
  
  /**
   * Creates a beam for the given chords.
   */
  static void writeBeam(MxlScoreDataContext context, LinkedList<Chord> chords)
  {
  	PVector<BeamWaypoint> waypoints = new PVector<BeamWaypoint>();
  	for (Chord chord : chords)
  	{
  		waypoints = waypoints.plus(new BeamWaypoint(chord, false));
  	}
  	context.setScore(ScoreController.writeBeam(context.getScore(),
  		Beam.beam(waypoints, context.getScore().getGlobals())));
  }
  
  
  /**
   * Writes an attachments to the given element.
   */
  static void writeAttachment(MxlScoreDataContext context,
  	MusicElement anchor, Attachable attachment)
  {
  	context.setScore(context.getScore().withGlobals(
  		context.getScore().getGlobals().plusAttachment(anchor, attachment)));
  }
  
  
}
