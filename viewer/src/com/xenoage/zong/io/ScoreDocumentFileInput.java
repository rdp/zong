package com.xenoage.zong.io;

import java.io.FilenameFilter;
import java.io.IOException;

import com.xenoage.util.exceptions.InvalidFormatException;
import com.xenoage.zong.documents.ScoreDocument;


/**
 * This is the interface for all classes
 * that allow the creation of a
 * {@link ScoreDocument} instance
 * from a file.
 * 
 * There may be a MusicXML reader and a MIDI
 * reader for example.
 *
 * @author Andreas Wenger
 */
public interface ScoreDocumentFileInput
{
  
  /**
   * Gets the localized name of the supported file format.
   */
  public String getFileFormatName();
  
  
  /**
   * Gets the filename filter of the supported format.
   */
  public FilenameFilter getFilenameFilter();
  
  
  /**
   * Creates a {@link ScoreDocument} instance from the document
   * behind the given path.
   */
  public ScoreDocument read(String filePath)
    throws InvalidFormatException, IOException;
  

}
