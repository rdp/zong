package com.xenoage.zong.view;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.xenoage.zong.documents.ViewerDocument;
import com.xenoage.zong.gui.controller.panels.AppDocumentPanelController;
import com.xenoage.zong.layout.Layout;
import com.xenoage.zong.renderer.GraphicsContext;


/**
 * Mock class for a simple view.
 *
 * @author Andreas Wenger
 */
public class ViewMock
  extends View
{
  
  private boolean paintCalled = false;
  
  
  public boolean isPaintCalled()
  {
    return paintCalled;
  }
  
  
  @Override public void paint(GraphicsContext g)
  {
    paintCalled = true;
  }


  @Override public ViewerDocument getDocument()
  {
    return null;
  }


  @Override public Layout getLayout()
  {
    return null;
  }


  @Override public AppDocumentPanelController getPanelController()
  {
    return null;
  }
  
  
  @Override public void mousePressed(MouseEvent e)
  {
  }


	@Override public void mouseDragged(MouseEvent e)
	{
	}


	@Override public void mouseMoved(MouseEvent e)
	{
	}


	@Override public void mouseReleased(MouseEvent e)
	{
	}


	@Override public void mouseClicked(MouseEvent e)
	{
	}


	@Override public void keyPressed(KeyEvent e)
	{
	}
  

}
