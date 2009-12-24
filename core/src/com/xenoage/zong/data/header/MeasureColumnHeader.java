package com.xenoage.zong.data.header;

import java.util.LinkedList;
import java.util.List;

import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.music.Volta;
import com.xenoage.zong.data.music.barline.Barline;
import com.xenoage.zong.data.music.barline.BarlineRepeat;
import com.xenoage.zong.data.music.directions.Tempo;
import com.xenoage.zong.data.music.key.Key;
import com.xenoage.zong.data.music.time.Time;


/**
 * A {@link MeasureColumnHeader} stores information that
 * is used for the whole measure column by default,
 * i.e. key signature and time signature.
 * 
 * The start and end barline as well as middle barlines
 * are saves here, and a volta if it begins in this
 * measure.
 * 
 * There is also a list of tempo directions for this
 * measure.
 * 
 * @author Andreas Wenger
 */
public class MeasureColumnHeader
{
	
	//key and time
	private Key key = null;
	private Time time = null;
	
	//barlines
	private Barline startBarline = null;
	private Barline endBarline = null;
	private LinkedList<Tuple2<Fraction, Barline>> middleBarlines = null;
	
	//volta
	private Volta volta = null;
	
	//tempo
	private LinkedList<Tuple2<Fraction, Tempo>> tempos = null;
	
	
	public Key getKey()
	{
		return key;
	}
	
	public void setKey(Key key)
	{
		this.key = key;
	}
	
	
	public Time getTime()
	{
		return time;
	}
	
	
	public void setTime(Time time)
	{
		this.time = time;
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
	public void setStartBarline(Barline startBarline)
	{
		//both side repeat is not allowed
		if (startBarline != null && startBarline.getRepeat() == BarlineRepeat.Both)
			throw new IllegalArgumentException(BarlineRepeat.Both + " is not supported for a start barline.");
		this.startBarline = startBarline;
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
	public void setEndBarline(Barline endBarline)
	{
		//both side repeat is not allowed
		if (endBarline != null && endBarline.getRepeat() == BarlineRepeat.Both)
			throw new IllegalArgumentException(BarlineRepeat.Both + " is not supported for an end barline.");
		this.endBarline = endBarline;
	}
	
	
	/**
	 * Adds the given barline at the given beat.
	 * If there was already one, it is replaced.
	 */
	public void addMiddleBarline(Barline barline, Fraction beat)
	{
		if (middleBarlines == null)
			middleBarlines = new LinkedList<Tuple2<Fraction,Barline>>();
		//get index of the new barline
		int index = 0;
		boolean remove = false;
		for (Tuple2<Fraction, Barline> bar : middleBarlines)
		{
			int c = beat.compareTo(bar.get1());
			if (c < 0)
			{
				break;
			}
			else if (c == 0)
			{
				remove = true;
				break;
			}
			index++;
		}
		//insert/set at the right position
		if (remove)
			middleBarlines.set(index, new Tuple2<Fraction, Barline>(beat, barline));
		else
			middleBarlines.add(index, new Tuple2<Fraction, Barline>(beat, barline));
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
	public List<Tuple2<Fraction,Barline>> getMiddleBarlines()
	{
		return middleBarlines;
	}
	
	
	/**
	 * Removes the given middle barline.
	 */
	public void removeMiddleBarline(Barline barline)
	{
		middleBarlines.remove(barline);
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
	 * Sets the {@link Volta} beginning at this measure,
	 * or null if there is none.
	 */
	public void setVolta(Volta volta)
	{
		this.volta = volta;
	}
	
	
	/**
	 * Gets the tempo changes in this measure, or null
	 * if there are none.
	 */
	public List<Tuple2<Fraction, Tempo>> getTempos()
	{
		return tempos;
	}
	
	
	/**
	 * Sets the tempo at the given beat.
	 * If there was already one, it is replaced. 
	 */
	public void addTempo(Tempo tempo, Fraction beat)
	{
		if (tempos == null)
			tempos = new LinkedList<Tuple2<Fraction,Tempo>>();
		//get index of the new tempo
		int index = 0;
		boolean remove = false;
		for (Tuple2<Fraction, Tempo> t : tempos)
		{
			int c = beat.compareTo(t.get1());
			if (c < 0)
			{
				break;
			}
			else if (c == 0)
			{
				remove = true;
				break;
			}
			index++;
		}
		//insert/set at the right position
		if (remove)
			tempos.set(index, new Tuple2<Fraction, Tempo>(beat, tempo));
		else
			tempos.add(index, new Tuple2<Fraction, Tempo>(beat, tempo));
	}
	
	
	/**
	 * Removes the given tempo.
	 */
	public void removeTempo(Tempo tempo)
	{
		tempos.remove(tempo);
	}
	

}
