package com.xenoage.zong.data.music;

import java.util.List;

import com.xenoage.util.math.Fraction;


/**
 * Interface for all musical elements that
 * have a duration, like notes and rests.
 * 
 * They can also have one or more dots,
 * which show their prolongation.
 * 
 * Directions (like dynamics) can be attached to
 * them, too.
 *
 * @author Andreas Wenger
 */
public interface DurationElement
{
  
  
  /**
   * Gets the duration.
   */
  public Fraction getDuration();
  
  
  /**
   * Gets the list of prolongation dots.
   */
  //TODO public List<Dot> getDots();
  
  
  /**
   * Gets a list of the {@link Direction}s attached to this element,
   * or null if there are none.
   */
  public List<Direction> getDirections();
  

}
