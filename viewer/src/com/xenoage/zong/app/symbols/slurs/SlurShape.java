package com.xenoage.zong.app.symbols.slurs;

import java.awt.Color;
import java.awt.Shape;

import com.xenoage.util.math.Point2f;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * Strategy to form a {@link Slur}.
 * 
 * There are methods which return the {@link Shape} to be used for printing
 * and to draw the slur with OpenGL.
 * 
 * @author Andreas Wenger
 */
public interface SlurShape
{
	
	//TODO (for more complicated slurs like S-slurs): different algorithms for different symbol pools.
  //for example printed style bounding quads look like this (because vertical lines are thinner than horizontal ones)
  //        ______
  //   __--|      |--___
  //  |    |______|  |   |
  //  |__--        --|___|
  //(this is the DefaultShapeStrategy)
  //
  //while handwritten style bounding quads look more than this (because they have equal line width everywhere).
  //        _______
  //  ___--\       /--____
  // \   \  |_____|   /   /
  //  \___\--     -- /___/
	
	
	/**
   * Creates the shape of a slur, using the given Bézier curve.
   * @param p1      the starting point in px
   * @param p2      the ending point in px
   * @param c1      the first control point in px
   * @param c2      the second control point in px
   * @param interlineSpace  the interline space in px
   */
  public void setSlur(Point2f p1, Point2f p2, Point2f c1, Point2f c2,
  	float interlineSpace);

	
	/**
	 * Gets the {@link Shape} of the slur, needed for printing.
	 */
	public Shape getShape();

	
	/**
	 * Draws the slur with OpenGL, using the given context and color.
	 */
	public void draw(GLGraphicsContext gl, Color color);

}
