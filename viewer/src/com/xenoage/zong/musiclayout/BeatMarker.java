package com.xenoage.zong.musiclayout;

import com.xenoage.util.math.Fraction;


/**
 * A beat marker is simply the
 * assingment of a horizontal coordinate in mm
 * relative to the begin of a staff to
 * a musical position within this staff.
 * 
 * Internally this class uses only a beat fraction,
 * the staff index and voice
 * index are not interesting, since this is managed
 * by the {@link StaffMarks} class.
 * 
 * This class is comparable to other instances of this
 * class by comparing the beat.
 * 
 * @author Andreas Wenger
 */
public final class BeatMarker
	implements Comparable<BeatMarker>
{
  
  private final float xMm;
  private final Fraction beat;
  
  
  public BeatMarker(float xMm, Fraction beat)
  {
    this.xMm = xMm;
    this.beat = beat;
  }

  
  public float getXMm()
  {
    return xMm;
  }
  
  
  public Fraction getBeat()
  {
    return beat;
  }


	public int compareTo(BeatMarker o)
	{
		return beat.compareTo(o.beat);
	}
	
	
	@Override public String toString()
	{
		return beat.toString() + " at " + xMm;
	}


}
