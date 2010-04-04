package com.xenoage.zong.io.musicxml.in;

import static com.xenoage.pdlib.IVector.ivec;
import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.util.NullTools.throwNullArg;
import static com.xenoage.util.iterators.It.it;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import proxymusic.Articulations;
import proxymusic.Notations;
import proxymusic.StemValue;
import proxymusic.Syllabic;
import proxymusic.TextElementData;

import com.xenoage.pdlib.IVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.NullTools;
import com.xenoage.util.Parser;
import com.xenoage.util.enums.VSide;
import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.error.ErrorProcessing;
import com.xenoage.util.iterators.It;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.MusicContext;
import com.xenoage.zong.core.music.Pitch;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.chord.Note;
import com.xenoage.zong.core.music.chord.Stem;
import com.xenoage.zong.core.music.chord.StemDirection;
import com.xenoage.zong.core.music.curvedline.CurvedLine;
import com.xenoage.zong.core.music.curvedline.CurvedLineWaypoint;
import com.xenoage.zong.core.music.format.BezierPoint;
import com.xenoage.zong.core.music.rest.Rest;
import com.xenoage.zong.core.music.text.Lyric;
import com.xenoage.zong.core.music.text.Lyric.SyllableType;
import com.xenoage.util.exceptions.InvalidFormatException;
import com.xenoage.zong.util.exceptions.MeasureFullException;


/**
 * This class reads a {@link Chord} from a given list
 * of note elements.
 * 
 * @author Andreas Wenger
 */
class MxlChord
{
	
	public enum Type
	{
		Normal, Grace, Cue, Rest;
	}
	
	//input
	private MxlScoreDataContext context;
	
	//output
	private Type type;
	
	//error handling
	private ErrorProcessing err;
	
	
	/**
   * Reads the given chord, consisting of a list of note elements, and writes
   * it to context of the given parent {@link MxlScoreData}, including slurs and ties.
   */
	public static void readChord(List<proxymusic.Note> mxlNotes, MxlScoreDataContext context,
		ErrorProcessing err)
		throws InvalidFormatException, MeasureFullException
	{
		new MxlChord(context, err).doReadChord(mxlNotes);
	}
	
	
	private MxlChord(MxlScoreDataContext context, ErrorProcessing err)
	{
		this.context = context;
		this.err = err;
	}
	

