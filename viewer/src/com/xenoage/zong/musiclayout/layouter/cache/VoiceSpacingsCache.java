package com.xenoage.zong.musiclayout.layouter.cache;

import java.util.HashMap;

import com.xenoage.util.InstanceID;
import com.xenoage.zong.data.music.MusicElement;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.musiclayout.notations.Notation;
import com.xenoage.zong.musiclayout.spacing.horizontal.VoiceSpacing;


/**
 * Cache for already computed {@link VoiceSpacing}s.
 * 
 * @author Andreas Wenger
 */
public class VoiceSpacingsCache
{
	
	private HashMap<InstanceID, VoiceSpacing> cache = new HashMap<InstanceID, VoiceSpacing>();
	
	
	/**
	 * Adds the given {@link Notation}, that belongs to the given {@link MusicElement}.
	 * If already there, it is replaced.
	 * 
	 * //LAYOUT-PERFORMANCE (needed 1 of 60 seconds)
	 */
	public void set(VoiceSpacing voiceSpacing, Voice voice)
	{
		cache.put(voice.getInstanceID(), voiceSpacing);
	}
	
	
	/**
	 * Gets the {@link VoiceSpacing}, that belongs to the given {@link Voice},
	 * or null if unknown.
	 */
	public VoiceSpacing get(Voice voice)
	{
		return cache.get(voice.getInstanceID());
	}
	

}
