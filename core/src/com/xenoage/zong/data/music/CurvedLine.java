package com.xenoage.zong.data.music;

import static com.xenoage.util.exceptions.ThrowableTools.throwNullArg;

import com.xenoage.util.enums.VSide;
import com.xenoage.zong.data.music.format.BezierPoint;
import com.xenoage.zong.data.music.util.DeepCopyCache;


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
public class CurvedLine
	extends MusicElement
{

	public enum Type
	{
		Slur, Tie;
	}
	
	protected Type type;
	protected CurvedLineWaypoint start, stop;
	protected VSide side = null; //null = default
	
	
	/**
	 * Creates a new {@link CurvedLine} and connects its chords to it.
	 * @param type   slur or tie
	 * @param start  the left end point of the curved line
	 * @param stop   the right end point of the curved line
	 */
	public static CurvedLine createAndConnect(Type type,
		CurvedLineWaypoint start, CurvedLineWaypoint stop)
	{
		return new CurvedLine(type, start, stop);
	}
	
	
	private CurvedLine(Type type, CurvedLineWaypoint start, CurvedLineWaypoint stop)
	{
		throwNullArg(type, start, stop);
		this.type = type;
		this.start = start;
		this.stop = stop;
		//connect
		start.getChord().addCurvedLine(this);
		stop.getChord().addCurvedLine(this);
	}
	
	
	/**
	 * Returns a copy of this {@link CurvedLine}.
	 * The waypoints are not copied deeply, but their references are reused.
	 */
	public CurvedLine copy()
	{
		return new CurvedLine(type, start, stop);
	}
	
	
	/**
	 * Not supported. Use <code>copy</code> instead.
	 */
	@Override public MusicElement deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		throw new UnsupportedOperationException();
	}
	
	
	/**
	 * Replaces the start waypoint with the given new one.
	 * The reference to the old one is removed from the connected chord.
	 */
	public void setStart(CurvedLineWaypoint start)
	{
		this.start = start;
	}
	
	
	/**
	 * Replaces the stop waypoint with the given new one.
	 */
	public void setStop(CurvedLineWaypoint stop)
	{
		this.stop = stop;
	}
	
	
	public Type getType()
	{
		return type;
	}
	
	
	public CurvedLineWaypoint getStart()
	{
		return start;
	}
	
	
	public CurvedLineWaypoint getStop()
	{
		return stop;
	}
	
	
	/**
	 * Replaces the bezier point of the given waypoint. The waypoint 
	 * is replaced by an updated waypoint, which is also returned.
	 */
	public CurvedLineWaypoint setBezierPoint(CurvedLineWaypoint oldWP, BezierPoint bezierPoint)
	{
		if (start == oldWP)
		{
			start = new CurvedLineWaypoint(start.getChord(), start.getNoteIndex(), bezierPoint);
			return start;
		}
		else if (stop == oldWP)
		{
			stop = new CurvedLineWaypoint(stop.getChord(), stop.getNoteIndex(), bezierPoint);
			return stop;
		}
		else
		{
			throw new IllegalArgumentException("given waypoint is not part of this curved line");
		}
	}
	
	
	/**
	 * Gets the waypoint which belongs to the given chord.
	 */
	public CurvedLineWaypoint getWaypoint(Chord chord)
	{
		if (chord == start.getChord())
			return start;
		else if (chord == stop.getChord())
			return stop;
		else
			throw new IllegalArgumentException("given chord is not part of this curved line");
	}


	/**
	 * Gets the type of the waypoint which belongs to the given chord.
	 */
	public CurvedLineWaypoint.Type getWaypointType(Chord chord)
	{
		if (chord == start.getChord())
			return CurvedLineWaypoint.Type.Start;
		else if (chord == stop.getChord())
			return CurvedLineWaypoint.Type.Stop;
		else
			throw new IllegalArgumentException("given chord is not part of this curved line");
	}


	/**
	 * Gets the vertical side of the slur. <code>null</code> means default.
	 */
	public VSide getSide()
	{
		return side;
	}


	/**
	 * Sets the vertical side of the slur. <code>null</code> means default.
	 */
	public void setSide(VSide side)
	{
		this.side = side;
	}
	

}
