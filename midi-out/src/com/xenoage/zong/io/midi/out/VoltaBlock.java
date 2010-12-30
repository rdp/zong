package com.xenoage.zong.io.midi.out;

import static com.xenoage.util.Range.range;

import java.util.HashMap;
import java.util.Set;

import com.xenoage.util.Range;
import com.xenoage.zong.core.music.volta.Volta;


/**
 * This class packs a number of connected {@link Volta}s to one block.
 * 
 * @author Uli Teschemacher
 */
public class VoltaBlock
{

	HashMap<Volta, Integer> map = new HashMap<Volta, Integer>();


	public void addVolta(Volta volta, int measure)
	{
		map.put(volta, measure);
	}


	/**
	 * Returns the measure numbers of the measures that have to be played in the
	 * volta.
	 */
	public Range getRange(int repeatTime)
	{
		Range ret = null;
		Set<Volta> voltas = map.keySet();
		for (Volta volta : voltas)
		{
			Range range = volta.getNumbers();
			if (range != null && range.isInRange(repeatTime))
			{
				ret = range(map.get(volta), map.get(volta) + volta.getLength() - 1);
				break;
			}
		}
		if (ret == null)
		{
			//Search for default volta
			for (Volta volta : voltas)
			{
				if (volta.getNumbers() == null)
				{
					ret = range(map.get(volta), map.get(volta) + volta.getLength() - 1);
					break;
				}
			}
		}
		return ret;
	}


	/**
	 * Calculates whether this time is the last time to repeat.
	 */
	public boolean isLastTime(int repeatTime)
	{
		if (repeatTime == getRepeatCount())
		{
			return true;
		}
		return false;
	}
	
	
	/**
	 * Calculates the number of repetitions.
	 * @return
	 */
	public int getRepeatCount()
	{
		int count = 0;
		for (Volta volta : map.keySet())
		{
			Range range = volta.getNumbers();
			int size;
			if (range == null)
			{
				size = 1;
			}
			else
			{
				size = range.getStop()-range.getStart() + 1;
			}
			count += size;
		}
		return count;
	}


	/**
	 * Returns the number of measures in the Block.
	 * @return
	 */
	public int getBlockLength()
	{
		int length = 0;
		for (Volta volta : map.keySet())
		{
			length += volta.getLength();
		}
		return length;
	}
}
