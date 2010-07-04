package com.xenoage.zong.io.musicxml.in.readers;

import com.xenoage.util.error.ErrorProcessing;


/**
 * General settings for the {@link MusicReader}.
 * 
 * @author Andreas Wenger
 */
public class MusicReaderSettings
{
	
	public final ErrorProcessing err;
	public final boolean ignoreErrors;
  
  
	public MusicReaderSettings(ErrorProcessing err, boolean ignoreErrors)
	{
		this.ignoreErrors = ignoreErrors;
		this.err = err;
	}


}
