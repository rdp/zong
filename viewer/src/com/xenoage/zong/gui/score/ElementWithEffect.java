package com.xenoage.zong.gui.score;

import com.xenoage.zong.gui.score.effects.GUIEffect;


/**
 * {@link GUIElement} which is assigned to a {@link GUIEffect}.
 * 
 * @author Andreas Wenger
 */
public class ElementWithEffect
{
	
	public GUIElement element;
	public GUIEffect effect;
	
	
	public ElementWithEffect(GUIElement element, GUIEffect effect)
	{
		this.element = element;
		this.effect = effect;
	}
	

}
