package com.xenoage.zong.gui.score.effects;

import com.xenoage.zong.gui.score.GUIElement;
import com.xenoage.zong.gui.score.GUIManager;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * Very simple effect, that shows nothing.
 * 
 * Useful for waiting a given period of time.
 * 
 * Works for all {@link GUIElement}s and works also for the
 * children of the affected elements.
 * 
 * @author Andreas Wenger
 */
public class Hide
	extends GUIEffect
{
	
	/**
	 * Creates a new {@link Hide} effect with the given duration in ms
	 * on the given {@link GUIManager}.
	 */
	public Hide(int duration, GUIManager guiManager)
	{
		super(duration, guiManager);
	}
	
	
	/**
	 * Paints the given {@link GUIElement} with this effect.
	 */
	@Override public void paintUnsupported(GUIElement element, GLGraphicsContext context)
	{
		//paint nothing
	}

}
