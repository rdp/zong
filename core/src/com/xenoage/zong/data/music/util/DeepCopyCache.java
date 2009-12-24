package com.xenoage.zong.data.music.util;

import java.util.Hashtable;

import com.xenoage.zong.data.music.Beam;
import com.xenoage.zong.data.music.CurvedLine;
import com.xenoage.util.InstanceID;


/**
 * This class stores information needed for doing deep copies
 * within the music classes.
 * 
 * It contains a hashtable which maps old beams and ties/slurs
 * to new beams and ties/slurs, allowing the same reference to be
 * used for multiple chords.
 * 
 * For example, if two chords are copied, which share the same
 * beam, and each chord is copied without respect to the other,
 * this would result into two different beams instead of one
 * sharing the two copied chords.
 * 
 * @author Andreas Wenger
 */
public class DeepCopyCache
{
	
	private Hashtable<InstanceID, Beam> beams = new Hashtable<InstanceID, Beam>();
	private Hashtable<InstanceID, CurvedLine> curvedLines = new Hashtable<InstanceID, CurvedLine>();
	
	
	public void addNewBeam(Beam oldBeam, Beam newBeam)
	{
		beams.put(oldBeam.getInstanceID(), newBeam);
	}
	
	
	public void addNewCurvedLine(CurvedLine oldCL, CurvedLine newCL)
	{
		curvedLines.put(oldCL.getInstanceID(), newCL);
	}
	
	
	public Beam getNewBeam(Beam oldBeam)
	{
		return beams.get(oldBeam.getInstanceID());
	}
	
	
	public CurvedLine getNewCurvedLine(CurvedLine oldCL)
	{
		return curvedLines.get(oldCL.getInstanceID());
	}
	

}
