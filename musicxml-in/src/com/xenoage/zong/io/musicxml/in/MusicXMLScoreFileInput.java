package com.xenoage.zong.io.musicxml.in;

import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;

import com.xenoage.util.FileTools;
import com.xenoage.util.error.ErrorProcessing;
import com.xenoage.util.exceptions.InvalidFormatException;
import com.xenoage.util.xml.XMLReader;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.io.ScoreFileInput;
import com.xenoage.zong.io.musicxml.in.readers.MusicReader;
import com.xenoage.zong.io.musicxml.in.readers.ScoreFormatReader;
import com.xenoage.zong.io.musicxml.in.readers.ScoreInfoReader;
import com.xenoage.zong.io.musicxml.in.readers.StavesListReader;
import com.xenoage.zong.musicxml.MusicXMLDocument;
import com.xenoage.zong.musicxml.types.MxlScorePartwise;
import com.xenoage.zong.musicxml.util.InvalidMusicXML;


/**
 * This class reads a MusicXML 2.0 file
 * into a instance of the {@link Score} class.
 * 
 * It uses the Zong! MusicXML Library.
 *
 * @author Andreas Wenger
 */
public class MusicXMLScoreFileInput
  implements ScoreFileInput
{
	
	
	/**
	 * Creates a new MusicXML 2.0 reader.
	 */
	public MusicXMLScoreFileInput()
	{
	}
  
  
  /**
   * Gets the localized name of the supported file format.
   */
  public String getFileFormatName()
  {
    return "MusicXML 2.0";
  }
  
  
  /**
   * Gets the file filter of the supported format.
   */
  public FilenameFilter getFilenameFilter()
  {
    return FileTools.getXMLFilter();
  }
  
  
  /**
   * Creates a {@link Score} instance from the document
   * behind the given {@link InputStream}. 
   * @param inputStream  the input stream with the MusicXML document
   * @param filePath     file path if known, null otherwise
   * @param err          the error handler, which is used for logging warnings,
   *                     but it may also be null
   */
  @Override public Score read(InputStream inputStream, String filePath, ErrorProcessing err)
    throws InvalidFormatException, IOException
  {
  	
  	//parse MusicXML file
  	MxlScorePartwise score;
  	try
  	{
  		Document xmlDoc = XMLReader.readFile(inputStream);
  		MusicXMLDocument doc = MusicXMLDocument.read(xmlDoc);
  		score = doc.getScore();
  	}
  	catch (InvalidMusicXML ex)
  	{
  		//no valid MusicXML
  		throw new InvalidFormatException(ex);
  	}
  	catch (Exception ex)
  	{
  		throw new IOException(ex);
  	}
  	
  	return read(score, err, true);
  }
  
  
  /**
   * Builds a {@link Score} entity from a {@link MxlScorePartwise} document.
   * @param doc           the provided ScorePartwise document
   * @param err           the error handler, which is used for logging warnings,
   *                      but it may also be null
   * @param ignoreErrors  if true, try to ignore errors (like overfull measures) as long
   *                      as a consistent state can be guaranteed, or false, to cancel
   *                      loading as soon as something is wrong
   */
  public Score read(MxlScorePartwise mxlScore, ErrorProcessing err, boolean ignoreErrors)
  	throws InvalidFormatException
  {
		//create new score
		Score score = Score.empty();
		
		//read information about the score
		score = score.withScoreInfo(ScoreInfoReader.read(mxlScore));

		//read score format
		ScoreFormatReader.Value scoreFormatValue = ScoreFormatReader.read(mxlScore);
		score = score.withScoreFormat(scoreFormatValue.scoreFormat);
		score = score.plusMetaData("layoutformat", scoreFormatValue.layoutFormat);

		//create the list of staves
		StavesListReader.Value stavesListValue = StavesListReader.read(mxlScore);
		score = score.withStavesList(stavesListValue.stavesList);

		//read the musical contents
		score = MusicReader.read(mxlScore, score, err, ignoreErrors);

		//remember the XML document for further application-dependend processing
		score = score.plusMetaData("mxldoc", mxlScore);

		return score;
  }
  

}
