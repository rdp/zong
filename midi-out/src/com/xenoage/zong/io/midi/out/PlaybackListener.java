package com.xenoage.zong.io.midi.out;

import com.xenoage.zong.core.music.MP;


/**
 * This interface is responsable to give the event of the
 * MIDIOutput back to the program.
 * 
 * @author Uli Teschemacher
 */
public interface PlaybackListener
{

	public void playbackAtMP(MP position);
	
	/**
	 * Returns the next {@link ScorePosition} of the position
	 * where the playback stopped.
	 * @param position
	 */
	public void playbackStopped(MP position);
	
	public void playbackAtEnd();
}
