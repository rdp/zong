package com.xenoage.zong.app;

import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;

import com.xenoage.util.FileTools;
import com.xenoage.util.logging.Log;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.gui.controller.frames.ViewerMainFrameController;
import com.xenoage.zong.io.musicxml.in.MxlScoreDocumentFileInput;
import com.xenoage.zong.util.demo.ScoreDocumentAlphaVersionWelcomeScreen;


/**
 * The main class implementation of the Zong! Viewer.
 * 
 * It extends the App class, which is the common base for both the application
 * and the applet.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class ViewerApplication
	extends DesktopApp
{
	
	private static final String PROJECT_FIRST_NAME = "Viewer";


	/**
	 * Gets the only instance of the {@link ViewerApplication} class. If this
	 * program is not running as this type, null is returned.
	 */
	public static ViewerApplication getInstance()
	{
		if (instance instanceof ViewerApplication)
			return (ViewerApplication) instance;
		else
			return null;
	}


	/**
	 * Creates a new {@link ViewerApplication} instance with the given
	 * command line  arguments.
	 */
	public ViewerApplication(String args[])
	{
		super(args);
		init();
	}
	
	
	/**
   * Creates the main frame.
   */
  @Override Frame createMainFrame()
  {
  	mainFrameCtrl = new ViewerMainFrameController(this, scorePanelController);
  	
  	//open demo document
  	ScoreDocument document = ScoreDocumentAlphaVersionWelcomeScreen.createScore(scorePanelController);
  	addDocument(document, true);
  	
  	return mainFrameCtrl.getMainFrame();
  }


	/**
	 * Closes the application.
	 */
	@Override public void close()
	{
		Log.close();
		System.exit(0);
	}


	/**
	 * Opens the given file. Currently supported formats:
	 * <ul>
	 * <li>MusicXML 1.1</li>
	 * </ul>
	 */
	@Override public void openFile(File file)
	{
		//MusicXML11Input input = new MusicXML11Input();
		MxlScoreDocumentFileInput input = new MxlScoreDocumentFileInput();
		try
		{
			setCursorWaiting(true);
			//input.checkFormatReadable(file.getAbsolutePath());
			ScoreDocument doc = input.read(file.getAbsolutePath());
			doc.setScorePanelController(scorePanelController);
			
			addDocument(doc, true);
			setCursorWaiting(false);
		}
		catch (Exception ex)
		{
			super.reportOpenFileError(ex, file.getAbsolutePath());
		}
	}
	
	
	/**
   * {@inheritDoc}
   */
  @Override public String getProjectFirstName()
  {
  	return PROJECT_FIRST_NAME;
  }

}
