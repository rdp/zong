package com.xenoage.zong.core.header;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.format.Break;
import com.xenoage.zong.core.music.barline.Barline;
import com.xenoage.zong.core.music.barline.BarlineRepeat;
import com.xenoage.zong.core.music.direction.Tempo;
import com.xenoage.zong.core.music.key.Key;
import com.xenoage.zong.core.music.time.Time;
import com.xenoage.zong.core.music.util.BeatE;
import com.xenoage.zong.core.music.util.BeatEList;
import com.xenoage.zong.core.music.util.BeatInterval;
import com.xenoage.zong.core.music.volta.Volta;


/**
 * A {@link ColumnHeader} stores information that
 * is used for the whole measure column,
 * i.e. key signature and time signature.
 * 
 * The start and end barline as well as middle barlines
 * are saves here, and a volta if it begins in this
 * measure.
 * 
 * There is also a list of tempo directions for this
 * measure and layout break information.
 * 
 * @author Andreas Wenger
 */
public final class ColumnHeader
{
	
	//time
	private final Time time;
	
	//barlines
	private final Barline startBarline;
	private final Barline endBarline;
	private final BeatEList<Barline> middleBarlines;
	
	//volta
	private final Volta volta;
	
	//keys, tempos
	private final BeatEList<Key> keys;
	private final BeatEList<Tempo> tempos;
	
	//layout
	private final Break breakInstance;
	
	//empty instance
	private static ColumnHeader empty =
		new ColumnHeader(null, null, null, null, null, null, null, null);
	

	/**
	 * Creates a new {@link ColumnHeader}.
	 * @param time            the time signatur, or null
	 * @param startBarline    start barline, or null
	 * @param endBarline      end barline, or null
	 * @param middleBarlines  list of middle barlines, or null
	 * @param volta           volta, or null
	 * @param keys            list of keys, or null
	 * @param tempos          list of tempos, or null
	 * @param breakInstance   layout break, or null
	 */
	public ColumnHeader(Time time, Barline startBarline,
		Barline endBarline, BeatEList<Barline> middleBarlines, Volta volta,
		BeatEList<Key> keys, BeatEList<Tempo> tempos, Break breakInstance)
	{
		checkStartBarline(startBarline);
		checkEndBarline(endBarline);
		this.time = time;
		this.startBarline = startBarline;
		this.endBarline = endBarline;
		this.middleBarlines = middleBarlines;
		this.volta = volta;
		this.keys = keys;
		this.tempos = tempos;
		this.breakInstance = breakInstance;
	}
	
	
	/**
	 * Creates an empty {@link ColumnHeader}.
	 */
	public static ColumnHeader empty()
	{
		return empty;
	}
	
	
	public Time getTime()
	{
		return time;
	}
	
	
	/**
	 * Sets the time signature, or null if unset.
	 */
	public ColumnHeader withTime(Time time)
	{
		return new ColumnHeader(time, startBarline, endBarline, middleBarlines,
			volta, keys, tempos, breakInstance);
	}
	
	
	/**
	 * Gets the barline at the beginning of this measure, or null if unset.
	 */
	public Barline getStartBarline()
	{
		return startBarline;
	}
	
	
	/**
	 * Sets the barline at the beginning of this measure, or null if unset.
	 */
	public ColumnHeader withStartBarline(Barline startBarline)
	{
		return new ColumnHeader(time, startBarline, endBarline, middleBarlines,
			volta, keys, tempos, breakInstance);
	}


