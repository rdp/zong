package com.xenoage.zong.renderer.screen.page;

import com.xenoage.zong.renderer.GraphicsContext;
import com.xenoage.zong.view.ScorePageView;


/**
 * Interface for renderers that draw
 * the page layouts of scores on the screen.
 *
 * @author Andreas Wenger
 */
public interface PageLayoutScreenRenderer
{
	
	/**
   * Paints the background with the current
   * renderer settings on the given graphics context.
   */
  public void paintDesktop(GraphicsContext g);
  
  
  /**
   * Paints the pages of the given PageView with the current
   * renderer settings on the given graphics context.
   */
  public void paintPages(ScorePageView pageView, GraphicsContext g);
  
  
}
