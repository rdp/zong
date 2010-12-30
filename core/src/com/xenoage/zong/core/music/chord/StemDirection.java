package com.xenoage.zong.core.music.chord;


/**
 * Direction of a chord stem.
 *
 * @author Andreas Wenger
 */
public enum StemDirection
{
	None,
	Up,
	Down;
	
	
	/**
	 * Gets the direction of the stem as its signum:
	 * 1 for up, -1 for down, 0 for none.
	 */
	public int getSignum()
	{
		switch (this)
		{
			case Up: return 1;
			case Down: return -1;
			default: return 0;
		}
	}
	
}