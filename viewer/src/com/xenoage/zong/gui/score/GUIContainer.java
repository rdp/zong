package com.xenoage.zong.gui.score;

import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;

import com.xenoage.util.math.Point2i;
import com.xenoage.zong.gui.score.layout.LayoutInfo;
import com.xenoage.zong.gui.score.layout.LayoutManager;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * Abstract base class for all {@link GUIElement}s
 * that may have children.
 * 
 * @author Andreas Wenger
 */
public abstract class GUIContainer
	extends GUIElement
{
	
	protected LayoutManager layoutManager;
	
	//GUI elements
	protected LinkedList<GUIElement> elements = new LinkedList<GUIElement>();
	protected GUIElement hoveredElement = null;
	
	
	/**
	 * Creates a new {@link GUIContainer} for the given {@link GUIManager},
	 * using the given {@link LayoutManager}.
	 */
	public GUIContainer(GUIManager guiManager, LayoutManager layoutManager)
	{
		super(guiManager);
		this.layoutManager = layoutManager;
	}
	
	
	/**
	 * Adds a {@link GUIElement}. This element exists, until it is removed by
	 * calling {@code removeGUIElement}. The {@code repaint} method must be
	 * called after this method if repainting the view is required.
	 */
	public void addElement(GUIElement element)
	{
		elements.add(element);
		element.setParent(this);
		layoutManager.layout(this);
	}
	
	
	/**
	 * Adds a {@link GUIElement}, using the given layout information.
	 * This element exists, until it is removed by
	 * calling {@code removeGUIElement}. The {@code repaint} method must be
	 * called after this method if repainting the view is required.
	 */
	public void addElement(GUIElement element, LayoutInfo layoutInfo)
	{
		element.setLayoutInfo(layoutInfo);
		elements.add(element);
		element.setParent(this);
		layoutManager.layout(this);
	}
	
	
	/**
   * Paints this element with the given OpenGL context.
   * If an effect is active, it is used.
   */
  @Override public void paint(GLGraphicsContext context)
  {
  	//remove dead objects
  	Iterator<GUIElement> elements = this.elements.iterator();
  	while (elements.hasNext())
  	{
  		GUIElement e = elements.next();
  		if (e.isDead())
  			elements.remove();
  	}
  	layoutManager.layout(this);
  	//paint children
  	super.paint(context);
  }
	
	
	
	/**
   * Paints this element with the given OpenGL context.
   */
  @Override public void paintNormal(GLGraphicsContext context)
  {
  	GL gl = context.getGL();
  	//paint children
  	gl.glPushMatrix();
  	Point2i position = getPosition();
  	gl.glTranslatef(position.x, position.y, 0);
  	for (GUIElement b : elements)
  	{
  		b.paint(context);
  	}
  	gl.glPopMatrix();
  }
	
	
	/**
   * Updates the hover states of the elements, based on the given {@link MouseEvent}.
   * The mouse is currently over the given element (or null).
   */
  public void updateHover(GUIElement hoveredElement, MouseEvent e)
  {
  	if (this.hoveredElement != hoveredElement)
  	{
  		if (this.hoveredElement != null)
  		{
  			Point2i p = this.hoveredElement.getPosition();
  			e.translatePoint(-p.x, -p.y);
  			this.hoveredElement.mouseExited(e);
  			this.hoveredElement.mouseExitedTooltip(e);
  			e.translatePoint(p.x, p.y);
  		}
  		this.hoveredElement = hoveredElement;
  		if (this.hoveredElement != null)
  		{
  			Point2i p = this.hoveredElement.getPosition();
  			e.translatePoint(-p.x, -p.y);
  			this.hoveredElement.mouseEntered(e);
  			this.hoveredElement.mouseEnteredTooltip(e);
  			e.translatePoint(p.x, p.y);
  		}
  	}
  }
  
  
  /**
   * Gets the list of child elements in this container.
   */
  public List<GUIElement> getElements()
  {
  	return elements;
  }
  
  
  /**
	 * Gets the active element at the given position in px (relative to the
	 * top left corner of this panel), or null if there is none.
	 */
	public GUIElement getElementAt(Point2i p)
	{
		for (GUIElement e : elements)
		{
			if (e.getState() == State.Active)
			{
				Shape bounding = e.getBoundingShape();
				Point2i ePos = e.getPosition();
				if (bounding != null && bounding.contains(p.x - ePos.x, p.y - ePos.y))
				{
					return e;
				}
			}
		}
		return null;
	}
	
	
	/**
   * This method is called when the mouse has been clicked within the bounding
   * shape of this element, but only when its state is active.
   */
	@Override public void mouseClicked(MouseEvent e)
	{
		GUIElement target = getElementAt(new Point2i(e.getX(), e.getY()));
		if (target != null)
		{
			Point2i p = target.getPosition();
			e.translatePoint(-p.x, -p.y);
			target.mouseClicked(e);
			e.translatePoint(p.x, p.y);
		}
	}
	
	
	/**
   * This method is called when the mouse entered the bounding
   * shape of this element, but only when its state is active.
   */
	@Override public void mouseEntered(MouseEvent e)
	{
	}
	
	
	/**
   * This method is called when the mouse entered the bounding
   * shape of this element, but only when its state is active.
   */
	@Override public void mouseExited(MouseEvent e)
	{
		updateHover(null, e);
	}
	
	
	/**
   * This method is called when the mouse has been moved over the panel,
   * but only when its state is active.
   */
	@Override public void mouseMoved(MouseEvent e)
	{
		GUIElement target = getElementAt(new Point2i(e.getX(), e.getY()));
		updateHover(target, e);
		if (target != null)
		{
			Point2i p = target.getPosition();
			e.translatePoint(-p.x, -p.y);
			target.mouseMoved(e);
			e.translatePoint(p.x, p.y);
		}
	}
	
	
	/**
   * This method is called when the mouse has been dragged over the panel,
   * but only when its state is active.
   */
	@Override public void mouseDragged(MouseEvent e)
	{
		GUIElement target = getElementAt(new Point2i(e.getX(), e.getY()));
		updateHover(target, e);
		if (target != null)
		{
			Point2i p = target.getPosition();
			e.translatePoint(-p.x, -p.y);
			target.mouseDragged(e);
			e.translatePoint(p.x, p.y);
		}
	}


}
