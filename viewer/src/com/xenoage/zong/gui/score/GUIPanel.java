package com.xenoage.zong.gui.score;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.gui.score.layout.LayoutManager;


/**
 * Panel for GUI elements, vertically aligned at the top right corner.
 * 
 * @author Andreas Wenger
 */
public class GUIPanel
	extends GUIContainer
{
	
	private Point2i position;
	private Size2i size;
	
	private Shape bounding;
	
	
	/**
	 * Creates a new {@link GUIPanel} for the given {@link GUIManager}
	 * having the given position, size and {@link LayoutManager}.
	 */
	public GUIPanel(GUIManager guiManager, Point2i position, Size2i size,
		LayoutManager layoutManager)
	{
		super(guiManager, layoutManager);
		this.position = position;
		this.size = size;
		layout();
	}
	
	
	/**
	 * Creates a new {@link GUIPanel} for the given {@link GUIManager}
	 * using the given {@link LayoutManager}. The position and size is
	 * 0 at the beginning.
	 */
	public GUIPanel(GUIManager guiManager, LayoutManager layoutManager)
	{
		super(guiManager, layoutManager);
		this.position = new Point2i(0, 0);
		this.size = new Size2i(0, 0);
		layout();
	}
  
  
  /**
   * Gets the bounding shape of this element (or null, if there is none,
   * e.g. because this elements is not interested in mouse events).
   */
  @Override public Shape getBoundingShape()
  {
  	return bounding;
  }

  
  /**
	 * Gets the position of the panel in px.
	 */
	@Override public Point2i getPosition()
	{
		return position;
	}


	/**
	 * Gets the size of the panel in px.
	 */
	@Override public Size2i getSize()
	{
		return size;
	}
  
  
  /**
   * Sets the position (upper left corner) in px.
   */
  @Override public void setPosition(Point2i position)
  {
  	this.position = position;
  }
  
  
  /**
   * Sets the size in px.
   */
  @Override public void setSize(Size2i size)
  {
  	this.size = size;
  	layout();
  }
  
  
  /**
   * Recomputes the layout and the bounding shape of this panel.
   */
  private void layout()
  {
  	//layout
  	layoutManager.layout(this);
  	//bounding shape
  	GeneralPath bounding = new GeneralPath();
  	for (GUIElement e : elements)
  	{
  		//add only elements having a bounding shape
  		if (e.getBoundingShape() != null)
  		{
  			//TODO: not simply a rectangle, but copy the shape
	  		Point2i position = e.getPosition();
	  		Size2i size = e.getSize();
	  		bounding.append(new Rectangle2D.Float(
	  			position.x, position.y, size.width, size.height), false);
  		}
  	}
  	this.bounding = bounding;
  }
	

}
