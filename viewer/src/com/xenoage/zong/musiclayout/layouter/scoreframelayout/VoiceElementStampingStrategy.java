package com.xenoage.zong.musiclayout.layouter.scoreframelayout;

import java.awt.Color;

import com.xenoage.zong.data.music.Beam;
import com.xenoage.zong.data.music.BeamWaypoint;
import com.xenoage.zong.data.music.DurationInfo;
import com.xenoage.zong.data.music.StemDirection;
import com.xenoage.zong.data.music.format.SP;
import com.xenoage.zong.musiclayout.Constants;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.cache.util.BeamedStemStampings.OpenBeamMiddleStem;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.util.ChordStampings;
import com.xenoage.zong.musiclayout.notations.ChordNotation;
import com.xenoage.zong.musiclayout.notations.RestNotation;
import com.xenoage.zong.musiclayout.notations.chord.AccidentalAlignment;
import com.xenoage.zong.musiclayout.notations.chord.AccidentalsAlignment;
import com.xenoage.zong.musiclayout.notations.chord.ArticulationAlignment;
import com.xenoage.zong.musiclayout.notations.chord.ArticulationsAlignment;
import com.xenoage.zong.musiclayout.notations.chord.NoteAlignment;
import com.xenoage.zong.musiclayout.notations.chord.NotesAlignment;
import com.xenoage.zong.musiclayout.notations.chord.StemAlignment;
import com.xenoage.zong.musiclayout.stampings.AccidentalStamping;
import com.xenoage.zong.musiclayout.stampings.ArticulationStamping;
import com.xenoage.zong.musiclayout.stampings.FlagsStamping;
import com.xenoage.zong.musiclayout.stampings.LegerLineStamping;
import com.xenoage.zong.musiclayout.stampings.NoteheadStamping;
import com.xenoage.zong.musiclayout.stampings.ProlongationDotStamping;
import com.xenoage.zong.musiclayout.stampings.RestStamping;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.StemStamping;


/**
 * Strategy to create stampings for chords and rests.
 * 
 * @author Andreas Wenger
 */
