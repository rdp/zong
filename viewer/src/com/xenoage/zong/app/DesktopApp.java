package com.xenoage.zong.app;

import java.util.ArrayList;

import javax.swing.JMenuItem;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.font.FontTools;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.documents.ViewerDocument;
import com.xenoage.zong.gui.controller.frames.MainFrameController;


/**
 * Abstract base class for desktop applications that
 * display a score.
 * 
 * @author Andreas Wenger
 */
public abstract class DesktopApp
	extends App
{
	
	//gui controller
	MainFrameController mainFrameCtrl;

	//documents
	private int activeDocumentIndex;
	private ArrayList<ViewerDocument> documents;
	
	
	/**
	 * Gets the only instance of the {@link DesktopApp} class. If this
	 * program is not running as this type, null is returned.
	 */
	public static DesktopApp getInstance()
	{
		if (instance instanceof DesktopApp)
			return (DesktopApp) instance;
		else
			return null;
	}
	
	
	DesktopApp(String args[])
	{
		//TODO: open documents in args
	}
	
	
	/**
   * {@inheritDoc}
   */
  @Override void initApp()
  {
  	super.initApp();
  	//load custom fonts
		try
		{
			FontTools.getInstance().loadShippedFonts();
		}
		catch (Exception ex)
		{
			App.err().report(ErrorLevel.Remark, "Shipped fonts could not be loaded.", ex);
		}
		//list of documents
		documents = new ArrayList<ViewerDocument>();
  }
  
  
  /**
   * {@inheritDoc}
   */
  @Override void initialized()
  {
  	if (mainFrameCtrl != null)
  		mainFrameCtrl.getMainFrame().setVisible(true);
  }
	
	
	public MainFrameController getMainFrameController()
	{
		return mainFrameCtrl;
	}
	
	
	/**
	 * Adds a document.
	 * @param document  the new document
	 * @param open      true, if it should be activated immediately
	 */
	public void addDocument(ViewerDocument document, boolean open)
	{
		String title = "Unnamed";
		if (document instanceof ScoreDocument)
		{
			ScoreDocument scoreDoc = (ScoreDocument) document;
			scoreDoc.setScorePanelController(scorePanelController);
			String docTitle = scoreDoc.getScore(0).getScoreInfo().getTitle();
			title = (docTitle != null ? docTitle : title);
		}
		if (!documents.contains(document))
		{
			documents.add(document);
			mainFrameCtrl.addDocument(title);
		}
		if (open)
		{
			setActiveDocument(documents.indexOf(document));
		}
	}
	
	
	/**
	 * Gets the document with the given index.
	 */
	public ViewerDocument getDocument(int index)
	{
		return documents.get(index);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override public ViewerDocument getActiveDocument()
	{
		return documents.get(activeDocumentIndex);
	}
	
	
	/**
	 * Activates the document with the given index.
	 */
	public void setActiveDocument(int index)
	{
		activeDocumentIndex = index;
		documentChanged();
		mainFrameCtrl.setSelectedDocumentTab(index);
		ViewerDocument doc = getActiveDocument();
		if (doc instanceof ScoreDocument)
		{
			ScoreDocument scoreDoc = (ScoreDocument) doc;
			scorePanelController.setView(scoreDoc.getCurrentView());
			scoreDoc.activated();
		}
	}
	
	
	/**
	 * Call this method when the current document was changed.
	 */
	private void documentChanged()
	{
		ViewerDocument doc = getActiveDocument();
		if (doc instanceof ScoreDocument)
		{
			ScoreDocument scoreDoc = (ScoreDocument) doc;
			//update the menu items
			scoreDoc.getCommandPerformer().updateMenuItems();
		}
		else
		{
			//TODO
		}
	}
	
	
	public void registerMenuItems(JMenuItem mnuUndu, JMenuItem mnuRedo)
	{
		//TODO
		//these menu items are the default ones that must be passed to the
		//command performer when opening a new score document
	}
	
	
	/**
	 * True, when all key events are caught and forwarded to the controller of
	 * the main frame.
	 */
	@Override public void setKeyDispatchingEnabled(boolean keyDispatchingEnabled)
	{
		mainFrameCtrl.setKeyDispatchingEnabled(keyDispatchingEnabled);
	}
	
	
	/**
	 * Requests to close the document with the given index.
	 */
	public void requestCloseDocument(int documentIndex)
	{
		//TODO: when last document is closed, add a "Welcome to Score"-screen
		//or something like that
		//last document can't be closed
		if (documents.size() > 1)
		{
			mainFrameCtrl.removeDocument(documentIndex);
			documents.remove(documentIndex);
		}
	}

	
	/**
	 * Closes the application.
	 */
	public abstract void close();


}
