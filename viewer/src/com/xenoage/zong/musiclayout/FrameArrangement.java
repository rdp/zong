package com.xenoage.zong.musiclayout;

import static com.xenoage.util.ArrayTools.containsNull;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.math.Size2f;


/**
 * A frame arrangement contains the arrangement
 * of systems on a single score frame.
 *
 * @author Andreas Wenger
 */
public final class FrameArrangement
{

  private final PVector<SystemArrangement> systems;
  private final Size2f usableSize;
  
  
  /**
   * Creates a new FrameArrangement, containing
   * the given systems.
   */
  public FrameArrangement(PVector<SystemArrangement> systems, Size2f usableSize)
  {
  	//null values are not allowed
  	if (containsNull(systems) || usableSize == null)
  		throw new IllegalArgumentException("Systems and size may not be null");
  	this.systems = systems;
  	this.usableSize = usableSize;
  }
  
  
  /**
   * Gets the systems.
   */
  public PVector<SystemArrangement> getSystems()
  {
    return systems;
  }


  /**
   * Gets the size in mm this frame arrangement may use.
   */
  public Size2f getUsableSize()
  {
    return usableSize;
  }
  
  
  /**
   * Returns a copy of this object, but with the given
   * {@link SystemArrangement} at the given position.
   */
  public FrameArrangement withSystem(SystemArrangement system, int systemIndex)
  {
  	return new FrameArrangement(systems.with(systemIndex, system), usableSize);
  }
  
  
  /**
   * Returns a copy of this object, but with the given
   * {@link SystemArrangement} added.
   */
  public FrameArrangement plusSystem(SystemArrangement system)
  {
  	return new FrameArrangement(systems.plus(system), usableSize);
  }
  
  
  /**
   * Gets the index of the first measure, or -1 if there are no measures.
   */
  public int getStartMeasureIndex()
  {
  	if (systems.size() == 0)
  		return -1;
  	else
  		return systems.getFirst().getStartMeasureIndex();
  }
  
  
  /**
   * Gets the index of the last measure, or -1 if there are no measures.
   */
  public int getEndMeasureIndex()
  {
  	if (systems.size() == 0)
  		return -1;
  	else
  		return systems.getLast().getEndMeasureIndex();
  }
  
  
}
