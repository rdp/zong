package com.xenoage.zong.data.music.directions;

import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.format.Position;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Diminuendo.
 * 
 * @author Andreas Wenger
 */
public class Diminuendo
	extends Wedge
{
	
	
	/**
	 * Creates a new {@link Diminuendo} with the given spread in IS,
	 * or null for default.
	 */
	public Diminuendo(Float spread, Position position)
	{
		super(spread, position);
	}
	
	
	@Override public Diminuendo deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}

}
