package com.xenoage.zong.app.tools;

import com.xenoage.zong.gui.cursor.Cursor;
import com.xenoage.zong.gui.event.ScoreKeyEvent;
import com.xenoage.zong.gui.event.ScoreKeyListener;
import com.xenoage.zong.gui.event.ScoreMouseEvent;
import com.xenoage.zong.gui.event.ScoreMouseListener;
import com.xenoage.zong.layout.frames.ScoreFrame;


/**
 * This is the abstract base class for all tools in this
 * application. A tool is used for processes which
 * can not be done by calling single commands, like
 * dragging a frame. The tool is activated at the
 * beginning of such a process, and it closed when
 * the process is finished.
 * 
 * For example, mouse input in {@link ScoreFrame}s needs
 * a tool, because the user first selects the note duration
 * (which changes the cursor and may show preview within the
 * score) and then writes the note by clicking into the score.
 * 
 * The methods of the mouse listener return
 * coordinates that are relative to the
 * upper left corner of the current score panel.
 * 
 * Mouse and key event handlers return true by default,
 * which means that the event was consumed (preventing another
 * tool to be activated) without doing anything.
 * 
 * TODO: For mouse events, the ScoreMouseListener is
 * probably not enough. Imagine a selection tool,
 * where the user spans a selection rectangle around
 * a page. Since the clicked coordinates may be
 * outside the page, no ScoreMouseEvent would be
 * generated.
 * 
 * @author Andreas Wenger
 */
public abstract class Tool
  implements ScoreMouseListener, ScoreKeyListener
{
	
	
	/**
   * Call this method when this tool is activated, i.e. when
   * it became the selected tool of a document.
   */
  public void activated()
  {
  }
  
  
  /**
   * Call this method when this tool is deactivated, i.e. when
   * it is not the selected tool of a document any more.
   */
  public void deactivated()
  {
  }
  
  
  /**
   * TODO: localization
   * Returns the name of this tool.
   */
  public abstract String getName();

  
  /**
   * Returns the mouse cursor that will be activated for the
   * score panel when the tool was selected.
   */
  public Cursor getScorePanelCursor()
  {
  	return Cursor.Default;
  }
  
  
  /**
   * Returns true, if the score panel GUI can be used while this
   * tool is active, otherwise false.
   * By default, the GUI stays active.
   */
  public boolean isGUIActive()
  {
  	return true;
  }

  
	public boolean mouseClicked(ScoreMouseEvent e)
	{
		return true;
	}

	
	public boolean mouseDragged(ScoreMouseEvent e)
	{
		return true;
	}

	
	public boolean mouseMoved(ScoreMouseEvent e)
	{
		return true;
	}

	
	public boolean mousePressed(ScoreMouseEvent e)
	{
		return true;
	}

	
	public boolean mouseReleased(ScoreMouseEvent e)
	{
		return true;
	}

	
	public boolean keyPressed(ScoreKeyEvent e)
	{
		return true;
	}

	
	public boolean keyReleased(ScoreKeyEvent e)
	{
		return true;
	}

	
	public boolean keyTyped(ScoreKeyEvent e)
	{
		return true;
	}


}
