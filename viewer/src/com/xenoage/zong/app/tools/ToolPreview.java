package com.xenoage.zong.app.tools;

import com.xenoage.util.math.Point2i;
import com.xenoage.zong.layout.frames.FrameHandle;
import com.xenoage.zong.view.ScorePageView;
import com.xenoage.zong.view.View;


/**
 * An instance of {@link ToolPreview} realizes
 * the preview of tools for a {@link View} instance
 * by reacting to mouse motion events.
 * 
 * For example, if the cursor is over an
 * adjustment handle, the handle will show
 * a hover effect.
 * 
 * @author Andreas Wenger
 */
public class ToolPreview
{
  
	
  /**
   * Call this method when the mouse cursor has been moved onto
   * the given view but no buttons have been pushed.
   */
  public void mouseMoved(ScorePageView view, Point2i px)
  {
    //look if there is an adjustment handle under the cursor
    FrameHandle handle = view.computeFrameHandleAt(px);
    //highlight the handle (or remove the highlight, if no handle was found)
    view.showHandleHover(handle);
  }
  

}
