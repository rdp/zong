package com.xenoage.zong.gui.score.layout;

import com.xenoage.zong.gui.score.GUIContainer;


/**
 * Interface for all layout managers for {@link GUIContainer}s.
 * 
 * @author Andreas Wenger
 */
public interface LayoutManager
{
	
	
	/**
	 * Recomputes the positions and the sizes of the children
	 * of the given container.
	 */
	public abstract void layout(GUIContainer container);

}
