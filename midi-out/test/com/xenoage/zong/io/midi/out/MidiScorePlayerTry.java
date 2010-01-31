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
		SynthManager.init(false);
		Score score = ScoreDemo.createDemoScore32Measures();
		MidiScorePlayer player = new MidiScorePlayer();
		player.addPlaybackListener(new MidiScorePlayerTry());
		player.openScore(score);
		player.play();
		try { Thread.sleep(2000); } catch (InterruptedException ex) { }
		player.pause();
		System.out.println("Pause");
		try { Thread.sleep(2000); } catch (InterruptedException ex) { }
		player.play();
		try { Thread.sleep(3000); } catch (InterruptedException ex) { }
		player.stop();
		System.out.println("Stop");
		try { Thread.sleep(2000); } catch (InterruptedException ex) { }
		player.play();
		try { Thread.sleep(3000); } catch (InterruptedException ex) { }
		player.stop();
		//TODO: JVM runs forever - why?
	}

	
	@Override public void playbackAtEnd()
	{
	}

	@Override public void playbackAtScorePosition(ScorePosition position)
	{
		System.out.println(position.toString());
	}

	
	@Override public void playbackStopped(ScorePosition position)
	{
	}
	
}
