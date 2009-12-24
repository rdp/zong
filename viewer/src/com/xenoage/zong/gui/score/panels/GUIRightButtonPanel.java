package com.xenoage.zong.gui.score.panels;

import javax.media.opengl.GL;

import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.gui.score.GUIManager;
import com.xenoage.zong.gui.score.GUIPanel;
import com.xenoage.zong.gui.score.layout.ColumnLayoutManager;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * Panel for GUI elements, vertically aligned.
 * Should be placed at the top right corner.
 * 
 * @author Andreas Wenger
 */
public class GUIRightButtonPanel
	extends GUIPanel
{
	
	
	/**
	 * Creates a new {@link GUIRightButtonPanel} for the given {@link GUIManager}.
	 */
	public GUIRightButtonPanel(GUIManager guiManager)
	{
		super(guiManager, new ColumnLayoutManager(10, 8, 10, 32, 10));
	}
	
	
	/**
   * Paints this element with the given OpenGL context.
   */
  @Override public void paintNormal(GLGraphicsContext context)
  {
  	GL gl = context.getGL();
  	Point2i position = getPosition();
  	Size2i size = getSize();
  	//paint background
  	gl.glEnable(GL.GL_BLEND);
    gl.glEnable(GL.GL_TEXTURE_2D);
    context.getTextureManager().activateAppTexture(TextureManager.ID_GUI_BUTTONPANEL);
  	gl.glColor4f(1f, 1f, 1f, 0.9f);
  	gl.glBegin(GL.GL_QUADS);
  	//top
    gl.glTexCoord2f(0,0); gl.glVertex3f(position.x, position.y, 0);
    gl.glTexCoord2f(1,0); gl.glVertex3f(position.x + size.width, position.y, 0);
    gl.glTexCoord2f(1,0.5f); gl.glVertex3f(position.x + size.width, position.y + size.height - size.width, 0);
    gl.glTexCoord2f(0,0.5f); gl.glVertex3f(position.x, position.y + size.height - size.width, 0);
    //bottom
    gl.glTexCoord2f(0,0.5f); gl.glVertex3f(position.x, position.y + size.height - size.width, 0);
    gl.glTexCoord2f(1,0.5f); gl.glVertex3f(position.x + size.width, position.y + size.height - size.width, 0);
    gl.glTexCoord2f(1,1f); gl.glVertex3f(position.x + size.width, position.y + size.height, 0);
    gl.glTexCoord2f(0,1f); gl.glVertex3f(position.x, position.y + size.height, 0);
    gl.glEnd();
  	//paint elements
    super.paintNormal(context);
  }
	

}
