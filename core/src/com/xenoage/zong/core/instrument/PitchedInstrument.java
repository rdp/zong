package com.xenoage.zong.core.instrument;

import com.xenoage.pdlib.Vector;
import com.xenoage.zong.core.music.Pitch;


/**
 * Pitched instrument, like piano or trumpet.
 * 
 * @author Andreas Wenger
 */
public final class PitchedInstrument
	extends Instrument
{
	
	private final int midiProgram;
	private final Transpose transpose;
	private final Pitch bottomPitch;
	private final Pitch topPitch;
	private final int polyphonic;
	
	
	/**
	 * Creates a new pitched instrument.
	 * @param id            the unique id of this instrument
	 * @param name          the international name, beginning with an uppercase letter
	 * @param abbreviation  the international abbreviation, beginning with an uppercase letter
	 * @param groups        the groups this instrument belongs to. null is also allowed.
	 * @param midiProgram   the midi program number, like 32 for "Acoustic Bass"
	 * @param transpose     transposition information
	 * @param bottomPitch   the bottommost playable note (in notation), or null
	 * @param topPitch      the topmost playable note (in notation), or null
	 * @param polyphonic    number of notes that can be played concurrently, or 0 for any number
	 */
	public PitchedInstrument(String id, String name, String abbreviation,
		Vector<InstrumentGroup> groups, int midiProgram, Transpose transpose,
		Pitch bottomPitch, Pitch topPitch, int polyphonic)
	{
		super(id, name, abbreviation, groups);
		if (midiProgram < 0 || midiProgram > 128)
		{
			throw new IllegalArgumentException("MIDI program must be between 0 and 127");
		}
		this.midiProgram = midiProgram;
		this.transpose = transpose;
		this.bottomPitch = bottomPitch;
		this.topPitch = topPitch;
		this.polyphonic = polyphonic;
	}
	
	
	/**
	 * Gets the MIDI program used for playback.
	 */
	public int getMidiProgram()
	{
		return midiProgram;
	}
	
	
	/**
	 * Gets the transposition information.
	 */
	public Transpose getTranspose()
	{
		return transpose;
	}
	
	
	/**
	 * Gets the bottommost playable note (in notation).
	 */
	public Pitch getBottomPitch()
	{
		return bottomPitch;
	}


	/**
	 * Gets the topmost playable note (in notation).
	 */
	public Pitch getTopPitch()
	{
		return topPitch;
	}


	/**
	 * Gets the number of notes that can be played at the same time with this instrument,
	 * or 0 if there is no limit.
	 */
	public int getPolyphonic()
	{
		return polyphonic;
	}

	
	/**
	 * Creates a new instrument based on this one.
	 */
	public PitchedInstrument deriveByMidiProgram(int midiProgram)
	{
		return new PitchedInstrument(id, name, abbreviation, groups,
			midiProgram, transpose, bottomPitch, topPitch, polyphonic);
	}
	
	
	

}
