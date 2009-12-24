/**
 * 
 */
package com.xenoage.zong.data.music.directions;

import com.xenoage.zong.data.music.Direction;
import com.xenoage.zong.data.music.NoVoiceElement;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Class for a DaCapo. This Element has no sign but only text.
 * 
 * @author Uli Teschemacher
 *
 */
public class DaCapo
	extends Direction
{

	private Direction direction;

	@Override public NoVoiceElement deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}

	public void setFine(Direction direction)
	{
		this.direction = direction;
	}
	
	public Direction getDirection()
	{
		return direction;
	}
}
