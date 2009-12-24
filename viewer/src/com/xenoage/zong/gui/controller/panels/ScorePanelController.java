package com.xenoage.zong.gui.controller.panels;

import javax.swing.JPopupMenu;

import com.xenoage.util.math.Point2i;
import com.xenoage.zong.gui.cursor.Cursor;
import com.xenoage.zong.renderer.screen.page.PageLayoutScreenRenderer;
import com.xenoage.zong.view.ScoreView;


/**
 * Interface for a panel that displays a score.
 * 
 * It can be implemented in pure Swing or OpenGL
 * for example.
 *
 * @author Andreas Wenger
 */
public interface ScorePanelController
  extends AppDocumentPanelController
{
	
	
	/**
	 * Level of the mouse cursor.
	 * <code>GUI</code> has highest priority, <code>Score</code> has lowest.
	 */
	public enum CursorLevel
	{
		
		/**
		 * GUI cursor. For example, a index finger cursor over a button.
		 */
		GUI,
		
		/**
		 * Cursor for scolling.
		 */
		Scroll,
		
		/**
		 * Cursor used by a tool, e.g. a crosshair cursor for positioning something.
		 */
		Tool,
		
		/**
		 * Cursor on musical level.
		 */
		Score;
	}
	
  
  /**
   * Sets the current view of this panel.
   */
  public void setView(ScoreView view);
  
  
  /**
   * Gets the page layout screen renderer for this panel.
   */
  public PageLayoutScreenRenderer createPageScreenRenderer();
  
  
  /**
   * Shows the given popup menu at the given position.
   */
  public void showPopupMenu(JPopupMenu popupMenu, Point2i position);
  
  
  /**
   * Sets the mouse cursor at the given level, or null.
   */
  public void setCursor(Cursor cursor, CursorLevel level);
  
  
}