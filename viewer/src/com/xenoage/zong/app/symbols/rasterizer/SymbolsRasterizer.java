/**
 * 
 */
package com.xenoage.zong.app.symbols.rasterizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.TextureRectangle2f;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.renderer.RenderingQuality;

/**
 * This class creates the texture. It paints the single symbols on a big
 * png-file. The positions a rendered by SymbolsAdjuster
 * 
 * @author Uli
 * 
 */
public class SymbolsRasterizer
{
	public static void rasterizeSymbols(File file, int width, int height,
			Symbol[] symbols, TextureRectangle2f[] positions)
			throws IOException
	{
		BufferedImage texture = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = texture.createGraphics();
		gr.setColor(Color.WHITE);
		for (int i = 0; i < symbols.length; i++)
		{
			Point2f scaling = new Point2f(positions[i].getWidth() * width / symbols[i].getBoundingRect().size.width);
			Point2f startpoint = new Point2f((positions[i].x1)*width - symbols[i].getBoundingRect().position.x * scaling.x,
					height-(positions[i].y2) * height - symbols[i].getBoundingRect().position.y * scaling.y);
			
			//Just for Debug
			//gr.setColor(rc());
			//gr.fillRect((int)((positions[i].x1)*width), ((int)(height - (positions[i].y1)*height - (symbols[i].getBoundingRect().size.height*scaling.y))),(int)(symbols[i].getBoundingRect().size.width*scaling.x),(int) (symbols[i].getBoundingRect().size.height*scaling.y));
			
			symbols[i].draw(gr, Color.WHITE, startpoint, scaling);
		}
		ImageIO.write(texture, "png", file);
	}
	
	//private static Color rc(){
	//	Random rd = new Random();
	//	return new Color(rd.nextFloat(),rd.nextFloat(),rd.nextFloat());
	//}
}
