/**
 * 
 */
package com.xenoage.zong.io.midi.out;

import com.xenoage.zong.data.ScorePosition;


/**
 * This interface is responsable to give the event of the MIDIOutput back to the
 * program.
 * 
 * @author Uli Teschemacher
 */
public interface PlaybackListener
{

	public void playbackAtScorePosition(ScorePosition position);
	
	/**
	 * Returns the next {@link ScorePosition} of the position where the playback stopped.
	 * @param position
	 */
	public void playbackStopped(ScorePosition position);
	
	public void playbackAtEnd();
}
