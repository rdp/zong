package com.xenoage.zong.musiclayout.layouter.cache;

import java.util.HashMap;

import com.xenoage.util.InstanceID;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.MusicElement;
import com.xenoage.zong.musiclayout.notations.ChordNotation;
import com.xenoage.zong.musiclayout.notations.Notation;


/**
 * Cache for already computed {@link Notation}s.
 * 
 * @author Andreas Wenger
 */
public class NotationsCache
{
	
	private HashMap<InstanceID, Notation> cache = new HashMap<InstanceID, Notation>();
	
	
	/**
	 * Adds the given {@link Notation}, that belongs to the given {@link MusicElement}.
	 * If already there, it is replaced.
	 * 
	 * //LAYOUT-PERFORMANCE (needed 1 of 60 seconds)
	 */
	public void set(Notation notation, MusicElement element)
	{
		cache.put(element.getInstanceID(), notation);
	}
	
	
	/**
	 * Adds the elements of the given {@link NotationCache}.
	 * If elements are already there, they are replaced.
	 */
	public void setAll(NotationsCache cache)
	{
		if (cache != null)
			this.cache.putAll(cache.cache);
	}
	
	
	/**
	 * Gets the {@link Notation}, that belongs to the given {@link MusicElement},
	 * or null if unknown.
	 */
	public Notation get(MusicElement element)
	{
		return cache.get(element.getInstanceID());
	}
	
	
	/**
	 * Gets the {@link ChordNotation}, that belongs to the given {@link Chord},
	 * or null if unknown.
	 * 
	 * //LAYOUT-PERFORMANCE (needed 1 of 60 seconds)
	 */
	public ChordNotation getChord(Chord chord)
	{
		return (ChordNotation) cache.get(chord.getInstanceID());
	}
	
	

}
