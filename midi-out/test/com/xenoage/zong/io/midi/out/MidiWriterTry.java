/**
 * 
 */
package com.xenoage.zong.io.midi.out;

import java.io.File;
import java.io.IOException;

import com.xenoage.zong.data.Score;
import com.xenoage.zong.util.demo.ScoreDemo;


/**
 * Class to test the {@link MidiWriter}.
 * 
 * @author Uli Teschemacher
 *
 */
public class MidiWriterTry
{
	public static void main(String args[])
	{
		Score score = ScoreDemo.createDemoScore32Measures();
		File file = new File("test/com/xenoage/zong/io/midi/out/midiwritertry.midi");
		if(file.exists())
		{
			System.out.println(file.getAbsoluteFile());
		}
		try
		{
			MidiWriter.saveToMidiFile(score, file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
