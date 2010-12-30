package com.xenoage.zong.io.midi.out;

import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.core.music.Pitch.pi;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.MidiUnavailableException;

import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.Pitch;
import com.xenoage.zong.util.demo.ScoreRevolutionary;


/**
 * 
 * @author Uli Teschemacher
 */
@Deprecated public class MidiOutputTry
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
		throws MidiUnavailableException
	{

		Pitch pitch = pi(2,0,4);
		MIDIOutput.getInstance().playSingleNote(pitch);
		try
		{
			Thread.sleep(5000);
		}
		catch (Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Score score = ScoreRevolutionary.createScore();
		
		//Save file
		File file = new File("C:/Users/Uli/Desktop/demoscore.midi");
		try
		{
			MIDIOutput.getInstance().saveToMidiFile(score, file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		//Play Score
		
		MP pos = MP.mp(0,2,0, fr(1,4));
		MIDIOutput.getInstance().playScore(score,pos,null,120);
	}
}
