package com.xenoage.zong.io.midi.out;

import java.util.Timer;
import java.util.TimerTask;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.Synthesizer;

import com.xenoage.zong.data.instrument.Instrument;
import com.xenoage.zong.data.instrument.PitchedInstrument;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.Pitch;


/**
 * This class provides the playback functions for simple chords. 
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class MidiChordPlayer
{

	private MidiChannel channel;
	private byte defaultvelocity = 60;
	private int duration = 300;


	public MidiChordPlayer()
	{
		//TODO: do we really have to search for a free channel? - i mean: do
		//we playback a score the same time when we play a chord?
		Synthesizer synth = SynthManager.getSynthesizer();
		for (int midichannel = 0; midichannel < 16; midichannel++)
		{
			channel = synth.getChannels()[midichannel];
			if (channel != null)
			{
				break;
			}
			if (midichannel == 15 && channel == null)
			{
				// No free midichannel => Now we have a Problem
				// TODO no free midichannel
			}
		}
	}


	/**
	 * Plays a single note with the default velocity.
	 * 
	 */
	public void playChord(Chord chord, Instrument instrument)
	{
		playChord(chord, instrument, defaultvelocity);
	}


	/**
	 * Plays a single note.
	 * 
	 */
	public void playChord(Chord chord, Instrument instrument,
		byte velocity)
	{
		for (int i = 0; i < chord.getPitches().length; i++)
		{
			for (Pitch pitch : chord.getPitches())
			{
				playNote(pitch, instrument, velocity);
			}
		}
	}


	/**
	 * Plays a single note with the default velocity.
	 */
	public void playNote(Pitch pitch, Instrument instrument)
	{
		playNote(pitch, instrument, defaultvelocity);
	}


	/**
	 * Plays a single note.
	 * 
	 */
	public void playNote(Pitch pitch, Instrument instrument, byte velocity)
	{
		if (instrument instanceof PitchedInstrument)
			setMidiprogram(((PitchedInstrument) instrument).getMidiProgram());
		int midipitch = MidiTools.getNoteNumberFromPitch(pitch);
		channel.noteOn(midipitch, velocity);
		final Pitch p = pitch;
		new Timer().schedule(new TimerTask()
		{

			public void run()
			{
				stopSingleNote(p);
			}
		}, duration);
	}


	/**
	 * Stops the playback of the note with the given {@link Pitch}.
	 */
	public void stopSingleNote(Pitch pitch)
	{
		channel.noteOff(MidiTools.getNoteNumberFromPitch(pitch));
	}


	/**
	 * Stops all currently played notes.
	 */
	public void stopAll()
	{
		channel.allNotesOff();
	}


	/**
	 * Changes the midiprogram for the playback.
	 * @param midiprogram
	 */
	private void setMidiprogram(int midiprogram)
	{
		channel.programChange(midiprogram);
	}


	/**
	 * Gets the duration of the played notes.
	 * @return
	 */
	public int getDuration()
	{
		return duration;
	}


	/**
	 * Sets the duration of the played notes. It nothing is set, the default value (300) is used.
	 * @param duration
	 */
	public void setDuration(int duration)
	{
		this.duration = duration;
	}


}
