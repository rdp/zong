/**
 * 
 */
package com.xenoage.zong.app.symbols.rasterizer;

import org.junit.Test;

import com.xenoage.util.math.Size2f;
import com.xenoage.util.math.TextureRectangle2f;
import com.xenoage.zong.app.symbols.rasterizer.SymbolsAdjuster;

/**
 * @author Uli
 *
 */

public class SymbolsAdjusterTest
{
  
  //TODO: This test does not assert anything?!
	@Test public void TestSymbolsAdjuster()
	{
		Size2f[] rectangles = new Size2f[1];
		
		rectangles[0] = new Size2f(0.5f,0.25f);
		//rectangles[1] = new Size2f(0.3f,0.2f);
		//rectangles[2] = new Size2f(0.3f,0.3f);
		//rectangles[3] = new Size2f(0.1f,0.1f);
		//rectangles[4] = new Size2f(0.04f,0.04f);
		
		SymbolsAdjuster adjuster = new SymbolsAdjuster(1024,1024,rectangles,4);
		
		TextureRectangle2f rec[] = new TextureRectangle2f[5];
		rec[0]= adjuster.getTextureRectangle2f(0);
		//rec[1]= adjuster.getTextureRectangle2f(1);
		//rec[2]= adjuster.getTextureRectangle2f(2);
		//rec[3]= adjuster.getTextureRectangle2f(3);
		//rec[4]= adjuster.getTextureRectangle2f(4);

/*		assertEquals(rec[0].x1,0f);
		assertEquals(rec[0].y1,0f);
		assertEquals(rec[0].x2,0.5f);
		assertEquals(rec[0].y2,0.25f);
		
		assertEquals(rec[1].x1,0.5f);
		assertEquals(rec[1].y1,0f);
		assertEquals(rec[1].x2,0.8f);
		assertEquals(rec[1].y2,0.2f);

		assertEquals(rec[2].x1,0f, Delta.DELTA_FLOAT);
		assertEquals(rec[2].y1,0.25f, Delta.DELTA_FLOAT);
		assertEquals(rec[2].x2,0.3f, Delta.DELTA_FLOAT);
		assertEquals(rec[2].y2,0.55f, Delta.DELTA_FLOAT);

		assertEquals(rec[3].x1,0.8f, Delta.DELTA_FLOAT);
		assertEquals(rec[3].y1,0f, Delta.DELTA_FLOAT);
		assertEquals(rec[3].x2,0.9f, Delta.DELTA_FLOAT);
		assertEquals(rec[3].y2,0.1f, Delta.DELTA_FLOAT);

		assertEquals(rec[4].x1,0.5f, Delta.DELTA_FLOAT);
		assertEquals(rec[4].y1,0.2f, Delta.DELTA_FLOAT);
		assertEquals(rec[4].x2,0.54f, Delta.DELTA_FLOAT);
		assertEquals(rec[4].y2,0.24f, Delta.DELTA_FLOAT);
*/
	}
}
