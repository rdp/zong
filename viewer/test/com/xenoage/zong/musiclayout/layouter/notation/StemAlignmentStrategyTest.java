package com.xenoage.zong.musiclayout.layouter.notation;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.music.ChordData;
import com.xenoage.zong.data.music.MusicContext;
import com.xenoage.zong.data.music.Note;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.zong.data.music.StemDirection;
import com.xenoage.zong.musiclayout.notations.chord.ChordLinePositions;
import com.xenoage.zong.musiclayout.notations.chord.NotesAlignment;
import com.xenoage.zong.musiclayout.notations.chord.StemAlignment;


/**
 * Test class for the {@link StemAlignmentStrategy}.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class StemAlignmentStrategyTest
{
  
	StemAlignmentStrategy strategy = new StemAlignmentStrategy();
	
	StemDirectionStrategy csdStrategy = new StemDirectionStrategy();
	NotesAlignmentStrategy cnaStrategy = new NotesAlignmentStrategy();
	
	
	@Test public void computeStemAlignmentTest()
	{
		/* TODO
		Pitch pitch;
		
		pitch = new Pitch('B',0,3);
		testPitch(pitch,-3,4); 

		pitch = new Pitch('C',0,4);
		testPitch(pitch,-2,5); 

		pitch = new Pitch('D',0,4);
		testPitch(pitch,-1,6); 

		pitch = new Pitch('E',0,4);
		testPitch(pitch,0,7); 

		pitch = new Pitch('F',0,4);
		testPitch(pitch,1,8); 

		pitch = new Pitch('G',0,4);
		testPitch(pitch,2,9); 

		pitch = new Pitch('A',0,4);
		testPitch(pitch,3,10);
		
		
		//Stem down
		pitch = new Pitch('B',0,5);
		testPitch(pitch,11,4); 

		pitch = new Pitch('A',0,5);
		testPitch(pitch,10,3); 

		pitch = new Pitch('G',0,5);
		testPitch(pitch,9,2); 

		pitch = new Pitch('F',0,5);
		testPitch(pitch,8,1); 

		pitch = new Pitch('E',0,5);
		testPitch(pitch,7,0); 

		pitch = new Pitch('D',0,5);
		testPitch(pitch,6,-1); 

		pitch = new Pitch('C',0,5);
		testPitch(pitch,5,-2); 

		pitch = new Pitch('B',0,4);
		testPitch(pitch,4,-3); 

		
		//longer stems
		pitch = new Pitch('C',0,6);
		testPitch(pitch,12,4); 

		pitch = new Pitch('E',0,6);
		testPitch(pitch,14,4); 

		pitch = new Pitch('F',0,6);
		testPitch(pitch,15,4); 

		pitch = new Pitch('A',0,6);
		testPitch(pitch,17,4); 

		pitch = new Pitch('A',0,3);
		testPitch(pitch,-4,4); 

		pitch = new Pitch('F',0,3);
		testPitch(pitch,-6,4); 

		pitch = new Pitch('E',0,3);
		testPitch(pitch,-7,4); 

		pitch = new Pitch('C',0,3);
		testPitch(pitch,-9,4); 

		//Some Chords...
		Pitch[] pitches = new Pitch[2];
		pitches[0] = new Pitch('C',0,3);
		pitches[1] = new Pitch('F',0,3);
		testPitch(pitches,-9,4); 

		pitches = new Pitch[2];
		pitches[0] = new Pitch('C',0,3);
		pitches[1] = new Pitch('F',0,4);;
		testPitch(pitches,-9,8); 
		*/
	}
	
	private void testPitch(Pitch pitch, float start, float end)
	{
		Pitch[] pitches = new Pitch[1];
		pitches[0] = pitch;
		testPitch(pitches, start, end);
	}
	
	private void testPitch(Pitch[] pitches, float start, float end)
	{
		MusicContext context = new MusicContext();
		ChordData chord;
		NotesAlignment chordNotesAlignment;
		StemAlignment chordStemAlignment;
		
		
		Fraction fraction = new Fraction(1,1);
		chord = new ChordData(Note.createNotes(pitches), fraction);
		ChordLinePositions linepositions = new ChordLinePositions(chord, context);
		StemDirection stemDirection = csdStrategy.computeStemDirection(linepositions, context.getLines());
		chordNotesAlignment = cnaStrategy.computeNotesAlignment(chord, stemDirection, context);
		chordStemAlignment = strategy.computeStemAlignment(null, chordNotesAlignment, stemDirection, 5);
		assertEquals(start,chordStemAlignment.getStartLinePosition());
		assertEquals(end,chordStemAlignment.getEndLinePosition());
	}
	
	
}