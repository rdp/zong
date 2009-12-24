package com.xenoage.zong.io.midi.out;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.MidiSystem;

import com.xenoage.util.logging.Log;
import com.xenoage.zong.data.Score;


/**
 * This class offers the interface to write midi files from {@link Score}s.
 * 
 * @author Uli Teschemacher
 *
 */
public class MidiWriter
{

	/**
	 * Saves the given {@link Score} to a MidiFile.
	 * @param score
	 * @param file
	 * @throws IOException
	 */
	public static void saveToMidiFile(Score score, File file) throws IOException
	{
		SequenceContainer container = MidiConverter.convertToSequence(score, false, false);
		int[] types = MidiSystem.getMidiFileTypes(container.getSequence());
		int type = 0;
		if (types.length != 0)
		{
			type = types[types.length - 1];
		}
		MidiSystem.write(container.getSequence(), type, file);
		Log.log(Log.MESSAGE, "Midi file written in format " + type + " to file " + file.getAbsolutePath());
	}
}
