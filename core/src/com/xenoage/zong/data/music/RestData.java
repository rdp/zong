package com.xenoage.zong.data.music;

import com.xenoage.util.math.Fraction;


/**
 * Musical data of a {@link Rest} element.
 * {@link RestData} instances are not bound to a certain
 * voice but can exist unattachedly.
 *
 * @author Andreas Wenger
 */
public final class RestData
{
	
	//the duration of the rest
  private final Fraction duration;
  
  //the list of dots
  //private final Dot[] dots; //TODO
  
  //the line position of the break
  //private final int line; //TODO
  
  
  public RestData(Fraction duration)
  {
    this.duration = duration;
  }
  
  
  public Fraction getDuration()
  {
    return duration;
  }
  

}
