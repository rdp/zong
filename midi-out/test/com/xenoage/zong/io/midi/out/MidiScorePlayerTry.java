package com.xenoage.zong.io.midi.out;

import javax.sound.midi.MidiUnavailableException;

import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.util.demo.ScoreRevolutionary;


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
		Score score = ScoreRevolutionary.createScore();
		SynthManager.init(false);
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

	@Override public void playbackAtMP(MP position)
	{
		System.out.println(position.toString());
	}

	
	@Override public void playbackStopped(MP position)
	{
	}
	
}
