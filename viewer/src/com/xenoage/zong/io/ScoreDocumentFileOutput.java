package com.xenoage.zong.io;

import java.io.IOException;

import com.xenoage.zong.documents.ScoreDocument;


/**
 * This is the interface for all classes
 * that allow the creation of a file
 * from a {@link ScoreDocument} instance.
 * 
 * There may be a MusicXML writer and a MIDI
 * writer for example.
 *
 * @author Andreas Wenger
 */
public interface ScoreDocumentFileOutput
{
  
  /**
   * Gets the localized name of the file format.
   */
  public String getFileFormatName();
  
  
  /**
   * Gets the filename extension of the file format.
   */
  public String getFileExtension();
  
  
  /**
   * Writes the given {@link ScoreDocument} to the given file.
   */
  public void write(ScoreDocument doc, String filepath)
    throws IOException;
  

}
