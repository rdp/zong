package com.xenoage.zong.view;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Point;
import java.awt.Robot;

import com.xenoage.util.MathTools;
import com.xenoage.util.math.Point2i;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.tools.Tool;
import com.xenoage.zong.gui.controller.panels.ScorePanelController.CursorLevel;
import com.xenoage.zong.gui.cursor.Cursor;
import com.xenoage.zong.gui.event.ScoreMouseEvent;


/**
 * With this tool the user can scroll on a {@link ScoreView}.
 * 
 * It is not a normal {@link Tool}, because it can be used
 * simultaneously with other tools like playback.
 * 
 * @author Andreas Wenger
 */
public class ScrollTool
{
	
	//the view
	private ScoreView view;
	
	//the position in px where the last scroll action began
	private Point2i lastPos;
	
	//the mouse button of the scrolling
	private int mouseButton;
	
	//true, when the AWT robot corrected the cursor position,
	//and false, when it was a mouse event caused by the user
	private boolean mouseCorrection = false;
	private boolean mouseCorrectionEnabled = false;
	
	
	/**
	 * Creates a {@link ScrollTool} on the given view, with the given start position
	 * of the mouse cursor and the given mouse button.
	 */
	public ScrollTool(ScoreView view, Point2i startPos, int mouseButton)
	{
		this.view = view;
		this.lastPos = startPos;
		this.mouseButton = mouseButton;
		
		//use mouse correction only for desktop applications (otherwise
		//a security exception caused by the AWT robot would be thrown)
		if (App.getInstance().isDesktopApp())
		{
			this.mouseCorrectionEnabled = true;
		}
		
		App.getInstance().getScorePanelController().setCursor(
			Cursor.ClosedHand, CursorLevel.Scroll);
		
	}


  public void mouseDragged(ScoreMouseEvent e)
  {
  	Point2i currentPos = e.getViewPositionPx();
  	
  	if (mouseCorrection)	
		{
  		//nothing should happen, because its just an
			//event caused by the robot
			mouseCorrection = false;
			lastPos = currentPos;
			return;
		}
  	
  	Point2i d = currentPos.sub(lastPos);
  	view.scrollInPx(d);
  	lastPos = currentPos;
  	
		//mouse correction:
  	//at the border of the panel, the mouse stays in position and document scrolls
  	if (mouseCorrectionEnabled)
  	{
			Component panel = view.getPanelController().getPanel();
			Point pos = panel.getLocationOnScreen();
			int panelX = pos.x;
			int panelY = pos.y;
			int panelWidth = panel.getWidth();
			int panelHeight = panel.getHeight();
			int mouseX = e.getScreenPositionPx().x;
			int mouseY = e.getScreenPositionPx().y;
			int x = MathTools.clamp(mouseX, panelX + 15, panelX + panelWidth - 15);
			int y = MathTools.clamp(mouseY, panelY, panelY + panelHeight);
			if (mouseX != x || mouseY != y)
			{
				try
				{
					Robot robot;
					robot = new Robot();
					robot.mouseMove(x, y);
					mouseCorrection = true;
				}
				catch (AWTException e1)
				{
					e1.printStackTrace();
				}
			}
		}
  	
  }
  
  
  public void mouseReleased(ScoreMouseEvent e)
	{
  	if (e.getButton() == mouseButton)
  	{
  		//finished
  		view.stopScrollTool();
  		App.getInstance().getScorePanelController().setCursor(null, CursorLevel.Scroll);
  	}
	}


}
