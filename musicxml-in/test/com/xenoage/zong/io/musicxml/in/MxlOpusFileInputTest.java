package com.xenoage.zong.io.musicxml.in;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.junit.Test;

import com.xenoage.util.filter.AllFilter;
import com.xenoage.zong.io.musicxml.opus.Opus;


/**
 * Test class for the {@link MxlOpusFileInput} class.
 * 
 * @author Andreas Wenger
 */
public class MxlOpusFileInputTest
{
	
	@Test public void test()
		throws Exception
	{
		MxlOpusFileInput opusInput = new MxlOpusFileInput();
		String dir = "data/test/MxlOpusFileInputTest";
		Opus opus = opusInput.readOpusFile(
			new FileInputStream(new File(dir + "/SomeOpus.xml")));
		//must contain one score, one opus and one opus-link
		assertTrue(opus.getItems().get(0) instanceof com.xenoage.zong.io.musicxml.opus.Score);
		assertTrue(opus.getItems().get(1) instanceof com.xenoage.zong.io.musicxml.opus.Opus);
		assertTrue(opus.getItems().get(2) instanceof com.xenoage.zong.io.musicxml.opus.OpusLink);
		//resolve links
		opus = opusInput.resolveOpusLinks(opus, dir);
		//check flattened list of scores and load the files
		List<String> scores = opus.getScoreFilenames();
		assertEquals(4, scores.size());
		String[] scoresExpected = new String[]{"BeetAnGeSample.xml",
			"BrahWiMeSample.mxl", "DebuMandSample.xml", "SchbAvMaSample.xml"};
		for (String scoreExpected : scoresExpected)
		{
			try
			{
				assertTrue(scores.contains(scoreExpected));
				String filePath = dir + "/" + scoreExpected;
				FileReader.loadScores(filePath, new AllFilter<String>());
			}
			catch (Exception ex)
			{
				throw new Exception("Failed to load " + scoreExpected, ex);
			}
		}
	}

}
