package com.xenoage.zong.io.musicxml.in.readers;

import static com.xenoage.pdlib.IVector.ivec;
import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.util.lang.Tuple2.t;
import static com.xenoage.util.math.Fraction._0;
import static com.xenoage.zong.io.musicxml.in.readers.MusicReader.readDuration;
import static com.xenoage.zong.io.musicxml.in.readers.OtherReader.readBezierPoint;
import static com.xenoage.zong.io.musicxml.in.readers.OtherReader.readVSide;
import static com.xenoage.zong.io.score.ScoreController.attachElement;

import com.xenoage.pdlib.PVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.enums.VSide;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.MusicContext;
import com.xenoage.zong.core.music.Pitch;
import com.xenoage.zong.core.music.chord.Articulation;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.chord.Grace;
import com.xenoage.zong.core.music.chord.Note;
import com.xenoage.zong.core.music.chord.Stem;
import com.xenoage.zong.core.music.chord.StemDirection;
import com.xenoage.zong.core.music.curvedline.CurvedLine;
import com.xenoage.zong.core.music.curvedline.CurvedLineWaypoint;
import com.xenoage.zong.core.music.direction.Dynamics;
import com.xenoage.zong.core.music.direction.DynamicsType;
import com.xenoage.zong.core.music.format.BezierPoint;
import com.xenoage.zong.core.music.rest.Rest;
import com.xenoage.zong.core.music.text.Lyric;
import com.xenoage.zong.core.music.text.Lyric.SyllableType;
import com.xenoage.zong.musicxml.types.MxlArticulations;
import com.xenoage.zong.musicxml.types.MxlBeam;
import com.xenoage.zong.musicxml.types.MxlCueNote;
import com.xenoage.zong.musicxml.types.MxlCurvedLine;
import com.xenoage.zong.musicxml.types.MxlDynamics;
import com.xenoage.zong.musicxml.types.MxlFullNote;
import com.xenoage.zong.musicxml.types.MxlGraceNote;
import com.xenoage.zong.musicxml.types.MxlLyric;
import com.xenoage.zong.musicxml.types.MxlNormalNote;
import com.xenoage.zong.musicxml.types.MxlNotations;
import com.xenoage.zong.musicxml.types.MxlNote;
import com.xenoage.zong.musicxml.types.MxlPitch;
import com.xenoage.zong.musicxml.types.MxlStem;
import com.xenoage.zong.musicxml.types.MxlSyllabicText;
import com.xenoage.zong.musicxml.types.MxlCurvedLine.MxlElementType;
import com.xenoage.zong.musicxml.types.choice.MxlArticulationsContent;
import com.xenoage.zong.musicxml.types.choice.MxlFullNoteContent;
import com.xenoage.zong.musicxml.types.choice.MxlNotationsContent;
import com.xenoage.zong.musicxml.types.choice.MxlArticulationsContent.MxlArticulationsContentType;
import com.xenoage.zong.musicxml.types.choice.MxlFullNoteContent.MxlFullNoteContentType;
import com.xenoage.zong.musicxml.types.choice.MxlLyricContent.MxlLyricContentType;
import com.xenoage.zong.musicxml.types.choice.MxlNotationsContent.MxlNotationsContentType;
import com.xenoage.zong.musicxml.types.choice.MxlNoteContent.MxlNoteContentType;
import com.xenoage.zong.musicxml.types.enums.MxlStartStopContinue;


/**
 * This class reads a {@link Chord} from a given list
 * of note elements.
 * 
 * @author Andreas Wenger
 */
