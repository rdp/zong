package com.xenoage.zong.gui.score.layout;

import java.util.List;

import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.gui.score.GUIContainer;
import com.xenoage.zong.gui.score.GUIElement;


/**
 * This layout manager places all elements horizontally
 * centered in a column.
 * 
 * The left, right, top and bottom margins can be set
 * and the container is resized so it fits to its content.
 * 
 * @author Andreas Wenger
 */
public class ColumnLayoutManager
	implements LayoutManager
{
	
	
	private int marginLeft, marginRight, marginTop, marginBottom, marginBetween;
	
	//state
	private boolean layouting = false;
	
	
	/**
	 * Creates a new {@link ColumnLayoutManager} with the given margins.
	 */
	public ColumnLayoutManager(int marginLeft, int marginRight, int marginTop,
		int marginBottom, int marginBetween)
	{
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.marginTop = marginTop;
		this.marginBottom = marginBottom;
		this.marginBetween = marginBetween;
	}




	/**
	 * Recomputes the positions and the sizes of the children
	 * and the size of their container.
	 */
	public void layout(GUIContainer container)
	{
		//already layouting? (ignore layout requests caused by this layouter)
		if (layouting)
			return;
		//begin layouting
		layouting = true;
		List<GUIElement> elements = container.getElements();
		//compute needed width and height
		int maxWidth = 0;
		int height = 0;
		for (GUIElement e : elements)
		{
			Size2i size = e.getSize();
			maxWidth = Math.max(size.width, maxWidth);
			height += size.height;
		}
		if (elements.size() > 1)
			height += (elements.size() - 1) * marginBetween;
		//place buttons
		int posY = marginTop;
		for (GUIElement e : elements)
		{
			Size2i size = e.getSize();
			e.setPosition(new Point2i(marginLeft + (maxWidth - size.width) / 2, posY));
			posY += size.height + marginBetween;
		}
		//set width and height
		container.setSize(new Size2i(maxWidth + marginLeft + marginRight,
			height + marginTop + marginBottom)); //TODO: let parent know?!
		//finished
		layouting = false;
	}
	

}
