package com.xenoage.zong.gui.score.effects;

import java.awt.Color;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;
import com.xenoage.util.MathTools;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Rectangle2i;
import com.xenoage.zong.app.opengl.OpenGLTools;
import com.xenoage.zong.gui.score.GUIButton;
import com.xenoage.zong.gui.score.GUIElement;
import com.xenoage.zong.gui.score.GUIManager;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * Slide-in or slide-out GUI effect.
 * 
 * Using slide-in: Begins paintining at a given position relative to
 * the normal position and ends at the normal position.
 * During the animation, the size grows from 0 to the
 * normal size. 
 * 
 * Using slide-out: The same in the other direction.
 * 
 * Special additional feature for {@link GUIButton}:
 * Begins painting with 100% transparency and ends with
 * the transparency of the target element.
 * 
 * @author Andreas Wenger
 */
public class SlideEffect
	extends GUIEffect
{
	
	private EffectType type;
	private Point2i startPosition;
	
	
	/**
	 * Creates a new {@link SlideEffect} effect with the given direction and duration in ms
	 * on the given {@link GUIManager}. The startPosition is relative
	 * to the normal position of the element!
	 */
	public SlideEffect(EffectType type, int duration, Point2i startPosition, GUIManager guiManager)
	{
		super(duration, guiManager);
		this.type = type;
		this.startPosition = startPosition;
	}
	
	
	/**
	 * Paints the given {@link GUIButton} with this effect.
	 */
	@Override public void paintButton(GUIButton button, GLGraphicsContext context)
	{
		start(context);
		//color
		Color targetColor = button.getColor();
		Color color = new Color(targetColor.getRed(), targetColor.getGreen(),
			targetColor.getBlue(), MathTools.clamp((int) (targetColor.getAlpha() * getState()), 0, 255));
		//texture
		Texture texture = context.getTextureManager().getAppTexture(button.getTextureID());
  	//position
  	Rectangle2i destRect = new Rectangle2i(button.getPosition(), button.getSize());
    OpenGLTools.drawImage(texture, destRect, color, context);
    end(context);
	}
	
	
	/**
	 * Paints the given {@link GUIElement} with this effect.
	 */
	@Override public void paintUnsupported(GUIElement element, GLGraphicsContext context)
	{
		start(context);
		element.paintNormal(context);
		end(context);
	}
	
	
	private void start(GLGraphicsContext context)
	{
		GL gl = context.getGL();
		gl.glPushMatrix();
		float state = (type == EffectType.In ? getState() : 1 - getState());
		Point2i p = new Point2i((int) (startPosition.x * (1 - state)),
			(int) (startPosition.y * (1 - state)));
		gl.glTranslatef(p.x, p.y, 0);
		gl.glScalef(state, state, 0);
	}
	
	
	private void end(GLGraphicsContext context)
	{
		GL gl = context.getGL();
		gl.glPopMatrix();
	}
	

}
