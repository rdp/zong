package com.xenoage.zong.gui.view.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.io.IO;
import com.xenoage.zong.app.App;
import com.xenoage.zong.gui.controller.frames.MainFrameController;


/**
 * A possible main frame of a Zong! program.
 *
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public abstract class MainFrame
  extends JFrame
  implements KeyEventDispatcher, WindowListener
{
  
  MainFrameController controller;
  
  
  /**
   * Creates the main frame.
   */
  public MainFrame(MainFrameController controller)
  {
    this.controller = controller;
    //set title
    String title = App.getInstance().getNameAndVersion();
    setTitle(title);
    setMinimumSize(new Dimension(400,300));
    setPreferredSize(new Dimension(1024,768));
    //set icon
    try
    {
    	List<Image> icons = new ArrayList<Image>();
    	icons.add(ImageIO.read(IO.openInputStream("data/img/icons/logo512.png")));
    	icons.add(ImageIO.read(IO.openInputStream("data/img/icons/logo256.png")));
    	icons.add(ImageIO.read(IO.openInputStream("data/img/icons/logo128.png")));
    	icons.add(ImageIO.read(IO.openInputStream("data/img/icons/logo64.png")));
    	icons.add(ImageIO.read(IO.openInputStream("data/img/icons/logo48.png")));
    	icons.add(ImageIO.read(IO.openInputStream("data/img/icons/logo32.png")));
    	icons.add(ImageIO.read(IO.openInputStream("data/img/icons/logo16.png")));
      setIconImages(icons);
    }
    catch (IOException ex)
    {
    	App.err().report(ErrorLevel.Remark, "Icon is missing", ex);
    }
    //adds the key event dispatcher for this frame
    KeyboardFocusManager.getCurrentKeyboardFocusManager(
      ).addKeyEventDispatcher(this);
    //add window listener (handles closing requests)
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    addWindowListener(this);
    //set layout
    setLayout(new BorderLayout());
    //set size
    setSize(1024, 768);
    //pack the frame
    pack();
  }
  
  
  /**
   * Gets the controller of this main frame.
   */
  public MainFrameController getController()
  {
    return controller;
  }
  
  
  /**
   * Adds the given component to the center area of this frame.
   */
  public void addCenter(JComponent c)
  {
    add(c, BorderLayout.CENTER);
  }
  
  
  public boolean dispatchKeyEvent(KeyEvent e)
  {
  	//if this frame is not active (but e.g. a dialog), don't
  	//catch the events here
  	if (!this.isActive() || !controller.isKeyDispatchingEnabled())
  		return false;
    //key press
    if (e.getID() == KeyEvent.KEY_PRESSED)
    {
      return controller.keyPressed(e);
    }
    //we did not handle the event.
    //let the active component receive it.
    return false;
  }


  public void windowActivated(WindowEvent e)
  {
  }


  public void windowClosed(WindowEvent e)
  {
  }


  public void windowClosing(WindowEvent e)
  {
    controller.requestClose();
  }


  public void windowDeactivated(WindowEvent e)
  {
  }


  public void windowDeiconified(WindowEvent e)
  {
  }


  public void windowIconified(WindowEvent e)
  {
  }


  public void windowOpened(WindowEvent e)
  {
  }
  

}
