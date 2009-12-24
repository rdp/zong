package com.xenoage.zong.musiclayout.continued;

import com.xenoage.util.enums.VSide;
import com.xenoage.zong.data.music.CurvedLine;
import com.xenoage.zong.data.music.MusicElement;


/**
 * Continued slur or tie.
 * 
 * The {@link MusicElement} is known, as well as the placement
 * (above or below staff), the global index of the staff
 * and the level of the slur (0: first slur, 1: another slur, ...
 * used for the vertical position, to avoid collisions).
 * 
 * @author Andreas Wenger
 */
public final class ContinuedCurvedLine
	implements ContinuedElement
{
	
	private final CurvedLine curvedLine;
	private final VSide side;
	private final int staffIndex;
	private final int level;
	
	
	public ContinuedCurvedLine(CurvedLine curvedLine, VSide side, int staffIndex, int level)
	{
		this.curvedLine = curvedLine;
		this.side = side;
		this.staffIndex = staffIndex;
		this.level = level;
	}
	
	
	public CurvedLine getMusicElement()
	{
		return curvedLine;
	}
	
	
	public int getStaffIndex()
	{
		return staffIndex;
	}
	
	
	public int getLevel()
	{
		return level;
	}
	
	
	/**
   * Gets the placement of the slur: above or below.
   * TODO: add more possibilities like S-curved slurs.
   */
  public VSide getSide()
  {
  	return side;
  }
	

}
