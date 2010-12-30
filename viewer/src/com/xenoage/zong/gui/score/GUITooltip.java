package com.xenoage.zong.gui.score;

import static com.xenoage.util.math.Rectangle2f.rf;
import static com.xenoage.util.math.Rectangle2i.ri;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.opengl.OpenGLTools;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.app.opengl.text.TextRenderer;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.gui.controller.panels.GLScorePanelController;
import com.xenoage.zong.gui.score.effects.GUIEffect;
import com.xenoage.zong.renderer.GLGraphicsContext;
import com.xenoage.util.Units;


/**
 * Tooltip, consisting of a background texture and a
 * {@link FormattedText}.
 * 
 * The background texture consists of 9 regions with
 * 20% border. The 8 border areas are drawn 20 px wide,
 * the center area and the 4 middle borders are stretched.
 * 
 * @author Andreas Wenger
 */
public class GUITooltip
	extends GUIElement
	implements GUIEffectElement
{
	
	protected FormattedText text;
	
	protected Point2i position;
	protected Size2i size;
	protected GUIElement parent;
	
	protected Shape bounding;
	
	protected Color color = new Color(255, 255, 255, 255);
	
	
	/**
	 * Creates a new {@link GUITooltip} for the given {@link GUIManager} at
	 * the given position (top, left) and size in px, showing the given text.
	 */
	public GUITooltip(GUIManager guiManager, Point2i position, Size2i size,
		FormattedText text, GUIElement parent)
	{
		super(guiManager);
		this.position = position;
		this.size = size;
		this.text = text;
		this.parent = parent;
		recomputeBounding();
	}
	
	
	public GUIElement getParent()
	{
		return parent;
	}
	
	
	/**
   * Paints this element with the given OpenGL context.
   */
  @Override public void paintNormal(GLGraphicsContext context)
  {
    
    //  --------------------------------
    //  |  NW   |      N       |  NE   |   x = 20 px on screen
    //  |  x/x  |     */x      |  x/x  |   x = 20% from texture
    //  --------------------------------
    //  |       |              |       |
    //  |   W   |      C       |   E   |
    //  |  x/*  |     */*      |  x/*  |
    //  |       |              |       |
    //  --------------------------------
    //  |  SW   |      S       |  SE   |
    //  |  x/x  |     */x      |  x/x  |
    //  --------------------------------
    int ws = 20;
    float wt = 0.2f;
    Texture texture = context.getTextureManager().getAppTexture(TextureManager.ID_GUI_TOOLTIP);
    if (size.width < 2 * ws + 100 || size.height < 2 * ws + 100)
    {
    	texture = context.getTextureManager().getAppTexture(TextureManager.ID_GUI_TOOLTIP_SMALL);
    	wt = 0.4f;
    }
    
    //NW
    OpenGLTools.drawImage(texture, rf(0, 0, wt, wt),
    	ri(position.x, position.y, ws, ws),
    	color, context); 
    //N
    OpenGLTools.drawImage(texture, rf(wt, 0, 1 - 2 * wt, wt),
    	ri(position.x + ws, position.y, size.width - 2 * ws, ws),
    	color, context); 
    //NE
    OpenGLTools.drawImage(texture, rf(1 - wt, 0, wt, wt),
    	ri(position.x + size.width - ws, position.y, ws, ws),
    	color, context); 
    //W
    OpenGLTools.drawImage(texture, rf(0, wt, wt, 1 - 2 * wt),
    	ri(position.x, position.y + ws, ws, size.height - 2 * ws),
    	color, context); 
    //C
    OpenGLTools.drawImage(texture, rf(wt, wt, 1 - 2 * wt, 1 - 2 * wt),
    	ri(position.x + ws, position.y + ws, size.width - 2 * ws, size.height - 2 * ws),
    	color, context);
    //E
    OpenGLTools.drawImage(texture, rf(1 - wt, wt, wt, 1 - 2 * wt),
    	ri(position.x + size.width - ws, position.y + ws, ws, size.height - 2 * ws),
    	color, context); 
    //SW
    OpenGLTools.drawImage(texture, rf(0, 1 - wt, wt, wt),
    	ri(position.x, position.y + size.height - ws, ws, ws),
    	color, context); 
    //SW
    OpenGLTools.drawImage(texture, rf(wt, 1 - wt, 1 - 2 * wt, wt),
    	ri(position.x + ws, position.y + size.height - ws, size.width - 2 * ws, ws),
    	color, context); 
    //SE
    OpenGLTools.drawImage(texture, rf(1 - wt, 1 - wt, wt, wt),
    	ri(position.x + size.width - ws, position.y + size.height - ws, ws, ws),
    	color, context); 
    
    //text
    context.getTextRenderer().drawFormattedText(text, position.x + ws,
    	position.y + ws, false, size.width - 2 * ws, Units.mmToPx_1_1, context);
    
  }
  
  
  /**
   * Paints this element with the given OpenGL context, using the
   * given effect.
   */
  @Override public void paintEffect(GLGraphicsContext context, GUIEffect effect)
  {
  	effect.paintUnsupported(this, context);
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
	 * Gets the position of the tooltip in px.
	 */
	@Override public Point2i getPosition()
	{
		return position;
	}


	/**
	 * Gets the size of the tooltip in px.
	 */
	@Override public Size2i getSize()
	{
		return size;
	}
	
	
	/**
	 * Gets the text of the tooltip.
	 */
	public FormattedText getText()
	{
		return text;
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
	

}
