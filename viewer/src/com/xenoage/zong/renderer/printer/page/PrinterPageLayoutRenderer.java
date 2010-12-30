package com.xenoage.zong.renderer.printer.page;

import com.xenoage.zong.layout.Layout;
import com.xenoage.zong.layout.Page;
import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.renderer.SwingGraphicsContext;


/**
 * This class renders a layout
 * on a printer document.
 *
 * @author Andreas Wenger
 */
public class PrinterPageLayoutRenderer
{
  
  private static PrinterPageLayoutRenderer instance = null;
  
  
  /**
   * Gets the page layout renderer for printing.
   */
  public static PrinterPageLayoutRenderer getInstance()
  {
    if (instance == null)
      instance = new PrinterPageLayoutRenderer();
    return instance;
  }
  
  
  private PrinterPageLayoutRenderer()
  {
  }
  

  /**
   * Paints the given page of the given layout.
   */
  public void paint(Layout layout, int pageIndex,
    SwingGraphicsContext g)
  {
    //draw page
    Page page = layout.getPages().get(pageIndex);
    //draw frames
    for (Frame frame : page.getFrames())
    {
      frame.paint(g);
    }
  }

}
