package com.xenoage.zong.io.midi.out;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;

import com.xenoage.util.lang.Tuple2;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.ScorePosition;


/**
 * This class offers the interface for MIDI playback in
 * the program to play a given {@link Score}.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class MidiScorePlayer
	implements ControllerEventListener
{

	private SequenceContainer sequenceContainer = null;
	private List<PlaybackListener> listeners = new LinkedList<PlaybackListener>();
	private boolean metronomeEnabled;
	private float volume = 0.7f;
	
	
	private static final int NOTE_ON_EVENT = 119;
	//NOTNEEDED private static final int METRONOME_EVENT = 118;
	private static final int PLAYBACK_END_EVENT = 117;


	/**
	 * Creates a new {@link MidiScorePlayer}.
	 */
	public MidiScorePlayer()
		throws MidiUnavailableException
	{
    setVolume(volume);
    SynthManager.removeAllControllerEventListeners();

		//Java Sound is only able to use listeners for controllerevents,
		//but not for normal note-on events. So we add a control-event on
		//every position of a note-on event. Control-event number 119 is
		//used, because it is undefined in the midi-documentation and so it
		//doesn't affect anything.
		//See http://www.midi.org/techspecs/midimessages.php
		int controllers[] = { NOTE_ON_EVENT };
    SynthManager.addControllerEventListener(this, controllers);
		
		/* NOTNEEDED
		//For the metronome control event 118 is used. It doesn't mean
		//anything and we are able to seperate the events.
		int controllersMetronome[] = { METRONOME_EVENT };
		SynthManager(this, controllersMetronome);
		*/
		
		//For the playbackAtEnd control event 117 is used.
		int controllersplaybackAtEnd[] = { PLAYBACK_END_EVENT };
		SynthManager.addControllerEventListener(this, controllersplaybackAtEnd);
	}
	
	
	/**
	 * Opens the given {@link Score} for playback.
	 */
	public void openScore(Score score)
	{
		stop();
		SequenceContainer container = MidiConverter.convertToSequence(score, true, false);
		this.sequenceContainer = container;
		try
		{
			SynthManager.getSequencer().setSequence(container.getSequence());
		}
		catch (InvalidMidiDataException ex)
		{
			//TODO: should never happen?
		}
	}
	
	
	/**
	 * Registers the given {@link PlaybackListener} which will be
	 * informed about playback events like the current position.
	 */
	public void addPlaybackListener(PlaybackListener listener)
	{
		if (!listeners.contains(listener))
			listeners.add(listener);
	}
	
	
	/**
	 * Unregisters the given {@link PlaybackListener}. 
	 */
	public void removePlaybackListener(PlaybackListener listener)
	{
		listeners.remove(listener);
	}
	
	
	/**
	 * Changes the position of the playback cursor to the given
	 * time in microseconds.
	 */
	public void setMicrosecondPosition(long ms)
	{
		SynthManager.getSequencer().setMicrosecondPosition(ms);
	}
	
	
	/**
	 * Changes the position of the playback cursor to the given
	 * {@link ScorePosition}.
	 */
	public void setScorePosition(ScorePosition sp)
	{
		long tickPosition = calculateTickFromScorePosition(sp,
			sequenceContainer.getMeasureStartTicks(), sequenceContainer.getSequence().getResolution());
		SynthManager.getSequencer().setTickPosition(tickPosition);
	}


	/**
	 * Starts playback at the current position.
	 */
	public void play()
	{
		Sequencer sequencer = SynthManager.getSequencer();
		if (sequencer.getSequence() != null)
			sequencer.start();
	}
	
	
	/**
	 * Stops the playback without resetting the
	 * current position.
	 */
	public void stop()
	{
		Sequencer sequencer = SynthManager.getSequencer();
		if (sequencer.isRunning())
		{
			sequencer.stop();
			for (PlaybackListener listener : listeners)
			{
				listener.playbackStopped(sequenceContainer.getScorePositionTicks().getFirst().get2());
			}
		}
	}
	
	
	public boolean getMetronomeEnabled()
	{
		return metronomeEnabled;
	}


	public void setMetronomeEnabled(boolean metronomeEnabled)
	{
		this.metronomeEnabled = metronomeEnabled;
		int metronomeBeatTrackNumber = sequenceContainer.getMetronomeTrackNumber();
		SynthManager.getSequencer().setTrackMute(metronomeBeatTrackNumber, !metronomeEnabled);
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
	 * The Method which catches the ControllerChangedEvent from the sequencer.
	 * The Method decides, which {@link ScorePosition} is the right one and
	 * notifies the listener.
	 */
	public void controlChange(ShortMessage message)
	{
		LinkedList<Tuple2<Long, ScorePosition>> scorePositionTicks = sequenceContainer
			.getScorePositionTicks();
		if (message.getData1() == NOTE_ON_EVENT)
		{
			// calls the listener with the most actual tick
			long currentTick = SynthManager.getSequencer().getTickPosition();
			//if playback is ahead: return nothing
			if (scorePositionTicks.getFirst().get1() > currentTick)
			{
				return;
			}
			//if the program hung up but the player continued, there programm would always be to late.
			//So the algorithm deletes all aruments before the current Element.
			while (scorePositionTicks.get(1).get1() <= currentTick)
			{
				scorePositionTicks.removeFirst();
			}
			ScorePosition pos = scorePositionTicks.getFirst().get2();
			for (PlaybackListener listener : listeners)
			{
				listener.playbackAtScorePosition(pos);
			}
			scorePositionTicks.removeFirst();
		}
		else if (message.getData1() == PLAYBACK_END_EVENT)
		{
			stop(); //stop to really ensure the end
			for (PlaybackListener listener : listeners)
			{
				listener.playbackAtEnd();
			}
		}
	}


	/**
	 * Gets the volume, which is a value between 0 (silent) and 1 (loud).
	 */
	public float getVolume()
	{
		return volume;
	}
  

	/**
	 * Sets the volume.
	 * @param volume  value between 0 (silent) and 1 (loud)
	 */
	public void setVolume(float volume)
	{
		this.volume = volume;
		MidiChannel[] channels = SynthManager.getSynthesizer().getChannels();

		//max should be 127, but we use 255 to allow more loudness (overdrive)
		//TODO: we do this because it works and 127 is quite quiet. but may we do it
		//according to the MIDI standard?
		int max = 255;

		for (int i = 0; i < channels.length; i++)
		{
			channels[i].controlChange(7, Math.round(volume * max));
		}
	}
	
	
	/**
	 * Returns true, if the playback cursor is at the end of the
	 * score, otherwise false.
	 */
	public boolean isPlaybackFinished()
	{
		Sequencer sequencer = SynthManager.getSequencer();
		return sequencer.getMicrosecondPosition() >= sequencer.getMicrosecondLength();
	}

	
	/**
	 * Gets the length of the current sequence in microseconds,
	 * or 0 if no score is loaded.
	 */
	public long getMicrosecondLength()
	{
		if (sequenceContainer == null)
			return 0;
		return sequenceContainer.getSequence().getMicrosecondLength();
	}
	
	
	/**
	 * Gets the current position within the sequence in microseconds,
	 * or 0 if no score is loaded.
	 */
	public long getMicrosecondPosition()
	{
		if (sequenceContainer == null)
			return 0;
		return SynthManager.getSequencer().getMicrosecondPosition();
	}
	
	
	public Sequence getSequence()
	{
		if (sequenceContainer != null)
			return sequenceContainer.getSequence();
		else
			return null;
	}
	

}
