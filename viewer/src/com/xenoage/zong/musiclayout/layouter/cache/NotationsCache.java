package com.xenoage.zong.musiclayout.layouter.cache;

import com.xenoage.pdlib.PMap;
import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.musiclayout.notations.ChordNotation;
import com.xenoage.zong.musiclayout.notations.Notation;


/**
 * Cache for already computed {@link Notation}s.
 * 
 * @author Andreas Wenger
 */
public final class NotationsCache
{
	
	public static final NotationsCache empty = new NotationsCache(
		new PMap<MusicElement, Notation>());
	
	private final PMap<MusicElement, Notation> cache;
	
	
	private NotationsCache(PMap<MusicElement, Notation> cache)
	{
		this.cache = cache;
	}
	
	
	/**
	 * Adds the given {@link Notation}, that belongs to the given {@link MusicElement}.
	 * If already there, it is replaced.
	 */
	public NotationsCache plus(Notation notation, MusicElement element)
	{
		return new NotationsCache(cache.plus(element, notation));
	}
	
	
	/**
	 * Adds the elements of the given {@link NotationCache}.
	 * If elements are already there, they are replaced.
	 */
	public NotationsCache merge(NotationsCache cache)
	{
		if (cache != null)
			return new NotationsCache(this.cache.plusAll(cache.cache));
		else
			return this;
	}
	
	
	/**
	 * Gets the {@link Notation}, that belongs to the given {@link MusicElement},
	 * or null if unknown.
	 */
	public Notation get(MusicElement element)
	{
		return cache.get(element);
	}
	
	
	/**
	 * Gets the {@link ChordNotation}, that belongs to the given {@link Chord},
	 * or null if unknown.
	 */
	public ChordNotation getChord(Chord chord)
	{
		return (ChordNotation) cache.get(chord);
	}
	
	
	@Override public String toString()
	{
		return "[" + getClass().getSimpleName() + " with " + cache.size() + " items]";
	}

}
