package com.xenoage.zong.gui.score.buttons;

import java.awt.Color;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.commands.help.WebsiteCommand;
import com.xenoage.zong.gui.score.GUIButton;
import com.xenoage.zong.gui.score.GUIManager;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * Button for the viewer, showing the logo.
 * 
 * @author Andreas Wenger
 */
public class LogoButton
	extends GUIButton
{
	
	
	public LogoButton(GUIManager guiManager, Point2i position, Size2i size)
	{
		super(guiManager, position, size, TextureManager.ID_GUI_LOGO_VIEWER);
		//no transparency
		normalColor = new Color(1f, 1f, 1f, 0.8f);
	}
	
	
	@Override public void mouseClicked(MouseEvent e)
	{
		super.mouseClicked(e);
		//TODO: command performer for general tasks!!
		App.getInstance().getScoreDocument().getCommandPerformer().execute(new WebsiteCommand());
	}
	
	
	/**
   * Gets the bounding shape of this element.
   */
  @Override public Shape getBoundingShape()
  {
  	return new Rectangle2D.Float(0, 2 * size.height / 5, size.width, size.height / 2);
  }
  
  
  /**
   * Paints this element with the given OpenGL context.
   */
  @Override public void paintNormal(GLGraphicsContext context)
  {
  	//no hover effect
  	paintNormalState(context);
  }

}
