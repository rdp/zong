/**
 * 
 */
package com.xenoage.zong.io.midi.out;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.error.ErrorProcessing;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.instrument.Instrument;
import com.xenoage.zong.data.instrument.PitchedInstrument;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.util.logging.Log;


/**
 * The MIDIOutput class is responsible for playing Midi Music. All Signals are
 * sent on Channel 0. If the Instrument is changed, all tones are stopped.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
@Deprecated public class MIDIOutput
	implements ControllerEventListener
{

	private int midiprogram = 0;
	private int velocity = 127;
	private MidiChannel channel;

	private static MIDIOutput instance;
	private Sequencer sequencer = null;
	private Synthesizer synth;

	private PlaybackListener listener = null;
	private boolean unMuteMetronome = false;
	private SequenceContainer sequenceContainer;


	private MIDIOutput()
		throws MidiUnavailableException
	{
		/* OBSOLETE
		 * Changed by Andi, because the following lines didn't work any
		 * more within the Applet on Andi's computer. Don't know yet, why (TODO).
		 */
		/*
		sequencer = MidiSystem.getSequencer();
		// Transmitter trans = sequencer.getTransmitter();
		synth = MidiSystem.getSynthesizer();
		// Receiver rcvr = synth.getReceiver();
		sequencer.open();
		// trans.setReceiver(rcvr);
		synth.open();
		*/
    sequencer = MidiSystem.getSequencer(false);
    synth = MidiSystem.getSynthesizer();
    Transmitter seqTransmitter = sequencer.getTransmitter();
    seqTransmitter.setReceiver(synth.getReceiver());
    synth.open();
    sequencer.open();


		// Java Sound is only able to use listeners for controllerevents,
		// but not for normal note-on events. So we add a control-event on
		// every position of a note-on event. Control-event number 119 is
		// used, because it is undefined in the midi-documentation and so it
		// doesn't affect anything.
		// See http://www.midi.org/techspecs/midimessages.php
		int controllers[] = { 119 };
		sequencer.addControllerEventListener(this, controllers);
		// For the metronome control event 118 is used. It doesn't mean
		// anything and we are able to seperate the events.
		int controllersMetronome[] = { 118 };
		sequencer.addControllerEventListener(this, controllersMetronome);
		// For the playbackAtEnd control event 117 is used.
		int controllersplaybackAtEnd[] = { 117 };
		sequencer.addControllerEventListener(this, controllersplaybackAtEnd);


		// System.out.println(synth.getDeviceInfo().getName());
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
		channel.programChange(midiprogram);
	}


	@Override
	protected void finalize() throws Throwable
	{
		sequencer.close();
		synth.close();
	}


	/**
	 * Gets the only instance of this class.
	 * Can throw {@link MidiUnavailableException} when the connection
	 * to the output device failed.
	 */
	public static MIDIOutput getInstance()
		throws MidiUnavailableException
	{
		if (instance == null)
		{
			instance = new MIDIOutput();
		}
		return instance;
	}
	
	
	/**
	 * Gets the only instance of this class.
	 * Instead of throwing an exception when the connection to the output
	 * device failed, the error is reported to the given {@link ErrorProcessing}.
	 */
	public static MIDIOutput getInstanceWithErr(ErrorProcessing err)
	{
		if (instance == null)
		{
			try
			{
				instance = new MIDIOutput();
			}
			catch (MidiUnavailableException ex)
			{
				err.report(ErrorLevel.Warning, "The MIDI device is not available!");
			}
		}
		return instance;
	}


	/**
	 * Plays a single note.
	 * 
	 * @param pitch
	 */
	public void playSingleChord(Chord chord, int midiprogram, int velocity)
	{
		stopPlayback();
		for (int i = 0; i < chord.getPitches().length; i++)
		{
			for (Pitch pitch : chord.getPitches())
			{
				playSingleNote(pitch);
			}
		}
	}


	/**
	 * Plays a single note.
	 * 
	 * @param pitch
	 */
	public void playSingleNote(Pitch pitch)
	{
		stopPlayback();
		int midipitch = MidiTools.getNoteNumberFromPitch(pitch);
		channel.noteOn(midipitch, velocity);
		final Pitch p = pitch;
		new Timer().schedule(new TimerTask()
		{

			public void run()
			{
				stopSingleNote(p);
			}
		}, 300);
		// TODO stop playback of current sequence
	}


	public void setMidiprogram(int program)
	{
		channel.allNotesOff();
		this.midiprogram = program;
		channel.programChange(midiprogram);
	}


	public void setInstrument(Instrument instrument)
	{
		if (instrument instanceof PitchedInstrument)
			setMidiprogram(((PitchedInstrument) instrument).getMidiProgram());
	}


	public void stopSingleNote(Pitch pitch)
	{
		channel.noteOff(MidiTools.getNoteNumberFromPitch(pitch));
	}


	/**
	 * Plays the {@link Score} from the beginning.
	 * @param score the score to be played
	 */
	@Deprecated
	public void playScore(Score score, PlaybackListener listener, int tempo)
	{
		ScorePosition position = new ScorePosition(0, 0, new Fraction(0), 0);
		playScore(score, position, listener, tempo);
	}


	/**
	 * Plays the {@link Score} from the given {@link ScorePosition}.
	 * @param score the score to be playes
	 * @param position the startposition
	 */
	@Deprecated
	public void playScore(Score score, ScorePosition position, PlaybackListener listener,
		int tempo)
	{
		this.listener = listener;
		// TODO start from position
		SequenceContainer container = MidiConverter.convertToSequence(score, true, true);
		long starttick = calculateTickFromScorePosition(position, container
			.getMeasureStartTicks(), container.getSequence().getResolution());
		this.sequenceContainer = container;
		playSequence(container.getSequence(), starttick, tempo);
	}


	/**
	 * stops the current playback
	 */
	public void stopPlayback()
	{
		sequencer.stop();
		if (sequencer.isRunning())
		{
			listener.playbackStopped(sequenceContainer.getScorePositionTicks().getFirst()
				.get2());
		}
	}


	private void playSequence(Sequence sequence, long starttick, int tempo)
	{
		if (sequencer == null)
			return;
		try
		{
			sequencer.setSequence(sequence);
			sequencer.setTickPosition(starttick);
			sequencer.setTempoInBPM(tempo);
			sequencer.start();
		}
		catch (InvalidMidiDataException e)
		{
			e.printStackTrace();
		}

	}


	/**
	 * Saves the given {@link Score} to a MidiFile.
	 * @param score
	 * @param file
	 * @throws IOException
	 */
	@Deprecated
	public void saveToMidiFile(Score score, File file) throws IOException
	{
		SequenceContainer container = MidiConverter
			.convertToSequence(score, false, false);
		int[] types = MidiSystem.getMidiFileTypes(container.getSequence());
		int type = 0;
		if (types.length != 0)
		{
			type = types[types.length - 1];
		}
		MidiSystem.write(container.getSequence(), type, file);
		Log.log(Log.MESSAGE, "Midi file written in format " + type);
	}


	/**
	 * The Method which catches the ControllerChangedEvent from the sequencer.
	 * The Method decides, which {@link ScorePosition} is the right on and
	 * notifies the listener.
	 */
	public void controlChange(ShortMessage message)
	{
		LinkedList<Tuple2<Long, ScorePosition>> scorePositionTicks = sequenceContainer
			.getScorePositionTicks();
		if (message.getData1() == 119)
		{
			// calls the listener with the most actual tick
			long currentTick = sequencer.getTickPosition();
			if (scorePositionTicks.getFirst().get1() > currentTick)
			{
				return;
			}
			while (scorePositionTicks.get(1).get1() <= currentTick)
			{
				scorePositionTicks.removeFirst();
			}
			ScorePosition pos = scorePositionTicks.getFirst().get2();
			if (listener != null)
			{
				listener.playbackAtScorePosition(pos);
			}
			scorePositionTicks.removeFirst();
		}
		else if (message.getData1() == 118)
		{
			if (unMuteMetronome)
			{
				enableMetronome(true);
				unMuteMetronome = false;
			}
		}
		else if (message.getData1() == 117)
		{
			listener.playbackAtEnd();
		}
	}


	public void setMetronomeEnabled(boolean metronomeEnabled)
	{
		if (metronomeEnabled)
		{
			unMuteMetronome = true;
		}
		else
		{
			enableMetronome(false);
		}
	}


	private void enableMetronome(boolean enable)
	{
		int metronomeBeatTrackNumber = sequenceContainer.getMetronomeTrackNumber();
		sequencer.setTrackMute(metronomeBeatTrackNumber, !enable);
	}


	public boolean getMetronomeEnabled()
	{
		int metronomeBeatTrackNumber = sequenceContainer.getMetronomeTrackNumber();
		return !sequencer.getTrackMute(metronomeBeatTrackNumber);
	}


	private long calculateTickFromScorePosition(ScorePosition pos,
		ArrayList<Long> measureTicks, int resolution)
	{
		if (pos == null)
		{
			return 0;
		}
		else
		{
			return measureTicks.get(pos.getMeasure())
				+ MidiConverter.calculateTickFromFraction(pos.getBeat(), resolution);
		}
	}
	
	
	/**
	 * All instruments are unmuted.
	 */
	public void tutti()
	{
		for(int track : sequenceContainer.getStaffTracks())
		{
			sequencer.setTrackMute(track, false);
		}
	}
	
	/**
	 * Only the given tracks are played.
	 */
	public void solo(int[] tracks)
	{
		ArrayList<Integer> staffTracks = sequenceContainer.getStaffTracks();
		for (int a = 0; a < staffTracks.size(); a++ )
		{
			boolean mute = true;
			for (int i : tracks)
			{
				if (a == i)
				{
					mute = false;
					break;
				}
				else
				{
					mute = true;
				}
			}
			sequencer.setTrackMute(staffTracks.get(a), mute);
		}		
	}
}