	/**
   * Does the work.
   */
  private void doReadChord(List<proxymusic.Note> mxlNotes)
    throws InvalidFormatException, MeasureFullException
  {
  	proxymusic.Note mxlFirstNote = mxlNotes.get(0);
  	
  	//type of chord/rest
  	if (mxlFirstNote.getGrace() != null)
  	{
  		type = Type.Grace;
  	}
  	else if (mxlFirstNote.getCue() != null)
  	{
  		type = Type.Cue;
  	}
  	else if (mxlFirstNote.getRest() != null)
  	{
  		type = Type.Rest;
  	}
  	else
  	{
  		type = Type.Normal;
  	}
  	
    //duration. here, the first duration is the duration of the whole chord.
  	BigDecimal mxlDuration = mxlFirstNote.getDuration();
  	Fraction duration = null;
  	if (mxlDuration != null)
  	{
  		duration = MxlScoreData.readDuration(context, mxlDuration);
  	}
    
    //staff
  	//TODO: not supported yet: multi-staff chords
    int staff = notNull(mxlFirstNote.getStaff(), 1).intValue() - 1;
    
    //voice
    //TODO: might not exist! we have to use a helper algorithm to determine the right voice
    //then, see ScoreData class documentation
    int partVoice = 0;
    String mxlVoice = mxlFirstNote.getVoice();
    if (mxlVoice != null)
    {
    	partVoice = Parser.parseInt(mxlVoice) - 1;
    } 
    int staffVoice = context.getVoice(staff, partVoice);
    
    //pitch or rest
    Pitch pitch = readPitch(mxlFirstNote);
    if (pitch == null && mxlFirstNote.getRest() == null)
    {
    	if (mxlFirstNote.getUnpitched() == null) //currently ignore unpitched
    	{
    		throw new InvalidFormatException("note has neither pitch nor rest");
    	}
    	return;
    }
    
    //create new chord or rest
    Chord chord = null;
    if (type == Type.Normal)
    {
    	//create a chord
      chord = new Chord(ivec(new Note(pitch)), duration, null, null);
    	//collect the following notes of this chord
      It<proxymusic.Note> mxlNotesIt = it(mxlNotes);
      for (proxymusic.Note mxlNote : mxlNotesIt)
      {
      	if (mxlNotesIt.getIndex() > 0)
      	{
	      	chord = plusChordNote(mxlNote, chord, staff);
      	}
      }
      //write the chord
      MxlScoreData.writeVoiceElement(context, chord, staff, staffVoice);
    }
    else if (type == Type.Rest)
    {
    	//write a rest
    	MxlScoreData.writeVoiceElement(context, new Rest(duration), staff, staffVoice);
    }
    
    //stem
    if (chord != null)
    {
    	chord = chord.withStem(readStem(mxlFirstNote, chord, staff));
    }
    
    //add beams
    if (chord != null)
    {
      //TODO: currently we read only the beam elements with number 1:
    	//support for more than one beam element to support custom beam styles
      for (proxymusic.Beam mxlBeam : mxlFirstNote.getBeam())
      {
        int number = mxlBeam.getNumber();
        //read only level 1 beams
        if (number != 1) continue;
        switch (mxlBeam.getValue())
        {
        	case BEGIN:
        	{
        		//open new beam
            context.openBeam(number);
            context.addBeamChord(chord, number);
            break;
        	}
        	case CONTINUE:
        	{
        		//add chord to beam
            context.addBeamChord(chord, number);
            break;
        	}
        	case END:
        	{
        		//close the beam and create it
            context.addBeamChord(chord, number);
            LinkedList<Chord> chords = context.closeBeam(number);
            MxlScoreData.writeBeam(context, chords);
        	}
        }
      }
    }
    
    //notations
    if (chord != null)
    {
	    List<Notations> mxlNotationsList = mxlFirstNote.getNotations();
	    if (mxlNotationsList.size() > 0)
	    {
	    	readNotations(mxlNotationsList.get(0), chord, 0, staff); //first note has index 0
	    }
    }
    
    //lyric
    if (chord != null) //TODO: in MusicXML also rests can have lyrics (?!). see mesaure 36 in Echigo-Jishi
    {
	    for (proxymusic.Lyric mxlLyric : mxlFirstNote.getLyric())
	    {
	    	//TODO: support for different lines (number AND name attribute)
	    	Lyric.SyllableType type = SyllableType.Single;
	    	//TODO: at the moment only a single syllablic/text is supported. support all kinds of lyrics!
	    	It<Object> mxlLyricElements = it(mxlLyric.getElisionAndSyllabicAndText());
	    	for (Object mxlLyricElement : mxlLyricElements)
	    	{
		    	if (mxlLyricElement instanceof Syllabic)
		    	{
		    		Syllabic mxlSyllabic = (Syllabic) mxlLyricElement;
		    		//a syllable
		    		if (mxlSyllabic == Syllabic.BEGIN)
		    			type = SyllableType.Begin;
		    		else if (mxlSyllabic == Syllabic.MIDDLE)
		    			type = SyllableType.Middle;
		    		else if (mxlSyllabic == Syllabic.END)
		    			type = SyllableType.End;
		    		//the next element must be the text element
		    		TextElementData mxlText = (TextElementData) mxlLyricElements.next();
		    		MxlScoreData.writeAttachment(context, chord, new Lyric(mxlText.getValue(), type));
		    		break; //we support only ony syllable currently
		    	}
		    	if (mxlLyric.getExtend() != null)
		    	{
		    		//extend - TODO: extension to next chord!
		    		//chord.setLyric(Lyric.createExtend(), 0);
		    	}
	    	}
	    }
    }
  }
  
  
  /**
   * Reads the given note element, which is part of
   * a chord (but not the first note element of the chord), and adds it to the given chord.
   * The context is modified when slurs and ties are found.
   */
  private Chord plusChordNote(proxymusic.Note mxlNote, Chord chord, int staffIndex)
  	throws InvalidFormatException
  {
  	//only pitch is interesting for us, since we do not allow
  	//different durations for notes within a chord or other strange stuff
  	Pitch pitch = readPitch(mxlNote);
  	if (pitch == null)
  		throw new InvalidFormatException("chord note is missing pitch");
  	Note note = new Note(pitch);
  	chord = chord.plusNote(note);
  	//notations. we are only interested in the first element.
  	List<Notations> mxlNotationsList = mxlNote.getNotations();
    if (mxlNotationsList.size() > 0)
    {
    	readNotations(mxlNotationsList.get(0), chord, chord.getNotes().indexOf(note),
    		staffIndex);
    }
    return chord;
  }
  
  
  /**
   * Reads the slurs, ties and dynamics belonging to the given chord and note.
   * The beginnings and endings of the curved lines are saved in the context.
   */
  private void readNotations(Notations mxlNotations, Chord chord, int noteIndex,
  	int staffIndex)
  {
  	/* <NO>
  	throwNullArg(mxlNotations, chord);
  	
		for (Object mxlElement : mxlNotations.getTiedOrSlurOrTuplet())
		{
			
			//slur or tie
			if (mxlElement instanceof proxymusic.Slur ||
				mxlElement instanceof proxymusic.Tied)
			{
				Pitch pitch = chord.getNotes()[noteIndex].getPitch();
				float noteLP = context.getCurrentMusicContext(staffIndex).computeLinePosition(pitch);
				
				//type formatting
				CurvedLine.Type type;
				CurvedLineWaypoint.Type waypointType = null;
				Integer number;
				BezierPoint bezierPoint;
				VSide side;
				if (mxlElement instanceof proxymusic.Slur)
				{
					//slur
					type = CurvedLine.Type.Slur;
					proxymusic.Slur mxlSlur = (proxymusic.Slur) mxlElement;
					number = notNull(mxlSlur.getNumber(), 1);
					switch (mxlSlur.getType())
					{
						case START:
							waypointType = CurvedLineWaypoint.Type.Start;
							break;
						case STOP:
							waypointType = CurvedLineWaypoint.Type.Stop;
							break;
					}
					bezierPoint = Util.getBezierPoint(mxlSlur, context.getTenthMm(),
						context.getCurrentMusicContext(staffIndex).getLines(), noteLP);
					side = Util.getVSide(mxlSlur.getPlacement());
				}
				else
				{
					//tie
					type = CurvedLine.Type.Tie;
					proxymusic.Tied mxlTied = (proxymusic.Tied) mxlElement;
					number = mxlTied.getNumber(); //tied does usually _not_ use number, but distinguishes by pitch
					switch (mxlTied.getType())
					{
						case START:
							waypointType = CurvedLineWaypoint.Type.Start;
							break;
						case STOP:
							waypointType = CurvedLineWaypoint.Type.Stop;
							break;
					}
					bezierPoint = Util.getBezierPoint(mxlTied, context.getTenthMm(),
						context.getCurrentMusicContext(staffIndex).getLines(), noteLP);
					side = Util.getVSide(mxlTied.getPlacement());
				}
	  		
	  		//waypoint
	  		if (waypointType == CurvedLineWaypoint.Type.Start)
	  		{
	  			//curved line starts at this chord
	  			CurvedLineWaypoint wp = new CurvedLineWaypoint(chord, noteIndex, bezierPoint);
	  			if (type == CurvedLine.Type.Tie && number == null)
	  				context.openUnnumberedTied(pitch, wp, side);
	  			else
	  				context.openCurvedLine(type, number, wp, side);
	  		}
	  		else if (waypointType == CurvedLineWaypoint.Type.Stop)
	  		{
	  			//curved line ends at this chord
	  			Tuple2<CurvedLineWaypoint, VSide> startWP;
	  			if (type == CurvedLine.Type.Tie && number == null)
	  				startWP = context.closeUnnumberedTied(pitch);
	  			else
	  				startWP = context.closeCurvedLine(type, number);
	  			if (startWP != null)
	  			{
	  				CurvedLineWaypoint stopWp = new CurvedLineWaypoint(chord, noteIndex, bezierPoint);
	  				CurvedLine cl = CurvedLine.createAndConnect(type, startWP.get1(), stopWp);
	  				cl.setSide(startWP.get2());
	  			}
	  			else if (err != null)
	  			{
	  				err.report(ErrorLevel.Remark, "found end of curved line (" +
	  					type + ") without corresponding start");
	  			}
	  		}
				
			}
			
			//dynamics
			else if (mxlElement instanceof proxymusic.Dynamics)
			{
				DynamicsType type = Util.getDynamicsType((proxymusic.Dynamics) mxlElement);
				if (type != null)
				{
					chord.addDirection(new Dynamics(type));
				}
			}
			
			//articulations
			if (mxlElement instanceof Articulations)
			{
				LinkedList<Articulation> articulations = new LinkedList<Articulation>();
				Articulations mxlArticulations = (Articulations) mxlElement;
				for (JAXBElement articulation : mxlArticulations.getAccentOrStrongAccentOrStaccato())
				{
					String name = articulation.getName().getLocalPart();
					if (name.equals("accent"))
						articulations.addFirst(new Articulation(Articulation.Type.Accent));
					else if (name.equals("staccato"))
						articulations.addFirst(new Articulation(Articulation.Type.Staccato));
					else if (name.equals("staccatissimo"))
						articulations.addFirst(new Articulation(Articulation.Type.Staccatissimo));
					else if (name.equals("strong-accent"))
						articulations.addFirst(new Articulation(Articulation.Type.StrongAccent));
					else if (name.equals("tenuto"))
						articulations.addFirst(new Articulation(Articulation.Type.Tenuto));
				}
				if (articulations.size() > 0)
				{
					chord.setData(new ChordData(chord.getNotes(), chord.getDuration(),
						articulations.toArray(new Articulation[0])));
				}
			}
			
		}
		*/
  }
  
  
  /**
   * Returns the pitch from the given note element, or null
   * if not available.
   */
  private Pitch readPitch(proxymusic.Note mxlNote)
  {
  	proxymusic.Pitch mxlPitch = mxlNote.getPitch();
    if (mxlPitch != null)
    {
    	byte step = 0;
    	switch (mxlPitch.getStep())
    	{
    		case C: step = 0; break;
    		case D: step = 1; break;
    		case E: step = 2; break;
    		case F: step = 3; break;
    		case G: step = 4; break;
    		case A: step = 5; break;
    		case B: step = 6; break;
    	}
      return new Pitch(
      	step, notNull(mxlPitch.getAlter(), 0).byteValue(), (byte) mxlPitch.getOctave());
    }
    else
    {
    	return null;
    }
  }

  
  /**
   * Reads and returns the stem of the given chord.
   * If not available, null is returned.
   * @param xmlNote   the note element, that contains the interesting
   *                  stem element. if not, null is returned.
   * @param chord     the chord, whose notes are already collected
   * @param staff     the staff index of the current chord
   */
	private Stem readStem(proxymusic.Note mxlNote, Chord chord, int staff)
	{
		Stem ret = null;
		proxymusic.Stem mxlStem = mxlNote.getStem();
		if (mxlStem != null)
		{
			//direction
			StemDirection direction = null;
			StemValue mxlValue = mxlStem.getValue();
			switch (mxlValue)
			{
				case NONE:
					direction = StemDirection.None;
					break;
				case UP:
					direction = StemDirection.Up;
					break;
				case DOWN:
					direction = StemDirection.Down;
					break;
				case DOUBLE:
					direction = StemDirection.Up; //TODO: support double
					break;
			}
			//length
			Float length = null;
			if (mxlStem.getDefaultY() != null)
			{
				//convert position in tenths relative to topmost staff line into
				//a length in interline spaces measured from the outermost chord note on stem side
				float stemEndLinePosition = convertDefaultYToLinePosition(
					mxlStem.getDefaultY().floatValue(), staff);
				length = Math.abs(stemEndLinePosition -
					getNoteLinePosition(chord, stemEndLinePosition, staff)) / 2;
			}
			//create stem
			ret = new Stem(direction, length);
		}
		return ret;
	}

	
	/**
	 * Converts the given default-y position in global tenths (that is always
	 * relative to the topmost staff line) to a line position, using the
	 * musical context from the given staff.
	 */
	private float convertDefaultYToLinePosition(float defaultY, int staff)
	{
		MxlScoreFormat scoreFormat = parentData.getScoreFormat();
		float defaultYInMm = defaultY * scoreFormat.getTenthMm();
		float interlineSpace = scoreFormat.getScoreFormat().getInterlineSpace(); //TODO: can be different for each staff!
		int linesCount = context.getCurrentMusicContext(staff).getLines();
		return 2 * (linesCount - 1) + 2 * defaultYInMm / interlineSpace;
	}
	
	
	/**
	 * Gets the line position of the note which is nearest to the given line position.
	 * using the musical context from the given staff.
	 */
	private float getNoteLinePosition(Chord chord, float nearTo, int staff)
	{
		MusicContext mc = context.getCurrentMusicContext(staff);
		Vector<Pitch> pitches = chord.getPitches();
		//if there is just one note, it's easy
		if (pitches.size() == 1)
		{
			return mc.computeLinePosition(pitches.getFirst());
		}
		//otherwise, test for the topmost and bottommost note
		else
		{
			float top = mc.computeLinePosition(pitches.getLast());
			float bottom = mc.computeLinePosition(pitches.getFirst());
			return (Math.abs(top - nearTo) < Math.abs(bottom - nearTo) ? top : bottom);
		}
	}
  
  
}
