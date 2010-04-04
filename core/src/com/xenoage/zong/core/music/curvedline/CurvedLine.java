package com.xenoage.zong.core.music.curvedline;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.NullTools.throwNullArg;
import static com.xenoage.util.Range.range;

import com.xenoage.pdlib.PVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.enums.VSide;
import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.core.music.chord.Chord;


/**
 * A slur or tie connecting two notes,
 * represented by two {@link CurvedLineWaypoint}s.
 * 
 * The name "curved line" was inspired by Wikipedia,
 * which states that "a slur is denoted with a curved line",
 * see http://en.wikipedia.org/wiki/Slur_(music)
 * 
 * @author Andreas Wenger
 */
public final class CurvedLine
	implements MusicElement
{

	public enum Type
	{
		Slur, Tie;
	}
	
	private final Type type;
	private final PVector<CurvedLineWaypoint> waypoints;
	private final VSide side; //null = default
	
	
	/**
	 * Creates a new {@link CurvedLine}.
	 * @param type   slur or tie
	 * @param start  the left end point of the curved line
	 * @param stop   the right end point of the curved line
	 * @param side   the vertical side of the line, or null for default
	 */
	public CurvedLine(Type type, CurvedLineWaypoint start, CurvedLineWaypoint stop, VSide side)
	{
		throwNullArg(type, start, stop);
		this.type = type;
		this.waypoints = pvec(start, stop);
		this.side = side;
	}
	
	
	/**
	 * Creates a new {@link CurvedLine}.
	 * @param type       slur or tie
	 * @param waypoints  the waypoints of the curved line (at least two)
	 * @param side       the vertical side of the line, or null for default
	 */
	public CurvedLine(Type type, PVector<CurvedLineWaypoint> waypoints, VSide side)
	{
		this.type = type;
		this.waypoints = waypoints;
		this.side = side;
	}
	
	
	public Type getType()
	{
		return type;
	}
	
	
	public CurvedLineWaypoint getStart()
	{
		return waypoints.getFirst();
	}
	
	
	public CurvedLineWaypoint getStop()
	{
		return waypoints.getLast();
	}
	
	
	public Vector<CurvedLineWaypoint> getWaypoints()
	{
		return waypoints;
	}
	
	
	public CurvedLineWaypoint getWaypoint(Chord chord)
	{
		for (CurvedLineWaypoint wp : waypoints)
		{
			if (chord == wp.getChord())
			{
				return wp;
			}
		}
		throw new IllegalArgumentException("Given chord is not part of this CurvedLine.");
	}
	
	
	public CurvedLineWaypoint.Type getWaypointType(Chord chord)
	{
		if (chord == waypoints.getFirst().getChord())
		{
			return CurvedLineWaypoint.Type.Start;
		}
		else if (chord == waypoints.getLast().getChord())
		{
			return CurvedLineWaypoint.Type.Stop;
		}
		else
		{
			for (int i : range(1, waypoints.size() - 2))
			{
				if (chord == waypoints.get(i).getChord())
				{
					return CurvedLineWaypoint.Type.Continue;
				}
			}
		}
		throw new IllegalArgumentException("Given chord is not part of this CurvedLine.");
	}


	/**
	 * Gets the vertical side of the slur. <code>null</code> means default.
	 */
	public VSide getSide()
	{
		return side;
	}
	
	
	/**
	 * Replaces the given chord.
	 */
	public CurvedLine replaceChord(Chord oldChord, Chord newChord)
	{
		for (int i : range(waypoints))
		{
			CurvedLineWaypoint wp = waypoints.get(i);
			if (waypoints.get(i).getChord() == oldChord)
			{
				//try to stay on the same note index, if possible
				int noteIndex = Math.min(wp.getNoteIndex(), newChord.getNotes().size());
				//replace waypoint
				PVector<CurvedLineWaypoint> waypoints = this.waypoints.with(i,
					new CurvedLineWaypoint(newChord, noteIndex, wp.getBezierPoint()));
				return new CurvedLine(type, waypoints, side);
			}
		}
		throw new IllegalArgumentException("Given chord is not part of this beam");
	}
	

}
