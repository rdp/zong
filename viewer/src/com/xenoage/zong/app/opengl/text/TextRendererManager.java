package com.xenoage.zong.app.opengl.text;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Hashtable;

import com.sun.opengl.util.j2d.TextRenderer;
import com.xenoage.util.Units;


/**
 * This class manages JOGL TextRenderers.
 * They are only valid within the GL context where they were created.
 * 
 * @author Andreas Wenger
 */
public class TextRendererManager
{
	
	private ArrayList<Hashtable<String, TextRenderer>> renderers;
	
	private int internFontSize = 30;
	
	
	public TextRendererManager()
	{
		renderers = new ArrayList<Hashtable<String, TextRenderer>>(4);
		for (int i = 0; i < 4; i++)
		{
			renderers.add(new Hashtable<String, TextRenderer>());
		}
	}
	
	
	/**
	 * Returns a {@link TextRenderer} for the given font.
	 */
	public TextRenderer getRenderer(Font font)
	{
		//TODO: remove unused renderers after some time
		//already existing? then return it.
		TextRenderer ret = renderers.get(font.getStyle()).get(font.getFontName());
		//otherwise, create, save and return it.
		if (ret == null)
		{
			ret = new TextRenderer(new Font(font.getFontName(), font.getStyle(), internFontSize), true, true, null, true);
			renderers.get(font.getStyle()).put(font.getFontName(), ret);
		}
		return ret;
	}
	
	
	/**
	 * Returns the scale factor that has to be used to render text.
	 */
	public float getScaleFactor(float fontSize)
	{
		return Units.pxToMm(fontSize / internFontSize, 1);
	}
	

}
