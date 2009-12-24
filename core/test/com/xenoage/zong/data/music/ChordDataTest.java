package com.xenoage.zong.data.music;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xenoage.util.math.Fraction;


/**
 * Tests for the {@link ChordData} class.
 * 
 * @author Andreas Wenger
 */
public class ChordDataTest
{
	
	@Test public void addPitch()
	{
		//chord: C4, D4, F4
		Pitch[] pitches = new Pitch[]{new Pitch(0, 0, 4), new Pitch(1, 0, 4), new Pitch(3, 0, 4)};
		ChordData chord = new ChordData(Note.createNotes(pitches), new Fraction(1));
		
		//add C3 to chord
		Pitch p = new Pitch(0, 0, 3);
		ChordData c = chord.addNote(new Note(p));
		assertEquals(p, c.getPitches()[0]);
		assertEquals(pitches[0], c.getPitches()[1]);
		assertEquals(pitches[1], c.getPitches()[2]);
		assertEquals(pitches[2], c.getPitches()[3]);
		
		//add E4 to chord
		p = new Pitch(2, 0, 4);
		c = chord.addNote(new Note(p));
		assertEquals(pitches[0], c.getPitches()[0]);
		assertEquals(pitches[1], c.getPitches()[1]);
		assertEquals(p, c.getPitches()[2]);
		assertEquals(pitches[2], c.getPitches()[3]);
		
		//add G4 to chord
		p = new Pitch(4, 0, 4);
		c = chord.addNote(new Note(p));
		assertEquals(pitches[0], c.getPitches()[0]);
		assertEquals(pitches[1], c.getPitches()[1]);
		assertEquals(pitches[2], c.getPitches()[2]);
		assertEquals(p, c.getPitches()[3]);
	}

}
