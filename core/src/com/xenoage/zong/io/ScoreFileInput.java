package com.xenoage.zong.io;

import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import com.xenoage.util.error.ErrorProcessing;
import com.xenoage.util.exceptions.InvalidFormatException;
import com.xenoage.zong.data.Score;


/**
 * This is the interface for all classes
 * that allow the creation of a {@link Score} instance
 * from a file.
 * 
 * There may be a MusicXML reader and a MIDI
 * reader for example.
 *
 * @author Andreas Wenger
 */
public interface ScoreFileInput
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
   * Creates a {@link Score} instance from the document
   * behind the given input stream. If the file path is known too,
   * it can be given, otherwise it is null.
   * The given {@link ErrorProcessing} instance is used for logging warnings,
   * but it may also be null.
   */
  public Score read(InputStream inputStream, String filePath, ErrorProcessing err)
    throws InvalidFormatException, IOException;
  

}
