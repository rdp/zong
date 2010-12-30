package com.xenoage.zong.io.midi.out;

import java.io.File;
import java.io.IOException;

import com.xenoage.util.io.IO;
import com.xenoage.util.logging.Log;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.util.demo.ScoreRevolutionary;


/**
 * Class to test the {@link MidiWriter}.
 * 
 * @author Uli Teschemacher
 */
public class MidiWriterTry
{
	public static void main(String args[])
	{
		IO.initTest();
		Log.initNoLog();
		Score score = ScoreRevolutionary.createScore();
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
