package com.xenoage.zong.layout.frames;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.data.format.PageFormat;
import com.xenoage.zong.data.format.PageMargins;
import com.xenoage.zong.layout.Layout;
import com.xenoage.zong.layout.Page;


/**
 * Test cases for a ImageFrame.
 *
 * @author Andreas Wenger
 */
public class ImageFrameTry
{
  
  
  /**
   * Creates and returns a page with some image frames.
   */
  public static Page createPageWithImageFrames(Layout parentLayout, TextureManager texMan)
  {
    Page page = parentLayout.addPage(new PageFormat(
      new Size2f(210, 297), new PageMargins(20, 20, 20, 20)));
    //create 7 image frames
    for (int i = 0; i < 7; i++)
    {
      ImageFrame frame = new ImageFrame(
        new Point2f(60 + i % 2 * 80, 40 + i / 2 * 60),
        new Size2f(60 + 10 * i, 40 + 8 * i),
        "data/test/images/flag" + /*(i + 1)*/ 2 + ".png");
      //OBSOLETE texMan.addImageTexture("data/test/images/flag" + (i + 1) + ".png");
      frame.setRelativeRotation(10 * i);
      page.addFrame(frame);
    }
    return page;
  }

}