	/**
	 * Gets the barline at the end of this measure, or null if unset.
	 */
	public Barline getEndBarline()
	{
		return endBarline;
	}
	
	
	/**
	 * Sets the barline at the end of this measure, or null if unset.
	 */
	public ColumnHeader withEndBarline(Barline endBarline)
	{
		return new ColumnHeader(time, startBarline, endBarline, middleBarlines,
			volta, keys, tempos, breakInstance);
	}
	
	
	/**
	 * Gets the number of barlines in the middle of the measure.
	 */
	public int getMiddleBarlinesCount()
	{
		if (middleBarlines == null)
			return 0;
		else
			return middleBarlines.size();
	}
	
	
	/**
	 * Gets the barlines in the middle of the measure, or null
	 * if there are none.
	 */
	public BeatEList<Barline> getMiddleBarlines()
	{
		return middleBarlines;
	}
	
	
	/**
	 * Sets a barline in the middle of the measure.
	 */
	public ColumnHeader withMiddleBarline(Barline middleBarline, Fraction beat)
	{
		BeatEList<Barline> middleBarlines =
			(this.middleBarlines != null ? this.middleBarlines : new BeatEList<Barline>());
		middleBarlines = middleBarlines.with(middleBarline, beat).get1();
		return new ColumnHeader(time, startBarline, endBarline, middleBarlines,
			volta, keys, tempos, breakInstance);
	}
	
	
	/**
	 * Removes a barline in the middle of the measure.
	 */
	public ColumnHeader minusMiddleBarline(Fraction beat)
	{
		if (middleBarlines == null)
		{
			return this;
		}
		else
		{
			BeatEList<Barline> middleBarlines = this.middleBarlines;
			middleBarlines = middleBarlines.minus(beat);
			return new ColumnHeader(time, startBarline, endBarline, middleBarlines,
				volta, keys, tempos, breakInstance);
		}
	}

	
	/**
	 * Gets the {@link Volta} beginning at this measure,
	 * or null if there is none.
	 */
	public Volta getVolta()
	{
		return volta;
	}
	
	
	/**
	 * Sets the volta beginning at this measure, or null if there is none.
	 */
	public ColumnHeader withVolta(Volta volta)
	{
		return new ColumnHeader(time, startBarline, endBarline, middleBarlines,
			volta, keys, tempos, breakInstance);
	}
	
	
	/**
	 * Gets the key signature changes in this measure, or null
	 * if there are none.
	 */
	public BeatEList<Key> getKeys()
	{
		return keys;
	}
	
	
	/**
   * Returns the last key within this measure.
   * If there is none, null is returned.
   */
  public BeatE<Key> getLastKey()
  {
    if (keys != null)
    	return keys.getLast();
    else
    	return null;
  }
	
	
	/**
   * Returns the last key at or before the given beat.
   * If there is none, null is returned.
   */
  public BeatE<Key> getLastKey(BeatInterval endpoint, Fraction beat)
  {
  	if (keys != null)
    	return keys.getLastBefore(endpoint, beat);
    else
    	return null;
  }
	
	
	/**
	 * Sets a key in this measure.
	 */
	public ColumnHeader withKey(Key key, Fraction beat)
	{
		BeatEList<Key> keys =
			(this.keys != null ? this.keys : new BeatEList<Key>());
		keys = keys.with(key, beat).get1();
		return new ColumnHeader(time, startBarline, endBarline, middleBarlines,
			volta, keys, tempos, breakInstance);
	}
	
	
	/**
	 * Removes a key from this measure.
	 */
	public ColumnHeader minusKey(Fraction beat)
	{
		if (keys == null)
		{
			return this;
		}
		else
		{
			BeatEList<Key> keys = this.keys;
			keys = keys.minus(beat);
			return new ColumnHeader(time, startBarline, endBarline, middleBarlines,
				volta, keys, tempos, breakInstance);
		}
	}
  
	
	/**
	 * Gets the tempo changes in this measure, or null
	 * if there are none.
	 */
	public BeatEList<Tempo> getTempos()
	{
		return tempos;
	}
	
	
	/**
	 * Sets a tempo in this measure.
	 */
	public ColumnHeader withTempo(Tempo tempo, Fraction beat)
	{
		BeatEList<Tempo> tempos =
			(this.tempos != null ? this.tempos : new BeatEList<Tempo>());
		tempos = tempos.with(tempo, beat).get1();
		return new ColumnHeader(time, startBarline, endBarline, middleBarlines,
			volta, keys, tempos, breakInstance);
	}
	
	
	/**
	 * Removes a tempo from this measure.
	 */
	public ColumnHeader minusTempo(Fraction beat)
	{
		if (tempos == null)
		{
			return this;
		}
		else
		{
			BeatEList<Tempo> tempos = this.tempos;
			tempos = tempos.minus(beat);
			return new ColumnHeader(time, startBarline, endBarline, middleBarlines,
				volta, keys, tempos, breakInstance);
		}
	}
	
	
	/**
	 * Gets the {@link Break} after this measure,
	 * or null if there is none.
	 */
	public Break getBreak()
	{
		return breakInstance;
	}
	
	
	/**
	 * Sets the {@link Break} after this measure, or null if there is none.
	 */
	public ColumnHeader withBreak(Break breakInstance)
	{
		return new ColumnHeader(time, startBarline, endBarline, middleBarlines,
			volta, keys, tempos, breakInstance);
	}
	
	
	/**
	 * Checks the given start barline.
	 */
	private void checkStartBarline(Barline startBarline)
	{
		//both side repeat is not allowed
		if (startBarline != null && startBarline.getRepeat() == BarlineRepeat.Both)
			throw new IllegalArgumentException(BarlineRepeat.Both + " is not supported for a start barline.");
	}
	
	
	/**
	 * Checks the given end barline.
	 */
	public void checkEndBarline(Barline endBarline)
	{
		//both side repeat is not allowed
		if (endBarline != null && endBarline.getRepeat() == BarlineRepeat.Both)
			throw new IllegalArgumentException(BarlineRepeat.Both + " is not supported for an end barline.");
	}
	

}
