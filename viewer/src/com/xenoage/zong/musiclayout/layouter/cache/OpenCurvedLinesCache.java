package com.xenoage.zong.musiclayout.layouter.cache;

import java.util.HashMap;
import java.util.Iterator;

import com.xenoage.util.InstanceID;
import com.xenoage.zong.data.music.CurvedLine;
import com.xenoage.zong.musiclayout.layouter.cache.util.CurvedLineCache;


/**
 * Cache for curved lines (ties, slurs) which are still not
 * stamped completely.
 * 
 * @author Andreas Wenger
 */
public class OpenCurvedLinesCache
	implements Iterable<CurvedLineCache>
{
	
	//slurs and ties, which are not stamped completely yet (key: InstanceID of the CurvedLine)
	private HashMap<InstanceID, CurvedLineCache> openCurvedLines = new HashMap<InstanceID, CurvedLineCache>();
	
	
	/**
	 * Adds the given {@link CurvedLineCache}.
	 */
	public void add(CurvedLineCache tiedChords)
	{
		openCurvedLines.put(tiedChords.getCurvedLine().getInstanceID(), tiedChords);
	}
	
	
	/**
	 * Gets the given {@link CurvedLine} instance.
	 * If not existing, null is returned.
	 */
	public CurvedLineCache get(CurvedLine curvedLine)
	{
		return openCurvedLines.get(curvedLine.getInstanceID());
	}
	
	
	/**
	 * Removes the given {@link CurvedLine} instance.
	 * If not existing, nothing happens.
	 */
	public void remove(CurvedLine curvedLine)
	{
		openCurvedLines.remove(curvedLine.getInstanceID());
	}
	
	
	/**
	 * Gets an iterator for all open beams.
	 */
	public Iterator<CurvedLineCache> iterator()
	{
		return openCurvedLines.values().iterator();
	}
	
	
}
