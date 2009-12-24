/**
 * 
 */
package com.xenoage.zong.io.midi.out;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.instrument.Instrument;
import com.xenoage.zong.data.instrument.PitchedInstrument;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.ChordData;
import com.xenoage.zong.data.music.Note;
import com.xenoage.zong.data.music.Pitch;


/**
 * Test class for {@link MidiChordPlayer}.
 * 
 * @author Uli Teschemacher
 *
 */
public class MidiChordPlayerTry
{

	public static void main(String args[])
	{
		MidiChordPlayer player = new MidiChordPlayer();
		Instrument instrument = new PitchedInstrument("kl", "Klavier", "Kl", null, 1, 0,
			new Pitch(1, 0, 0), new Pitch(6, 0, 0), 12);

		Pitch pitch = new Pitch(2, 0, 4);

		player.playNote(pitch,instrument);
		sleep();
		
		Note[] notes = {new Note(new Pitch(2,0,4)), new Note(new Pitch(4,0,4))};
		ChordData data = new ChordData(notes, new Fraction(1));
		Chord chord = new Chord(data);
		player.playChord(chord, instrument);
		
		sleep();
		
		player.playChord(chord, instrument, (byte)127);
	}

	private static void sleep()
	{
		try
		{
			Thread.sleep(2000);
		}
		catch (Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
