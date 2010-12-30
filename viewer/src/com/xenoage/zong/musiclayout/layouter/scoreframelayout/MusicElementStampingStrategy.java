package com.xenoage.zong.musiclayout.layouter.scoreframelayout;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.Range.range;
import static com.xenoage.zong.core.music.format.SP.sp;

import java.awt.Color;

import com.xenoage.pdlib.PVector;
import com.xenoage.zong.core.music.Globals;
import com.xenoage.zong.core.music.beam.Beam;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.chord.StemDirection;
import com.xenoage.zong.core.music.time.CommonTime;
import com.xenoage.zong.core.music.util.DurationInfo;
import com.xenoage.zong.musiclayout.Constants;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.cache.util.OpenBeamMiddleStem;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.util.ChordStampings;
import com.xenoage.zong.musiclayout.notations.ChordNotation;
import com.xenoage.zong.musiclayout.notations.ClefNotation;
import com.xenoage.zong.musiclayout.notations.NormalTimeNotation;
import com.xenoage.zong.musiclayout.notations.RestNotation;
import com.xenoage.zong.musiclayout.notations.TraditionalKeyNotation;
import com.xenoage.zong.musiclayout.notations.chord.AccidentalAlignment;
import com.xenoage.zong.musiclayout.notations.chord.AccidentalsAlignment;
import com.xenoage.zong.musiclayout.notations.chord.ArticulationAlignment;
import com.xenoage.zong.musiclayout.notations.chord.ArticulationsAlignment;
import com.xenoage.zong.musiclayout.notations.chord.NoteAlignment;
import com.xenoage.zong.musiclayout.notations.chord.NotesAlignment;
import com.xenoage.zong.musiclayout.notations.chord.StemAlignment;
import com.xenoage.zong.musiclayout.stampings.AccidentalStamping;
import com.xenoage.zong.musiclayout.stampings.ArticulationStamping;
import com.xenoage.zong.musiclayout.stampings.ClefStamping;
import com.xenoage.zong.musiclayout.stampings.CommonTimeStamping;
import com.xenoage.zong.musiclayout.stampings.FlagsStamping;
import com.xenoage.zong.musiclayout.stampings.KeySignatureStamping;
import com.xenoage.zong.musiclayout.stampings.LegerLineStamping;
import com.xenoage.zong.musiclayout.stampings.NormalTimeStamping;
import com.xenoage.zong.musiclayout.stampings.NoteheadStamping;
import com.xenoage.zong.musiclayout.stampings.ProlongationDotStamping;
import com.xenoage.zong.musiclayout.stampings.RestStamping;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.Stamping;
import com.xenoage.zong.musiclayout.stampings.StemStamping;


/**
 * Strategy to create stampings for chords, rests,
 * clefs, keys and time signatures.
 * 
 * @author Andreas Wenger
 */
