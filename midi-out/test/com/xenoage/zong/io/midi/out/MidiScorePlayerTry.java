/**
 * 
 */
package com.xenoage.zong.io.midi.out;

import javax.sound.midi.MidiUnavailableException;

import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.util.demo.ScoreDemo;


/**
 * Test class for {@link MidiScorePlayer}
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class MidiScorePlayerTry
	implements PlaybackListener
{
	
	public static void main(String args[])
		throws MidiUnavailableException
	{
		Score score = ScoreDemo.createDemoScore32Measures();
		MidiScorePlayer player = new MidiScorePlayer();
		player.openScore(score);
		player.play();
		try { Thread.sleep(2000); } catch (InterruptedException ex) { }
		player.stop();
		//TODO: JVM runs forever - why?
	}

	
	@Override public void playbackAtEnd()
	{
	}

	@Override public void playbackAtScorePosition(ScorePosition position)
	{
	}

	
	@Override public void playbackStopped(ScorePosition position)
	{
	}
	
}
