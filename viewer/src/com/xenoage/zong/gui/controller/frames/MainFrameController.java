package com.xenoage.zong.gui.controller.frames;

import java.awt.Cursor;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.error.ErrorProcessing;
import com.xenoage.util.language.Lang;
import com.xenoage.util.language.LanguageComponent;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.DesktopApp;
import com.xenoage.zong.app.ViewerApplication;
import com.xenoage.zong.app.ViewerMain;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.documents.ViewerDocument;
import com.xenoage.zong.gui.components.TabbedPane;
import com.xenoage.zong.gui.controller.menues.MainMenuBarController;
import com.xenoage.zong.gui.controller.panels.ScorePanelController;
import com.xenoage.zong.gui.view.frames.MainFrame;


/**
 * Abstract class for a controller of a main frame.
 * 
 * @author Andreas Wenger
 */
public abstract class MainFrameController
	implements LanguageComponent
{
	
	DesktopApp app;
	MainFrame frame = null;
	MainMenuBarController mainMenuBarController;
	ScorePanelController scorePanelController;
	TabbedPane tabbedPane;
	
	private boolean keyDispatchingEnabled = true;
	
	
	/**
   * Creates a controller of a main frame.
   * @param app  the main class of the application
   */
  public MainFrameController(DesktopApp app)
  {
    this.app = app;
    Lang.registerComponent(this);
  }
  
  
  /**
   * Inits the given main frame by adding the main menu bar and a
   * {@link TabbedPane} using the given {@link ScorePanelController}.
   */
  public void initMainFrame(MainFrame frame, ScorePanelController scorePanelController)
  {
  	this.frame = frame;
  	this.scorePanelController = scorePanelController;
  	//adds the main menubar
    mainMenuBarController = new MainMenuBarController();
    frame.setJMenuBar(mainMenuBarController.getJMenuBar());
    //start maximized
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    //tabbed pane for documents
    tabbedPane = new TabbedPane(scorePanelController.getPanel());
    frame.addCenter(tabbedPane);
  }
  
	
	/**
   * Gets the main frame.
   */
  public MainFrame getMainFrame()
  {
  	return frame;
  }
  
  
  /**
	 * True, when all key events are caught and forwarded to
	 * this controller.
	 */
	public boolean isKeyDispatchingEnabled()
	{
		return keyDispatchingEnabled;
	}


	/**
	 * True, when all key events are should be caught and forwarded to
	 * this controller, or false if key events should be handled 
	 * by the appropriate Swing component.
	 */
	public void setKeyDispatchingEnabled(boolean keyDispatchingEnabled)
	{
		this.keyDispatchingEnabled = keyDispatchingEnabled;
	}
	
	
	/**
   * Called, when a key was pressed.
   * @param e  the key event
   * @return   true, when the event could be used, otherwise false
   *           (so it can be forwarded to the focussed component)
   */
  public boolean keyPressed(KeyEvent e)
  {
    ViewerDocument appDoc = App.getInstance().getActiveDocument();
    if (appDoc instanceof ScoreDocument)
    {
      ScoreDocument doc = (ScoreDocument) appDoc;
      doc.getCurrentView().keyPressed(e);
      return true;
    }
    return false;
  }
  
  
  /**
   * This method must be called when the user wants to
   * close the main frame.
   */
  public void requestClose()
  {
  	app.close();
  }
  
  
  /**
   * Shows the main frame.
   */
  public void show()
  {
    frame.setVisible(true);
  }
  
  
  /**
   * Adds a document to the tabbed pane without activating this new tab.
   */
  public void addDocument(String title)
  {
    tabbedPane.addTab(title);
  }
  
  
  /**
   * Removes the document with the given index.
   */
  public void removeDocument(int index)
  {
  	tabbedPane.removeTab(index);
  }
  
  
  public void setScorePanelCursor(Cursor cursor)
  {
  	scorePanelController.getPanel().setCursor(cursor);
  }
  
  
  public void setSelectedDocumentTab(int index)
	{
		tabbedPane.setSelectedIndex(index);
	}
  
  

  /**
   * Gets the main menubar controller.
   */
  public MainMenuBarController getMainMenuBarController()
  {
    return mainMenuBarController;
  }
  
  
  @Override public void languageChanged()
  {
  	updateMainMenuBar();
  }
  
  
  public void updateMainMenuBar()
  {
  	mainMenuBarController.updateJMenuBar();
  	frame.pack();
  }

}
