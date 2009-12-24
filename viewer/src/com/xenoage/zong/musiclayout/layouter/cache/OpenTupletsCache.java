package com.xenoage.zong.musiclayout.layouter.cache;

import java.util.HashMap;
import java.util.Iterator;

import com.xenoage.util.InstanceID;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.TupletInfo;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.util.ChordStampings;


/**
 * Cache for beams which still have to be created.
 * 
 * @author Andreas Wenger
 */
public class OpenTupletsCache
	implements Iterable<TupletInfo>
{
	
	//tuplets, whose bracket/number are not stamped yet
	//first key: TupletInfo. second key: InstanceID of the chord
	private HashMap<TupletInfo, HashMap<InstanceID, ChordStampings>> openChords = new HashMap<TupletInfo, HashMap<InstanceID, ChordStampings>>();
	
	
	/**
	 * Adds the given {@link ChordStampings} belonging to the given {@link Chord}
	 * to the cache.
	 */
	public void addChord(Chord chord, ChordStampings chordStampings)
	{
		HashMap<InstanceID, ChordStampings> tupletData = openChords.get(chord.getTupletInfo());
		if (tupletData == null)
		{
			tupletData = new HashMap<InstanceID, ChordStampings>();
			openChords.put(chord.getTupletInfo(), tupletData);
		}
		tupletData.put(chord.getInstanceID(), chordStampings);
	}
	
	
	/**
	 * Gets the {@link ChordStampings} for the given chord, or null if unknown.
	 */
	public ChordStampings getChord(Chord chord)
	{
		HashMap<InstanceID, ChordStampings> tupletData = openChords.get(chord.getTupletInfo());
		if (tupletData != null)
		{
			return tupletData.get(chord.getInstanceID());
		}
		else
		{
			return null;
		}
	}
	
	
	/**
	 * Gets an iterator for all open tuplets.
	 */
	public Iterator<TupletInfo> iterator()
	{
		return openChords.keySet().iterator();
	}
	
	
}
