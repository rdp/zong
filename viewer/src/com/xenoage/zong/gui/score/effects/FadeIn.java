package com.xenoage.zong.gui.score.effects;

import java.awt.Color;

import com.sun.opengl.util.texture.Texture;
import com.xenoage.util.MathTools;
import com.xenoage.util.math.Rectangle2i;
import com.xenoage.zong.app.opengl.OpenGLTools;
import com.xenoage.zong.gui.score.GUIButton;
import com.xenoage.zong.gui.score.GUIManager;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * Fade-in GUI effect.
 * Begins painting with 100% transparency and ends with
 * the transparency of the target element.
 * 
 * @author Andreas Wenger
 */
public class FadeIn
	extends GUIEffect
{
	
	/**
	 * Creates a new {@link FadeIn} effect with the given duration in ms
	 * on the given {@link GUIManager}.
	 */
	public FadeIn(int duration, GUIManager guiManager)
	{
		super(duration, guiManager);
	}
	
	
	/**
	 * Paints the given {@link GUIButton} with this effect.
	 */
	@Override public void paintButton(GUIButton button, GLGraphicsContext context)
	{
		//color
		Color targetColor = button.getColor();
		Color color = new Color(targetColor.getRed(), targetColor.getGreen(),
			targetColor.getBlue(), MathTools.clamp((int) (targetColor.getAlpha() * getState()), 0, 255));
		//texture
		Texture texture = context.getTextureManager().getAppTexture(button.getTextureID());
  	//position
  	Rectangle2i destRect = new Rectangle2i(button.getPosition(), button.getSize());
    OpenGLTools.drawImage(texture, destRect, color, context);
	}

}
