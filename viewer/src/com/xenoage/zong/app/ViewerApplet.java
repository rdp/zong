package com.xenoage.zong.app;

import static com.xenoage.util.NullTools.notNull;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.io.DownloadTools;
import com.xenoage.util.io.IO;
import com.xenoage.util.language.Lang;
import com.xenoage.util.logging.Log;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.commands.dialogs.SaveDocumentDialogViewerCommand.FileFormat;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.documents.ViewerDocument;
import com.xenoage.zong.gui.applet.ZongApplet;
import com.xenoage.zong.io.midi.out.MidiScoreDocumentFileOutput;
import com.xenoage.zong.io.musicxml.in.MusicXMLScoreDocumentFileInput;
import com.xenoage.zong.util.demo.ScoreDocumentAlphaVersionWelcomeScreen;


/**
 * The main class of the Zong! Viewer applet.
 *
 * @author Andreas Wenger
 */
public class ViewerApplet
  extends App
  implements KeyListener, MouseListener
{
  
  //the applet component - TODO: needed?
	private JApplet appletSwing;
  private ZongApplet appletZong;
  
  //the open document
  protected ScoreDocument document = null;
  
  //list of files that can be opened
  private class AppletFile
  {
  	public String path = null;
  	public String caption = null;
  }
  private AppletFile[] files = null;
  
  
  /**
   * Creates a new {@link ViewerApplet} instance, using the
   * given applet component.
   */
  public ViewerApplet(JApplet appletSwing, ZongApplet appletZong)
  {
  	this.appletSwing = appletSwing;
  	this.appletZong = appletZong;
  	
  	init();
  	
    //load the list of possible files
    String paramFiles = appletSwing.getParameter("files");
    if (paramFiles == null || paramFiles.length() == 0)
    {
    	//no files can be loaded
    	files = new AppletFile[0];
    }
    else
    {
    	String[] filesList = paramFiles.split(";");
    	//load the captions
    	String[] filesCaptionList = new String[0];
    	String paramFilenames = appletSwing.getParameter("filenames");
    	if (paramFilenames != null && paramFilenames.length() > 0)
    	{
    		filesCaptionList = paramFilenames.split(";"); 
    	}
    	this.files = new AppletFile[filesList.length];
    	for (int i = 0; i < filesList.length; i++)
    	{
    		this.files[i] = new AppletFile();
    		this.files[i].path = filesList[i];
    		this.files[i].caption = (i < filesCaptionList.length ? filesCaptionList[i] : null);
    	}
    }
    
    //create an empty document
    /* Score score = ScoreDemo.createDemoScore32Measures();
    document = new ScoreDocument(score, null, scorePanelController);
    Page page = document.getCurrentLayout().addPage(new PageFormat(new Size2f(220, 250), new PageMargins(20, 20, 20, 20)));
    ScoreFrame scoreFrame = new ScoreFrame(new Point2f(110, 125), new Size2f(180, 210), score);
    page.addFrame(scoreFrame);*/
    document = ScoreDocumentAlphaVersionWelcomeScreen.createScore(scorePanelController);
    
    scorePanelController.setView(document.getCurrentView());
    //adds a key event dispatcher and a key listener
    //(and hope one of them works)
    scorePanelController.getPanel().requestFocus();
    scorePanelController.getPanel().addKeyListener(this);
    //adds the score panel to the applet
    //TODO: only works when adding the GL canvas. when adding it's parent JPanel,
    //the display method of the GL canvas is not called - why?
    appletSwing.add(scorePanelController.getPanel());
  }
  
  
  @Override void initIO()
  {
  	//use applet file reader
    IO.initApplet(appletSwing.getCodeBase());
  }
  
  
  @Override void initLogging()
  {
  	//use applet logging
  	Log.initAppletLog();
  }
  
  
  @Override void initAudio(boolean readSettings)
  {
  	//don't use settings file. use default settings
  	super.initAudio(false);
  }
  
  
  @Override Frame createMainFrame()
  {
  	//use already existing applet frame
  	return appletZong.getParentFrame();
  }
  
  
  /**
	 * Returns false, because this program is an applet.
	 */
	@Override public boolean isDesktopApp()
	{
		return false;
	}
  
  
  @Override public ViewerDocument getActiveDocument()
  {
    return document;
  }


  /**
   * This method is called when a key was pressed.
   */
  public void keyPressed(KeyEvent e)
  {
    int keyCode = e.getKeyCode();
    if (keyCode == KeyEvent.VK_PLUS ||
      keyCode == KeyEvent.VK_MINUS)
    {
    	document.getCurrentView().keyPressed(e);
    }
  }


  public void keyReleased(KeyEvent e)
  {
  }


  public void keyTyped(KeyEvent e)
  {
  }


  public void mouseClicked(MouseEvent e)
  {
  }


  public void mouseEntered(MouseEvent e)
  {
  }


  public void mouseExited(MouseEvent e)
  {
  }


  public void mousePressed(MouseEvent e)
  {
    //request focus for the score panel (important for key events)
    scorePanelController.getPanel().requestFocus();
  }


  public void mouseReleased(MouseEvent e)
  {
  }


	@Override public void openFile(File file)
	{
		MusicXMLScoreDocumentFileInput input = new MusicXMLScoreDocumentFileInput();
		try
		{
			setCursorWaiting(true);
			//input.checkFormatReadable(file.getAbsolutePath());
			ScoreDocument doc = input.read(file.getAbsolutePath());
			doc.setScorePanelController(scorePanelController);
			this.document = doc;
			setCursorWaiting(false);
		}
		catch (Exception ex)
		{
			super.reportOpenFileError(ex, file.getAbsolutePath());
		}
	}
	
	
	/**
	 * Loads textures, that are specific for this kind of app.
	 */
	@Override public void loadAppTextures(TextureManager textureManager)
		throws IOException
	{
		//load gui textures
		Texture tex = TextureIO.newTexture(IO
			.openInputStream("data/img/gui/button-open.png"), true, "png");
		textureManager.addAppTexture(tex, TextureManager.ID_GUI_BUTTON_OPEN);
		tex = TextureIO.newTexture(IO
			.openInputStream("data/img/gui/button-save.png"), true, "png");
		textureManager.addAppTexture(tex, TextureManager.ID_GUI_BUTTON_SAVE);
		tex = TextureIO.newTexture(IO
			.openInputStream("data/img/gui/button-print.png"), true, "png");
		textureManager.addAppTexture(tex, TextureManager.ID_GUI_BUTTON_PRINT);
		tex = TextureIO.newTexture(IO
			.openInputStream("data/img/gui/button-play.png"), true, "png");
		textureManager.addAppTexture(tex, TextureManager.ID_GUI_BUTTON_PLAY_TUTTI);
		tex = TextureIO.newTexture(IO
			.openInputStream("data/img/gui/gui-buttonpanel.png"), true, "png");
		textureManager.addAppTexture(tex, TextureManager.ID_GUI_BUTTONPANEL);
		tex = TextureIO.newTexture(IO
			.openInputStream("data/img/gui/logo-viewer.png"), true, "png");
		textureManager.addAppTexture(tex, TextureManager.ID_GUI_LOGO_VIEWER);
		tex = TextureIO.newTexture(IO
			.openInputStream("data/img/gui/gui-tooltip.png"), true, "png");
		textureManager.addAppTexture(tex, TextureManager.ID_GUI_TOOLTIP);
		tex = TextureIO.newTexture(IO
			.openInputStream("data/img/gui/gui-tooltip-small.png"), true, "png");
		textureManager.addAppTexture(tex, TextureManager.ID_GUI_TOOLTIP_SMALL);
	}
	
	
	/**
	 * Opens the given website in the web browser.
	 * @param url        the URL to open
	 * @param newWindow  if true, a new window is opened in the browser.
	 */
	@Override public void openWebsite(URL url, boolean newWindow)
	{
		if (newWindow)
		{
			appletSwing.getAppletContext().showDocument(url, "_blank");
		}
		else
		{
			appletSwing.getAppletContext().showDocument(url);
		}
	}
	
	
	/**
	 * Shows a small dialog that allows the user to select a file for opening.
	 */
	public void showFileOpenDialog()
	{
		if (files.length == 0)
		{
			JOptionPane.showMessageDialog(appletSwing, Lang.get(Voc.Message_NoDocumentsAvailable));
		}
		else
		{
			//collect file names
			String[] fileCaptions = new String[files.length];
			for (int i = 0; i < files.length; i++)
			{
				fileCaptions[i] = notNull(files[i].caption, files[i].path);
			}
			//show dialog
			String selectedFile = (String) JOptionPane.showInputDialog(null, Lang.get(Voc.Label_SelectDocument),
	      Lang.get(Voc.Dialog_OpenDocument), JOptionPane.PLAIN_MESSAGE, null, fileCaptions, fileCaptions[0]);
			//open file
			if (selectedFile != null)
			{
				//find file that belongs to the selected caption
				int selectedFileIndex = -1;
				for (int i = 0; i < files.length; i++)
				{
					if (files[i].caption == selectedFile || files[i].path == selectedFile)
					{
						selectedFileIndex = i;
						break;
					}
				}
		    if (selectedFileIndex > -1)
		    {
		      openURL(files[selectedFileIndex].path);
		    }
			}
		}
	}
	
	
	public void openURL(String path)
	{
		MusicXMLScoreDocumentFileInput input = new MusicXMLScoreDocumentFileInput();
		URL file = null;
		try
		{
			setCursorWaiting(true);
			URL codebase = appletSwing.getCodeBase();
			file = new URL(codebase, path);
			ScoreDocument doc = input.read(file.toString());
			doc.setScorePanelController(scorePanelController);
			this.document = doc;
			setCursorWaiting(false);
		}
		catch (Exception ex)
		{
			super.reportOpenFileError(ex, file != null ? file.getPath() : null);
		}
	}
	
	
	/**
	 * Saves the given {@link ScoreDocument} to the given file, using the
	 * given format. Supported formats:
	 * <ul>
	 * 	<li>MusicXML: Trys to copy the original file behind the given document</li>
	 * 	<li>MIDI: Uses the {@link MidiScoreDocumentFileOutput} class to create a MIDI file</li>
	 * </ul>
	 */
	@Override public void saveFile(ScoreDocument doc, File file, FileFormat format)
	{
		if (format == FileFormat.MusicXML)
		{
			//MusicXML
			String originalPath = doc.getFilePath();
			if (originalPath == null)
			{
				App.err().report(ErrorLevel.Warning, Voc.Error_SaveFileUnknownOrigin);
			}
			else
			{
				//TODO: open new window for the download (including progress bar), so that the
				//user can immediately go on
				setCursorWaiting(true);
				try
				{
					DownloadTools.downloadFile(originalPath, file);
				}
				catch (IOException ex)
				{
					App.err().report(ErrorLevel.Warning, Voc.Error_Download,
						Arrays.asList(originalPath, file.toString()));
				}
				setCursorWaiting(false);
			}
		}
		else
		{
			//forward to base class
			super.saveFile(doc, file, format);
		}
	}
  

}