public final class ChordReader
{
	
	
	/**
   * Reads the given chord, consisting of the given list of note elements,
   * including slurs and ties.
   * All but the first given note must have a chord-element inside.
   */
	public static MusicReaderContext readChord(MusicReaderContext context,
		PVector<MxlNote> mxlNotes)
  {
		MxlNote mxlFirstNote = mxlNotes.get(0);
  	MxlNoteContentType mxlFirstNoteType = mxlFirstNote.getContent().getNoteContentType();
  	
  	//type of chord/rest
  	//(unpitched is still unsupported)
  	MxlFullNote mxlFirstFullNote = mxlFirstNote.getContent().getFullNote();
  	MxlNormalNote mxlFirstNormalNote = null;
  	MxlCueNote mxlFirstCueNote = null;
  	MxlGraceNote mxlFirstGraceNote = null;
  	boolean cue = false;
  	if (mxlFirstNoteType == MxlNoteContentType.Normal)
  	{
  		mxlFirstNormalNote = (MxlNormalNote) mxlNotes.get(0).getContent();
  	}
  	else if (mxlFirstNoteType == MxlNoteContentType.Cue)
  	{
  		mxlFirstCueNote = (MxlCueNote) mxlNotes.get(0).getContent();
  		cue = true;
  	}
  	else if (mxlFirstNoteType == MxlNoteContentType.Grace)
  	{
  		mxlFirstGraceNote = (MxlGraceNote) mxlNotes.get(0).getContent();
  		cue = false; //may also be true later, see "Zong-Library/Discussions/MusicXML/Note - cue vs grace.txt"
  	}
  	MxlFullNoteContentType mxlFNCType = mxlFirstFullNote.getContent().getFullNoteContentType();
  	
    //duration. here, the first duration is the duration of the whole chord.
  	Fraction duration = _0;
  	if (mxlFirstNormalNote != null)
  	{
  		duration = readDuration(context, mxlFirstNormalNote.getDuration());
  	}
  	else if (mxlFirstCueNote != null)
  	{
  		duration = readDuration(context, mxlFirstCueNote.getDuration());
  	}
    
    //staff
  	//(not supported yet: multi-staff chords)
    int staff = notNull(mxlFirstNote.getStaff(), 1) - 1;
    
    //voice
    //TODO: might not exist! we have to use a helper algorithm to determine the right voice
    //then, see MusicReader class documentation
    int staffVoice = 0;
    String mxlVoice = mxlFirstNote.getEditorialVoice().getVoice();
    if (mxlVoice != null)
    {
    	Tuple2<MusicReaderContext, Integer> tVoice = context.getVoice(staff, mxlVoice);
      context = tVoice.get1();
      staffVoice = tVoice.get2();
    }
    
    //create new chord or rest
    Chord chord = null;
    if (mxlFNCType == MxlFullNoteContentType.Pitch)
    {
    	//create a chord
    	Pitch pitch = ((MxlPitch) mxlFirstFullNote.getContent()).getPitch();
    	Grace grace = null;
    	if (mxlFirstGraceNote != null)
    	{
    		grace = new Grace(notNull(mxlFirstGraceNote.getSlash(), true), _0); //TODO: duration
    	}
      chord = new Chord(ivec(new Note(pitch)), duration, null, cue, grace, null);
      context = context.writeVoiceElement(chord, staff, staffVoice);
    	//collect the following notes of this chord
      for (int i = 1; i < mxlNotes.size(); i++)
      {
      	Tuple2<MusicReaderContext, Chord> tChord = plusChordNote(context,
      		mxlNotes.get(i), chord, staff);
      	context = tChord.get1();
      	chord = tChord.get2();
      }
    }
    else if (mxlFNCType == MxlFullNoteContentType.Rest)
    {
    	//write a rest
    	context = context.writeVoiceElement(new Rest(duration, cue), staff, staffVoice);
    }
    
    //stem
    if (chord != null)
    {
    	Stem stem = readStem(context, mxlFirstNote, chord, staff);
    	if (stem != null)
    	{
	    	Chord oldChord = chord;
	    	chord = oldChord.withStem(stem);
	    	context = context.replaceChord(oldChord, chord);
    	}
    }
    
    //add beams
    //TODO: also read beams for grace and cue chords
    if (chord != null && mxlFirstGraceNote == null && !cue)
    {
      //currently we read only the beam elements with number 1
      for (MxlBeam mxlBeam : mxlFirstNote.getBeams())
      {
        int number = mxlBeam.getNumber();
        //read only level 1 beams
        if (number != 1) continue;
        switch (mxlBeam.getValue())
        {
        	case Begin:
        	{
        		//open new beam
        		context = context.openBeam(number);
        		context = context.addBeamChord(chord, number);
            break;
        	}
        	case Continue:
        	{
        		//add chord to beam
        		context = context.addBeamChord(chord, number);
            break;
        	}
        	case End:
        	{
        		//close the beam and create it
            context = context.addBeamChord(chord, number);
            Tuple2<MusicReaderContext, PVector<Chord>> t = context.closeBeam(number);
            context = t.get1();
            context = context.writeBeam(t.get2());
        	}
        }
      }
    }
    
    //notations
    if (chord != null)
    {    
    	for (MxlNotations mxlNotations : mxlFirstNote.getNotations())
	    {
    		context = readNotations(context, mxlNotations, chord, 0, staff).get1(); //first note has index 0
	    }
    }
    
    //lyric
    //not supported yet: in MusicXML also rests can have lyrics. see mesaure 36 in Echigo-Jishi
    if (chord != null) 
    {
	    for (MxlLyric mxlLyric : mxlFirstNote.getLyrics())
	    {
	    	//not supported yet:  different lines (number AND name attribute)
	    	Lyric.SyllableType syllableType = SyllableType.Single;
	    	MxlLyricContentType mxlLCType = mxlLyric.getContent().getLyricContentType();
	    	if (mxlLCType == MxlLyricContentType.SyllabicText)
	    	{
	    		MxlSyllabicText mxlSyllabicText = (MxlSyllabicText) mxlLyric.getContent();
	    		//a syllable
	    		switch (mxlSyllabicText.getSyllabic())
	    		{
	    			case Begin:
	    				syllableType = SyllableType.Begin;
	    				break;
	    			case Middle:
	    				syllableType = SyllableType.Middle;
	    				break;
	    			case End:
	    				syllableType = SyllableType.End;
	    				break;
	    		}
	    		//the next element must be the text element
	    		context = context.writeAttachment(chord,
	    			new Lyric(mxlSyllabicText.getText().getValue(), syllableType, 0));
	    		break; //currently we support only one syllable
	    	}
	    	else if (mxlLCType == MxlLyricContentType.Extend)
	    	{
	    		//extend - TODO: extension to next chord!
	    		context = context.writeAttachment(chord, Lyric.createExtend(0));
	    	}
	    }
    }
    
    context = context.moveCursorForward(duration);
    return context;
  }
  
  
  /**
   * Reads the given note element, which is part of
   * a chord (but not the first note element of the chord), and adds it to the given chord.
   * Also the notations of this note are read.
   */
  private static Tuple2<MusicReaderContext, Chord> plusChordNote(MusicReaderContext context,
  	MxlNote mxlNote, Chord chord, int staffIndexInPart)
  {
  	//only pitch is interesting for us, since we do not allow
  	//different durations for notes within a chord or other strange stuff
  	if (mxlNote.getContent().getNoteContentType() == MxlNoteContentType.Normal)
  	{
	  	MxlFullNoteContent mxlFNC = ((MxlNormalNote) mxlNote.getContent()).getFullNote().getContent();
	  	if (mxlFNC.getFullNoteContentType() == MxlFullNoteContentType.Pitch)
	  	{
		  	Pitch pitch = ((MxlPitch) mxlFNC).getPitch();
		  	Note note = new Note(pitch);
		  	Chord oldChord = chord;
		  	chord = chord.plusNote(note);
		  	context = context.replaceChord(oldChord, chord);
		  	//notations. we are only interested in the first element.
		  	for (MxlNotations notations : mxlNote.getNotations())
		    {
		  		Tuple2<MusicReaderContext, Chord> t = readNotations(
		  			context, notations, chord, chord.getNotes().indexOf(note), staffIndexInPart);
		  		context = t.get1();
		  		chord = t.get2();
		    }
	  	}
  	}
    return t(context, chord);
  }
  
  
  /**
   * Reads the slurs, ties and dynamics belonging to the given chord and note.
   * The beginnings and endings of the curved lines are saved in the context.
   */
  private static Tuple2<MusicReaderContext, Chord> readNotations(MusicReaderContext context,
  	MxlNotations mxlNotations, Chord chord, int noteIndex, int staffIndexInPart)
  {
		for (MxlNotationsContent mxlNC : mxlNotations.getElements())
		{
			MxlNotationsContentType mxlNCType = mxlNC.getNotationsContentType();
			
			switch (mxlNCType)
			{
				case CurvedLine:
				{
					//slur or tie
					MxlCurvedLine mxlCurvedLine = (MxlCurvedLine) mxlNC;
					Pitch pitch = chord.getNotes().get(noteIndex).getPitch();
					float noteLP = context.getMusicContext(staffIndexInPart).computeLinePosition(pitch);
					
					//type
					CurvedLine.Type type;
					if (mxlCurvedLine.getElementType() == MxlElementType.Slur)
						type = CurvedLine.Type.Slur;
					else
						type = CurvedLine.Type.Tie;
					
					//number (tied does usually not use number, but distinguishes by pitch)
					Integer number = mxlCurvedLine.getNumber();
					BezierPoint bezierPoint = readBezierPoint(mxlCurvedLine.getPosition(),
						mxlCurvedLine.getBezier(), context.getTenthMm(),
						context.getStaffLinesCount(staffIndexInPart), noteLP);
					VSide side = readVSide(mxlCurvedLine.getPlacement());
	  		
		  		//waypoint
					CurvedLineWaypoint wp = new CurvedLineWaypoint(chord, noteIndex, bezierPoint);
					if (type == CurvedLine.Type.Tie && number == null)
					{
						//unnumbered tied
						if (mxlCurvedLine.getType() == MxlStartStopContinue.Start)
							context = context.openUnnumberedTied(pitch, wp, side);
						else
							context = context.closeUnnumberedTied(pitch, wp, side);
					}
					else
					{
						//numbered curved line
						CurvedLineWaypoint.Type wpType;
						if (mxlCurvedLine.getType() == MxlStartStopContinue.Start)
							wpType = CurvedLineWaypoint.Type.Start;
						else if (mxlCurvedLine.getType() == MxlStartStopContinue.Stop)
							wpType = CurvedLineWaypoint.Type.Stop;
						else
							wpType = CurvedLineWaypoint.Type.Continue;
						context = context.registerCurvedLine(type, wpType, number, wp, side);
		  		}
		  		break;
				}
			
				case Dynamics:
				{
					//dynamics
					MxlDynamics mxlDynamics = (MxlDynamics) mxlNC;
					DynamicsType type = mxlDynamics.getElement();
					context = context.withScore(attachElement(
						context.getScore(), chord, new Dynamics(type)));
					break;
				}
			
				case Articulations:
				{
					//articulations
					MxlArticulations mxlArticulations = (MxlArticulations) mxlNC;
					PVector<Articulation> articulations = pvec();
					for (MxlArticulationsContent mxlAC : mxlArticulations.getContent())
					{
						MxlArticulationsContentType mxlACType = mxlAC.getArticulationsContentType();
						Articulation.Type at = null;
						switch (mxlACType)
						{
							case Accent:
								at = Articulation.Type.Accent;
								break;
							case Staccatissimo:
								at = Articulation.Type.Staccatissimo;
								break;
							case Staccato:
								at = Articulation.Type.Staccato;
								break;
							case StrongAccent:
								at = Articulation.Type.StrongAccent;
								break;
							case Tenuto:
								at = Articulation.Type.Tenuto;
								break;
						}
						if (at != null)
						{
							articulations = articulations.plus(new Articulation(at));
						}
					}
					if (articulations.size() > 0)
					{
						Chord oldChord = chord;
						chord = chord.withArticulations(articulations);
						context = context.replaceChord(oldChord, chord);
					}
					break;
				}
			}
		}
		return t(context, chord);
  }

  
  /**
   * Reads and returns the stem of the given chord.
   * If not available, null is returned.
   * @param context   the global context
   * @param xmlNote   the note element, that contains the interesting
   *                  stem element. if not, null is returned.
   * @param chord     the chord, whose notes are already collected
   * @param staff     the staff index of the current chord
   */
	private static Stem readStem(MusicReaderContext context, MxlNote mxlNote,
		Chord chord, int staff)
	{
		Stem ret = null;
		MxlStem mxlStem = mxlNote.getStem();
		if (mxlStem != null)
		{
			//direction
			StemDirection direction = null;
			switch (mxlStem.getValue())
			{
				case None:
					direction = StemDirection.None;
					break;
				case Up:
					direction = StemDirection.Up;
					break;
				case Down:
					direction = StemDirection.Down;
					break;
				case Double:
					direction = StemDirection.Up; //currently double is not supported
					break;
			}
			//length
			Float length = null;
			if (mxlStem.getYPosition().getDefaultY() != null)
			{
				//convert position in tenths relative to topmost staff line into
				//a length in interline spaces measured from the outermost chord note on stem side
				float stemEndLinePosition = convertDefaultYToLinePosition(context,
					mxlStem.getYPosition().getDefaultY(), staff);
				length = Math.abs(stemEndLinePosition -
					getNoteLinePosition(context, chord, stemEndLinePosition, staff)) / 2;
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
	private static float convertDefaultYToLinePosition(MusicReaderContext context,
		float defaultY, int staff)
	{
		Score score = context.getScore();
		float defaultYInMm = defaultY * score.getScoreFormat().getInterlineSpace() / 10;
		float interlineSpace = score.getInterlineSpace(context.getPartStavesIndices().getStart() + staff);
		int linesCount = context.getStaffLinesCount(staff);
		return 2 * (linesCount - 1) + 2 * defaultYInMm / interlineSpace;
	}
	
	
	/**
	 * Gets the line position of the note which is nearest to the given line position,
	 * using the musical context from the given staff.
	 */
	private static float getNoteLinePosition(MusicReaderContext context,
		Chord chord, float nearTo, int staff)
	{
		MusicContext mc = context.getMusicContext(staff);
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
