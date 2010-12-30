package com.xenoage.zong.gui.score.effects;

import com.xenoage.util.MathTools;
import com.xenoage.zong.gui.score.GUIButton;
import com.xenoage.zong.gui.score.GUIElement;
import com.xenoage.zong.gui.score.GUIManager;
import com.xenoage.zong.gui.score.GUITooltip;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * Base class for GUI effects like fade-in or slide-out.
 * 
 * Currently only {@link GUIButton}s are supported.
 * 
 * @author Andreas Wenger
 */
public abstract class GUIEffect
{
	
	protected int duration;
	protected GUIManager guiManager;
	
	protected long startTime;
	
	
	/**
	 * Creates a new {@link GUIEffect} with the given duration in ms
	 * on the given {@link GUIManager}.
	 */
	public GUIEffect(int duration, GUIManager guiManager)
	{
		this.duration = duration;
		this.guiManager = guiManager;
	}
	
	
	/**
	 * Starts the effect.
	 */
	public void start()
	{
		startTime = System.currentTimeMillis();
		guiManager.requestRepaintsForTime(duration + 500); //+ 500 additional ms
	}
	
	
	/**
	 * Gets a value between 0 and 1, representing the state of the effect.
	 * 0 is the state at the beginning, 1 is the state at the end.
	 */
	public float getState()
	{
		float ret = 1f * (System.currentTimeMillis() - startTime) / duration;
		ret = MathTools.clamp(ret, 0, 1);
		return ret;
	}
	
	
	/**
	 * Returns true, if the effect is finished, otherwise false.
	 */
	public boolean isFinished()
	{
		return System.currentTimeMillis() - startTime > duration;
	}
	
	
	/**
	 * Paints the given {@link GUIButton} with this effect.
	 * By default, there is no effect, but the default paint style is used.
	 */
	public void paintButton(GUIButton button, GLGraphicsContext context)
	{
		paintUnsupported(button, context);
	}
	
	
	/**
	 * Paints the given {@link GUITooltip} with this effect.
	 * By default, there is no effect, but the default paint style is used.
	 */
	public void paintToolTip(GUITooltip tooltip, GLGraphicsContext context)
	{
		paintUnsupported(tooltip, context);
	}
	
	
	/**
	 * Paints the given unsupported {@link GUIElement} with this effect.
	 * There is no effect, but the default paint style is used.
	 */
	public void paintUnsupported(GUIElement element, GLGraphicsContext context)
	{
		element.paintNormal(context);
	}
	

}
