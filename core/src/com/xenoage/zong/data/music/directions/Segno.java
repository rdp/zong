package com.xenoage.zong.data.music.directions;

import com.xenoage.zong.data.music.Direction;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Class for a segno sign.
 *
 * @author Andreas Wenger
 */
public final class Segno
  extends Direction
{
	
	@Override public Direction deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}
  
}
