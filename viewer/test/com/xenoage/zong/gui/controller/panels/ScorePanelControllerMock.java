package com.xenoage.zong.gui.controller.panels;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.gui.cursor.Cursor;
import com.xenoage.zong.renderer.screen.page.PageLayoutScreenRenderer;
import com.xenoage.zong.view.ScoreView;
import com.xenoage.zong.view.View;


/**
 * Mock class for a ScorePanelController.
 * 
 * @author Andreas Wenger
 */
public class ScorePanelControllerMock
  implements ScorePanelController
{
  
  private ScoreView view = null;
  private Size2i panelSize;
  
  
  public ScorePanelControllerMock(Size2i panelSize)
  {
    this.panelSize = panelSize;
  }

  
  public PageLayoutScreenRenderer createPageScreenRenderer()
  {
    return null;
  }


  public boolean keyPressed(int keyCode)
  {
    return false;
  }


  public void setView(ScoreView view)
  {
    this.view = view;
  }


  public JPanel getPanel()
  {
    return null;
  }


  public Size2i getSize()
  {
    return panelSize;
  }


  public View getView()
  {
    return view;
  }


  public void repaint()
  {
  }


	public void showPopupMenu(JPopupMenu popupMenu, Point2i position)
	{
	}


	public void setCursor(Cursor cursor, CursorLevel level)
	{
	}
  

}
