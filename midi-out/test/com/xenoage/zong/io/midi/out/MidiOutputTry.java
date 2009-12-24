package com.xenoage.zong.io.midi.out;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.MidiUnavailableException;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.zong.io.midi.out.MIDIOutput;
import com.xenoage.zong.util.demo.ScoreDemo;


/**
 * 
 * @author Uli Teschemacher
 */
public class MidiOutputTry
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
		throws MidiUnavailableException
	{

		Pitch pitch = new Pitch(2,0,4);
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

		Score score = ScoreDemo.createDemoScore32Measures();
		
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
		
		ScorePosition pos = new ScorePosition(0,2,new Fraction(1,4),0);
		MIDIOutput.getInstance().playScore(score,pos,null,120);
	}
}
