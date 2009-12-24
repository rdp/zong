package com.xenoage.zong.gui.score.layout;

import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.gui.score.GUIContainer;
import com.xenoage.zong.gui.score.GUIElement;


/**
 * This layout manager layouts only the elements of its
 * container by placing them in the given corner of their parent
 * element.
 * 
 * @author Andreas Wenger
 */
public class CornerLayoutManager
	implements LayoutManager
{
	
	/**
	 * The corner.
	 */
	public enum Corner implements LayoutInfo { NE, SE, SW, NW };
	
	
	/**
	 * Creates a new {@link CornerLayoutManager}.
	 */
	public CornerLayoutManager()
	{
	}
	
	
	/**
	 * Recomputes the positions and the sizes of the children.
	 */
	public void layout(GUIContainer container)
	{
		for (GUIElement element : container.getElements())
		{
			if (element.getLayoutInfo() instanceof Corner)
			{
				Corner corner = (Corner) element.getLayoutInfo();
				Size2i size = element.getSize();
				Size2i containerSize = container.getSize();
				if (corner == Corner.NE)
				{
					element.setPosition(new Point2i(containerSize.width - size.width, 0));
				}
				else if (corner == Corner.SE)
				{
					element.setPosition(new Point2i(containerSize.width - size.width,
						containerSize.height - size.height));
				}
				else if (corner == Corner.SW)
				{
					element.setPosition(new Point2i(0, containerSize.height - size.height));
				}
				else if (corner == Corner.NW)
				{
					element.setPosition(new Point2i(0, 0));
				}
			}
		}
	}
	

}
