package com.xenoage.zong.io.midi.out;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.MidiUnavailableException;

import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.io.ScoreDocumentFileOutput;


/**
 * This class writes a Midi-file from a given {@link ScoreDocument}
 * 
 * @author Uli Teschemacher
 * 
 */
public class MidiScoreDocumentFileOutput
	implements ScoreDocumentFileOutput
{

	/**
	 * Gets the filename extension of the file format.
	 */
	public String getFileExtension()
	{
		return ".mid";
	}


	/**
	 * Gets the format name of the file format.
	 */
	public String getFileFormatName()
	{
		return "MIDI";
	}


	/**
	 * Writes the given {@link ScoreDocument} to the given file. If there is
	 * more than one ScoreFrameChain, multiple files are created.
	 */
	public void write(ScoreDocument doc, String filepath) throws IOException
	{
		for (int i = 0; i < doc.getScoreCount(); i++)
		{
			String filename;
			if (i != 0)
			{
				filename = filepath.replaceAll("(\\.)mid?$",i + ".mid");
			}
			else
			{
				filename = filepath;
			}
			try
			{
				MIDIOutput.getInstance().saveToMidiFile(doc.getScore(i), new File(filename));
			}
			catch (MidiUnavailableException ex)
			{
				throw new IOException(ex);
			}
		}
	}
	
}
