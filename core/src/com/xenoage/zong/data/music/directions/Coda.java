package com.xenoage.zong.data.music.directions;

import com.xenoage.zong.data.music.Direction;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Class for a coda sign.
 *
 * @author Andreas Wenger
 */
public final class Coda
  extends Direction
{
	
	@Override public Coda deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}
  
}
