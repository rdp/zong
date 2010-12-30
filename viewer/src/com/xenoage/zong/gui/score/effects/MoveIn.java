package com.xenoage.zong.gui.score.effects;

import javax.media.opengl.GL;

import com.xenoage.util.math.Point2i;
import com.xenoage.zong.gui.score.GUIElement;
import com.xenoage.zong.gui.score.GUIManager;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * Move-in GUI effect.
 * 
 * Begins painting at the given position and ends at the normal
 * position of the element.
 * 
 * Works for all {@link GUIElement}s and works also for the
 * children of the affected elements.
 * 
 * @author Andreas Wenger
 */
public class MoveIn
	extends GUIEffect
{
	
	private Point2i startPosition;
	
	
	/**
	 * Creates a new {@link MoveIn} effect with the given duration in ms
	 * on the given {@link GUIManager}. The startPosition is relative
	 * to the normal position of the element!
	 */
	public MoveIn(int duration, Point2i startPosition, GUIManager guiManager)
	{
		super(duration, guiManager);
		this.startPosition = startPosition;
	}
	
	
	/**
	 * Paints the given {@link GUIElement} with this effect.
	 */
	@Override public void paintUnsupported(GUIElement element, GLGraphicsContext context)
	{
		GL gl = context.getGL();
		gl.glPushMatrix();
		Point2i p = new Point2i((int) (startPosition.x * (1 - getState())),
			(int) (startPosition.y * (1 - getState())));
		gl.glTranslatef(p.x, p.y, 0);
		element.paintNormal(context);
		gl.glPopMatrix();
	}

}
