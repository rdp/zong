package com.xenoage.zong.io.musicxml.in;

import com.xenoage.util.FileTools;
import com.xenoage.util.error.ErrorProcessing;
import com.xenoage.util.exceptions.InvalidFormatException;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.io.ScoreFileInput;
import com.xenoage.zong.io.musicxml.in.MxlScoreData;
import com.xenoage.zong.io.musicxml.in.MxlScoreFormat;
import com.xenoage.zong.io.musicxml.in.MxlScoreInfo;
import com.xenoage.zong.io.musicxml.in.MxlStavesList;
import com.xenoage.zong.io.score.ScoreInput;
import com.xenoage.zong.util.xml.ZongMarshalling;

import java.io.*;

import javax.xml.bind.UnmarshalException;

import proxymusic.ScorePartwise;


/**
 * This class reads a MusicXML 2.0 file
 * into a instance of the {@link Score} class.
 * 
 * It uses the proxymusic library,
 * https://proxymusic.dev.java.net/
 *
 * @author Andreas Wenger
 */
public class MxlScoreFileInput
  implements ScoreFileInput
{
	
	
	/**
	 * Creates a new MusicXML 2.0 reader.
	 */
	public MxlScoreFileInput()
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
   * behind the given {@link InputStream}. If the file path is known too,
   * it can be given, otherwise it is null.
   * The given {@link ErrorProcessing} instance is used for logging warnings,
   * but it may also be null.
   */
  @Override public Score read(InputStream inputStream, String filePath, ErrorProcessing err)
    throws InvalidFormatException, IOException
  {
  	
  	//parse MusicXML file
  	ScorePartwise doc;
  	try
  	{
  		//don't resolve URLs
  		doc = ZongMarshalling.unmarshal(inputStream, false);
  	}
  	catch (UnmarshalException ex)
  	{
  		//no valid XML
  		throw new InvalidFormatException(ex);
  	}
  	catch (Exception ex)
  	{
  		throw new IOException(ex);
  	}
  	
  	//create new score
  	Score score = new Score();
  	
  	//read information about the score
  	MxlScoreInfo mxlScoreInfo = new MxlScoreInfo(doc);
  	score.setScoreInfo(mxlScoreInfo.getScoreInfo());
  	
    //read score format
    MxlScoreFormat scoreFormatReader = new MxlScoreFormat(doc);
    score.setScoreFormat(scoreFormatReader.getScoreFormat());
    score.addMetaData("layoutformat", scoreFormatReader.getPageFormat());
    
    //create the list of staves
    MxlStavesList stavesInfo = new MxlStavesList(doc, score);
    score.setStavesList(stavesInfo.getStavesList());
    
    //read the musical contents
    ScoreInput input = new ScoreInput(score);
    MxlScoreData.read(doc, input, scoreFormatReader, err);
    
    //remember the XML document for further application-dependend processing
    score.addMetaData("xmldoc", doc);
    
    return score;
  }
  

}
