package com.xenoage.zong.io.musicxml.in;

import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.util.iterators.It.it;

import java.math.BigDecimal;
import java.math.BigInteger;
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

import com.xenoage.util.Parser;
import com.xenoage.util.iterators.It;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.format.MeasureLayout;
import com.xenoage.zong.data.format.StaffLayout;
import com.xenoage.zong.data.format.SystemLayout;
import com.xenoage.zong.data.header.ScoreHeader;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.NoVoiceElement;
import com.xenoage.zong.data.music.Rest;
import com.xenoage.zong.data.music.RestData;
import com.xenoage.zong.data.music.Staff;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.VoiceElement;
import com.xenoage.zong.data.music.barline.BarlineStyle;
import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.clef.ClefType;
import com.xenoage.zong.data.music.directions.Tempo;
import com.xenoage.zong.data.music.key.Key;
import com.xenoage.zong.data.music.key.TraditionalKey;
import com.xenoage.zong.data.music.layout.PageBreak;
import com.xenoage.zong.data.music.layout.SystemBreak;
import com.xenoage.zong.data.music.time.NormalTime;
import com.xenoage.zong.data.music.time.SenzaMisura;
import com.xenoage.zong.data.music.time.Time;
import com.xenoage.zong.data.music.transpose.Transpose;
import com.xenoage.zong.data.music.util.PositionedVoiceElement;
import com.xenoage.zong.io.score.ScoreInput;
import com.xenoage.zong.io.score.selections.Cursor;
import com.xenoage.util.error.ErrorProcessing;
import com.xenoage.util.exceptions.InvalidFormatException;
import com.xenoage.zong.util.exceptions.InvalidScorePositionException;
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

	private final ScoreInput input;
	private final MxlScoreFormat scoreFormat;
	private MxlScoreDataContext context;
	
	
	private MxlScoreData(ScoreInput input, MxlScoreFormat mxlScoreFormat)
	{
		this.input = input;
		this.scoreFormat = mxlScoreFormat;
		this.context = new MxlScoreDataContext(mxlScoreFormat.getTenthMm());
	}
	
  
  /**
   * Reads the given MusicXML document and
   * writes the data to the given {@link ScoreInput}.
   */
  public static void read(ScorePartwise doc, ScoreInput input, MxlScoreFormat mxlScoreFormat,
  	ErrorProcessing err)
    throws InvalidFormatException
  {
  	MxlScoreData mxlScoreData = new MxlScoreData(input, mxlScoreFormat); 
    Score score = input.getScore();
    
    //read the parts
    int staffIndexOffset = 0;
    It<proxymusic.ScorePartwise.Part> mxlParts = 
    	new It<proxymusic.ScorePartwise.Part>(doc.getPart());
    for (proxymusic.ScorePartwise.Part mxlPart : mxlParts)
    {
      //clear part-dependent context values
      int stavesCount = score.getParts().get(mxlParts.getIndex()).getStavesCount();
      mxlScoreData.context.beginNewPart(mxlParts.getIndex(), staffIndexOffset, staffIndexOffset + stavesCount - 1);
      staffIndexOffset += stavesCount;
      //read the measures
      for (proxymusic.ScorePartwise.Part.Measure mxlMeasure : mxlPart.getMeasure())
      {
        try
        {
        	mxlScoreData.readMeasure(mxlMeasure, err);
        }
        catch (Exception ex)
        {
          throw new InvalidFormatException("Error at " + mxlScoreData.context.toString(), ex);
        }
        mxlScoreData.context.incMeasureNumber();
    	}
    }
    
    //go through the whole score, and fill empty measures (that means, measures where
    //voice 0 has no single VoiceElement) with rests
    Fraction measureDuration = new Fraction(1, 4);
    for (Staff staff : input.getScore().getStavesList().getStaves())
    {
    	for (Measure measure : staff.getMeasures())
    	{
    		Time newTime = measure.getTime();
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
    		if (voice0.getFilledBeats().equals(Fraction._0))
    		{
    			//TODO: "whole rests" or split. currently, also 3/4 rests are possible
    			voice0.addElement(new Rest(new RestData(measureDuration)));
    		}
    	}
    }
    
  }
  
  
  /**
   * Reads the given measure element and uses the current context.
   * The context may be changed by this method.
   */
  @SuppressWarnings("unchecked")
  private void readMeasure(proxymusic.ScorePartwise.Part.Measure mxlMeasure,
  	ErrorProcessing err)
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
    			MxlChord.readChord((List<proxymusic.Note>) data, this, err);
    			break;
    		case Attributes:
          readAttributes((Attributes) data);
          break;
    		case Backup:
    			readBackup((Backup) data);
    			break;
    		case Forward:
    			readForward((Forward) data);
    			break;
    		case Print:
    			readPrint((proxymusic.Print) data);
    			break;
    		case Direction:
    			MxlDirection.readDirection((proxymusic.Direction) data, this);
    			break;
    		case Barline:
    			 readBarline((Barline) data);
    	}
    }
  }
  
  
  /**
   * Reads the given attributes element and uses the current context.
   * The context may be changed by this method.
   */
  private void readAttributes(Attributes mxlAttributes)
  	throws MeasureFullException
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
        	writeNoVoiceElement(key, staff);
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
  private void readBackup(Backup mxlBackup)
  	throws InvalidFormatException
  {
    //duration
    Fraction duration = readDuration(mxlBackup.getDuration()).invert();
    //move cursor
    context.moveCurrentBeat(duration);
  }
  
  
  /**
   * Reads the given forward element.
   */
  private void readForward(Forward mxlForward)
    throws InvalidFormatException
  {
    //duration
    Fraction duration = readDuration(mxlForward.getDuration());
    //move cursor
    context.moveCurrentBeat(duration);
  }

  
  /**
   * Returns the duration as a {@link Fraction} from the given duration element.
   */
  public Fraction readDuration(BigDecimal mxlDuration)
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
  private void readPrint(proxymusic.Print mxlPrint)
  {
  	//layout information
  	MeasureLayout ml = input.getScore().getScoreHeader().getMeasureLayout(context.getMeasureIndex());
  	//system and page break
  	YesNo mxlNewSystem = mxlPrint.getNewSystem();
  	ml.systemBreak = (mxlNewSystem == null ? null :
  		(mxlNewSystem == YesNo.YES ? SystemBreak.NewSystem : SystemBreak.NoNewSystem));
  	YesNo mxlNewPage = mxlPrint.getNewPage();
  	ml.pageBreak = (mxlNewPage == null ? null :
  		(mxlNewPage == YesNo.YES ? PageBreak.NewPage : PageBreak.NoNewPage));
  	
  	//we assume that custom system layout information is just used in combination with
  	//forced system/page breaks. so we ignore system-layout elements which are not combined with system/page breaks.
  	//the first measure of a score is also ok.
  	if (context.getMeasureIndex() == 0 || ml.systemBreak == SystemBreak.NewSystem || ml.pageBreak == PageBreak.NewPage)
  	{
  		
  		//first page or new page?
  		boolean pageBreak = ml.pageBreak == PageBreak.NewPage;
  		boolean pageStarted = (context.getMeasureIndex() == 0 || pageBreak);
			if (pageBreak)
			{
				//increment page index
				context.incCurrentPageIndex();
			}
  		
  		//first system or new system?
			boolean systemBreak = pageBreak || ml.systemBreak == SystemBreak.NewSystem;
  		if (systemBreak)
  		{
  			//increment system index 
  			context.incCurrentSystemIndex();
  		}	

  		//read system layout, if there
  		MxlSystemLayout mxlSystemLayout = null;
  		if (mxlPrint.getSystemLayout() != null)
  		{
  			mxlSystemLayout = new MxlSystemLayout(mxlPrint.getSystemLayout(),
    			scoreFormat.getTenthMm());
  			SystemLayout systemLayout = mxlSystemLayout.getSystemLayout();
  			
  			//for first systems on a page, use top-system-distance
  			if (pageStarted)
  			{
  				systemLayout.setSystemDistance(mxlSystemLayout.getTopSystemDistance());
  			}
  			
  			//apply values
  			input.getScore().getScoreHeader().setSystemLayout(
  				context.getCurrentSystemIndex(), systemLayout);
  		}
  		
  	}
  	
  	//staff layouts
  	It<proxymusic.StaffLayout> mxlStaffLayouts = it(mxlPrint.getStaffLayout());
		for (proxymusic.StaffLayout mxlStaffLayout : mxlStaffLayouts)
		{
			int staffIndex = notNull(mxlStaffLayout.getNumber(), 1).intValue() - 1;
  		input.getScore().getScoreHeader().setStaffLayout(context.getCurrentSystemIndex(),
  			context.getPartStavesIndices().getStart() + staffIndex, readStaffLayout(mxlStaffLayout));
		}
		
  }
  
  
  /**
   * Reads the given barline element.
   * Currently only left and right barlines are supported.
   */
  private void readBarline(Barline mxlBarline)
    throws InvalidFormatException
  {
  	RightLeftMiddle location = notNull(mxlBarline.getLocation(), RightLeftMiddle.RIGHT);
  	Repeat repeat = mxlBarline.getRepeat();
  	ScoreHeader header = input.getScore().getScoreHeader();
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
	    		header.getMeasureColumnHeader(measureIndex).setStartBarline(
	    			com.xenoage.zong.data.music.barline.Barline.createForwardRepeatBarline(style));
	    	}
	    }
	    else if (location == RightLeftMiddle.RIGHT)
	    {
	    	//right barline
	    	if (repeat.getDirection() == BackwardForward.BACKWARD)
	    	{
	    		style = notNull(style, BarlineStyle.LightHeavy);
	    		int times = notNull(repeat.getTimes(), 1).intValue();
	    		header.getMeasureColumnHeader(measureIndex).setEndBarline(
	    			com.xenoage.zong.data.music.barline.Barline.createBackwardRepeatBarline(style, times));
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
    		header.getMeasureColumnHeader(measureIndex).setStartBarline(
    			com.xenoage.zong.data.music.barline.Barline.createBarline(style));
	    }
	    else if (location == RightLeftMiddle.RIGHT)
	    {
	    	//right barline
	    	header.getMeasureColumnHeader(measureIndex).setEndBarline(
    			com.xenoage.zong.data.music.barline.Barline.createBarline(style));
	    }
  	}
  }
  
  
  /**
   * Reads the given {@link proxymusic.Print} element and returns it.
   */
  private StaffLayout readStaffLayout(proxymusic.StaffLayout mxlStaffLayout)
  {
  	StaffLayout ret = StaffLayout.createEmptyStaffLayout();

		//staff-distance
  	BigDecimal mxlStaffDistance = mxlStaffLayout.getStaffDistance();
		if (mxlStaffDistance != null)
			ret.setStaffDistance(mxlStaffDistance.floatValue() * scoreFormat.getTenthMm());
		
		return ret;
  }
  
  
  /**
   * Writes the given {@link VoiceElement} to the current measure.
   * The given staff (index within part) and voice is used.
   */
  void writeVoiceElement(VoiceElement element, int staff, int voice)
    throws InvalidScorePositionException, MeasureFullException
  {
  	Cursor cursor = new Cursor(input,
  		new ScorePosition(context.getPartStavesIndices().getStart() + staff,
  			context.getMeasureIndex(), context.getCurrentBeat(), voice), false);
    cursor.write(element);
    //when in voice 0, we move the "cursor" forward, that is
    //needed when writing time/key signatures and so on.
    if (element.getDuration() != null) 
      context.moveCurrentBeat(element.getDuration());
  }
  
  
  /**
   * Writes the given {@link NoVoiceElement} at the given staff (index relative to first
   * staff in current part), current measure, current beat and voice 0.
   */
  void writeNoVoiceElement(NoVoiceElement element, int staff)
  	throws InvalidScorePositionException
  {
  	int staffIndex = context.getPartStavesIndices().getStart() + staff;
  	int measureIndex = context.getMeasureIndex();
  	Fraction beat = context.getCurrentBeat();
  	ScorePosition pos = new ScorePosition(staffIndex, measureIndex, beat, 0);
  	//write NoVoiceElements always before the chord to which they belong.
  	//other than MusicXML, a NoVoiceElement may not be placed "within" the
  	//duration a chord.
  	PositionedVoiceElement voiceElement =
  		input.getScore().getController().getVoiceElementAt(pos);
  	ScorePosition currectedPos = (voiceElement != null ? voiceElement.getPosition() : pos);
  	Cursor cursor = new Cursor(input, currectedPos, false);
    cursor.write(element);
  }
  
  
  /**
   * Writes the given {@link Tempo} at the given staff (index relative to first
   * staff in current part), current measure, current beat and voice 0,
   * but also to the measure column header.
   */
  void writeTempo(Tempo tempo, int staff)
  	throws InvalidScorePositionException
  {
  	writeNoVoiceElement(tempo, staff);
  	int measureIndex = context.getMeasureIndex();
  	Fraction beat = context.getCurrentBeat();
  	input.getScore().getScoreHeader().getMeasureColumnHeader(measureIndex).addTempo(tempo, beat);
  }
  
  
  protected MxlScoreDataContext getContext()
  {
  	return context;
  }
  
  
  protected MxlScoreFormat getScoreFormat()
  {
  	return scoreFormat;
  }
  
  
}
