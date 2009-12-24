package com.xenoage.zong.musiclayout.stampings;

import java.util.ArrayList;
import java.util.List;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Shape;
import com.xenoage.zong.data.music.MusicElement;
import com.xenoage.zong.renderer.RenderingParams;


/**
 * Class for an stamping. Stampings can
 * be visible objects like notes, clefs, texts, but
 * also invisible objects like empty rooms between
 * staves and so on are possible.
 * 
 * Stamps were used in the early days of music notation to
 * paint the symbols. This class is called stamping, because it
 * is the result of placing a stamp, that means, in most cases,
 * a given symbol at a given position. 
 * 
 * Stampings can be painted of course. Each stamping
 * can delegate the painting to another class, e.g.
 * special renderers for this stamping.
 *
 * @author Andreas Wenger
 */
public abstract class Stamping
{
  
  //level
  public static final int LEVEL_EMPTYSPACE = 0; //empty space
  public static final int LEVEL_STAFF = 1; //staff
  public static final int LEVEL_MUSIC = 2; //notes, barlines, ...
  public static final int LEVEL_TEXT = 2; //text, dynamic symbols, ...
  private int level;
  
  //parent staff stamping
  protected StaffStamping parentStaff = null;

  //bounding geometry
  private ArrayList<Shape> boundingShapes = new ArrayList<Shape>();
  
  //the musical element for which this stamping was created,
  //or null, if not availabe (e.g. for staves)
  //this may be another element than expected, e.g. an accidental layout
  //element may refer to a chord musical element.
  private MusicElement musicElement;
  
  
  /**
   * Creates a new stamping that belongs to the given
   * staff element. It may also belong to more than only this
   * staff.
   * @param parentStaff   the parent staff stamping
   * @param level         the level, like LEVEL_STAFF or LEVEL_TEXT
   * @param musicElement  the musical element for which this stamping was created, or null
   */
  public Stamping(StaffStamping parentStaff, int level, MusicElement musicElement)
  {
    this.level = level;
    this.parentStaff = parentStaff;
    this.musicElement = musicElement;
  }
  
  
  /**
   * Creates a new stamping that belongs to no
   * staff element.
   * @param level         the level, like LEVEL_STAFF or LEVEL_TEXT
   */
  public Stamping(int level, MusicElement musicElement)
  {
    this.level = level;
    this.parentStaff = null;
    this.musicElement = musicElement;
  }
  
  
  /**
   * Gets the level of this element, like LEVEL_STAFF or LEVEL_TEXT.
   */
  public int getLevel()
  {
    return level;
  }
  
  
  protected void clearBoundingShape()
  {
    boundingShapes.clear();
  }
  
  
  protected void addBoundingShape(Shape boundingShape)
  {
    boundingShapes.add(boundingShape);
  }
  
  
  public List<Shape> getBoundingShapes()
  {
    return boundingShapes;
  }
  
  
  /**
   * Returns true, if at least one of the bounding shapes
   * of this element contains the given point, otherwise false.
   */
  public boolean containsPoint(Point2f point)
  {
    for (Shape s : boundingShapes)
    {
      if (s.contains(point))
        return true;
    }
    return false;
  }


  /**
   * Gets the parent staff stamping of this staff or null.
   * This is important for the renderer, when it needs some
   * information from the parent staff of this element.
   */
  public StaffStamping getParentStaff()
  {
    return parentStaff;
  }
  
  
  /**
   * Gets the musical element for which this stamping was created, or null.
   */
  public MusicElement getMusicElement()
  {
    return musicElement;
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  public abstract void paint(RenderingParams params);
  
  
}
