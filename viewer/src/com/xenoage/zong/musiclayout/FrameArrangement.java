package com.xenoage.zong.musiclayout;

import static com.xenoage.util.ArrayTools.containsNull;

import com.xenoage.util.math.Size2f;


/**
 * A frame arrangement contains the arrangement
 * of systems on a single score frame.
 *
 * @author Andreas Wenger
 */
public final class FrameArrangement
{

  private final SystemArrangement[] systems;
  private final Size2f usableSize;
  
  
  /**
   * Creates a new FrameArrangement, containing
   * the given systems.
   */
  public FrameArrangement(SystemArrangement[] systems, Size2f usableSize)
  {
  	//null values are not allowed
  	if (containsNull(systems) || usableSize == null)
  		throw new IllegalArgumentException("Systems and size may not be null");
  	this.systems = systems;
  	this.usableSize = usableSize;
  }
  
  
  /**
   * Gets the number of systems on this frame.
   */
  public int getSystemsCount()
  {
    return systems.length;
  }
  
  
  /**
   * Gets the system with the given index.
   */
  public SystemArrangement getSystem(int index)
  {
    return systems[index];
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
   * SystemArrangement at the given position.
   */
  public FrameArrangement changeSystem(SystemArrangement system, int systemIndex)
  {
  	SystemArrangement[] newSystems = new SystemArrangement[systems.length];
  	for (int i = 0; i < systems.length; i++)
  	{
  		if (i == systemIndex)
  			newSystems[i] = system;
  		else
  			newSystems[i] = systems[i];
  	}
  	return new FrameArrangement(newSystems, usableSize);
  }
  
  
  /**
   * Returns a copy of this object, but with the given
   * SystemArrangement added.
   */
  public FrameArrangement addSystem(SystemArrangement system)
  {
  	SystemArrangement[] newSystems = new SystemArrangement[systems.length + 1];
  	for (int i = 0; i < systems.length; i++)
  	{
  		newSystems[i] = systems[i];
  	}
  	newSystems[systems.length] = system;
  	return new FrameArrangement(newSystems, usableSize);
  }
  
  
  /**
   * Gets the index of the first measure, or -1 if there are no measures.
   */
  public int getStartMeasureIndex()
  {
  	if (systems.length == 0)
  		return -1;
  	else
  		return systems[0].getStartMeasureIndex();
  }
  
  
  /**
   * Gets the index of the last measure, or -1 if there are no measures.
   */
  public int getEndMeasureIndex()
  {
  	if (systems.length == 0)
  		return -1;
  	else
  		return systems[systems.length - 1].getEndMeasureIndex();
  }
  
  
}
