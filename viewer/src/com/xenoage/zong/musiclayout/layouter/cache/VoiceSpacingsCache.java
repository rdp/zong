package com.xenoage.zong.musiclayout.layouter.cache;

import java.util.HashMap;

import com.xenoage.zong.core.music.Voice;
import com.xenoage.zong.musiclayout.spacing.horizontal.VoiceSpacing;


/**
 * Cache for already computed {@link VoiceSpacing}s.
 * 
 * @author Andreas Wenger
 */
public class VoiceSpacingsCache
{
	
	private HashMap<Voice, VoiceSpacing> cache = new HashMap<Voice, VoiceSpacing>();
	
	
	/**
	 * Adds the given {@link VoiceSpacing}, that belongs to the given {@link voice}.
	 * If already there, it is replaced.
	 * 
	 * //LAYOUT-PERFORMANCE (needed 1 of 60 seconds)
	 */
	public void set(VoiceSpacing voiceSpacing, Voice voice)
	{
		cache.put(voice, voiceSpacing);
	}
	
	
	/**
	 * Gets the {@link VoiceSpacing}, that belongs to the given {@link Voice},
	 * or null if unknown.
	 */
	public VoiceSpacing get(Voice voice)
	{
		return cache.get(voice);
	}
	

}