public class VoiceElementStampingStrategy
	implements ScoreLayouterStrategy
{
	
	
	/**
	 * Creates stampings for the given chord.
	 * Beams, ties, slurs and lyrics are not handled here,
	 * those must be handled by other strategies.
	 * 
	 * //LAYOUT-PERFORMANCE (needed 2 of 60 seconds)
	 */
	public ChordStampings createChordStampings(ChordNotation chord,
		float positionX, StaffStamping staff)
	{
		ChordStampings ret = new ChordStampings(positionX, staff);
		float interlineSpace = staff.getInterlineSpace();
		
    //leger lines
    //TODO: suspended notes
    NotesAlignment cna = chord.getNotesAlignment();
    int topNoteLinePosition = cna.getNoteAlignment(
      cna.getNotesCount() - 1).getLinePosition();
    float topNoteOffset = cna.getNoteAlignment(
      cna.getNotesCount() - 1).getOffset();
    int bottomNoteLinePosition = cna.getNoteAlignment(0).getLinePosition();
    float bottomNoteOffset = cna.getNoteAlignment(0).getOffset();
    int staffLines = staff.getLinesCount();
    if (topNoteLinePosition >= staffLines * 2)
    {
      //create leger lines above the staff
      for (int line = staffLines * 2; line <= topNoteLinePosition; line+=2)
      {
        LegerLineStamping legerLineSt = new LegerLineStamping(staff, chord.getMusicElement(),
          //TODO: 0.5f = half notehead width
          positionX + (topNoteOffset + 0.5f) * interlineSpace, line);
        ret.legerLines.add(legerLineSt);
      }
    }
    if (bottomNoteLinePosition <= -2)
    {
      //create leger lines below the staff
      for (int line = -2; line >= bottomNoteLinePosition; line-=2)
      {
        LegerLineStamping legerLineSt = new LegerLineStamping(staff, chord.getMusicElement(),
          //TODO: 0.5f = half notehead width
        	positionX + (bottomNoteOffset + 0.5f) * interlineSpace, line);
        ret.legerLines.add(legerLineSt);
      }
    }
    
    //stem
    float stemEndPos = 0;
    int flagsCount = DurationInfo.getFlagsCount(chord.getMusicElement().getDuration());
    StemDirection csd = chord.getStemDirection();
    if (csd != StemDirection.None)
    {
    	//collect known information
    	float stemX = positionX + cna.getStemOffset() * interlineSpace;
    	
    	//stamp all stems except middle-beam stems
    	BeamWaypoint beamWaypoint = chord.getMusicElement().getBeamWaypoint();
    	boolean stampNow = false;
    	if (beamWaypoint == null)
    	{
    		stampNow = true;
    	}
    	else
    	{
    		Beam beam = beamWaypoint.getBeam();
    		if (beam.getFirstWaypoint() == beamWaypoint || beam.getLastWaypoint() == beamWaypoint)
    		{
    			stampNow = true;
    		}
    	}
    	
    	//stamp or remember
    	if (stampNow)
    	{
    		//stamp it now
    		StemAlignment csa = chord.getStemAlignment();
	      float stemStartPos = csa.getStartLinePosition();
	      stemEndPos = csa.getEndLinePosition(); 
	      StemStamping stemSt = new StemStamping(staff, chord.getMusicElement(),
	        stemX, stemStartPos, stemEndPos, -1 * csd.getSignum());
	      ret.stem = stemSt;
    	}
    	else
    	{
    		//for beamed chords in the middle of a beam only: we do not know the end position yet,
    		//but remember it as an open stem
				OpenBeamMiddleStem openStem = new OpenBeamMiddleStem();
				openStem.staff = staff;
				openStem.chord = chord.getMusicElement();
				openStem.stemDirection = chord.getStemDirection();
				openStem.positionX = stemX;
				openStem.bottomNoteLP = cna.getBottomNoteAlignment().getLinePosition();
				openStem.topNoteLP = cna.getTopNoteAlignment().getLinePosition();
				ret.openStem = openStem;
    	}
    }
    
    //type of notehead
    int noteheadType = NoteheadStamping.NOTEHEAD_WHOLE;
    DurationInfo.Type symbolType = DurationInfo.getNoteheadSymbolType(
      chord.getMusicElement().getDuration());
    if (symbolType == DurationInfo.Type.Half)
      noteheadType = NoteheadStamping.NOTEHEAD_HALF;
    else if (symbolType == DurationInfo.Type.Quarter)
      noteheadType = NoteheadStamping.NOTEHEAD_QUARTER;
    
    //noteheads
    NoteheadStamping[] noteheads = new NoteheadStamping[cna.getNotesCount()];
    for (int iNote = 0; iNote < cna.getNotesCount(); iNote++)
    {
      NoteAlignment na = cna.getNoteAlignment(iNote);
      
      Color color = Color.black;
      ////OBSOLETE: test: draw chords that are not part of voice 0 in blue
      //color = (chord.getMusicElement().getVoice().getMeasure().getVoices().get(0) ==
      //	chord.getMusicElement().getVoice() ? Color.black : Color.blue);
      
      
      NoteheadStamping noteSt = new NoteheadStamping(chord.getMusicElement(), noteheadType, color, staff,
        new SP(positionX + na.getOffset() * interlineSpace, na.getLinePosition()), NoteheadStamping.SIDE_LEFT, 1);
      noteheads[iNote] = noteSt;
      ret.noteheads.add(noteSt); 
    }
    
    //flags (only drawn if there is no beam)
    if (chord.getMusicElement().getBeamWaypoint() == null && flagsCount > 0)
    {
      int flag = (csd == StemDirection.Up ?
        FlagsStamping.FLAG_DOWN : FlagsStamping.FLAG_UP);
      FlagsStamping flagsSt = new FlagsStamping(flag, flagsCount, staff, chord.getMusicElement(),
        new SP(positionX + cna.getStemOffset() * interlineSpace, stemEndPos));
      ret.flags = flagsSt;
    }
    
    //accidentals
    AccidentalsAlignment caa = chord.getAccidentalsAlignment();
    if (caa != null)
    {
	    for (int iAcc = 0; iAcc < caa.getAccidentals().length; iAcc++)
	    {
	      AccidentalAlignment aa = caa.getAccidentals()[iAcc];
	      AccidentalStamping accSt = new AccidentalStamping(chord.getMusicElement(), aa.getType(), staff,
	        new SP(positionX + (aa.getOffset() - chord.getWidth().getFrontGap()
	          +0.5f /* 0.5f: half accidental width - TODO */) * interlineSpace,
	        aa.getLinePosition()), 1);
	      ret.accidentals.add(accSt);
	    }
    }
    
    //dots
    int[] dots = cna.getDotPositions();
    int dotsPerNote = cna.getDotsPerNoteCount();
    for (int iNote = 0; iNote < dots.length; iNote++)
    {
      for (int iDot = 0; iDot < dotsPerNote; iDot++)
      {
        ProlongationDotStamping dotSt = new ProlongationDotStamping(staff, chord.getMusicElement(),
          new SP(positionX + cna.getDotsOffset(iDot) * interlineSpace, dots[iNote]));
        ret.dots.add(dotSt);
      }
    }
    
    //articulations
    ArticulationsAlignment cara = chord.getArticulationsAlignment();
    if (cara != null)
    {
	    for (int iArt = 0; iArt < cara.getArticulations().length; iArt++)
	    {
	      ArticulationAlignment ara = cara.getArticulations()[iArt];
	      ArticulationStamping araSt = new ArticulationStamping(chord.getMusicElement(),
	      	ara.getType(), staff,
	      	new SP(positionX + (ara.getXOffsetIS() + (Constants.WIDTH_QUARTER / 2)) * interlineSpace, //TODO
	      	ara.getYLP()), 1);
	      ret.articulations.add(araSt);
	    }
    }
    
    return ret;
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
	

}
