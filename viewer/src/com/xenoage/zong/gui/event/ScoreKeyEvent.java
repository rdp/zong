package com.xenoage.zong.gui.event;

import java.awt.event.KeyEvent;

import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.layout.Layout;


/**
 * An event which indicates that a key
 * action occurred on a score document.
 * 
 * @author Andreas Wenger
 */
public final class ScoreKeyEvent
{
	
	public static final int SHIFT = 1 << 0;
	public static final int CTRL = 1 << 1;
	public static final int ALT = 1 << 2;
  
  private final ScoreDocument document;
  private final Layout layout;
  private final int keyCode;
  private final int modifiers;
  
  
  /**
   * Creates a new key event that occurred on a score document.
   * @param document     the score document
   * @param layout       the appropriate layout (since the document can have different layouts)
   * @param keyCode      one of the KeyEvent.VK_-constants
   * @param modifiers    combination of SHIFT, CTRL and ALT
   */
  public ScoreKeyEvent(ScoreDocument document, Layout layout, int keyCode, int modifiers)
  {
    this.document = document;
    this.layout = layout;
    this.keyCode = keyCode;
    this.modifiers = modifiers;
  }
  
  
  /**
   * Creates a new key event that occurred on a score document.
   * @param document     the score document
   * @param layout       the appropriate layout (since the document can have different layouts)
   * @param keyEvent     the AWT key event
   */
  public ScoreKeyEvent(ScoreDocument document, Layout layout, KeyEvent event)
  {
    this.document = document;
    this.layout = layout;
    this.keyCode = event.getKeyCode();
    this.modifiers = 0 + ((event.getModifiers() & KeyEvent.SHIFT_MASK) > 0 ? SHIFT : 0)
			+ ((event.getModifiers() & KeyEvent.CTRL_MASK) > 0 ? CTRL : 0)
			+ ((event.getModifiers() & KeyEvent.ALT_MASK) > 0 ? ALT : 0);
  }
  
  
  public ScoreDocument getDocument()
  {
    return document;
  }
  
  
  public Layout getLayout()
  {
    return layout;
  }

  
  public int getKeyCode()
  {
    return keyCode;
  }
  
  
  public int getModifiers()
  {
    return modifiers;
  }

}
