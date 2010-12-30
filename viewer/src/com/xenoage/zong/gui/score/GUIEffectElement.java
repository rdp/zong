package com.xenoage.zong.gui.score;

import com.xenoage.zong.gui.score.effects.GUIEffect;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * Interface for all {@link GUIElement}s that are supported
 * by {@link GUIEffect}s.
 * 
 * @author Andreas Wenger
 */
public interface GUIEffectElement
{
	
	/**
   * Paints this element with the given OpenGL context, using the
   * given effect.
   */
  public void paintEffect(GLGraphicsContext context, GUIEffect effect);

}
