package com.xenoage.zong.gui.score;

import java.awt.Color;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import com.sun.opengl.util.texture.Texture;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Rectangle2i;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.opengl.OpenGLTools;
import com.xenoage.zong.app.tools.Tool;
import com.xenoage.zong.commands.Command;
import com.xenoage.zong.gui.controller.panels.ScorePanelController.CursorLevel;
import com.xenoage.zong.gui.cursor.Cursor;
import com.xenoage.zong.gui.score.effects.GUIEffect;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * Simple GUI button, consisting of a texture.
 * 
 * If the mouse cursor is not over the button, it is drawn
 * with some transparency. If the mouse cursor is over it, it
 * is drawn opaquely and 10% bigger.
 * 
 * When clicked, a given {@link Command} is performed or a
 * given {@link Tool} is activated.
 * 
 * @author Andreas Wenger
 */
public class GUIButton
	extends GUIElement
	implements GUIEffectElement
{
	
	protected Point2i position;
	protected Size2i size;
	protected int textureID;
	protected Shape bounding;
	
	protected boolean hover = false;
	
	protected Color normalColor = new Color(1f, 1f, 1f, 0.85f);
	protected Color hoverColor = new Color(1f, 1f, 1f, 1f);
	
	
	/**
	 * Creates a new {@link GUIButton} for the given {@link GUIManager} at
	 * the given position (top, left) and size in px, using the given
	 * texture ID.
	 */
	public GUIButton(GUIManager guiManager, Point2i position, Size2i size,
		int textureID)
	{
		super(guiManager);
		this.position = position;
		this.size = size;
		this.textureID = textureID;
		recomputeBounding();
	}
	
	
	/**
   * Paints this element with the given OpenGL context.
   */
  @Override public void paintNormal(GLGraphicsContext context)
  {
    if (hover)
    {
    	paintHoverState(context);
    }
    else
    {
    	paintNormalState(context);
    }
  }
  
  
  /**
   * Paints this element with the given OpenGL context in normal state.
   */
  protected void paintNormalState(GLGraphicsContext context)
  {
  	Color color = normalColor;
  	Rectangle2i destRect = new Rectangle2i(position, size);
    Texture texture = context.getTextureManager().getAppTexture(textureID);
    OpenGLTools.drawImage(texture, destRect, color, context);
  }
  
  
  /**
   * Paints this element with the given OpenGL context in hover state.
   */
  protected void paintHoverState(GLGraphicsContext context)
  {
  	Color color = hoverColor;
  	int dx = (int) (size.width * 0.1f);
  	int dy = (int) (size.height * 0.1f);
  	Rectangle2i destRect = new Rectangle2i(
  		position.x - dx, position.y - dy, size.width + 2 * dx, size.height + 2 * dy);
    Texture texture = context.getTextureManager().getAppTexture(textureID);
    OpenGLTools.drawImage(texture, destRect, color, context);
  }
  
  
  /**
   * Paints this element with the given OpenGL context, using the
   * given effect.
   */
  @Override public void paintEffect(GLGraphicsContext context, GUIEffect effect)
  {
  	effect.paintButton(this, context);
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
   * This method is called when the mouse entered the bounding
   * shape of this element, but only when its state is active.
   */
	@Override public void mouseEntered(MouseEvent e)
	{
		this.hover = true;
		App.getInstance().getScorePanelController().setCursor(Cursor.IndexFinger, CursorLevel.GUI);
		guiManager.repaint();
	}
	
	
	/**
   * This method is called when the mouse entered the bounding
   * shape of this element, but only when its state is active.
   */
	@Override public void mouseExited(MouseEvent e)
	{
		this.hover = false;
		App.getInstance().getScorePanelController().setCursor(null, CursorLevel.GUI);
		guiManager.repaint();
	}


	/**
	 * Gets the position of the button in px.
	 */
	@Override public Point2i getPosition()
	{
		return position;
	}


	/**
	 * Gets the size of the button in px.
	 */
	@Override public Size2i getSize()
	{
		return size;
	}
	
	
	/**
	 * Gets the ID of the texture.
	 */
	public int getTextureID()
	{
		return textureID;
	}
	
	
	/**
	 * Gets the normal (not-hover) color of the button.
	 */
	public Color getColor()
	{
		return normalColor;
	}
	
	
	/**
   * Sets the position (upper left corner) in px.
   */
  @Override public void setPosition(Point2i position)
  {
  	this.position = position;
  	recomputeBounding();
  }
  
  
  /**
   * Sets the size in px.
   */
  @Override public void setSize(Size2i size)
  {
  	this.size = size;
  	recomputeBounding();
  }
  
  
  private void recomputeBounding()
  {
  	this.bounding = new Rectangle2D.Float(0, 0, size.width, size.height);
  }
  
  
  @Override public void mouseClicked(MouseEvent e)
	{
		this.hover = false;
		if (tooltip != null)
		{
			guiManager.getTooltipManager().unregisterTooltip(tooltip);
		}
	}
	

}
