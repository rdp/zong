package com.xenoage.zong.core.music;

import com.xenoage.pdlib.PMap;
import com.xenoage.pdlib.PSet;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.curvedline.CurvedLine;
import com.xenoage.zong.core.music.curvedline.CurvedLineWaypoint;


/**
 * Set of slurs and ties belonging to chords. A single instance of this
 * class should be used for the whole score.
 * 
 * Given a chord, its curved lines can be queried in near hash lookup time.
 * Adding or removing a curved line is done in near hash lookup time
 * (actually in linear time regarding the number of chords
 * within the curved line).
 * 
 * @author Andreas Wenger
 */
public class CurvedLines
{
	
	private final PSet<CurvedLine> curvedLines;
	private final PMap<Chord, PSet<CurvedLine>> chordsMap;
	
	private static final CurvedLines empty =
		new CurvedLines(new PSet<CurvedLine>(), new PMap<Chord, PSet<CurvedLine>>());
	

	/**
	 * Creates a set of curved lines with the given chord-to-beam mapping.
	 */
	private CurvedLines(PSet<CurvedLine> curvedLines, PMap<Chord, PSet<CurvedLine>> chordsMap)
	{
		this.curvedLines = curvedLines;
		this.chordsMap = chordsMap;
	}
	
	
	/**
	 * Gets an empty set of curved lines.
	 */
	public static CurvedLines empty()
	{
		return empty;
	}
	
	
	/**
	 * Adds a curved line.
	 */
	public CurvedLines plus(CurvedLine curvedLine)
	{
		PSet<CurvedLine> curvedLines = this.curvedLines.plus(curvedLine);
		PMap<Chord, PSet<CurvedLine>> chordsMap = this.chordsMap;
		for (CurvedLineWaypoint wp : curvedLine.getWaypoints())
		{
			PSet<CurvedLine> chordCurvedLines = chordsMap.get(wp.getChord());
			if (chordCurvedLines == null)
				chordCurvedLines = new PSet<CurvedLine>();
			chordCurvedLines = chordCurvedLines.plus(curvedLine);
			chordsMap = chordsMap.plus(wp.getChord(), chordCurvedLines);
		}
		return new CurvedLines(curvedLines, chordsMap);
	}
	
	
	/**
	 * Gets the curved lines belonging to the given chord, or null if there are none.
	 */
	public PSet<CurvedLine> get(Chord chord)
	{
		return chordsMap.get(chord);
	}
	
	
	/**
	 * Removes a curved line.
	 */
	public CurvedLines minus(CurvedLine curvedLine)
	{
		PSet<CurvedLine> curvedLines = this.curvedLines.minus(curvedLine);
		PMap<Chord, PSet<CurvedLine>> chordsMap = this.chordsMap;
		for (CurvedLineWaypoint wp : curvedLine.getWaypoints())
		{
			PSet<CurvedLine> chordCurvedLines = chordsMap.get(wp.getChord());
			if (chordCurvedLines != null)
			{
				chordCurvedLines = chordCurvedLines.minus(curvedLine);
				if (chordCurvedLines.size() > 0)
				{
					//still one or more curved lines for this chord
					chordsMap = chordsMap.plus(wp.getChord(), chordCurvedLines);
				}
				else
				{
					//chord has no curved lines any more
					chordsMap = chordsMap.minus(wp.getChord());
				}
			}
		}
		return new CurvedLines(curvedLines, chordsMap);
	}
	
	
	/**
	 * Removes all curved lines of the given chord.
	 */
	public CurvedLines minusAll(Chord chord)
	{
		PSet<CurvedLine> chordCurvedLines = chordsMap.get(chord);
		if (chordCurvedLines != null)
		{
			PSet<CurvedLine> curvedLines = this.curvedLines.minusAll(chordCurvedLines);
			PMap<Chord, PSet<CurvedLine>> chordsMap = this.chordsMap.minus(chord);
			return new CurvedLines(curvedLines, chordsMap);
		}
		else
		{
			return this;
		}
	}
	
	
	/**
	 * Replaces the curved lines (if any) at the given old chord by equal new
	 * curved lines at the given new chord.
	 */
	public CurvedLines replaceChord(Chord oldChord, Chord newChord)
	{
		PSet<CurvedLine> chordCL = get(oldChord);
		if (chordCL != null)
		{
			CurvedLines ret = this.minusAll(oldChord);
			for (CurvedLine cl : chordCL)
			{
				CurvedLine newCL = cl.replaceChord(oldChord, newChord);
				ret = ret.plus(newCL);
			}
			return ret;
		}
		else
		{
			return this;
		}
	}


}