public class MusicElementStampingStrategy
	implements ScoreLayouterStrategy
{
	
	
	/**
	 * Creates stampings for the given chord.
	 * Beams, ties, slurs and lyrics are not handled here,
	 * those must be handled by other strategies.
	 */
	public ChordStampings createChordStampings(ChordNotation chordNot,
		float positionX, StaffStamping staffStamping, Globals globals)
	{
		float interlineSpace = staffStamping.getInterlineSpace();
		Chord chord = chordNot.getMusicElement();
		
		PVector<NoteheadStamping> noteheads = pvec();
		PVector<LegerLineStamping> legerLines = pvec();
		PVector<ProlongationDotStamping> dots = pvec();
		PVector<AccidentalStamping> accidentals = pvec();
		PVector<ArticulationStamping> articulations = pvec();
		FlagsStamping flags = null;
		StemStamping stem = null;
		OpenBeamMiddleStem openStem = null;
		
		//left-suspended chord? then move chord to the left by the width of a notehead
		float leftNoteX = positionX;
		NotesAlignment notesAlign = chordNot.getNotesAlignment();
		float leftSuspendedWidth = (notesAlign.hasLeftSuspendedNotes() ?
			notesAlign.getNoteheadWidth() * staffStamping.getInterlineSpace() : 0);
		leftNoteX -= leftSuspendedWidth;
		
    //leger lines
    NotesAlignment cna = chordNot.getNotesAlignment();
    int topNoteLinePosition = cna.getNoteAlignment(
      cna.getNotesCount() - 1).getLinePosition();
    float topNoteOffset = cna.getNoteAlignment(
      cna.getNotesCount() - 1).getOffset();
    int bottomNoteLinePosition = cna.getNoteAlignment(0).getLinePosition();
    float bottomNoteOffset = cna.getNoteAlignment(0).getOffset();
    int staffLines = staffStamping.getLinesCount();
    if (topNoteLinePosition >= staffLines * 2)
    {
      //create leger lines above the staff
      for (int line = staffLines * 2; line <= topNoteLinePosition; line+=2)
      {
        LegerLineStamping legerLineSt = new LegerLineStamping(staffStamping, chord,
          //TODO: 0.5f = half notehead width
        	leftNoteX + (topNoteOffset + 0.5f) * interlineSpace, line);
        legerLines = legerLines.plus(legerLineSt);
      }
    }
    if (bottomNoteLinePosition <= -2)
    {
      //create leger lines below the staff
      for (int line = -2; line >= bottomNoteLinePosition; line-=2)
      {
        LegerLineStamping legerLineSt = new LegerLineStamping(staffStamping, chord,
          //TODO: 0.5f = half notehead width
        	leftNoteX + (bottomNoteOffset + 0.5f) * interlineSpace, line);
        legerLines = legerLines.plus(legerLineSt);
      }
    }
    
    //stem
    float stemEndPos = 0;
    int flagsCount = DurationInfo.getFlagsCount(chord.getDuration());
    StemDirection csd = chordNot.getStemDirection();
    if (csd != StemDirection.None)
    {
    	//collect known information
    	float stemX = leftNoteX + cna.getStemOffset() * interlineSpace;
    	
    	//stamp all stems except middle-beam stems
    	Beam beam = globals.getBeams().get(chord);
    	boolean stampNow = false;
    	if (beam == null)
    	{
    		stampNow = true;
    	}
    	else
    	{
    		if (beam.getFirstWaypoint().getChord() == chord ||
    			beam.getLastWaypoint().getChord() == chord)
    		{
    			stampNow = true;
    		}
    	}
    	
    	//stamp or remember
    	if (stampNow)
    	{
    		//stamp it now
    		StemAlignment csa = chordNot.getStemAlignment();
	      float stemStartPos = csa.getStartLinePosition();
	      stemEndPos = csa.getEndLinePosition(); 
	      StemStamping stemSt = new StemStamping(staffStamping, chord,
	        stemX, stemStartPos, stemEndPos, -1 * csd.getSignum());
	      stem = stemSt;
    	}
    	else
    	{
    		//for beamed chords in the middle of a beam only: we do not know the end position yet,
    		//but remember it as an open stem
				openStem = new OpenBeamMiddleStem(staffStamping, chord, chordNot.getStemDirection(),
					stemX, cna.getBottomNoteAlignment().getLinePosition(),
					cna.getTopNoteAlignment().getLinePosition());
    	}
    }
    
    //type of notehead
    int noteheadType = NoteheadStamping.NOTEHEAD_WHOLE;
    DurationInfo.Type symbolType = DurationInfo.getNoteheadSymbolType(
      chord.getDuration());
    if (symbolType == DurationInfo.Type.Half)
      noteheadType = NoteheadStamping.NOTEHEAD_HALF;
    else if (symbolType == DurationInfo.Type.Quarter)
      noteheadType = NoteheadStamping.NOTEHEAD_QUARTER;
    
    //noteheads
    for (int iNote : range(cna.getNotesCount()))
    {
      NoteAlignment na = cna.getNoteAlignment(iNote);
      
      Color color = Color.black;
      //TEST: draw chords that are not part of voice 0 in blue
      //color = (chord.getMusicElement().getVoice().getMeasure().getVoices().get(0) ==
      //	chord.getMusicElement().getVoice() ? Color.black : Color.blue);
      
      NoteheadStamping noteSt = new NoteheadStamping(chord, noteheadType, color, staffStamping,
      	sp(leftNoteX + na.getOffset() * interlineSpace, na.getLinePosition()), NoteheadStamping.SIDE_LEFT, 1);
      noteheads = noteheads.plus(noteSt); 
    }
    
    //flags (only drawn if there is no beam)
    Beam beam = globals.getBeams().get(chord);
    if (beam == null && flagsCount > 0)
    {
      int flag = (csd == StemDirection.Up ?
        FlagsStamping.FLAG_DOWN : FlagsStamping.FLAG_UP);
      flags = new FlagsStamping(flag, flagsCount, staffStamping, chord,
      	sp(leftNoteX + cna.getStemOffset() * interlineSpace, stemEndPos));
    }
    
    //accidentals
    AccidentalsAlignment caa = chordNot.getAccidentalsAlignment();
    if (caa != null)
    {
	    for (int iAcc = 0; iAcc < caa.getAccidentals().size(); iAcc++)
	    {
	      AccidentalAlignment aa = caa.getAccidentals().get(iAcc);
	      AccidentalStamping accSt = new AccidentalStamping(chord, aa.getType(), staffStamping,
	      	sp(positionX + (aa.getOffset() - chordNot.getWidth().getFrontGap()
	          +0.5f /* 0.5f: half accidental width - TODO */) * interlineSpace,
	        aa.getLinePosition()), 1);
	      accidentals = accidentals.plus(accSt);
	    }
    }
    
    //dots
    int[] dotPositions = cna.getDotPositions();
    int dotsPerNote = cna.getDotsPerNoteCount();
    for (int iNote = 0; iNote < dotPositions.length; iNote++)
    {
      for (int iDot = 0; iDot < dotsPerNote; iDot++)
      {
        ProlongationDotStamping dotSt = new ProlongationDotStamping(staffStamping, chord,
          sp(leftNoteX + cna.getDotsOffset(iDot) * interlineSpace, dotPositions[iNote]));
        dots = dots.plus(dotSt);
      }
    }
    
    //articulations
    ArticulationsAlignment cara = chordNot.getArticulationsAlignment();
    if (cara != null)
    {
	    for (int iArt = 0; iArt < cara.getArticulations().length; iArt++)
	    {
	      ArticulationAlignment ara = cara.getArticulations()[iArt];
	      ArticulationStamping araSt = new ArticulationStamping(chordNot.getMusicElement(),
	      	ara.getType(), staffStamping,
	      	sp(leftNoteX + (ara.getXOffsetIS() + (Constants.WIDTH_QUARTER / 2)) * interlineSpace, //TODO
	      	ara.getYLP()), 1);
	      articulations = articulations.plus(araSt);
	    }
    }
    
    return new ChordStampings(positionX, staffStamping, noteheads, legerLines, dots,
    	accidentals, articulations, flags, stem, openStem);
	}
	
	
	/**
   * Creates a stamping for the given rest.
   */
  public RestStamping createRestStamping(RestNotation rest, float positionX,
  	StaffStamping staff)
  {
    return new RestStamping(rest.getMusicElement(),
    	DurationInfo.getRestType(rest.getMusicElement().getDuration()),
    	staff, positionX, 1); 
  }
  
  
  /**
   * Creates a stamping for the given clef.
   */
  public ClefStamping createClefStamping(ClefNotation clef, float positionX, StaffStamping staff)
  {
    return new ClefStamping(
      clef.getMusicElement(), staff, positionX, clef.getScaling());
  }
  
  
  /**
   * Creates a stamping for the given key signature.
   */
  public KeySignatureStamping createKeyStamping(TraditionalKeyNotation key, float positionX, StaffStamping staff)
  {
    return new KeySignatureStamping(
      key.getMusicElement(), key.getLinePositionC4(), key.getLinePositionMin(), positionX, staff);
  }
  
  
  /**
   * Creates a stamping for the given time signature.
   */
  public Stamping createTimeStamping(NormalTimeNotation time, float positionX, StaffStamping staff)
  {
  	if (time.getMusicElement() instanceof CommonTime)
  	{
  		return new CommonTimeStamping((CommonTime) time.getMusicElement(), positionX, staff);
  	}
  	else
  	{
	    return new NormalTimeStamping(
	      time.getMusicElement(), positionX, staff,
	      time.getNumeratorOffset(), time.getDenominatorOffset(), time.getDigitGap());
  	}
  }
	

}
