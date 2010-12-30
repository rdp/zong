package com.xenoage.zong.app.symbols.rasterizer;

import com.xenoage.util.math.Size2f;
import com.xenoage.util.math.TextureRectangle2f;


/**
 * The Class tries to arrange the textures in a way, that all the textures fit
 * in it.
 * 
 * @author Uli Teschemacher
 */
public class SymbolsAdjuster
{
	TextureRectangle2f rectangles[];

	public SymbolsAdjuster(int width, int height,Size2f[] texturesizes, int spacing)
	{
		rectangles = new TextureRectangle2f[texturesizes.length];
		// An Array in which the indices for every element is saved to get the
		// real element to an ID, even if it has moved when sorting
		int order[] = new int[texturesizes.length];
		for (int i = 0; i < texturesizes.length; i++)
		{
			order[i] = i;
		}

		
		float vspace = (float)spacing / (float)width;
		float hspace = (float)spacing / (float)height;
		Size2f space = new Size2f(vspace,hspace);
		// order textures by size to avoid a waste of space (bubble sort)
		for (int i = 0; i < texturesizes.length - 1; i++)
		{
			for (int a = i + 1; a < texturesizes.length - 2; a++)
			{
				if (texturesizes[i].height * texturesizes[i].width < texturesizes[a].height
						* texturesizes[a].width)
				{
					Size2f cache = texturesizes[i];
					texturesizes[i] = texturesizes[a];
					texturesizes[a] = cache;
					int t = order[i];
					order[i] = order[a];
					order[a] = t;
				}
			}
		}

		float area = area(texturesizes);

		for (float percentage=1.0f;;percentage-=0.01f)
		{
			float factor = percentage /area;
			if (calculatePositions(texturesizes, order, factor,space))
				break;
		}
	}

	/**
	 * The boolean value returns, whether all the symbols fitted in the texture.
	 */
	private boolean calculatePositions(Size2f[] texturesizes, int[] order,
			float factor, Size2f spacing)
	{
		Size2f[] newTextureSizes = new Size2f[texturesizes.length];
		for (int i=0; i<texturesizes.length; i++)
		{
			newTextureSizes[i] = new Size2f(
				texturesizes[i].width * (float)Math.sqrt(factor), texturesizes[i].height* (float)Math.sqrt(factor));
		}
		TextureNode main = new TextureNode(new TextureRectangle2f(0, 0, 1, 1));
		for (int i = 0; i < newTextureSizes.length; i++)
		{
			TextureNode node = main.Insert(newTextureSizes[i],spacing);
			if (node==null)			//Factor was too big
				return false;
			rectangles[order[i]] = node.getImageRectangle();
		}
		return true;
	}
	
	private float area(Size2f[] sizes)
	{
		float ret=0f;
		for (Size2f size : sizes)
		{
			ret += size.width * size.height;
		}
		return ret;
	}

	public TextureRectangle2f getTextureRectangle2f(int ID)
	{
		return rectangles[ID];
	}
	
	public TextureRectangle2f[] getAllTextureRectangles2f()
	{
		return rectangles;
	}
}
