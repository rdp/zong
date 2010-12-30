/**
 * 
 */
package com.xenoage.zong.app.symbols.rasterizer;

import com.xenoage.util.math.Size2f;
import com.xenoage.util.math.TextureRectangle2f;

/**
 * A Class that represents the single Textures on the complete Texture
 * 
 * @author Uli
 * 
 */
public class TextureNode extends TextureRectangle2f
{
	public TextureNode[] child = new TextureNode[2];
	Size2f imageSize;

	public TextureNode(TextureRectangle2f rectangle)
	{
		super(rectangle.x1, rectangle.y1, rectangle.x2, rectangle.y2);
	}

	public TextureNode Insert(Size2f size, Size2f spacing)
	{
		if (imageSize == null)
		{
			// check wether the image fits into the node
			if (!fitsin(size, spacing))
				return null;
			imageSize = size;
			// if empty space is left, it gets divided into two new nodes
			float dh = this.getHeight() - imageSize.height;
			float dw = this.getWidth() - imageSize.width;
			if (dh <= dw)
			{
				child[0] = new TextureNode(new TextureRectangle2f(x1, y1
						+ imageSize.height + spacing.height, x1
						+ imageSize.width + spacing.width, y2));
				child[1] = new TextureNode(new TextureRectangle2f(x1
						+ spacing.width + imageSize.width, y1, x2, y2));
			}
			else
			{
				child[0] = new TextureNode(new TextureRectangle2f(x1
						+ imageSize.width + spacing.width, y1, x2, y1
						+ imageSize.height + spacing.height));
				child[1] = new TextureNode(new TextureRectangle2f(x1, y1
						+ imageSize.height+spacing.height, x2, y2));
			}
			return this;
		}
		else
		{
			if (child[0] == null && child[1] == null)
				return null;
			for (TextureNode node : child)
			{
				TextureNode newNode = node.Insert(size, spacing);
				if (newNode != null)
					return newNode;
			}
			return null;
		}
	}

	private boolean fitsin(Size2f size, Size2f spacing)
	{
		if (size.height + spacing.height <= getHeight() && size.width + spacing.width <= getWidth())
			return true;
		return false;
	}

	public TextureRectangle2f getImageRectangle()
	{
		TextureRectangle2f ret = new TextureRectangle2f(x1, y1, x1
				+ imageSize.width, y1 + imageSize.height);
		return ret;
	}
}
