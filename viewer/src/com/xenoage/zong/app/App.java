package com.xenoage.zong.app;

import java.awt.Desktop;
import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.exceptions.InvalidFormatException;
import com.xenoage.zong.Zong;
import com.xenoage.zong.app.error.ErrorProcessing;
import com.xenoage.util.io.IO;
import com.xenoage.util.language.Lang;
import com.xenoage.util.language.LanguageInfo;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.settings.Settings;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.app.symbols.SymbolPool;
import com.xenoage.zong.app.tools.ToolPreview;
import com.xenoage.zong.app.tools.choice.ToolChoiceStrategy;
import com.xenoage.zong.commands.Command;
import com.xenoage.zong.commands.dialogs.SaveDocumentDialogViewerCommand.FileFormat;
import com.xenoage.zong.documents.ViewerDocument;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.gui.controller.panels.GLScorePanelController;
import com.xenoage.zong.gui.controller.panels.ScorePanelController;
import com.xenoage.zong.io.midi.out.MidiScoreDocumentFileOutput;
import com.xenoage.zong.io.midi.out.SynthManager;
import com.xenoage.zong.io.score.ScoreInputOptions;
import com.xenoage.zong.layout.Layout;
import com.xenoage.zong.layout.frames.ScoreFrame;
import com.xenoage.zong.musiclayout.stampings.Stamping;
import com.xenoage.zong.print.PDFPrinter;
import com.xenoage.util.logging.ApplicationLog;
import com.xenoage.util.logging.Log;
import com.xenoage.zong.util.LookAndFeel;
import com.xenoage.zong.view.ScorePageView;


//TODO: remove dependency from viewer to player by moving
//AudioSettingsDialog + its vocabulary to the core project


/**
 * This is the base class for the main class of both the
 * application and the applet of any Zong! program that
 * displays scores.
 * 
 * It has a single instance and manages the score panel,
 * symbol pool, main frame, error processing and other
 * important ressources which are needed throughout the
 * whole program.
 *
 * @author Andreas Wenger
 */
public class App
{
  
  //the only instance of the App class (singleton pattern)
  static App instance = null;
  
  //the score panel
  ScorePanelController scorePanelController = null;
  
  //the pools for symbols, ...
  SymbolPool symbolPool = SymbolPool.empty();
  
  //main frame, used as parent for showing dialogs (should also work for applets)
  Frame mainFrame;

	//error handling class
  //initialize here, because must be available from the very beginning
  ErrorProcessing errorProcessing = new ErrorProcessing(); 
  
  //cursors
  private java.awt.Cursor lastCursorNotWaiting = java.awt.Cursor.getDefaultCursor();
  private boolean cursorWaiting = false;
  
  //input - TIDY
  private ScoreInputOptions scoreInputOptions = new ScoreInputOptions();
  
  
  /**
   * Gets the only instance of the {@link App} class.
   * One of {@link App}'s subclasses should be instatiated before. Then
   * that one is returned. Otherwise, a new instance of this class
   * is created.
   */
  public static App getInstance()
  {
    if (instance == null)
    {
    	//create minimal app for testing purposes
      instance = new App();
    }
    return instance;
  }
  
  
  /**
   * Initialized this a new {@link App}. Call this method immediately after
   * the constructor. May be called only one time during the lifetime of the program.
   */
  public void init()
  {
  	//initialize objects using the template pattern
  	initIO();
  	initLogging();
  	initApp();
  	initLanguage();
  	initGUI();
  	initAudio(true);
  	initialized();
  }
  
  
  /**
   * Initializes this instance.
   */
  void initApp()
  {
  	if (instance != null)
  	{
  		throw new IllegalStateException("App was already initialized");
  	}
  	instance = this;
  	Settings.setErrorProcessing(errorProcessing);
  	
  	Lang.registerToken("{app.name}", getName());
  	
  	//create the score input options
    scoreInputOptions = new ScoreInputOptions();
  }
  
  
  /**
   * Inits the IO. By default, the application IO is used.
   */
  void initIO()
  {
  	IO.initApplication(ViewerApplication.FILENAME);
  }
  
  
  /**
   * Inits logging.
   */
  void initLogging()
  {
  	Log.initApplicationLog(ApplicationLog.FILENAME_DEFAULT, getNameAndVersion());
  }
  
  
  /**
   * Inits the default language pack.
   */
  void initLanguage()
  {
  	Lang.setErrorProcessing(errorProcessing);
  	try
  	{
	  	//get available languages
	  	List<LanguageInfo> languages = LanguageInfo.getAvailableLanguages(Lang.defaultLangPath);
	  	//use system's default (TODO: config)
	  	String langID = LanguageInfo.getDefaultID(languages);
			Lang.loadLanguage(langID);
  	}
  	catch (Exception ex)
  	{
  		errorProcessing.report(ErrorLevel.Remark, "Could not load language", ex);
  		Lang.loadLanguage("en");
  	}
  }
  
  
  /**
   * Initializes the GUI.
   */
  void initGUI()
  {
  	//use operating system style
		LookAndFeel.activateSystemLookAndFeel();
		//enable menus for AWT
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		//symbol pool
  	this.symbolPool = createSymbolPool();
  	//score panel
  	this.scorePanelController = new GLScorePanelController();
  	//main frame
  	this.mainFrame = createMainFrame();
  	errorProcessing.setParentFrame(mainFrame);
  	
		//TEMP
  	Zong.showWorkInProgressWarning();
		
  }
  
  
  /**
   * Initialized the audio.
   */
  void initAudio(boolean readSettings)
  {
    try
    {
    	//TODO: known bug. workaround (close program, but avoid crash)
    	//Stack Trace: (program in path "C:\Users\andi\test\Zong!\doesntmatteranymore\")
    	//sun.misc.ServiceConfigurationError: javax.sound.midi.spi.MidiDeviceProvider: : java.io.FileNotFoundException: C:\Users\andi\test\Zong (Das System kann die angegebene Datei nicht finden)
    	//at sun.misc.Service.fail(Unknown Source)
    	//...
    	//at javax.sound.midi.MidiSystem.getSynthesizer(Unknown Source)
    	try
    	{
	    	if (System.getProperty("user.dir").contains("!"))
				{
					showMessageDialog("Due to a bug in a Java Sound module this program\n" +
						"can not be started under a path which contains an \"!\".\n" +
						"Please move the whole program directory to another location.");
					System.exit(0);
				}
    	}
    	catch (SecurityException ex)
    	{
    		//ignore this (happens in applets)
    	}
    	SynthManager.init(readSettings);
    }
    catch (MidiUnavailableException ex)
    {
    	err().report(ErrorLevel.Warning, Voc.Error_MidiNotAvailable, ex);
    }
  }
  
  
  /**
   * Called, when initialization has finished.
   */
  void initialized()
  {
  }
  
  
  /**
   * Creates the pool of symbols.
   */
  SymbolPool createSymbolPool()
  {
  	return SymbolPool.loadDefault();
  }
  
  
  /**
   * Creates the main frame.
   * It is null by default.
   */
  Frame createMainFrame()
  {
  	return null;
  }
  
  
  /**
   * Gets the main frame of the application. Should also work for applets,
   * but currently no guarantee can be given that it is not null in any case.
   */
  public Frame getMainFrame()
	{
		return mainFrame;
	}

  
  /**
   * Gets the active document. Returns null by default.
   */
  public ViewerDocument getActiveDocument()
  {
  	return null;
  }
  
  
  /**
   * Gets the active score document, or null, if the current document is no score document.
   */
  public ScoreDocument getScoreDocument()
  {
    ViewerDocument doc = getActiveDocument();
    if (doc instanceof ScoreDocument)
      return (ScoreDocument) doc;
    else
      return null;
  }


  /**
   * Gets the default symbol pool.
   */
  public SymbolPool getSymbolPool()
  {
    return symbolPool;
  }
  
  
  /**
   * Gets the serial number of this copy of the application.
   * TODO
   */
  public String getSerialNumber()
  {
    return "123456789";
  }
   

  /**
   * Shows a message dialog with informations.
   */
  public void showMessageDialog(String message, String title)
  {
    Log.log(Log.MESSAGE, "Message: " + message);
    JOptionPane.showMessageDialog(mainFrame, message, title, JOptionPane.OK_OPTION | JOptionPane.INFORMATION_MESSAGE);
  }
  
  
  /**
   * Shows a message dialog with information.
   */
  public void showMessageDialog(String message)
  {
    showMessageDialog(message, getNameAndVersion());
  }
  
  
  /**
   * Shows a confirm dialog with the given options
   * (JOptionPane-constants).
   */
  public int showConfirmDialog(String message, String title, int options)
  {
    Log.log(Log.MESSAGE, "Confirm: " + message);
    try
    {
      return JOptionPane.showConfirmDialog(mainFrame, message, title, options);
    }
    catch (Exception ex)
    {
      Log.log(Log.ERROR, this, ex);
      return 0;
    }
  }
  
  
  /**
   * Opens the given file. Must be implemented by the subclasses.
   */
  public void openFile(File file)
  {
  }
  
  
  /**
	 * Saves the given {@link ScoreDocument} to the given file, using
	 * the given format. If the format is unsupported by this type of
	 * application, an {@link IllegalStateException} is thrown.
	 * 
	 * Currently supported formats (by all kinds of applications):
	 * <ul>
	 * 	<li>MIDI</li>
	 * 	<li>PDF</li>
	 * </ul>
	 */
	public void saveFile(ScoreDocument doc, File file, FileFormat format)
	{
		if (format == FileFormat.Midi)
		{
			//write midi
			MidiScoreDocumentFileOutput output = new MidiScoreDocumentFileOutput();
			try
			{
				setCursorWaiting(true);
				output.write(doc, file.getAbsolutePath());
				setCursorWaiting(false);
			}
			catch (IOException e)
			{
				setCursorWaiting(false);
				App.err().report(ErrorLevel.Error, Voc.Error_CouldNotSaveDocument, e, file.getAbsolutePath());
			}
		}
		else if (format == FileFormat.PDF)
		{
			//write PDF
			setCursorWaiting(true);
			PDFPrinter.print(doc.getDefaultLayout(), file);
			setCursorWaiting(false);
		}
		else
		{
			//unsupported
			throw new IllegalStateException("This kind of application does not support " +
				"saving files in format " + format);
		}
	}

	
	public static ErrorProcessing err()
	{
		return App.getInstance().errorProcessing;
	}
	
	
	/**
   * Activates or deactivates the waiting cursor.
   */
  public void setCursorWaiting(boolean waiting)
  {
    if (!this.cursorWaiting && waiting)
    {
    	//cursor changed from non-waiting into waiting
    	this.lastCursorNotWaiting = this.mainFrame.getCursor();
      this.mainFrame.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
    }
    else if (this.cursorWaiting && !waiting)
    {
    	//cursor changed from waiting into non-waiting
      this.mainFrame.setCursor(this.lastCursorNotWaiting);
    }
    this.cursorWaiting = waiting;
  }
  
  
  /**
   * Gets the current score input options of this
   * application.
   */
  public ScoreInputOptions getScoreInputOptions()
  {
    return scoreInputOptions;
  }
  
  
  /**
   * Invokes the handler of the given {@link Stamping}
   * within the given {@link ScoreFrame}, at the given point in
   * pixel coordinates.
   * Does nothing by default.
   */
  public void invokeHandler(Stamping stamping, ScoreFrame scoreFrame, Point2i point)
  {
  }
  
  
  /**
   * Update the menu items for undo and redo, if they exist.
   * Does nothing by default.
   */
  public void updateMenuItems(Command lastCommand, Command lastUndoneCommand)
  {
  }
  
  
  /**
   * Returns true, if this program is a desktop application that can be
   * closed by <code>System.exit(0)</code>. Applets return false.
   * Returns true by default.
   */
  public boolean isDesktopApp()
  {
  	return true;
  }
  
  
  /**
	 * True, when all key events are caught and forwarded to the controller
	 * of the main frame. Only applicable for desktop applications.
	 * 
	 * TIDY: move into other class
	 */
	public void setKeyDispatchingEnabled(boolean keyDispatchingEnabled)
	{
	}
	
	
	/**
	 * Gets the controller of the score panel.
	 */
	public ScorePanelController getScorePanelController()
  {
		return scorePanelController;
  }
	
	
	public ToolChoiceStrategy getToolChoiceStrategy()
	{
		return null;
	}
	
	
	public ToolPreview getToolPreview()
	{
		return null;
	}
	
	
	/**
	 * Creates a new {@link ScorePageView}, showing the given document and layout.
	 */
	public ScorePageView createScorePageView(ScoreDocument document, Layout layout)
	{
		return new ScorePageView(document, layout, scorePanelController);
	}
	
	
	/**
	 * Loads textures, that are specific for this kind of app.
	 */
	public void loadAppTextures(TextureManager textureManager)
		throws IOException
	{
	}
	
	
	/**
	 * Opens the given website in the web browser.
	 * @param url        the URL to open
	 * @param newWindow  if true, a new window is opened in the browser.
	 */
	public void openWebsite(URL url, boolean newWindow)
	{
		try
		{
			Desktop.getDesktop().browse(url.toURI());
		}
		catch (Exception ex)
		{
		}
	}
	
	
	/**
	 * Reports an error when opening a file.
	 */
	public void reportOpenFileError(Exception fileOpenError, String filePath)
	{
		setCursorWaiting(false);
		try
		{
			throw fileOpenError;
		}
		catch (FileNotFoundException ex)
		{
			App.err().report(ErrorLevel.Warning, Voc.Error_OpenFileNotFound, ex, filePath);
		}
		catch (InvalidFormatException ex)
		{
			App.err().report(ErrorLevel.DocumentFormatError, Voc.Error_OpenFileInvalidFormat, ex, filePath);
		}
		catch (IOException ex)
		{
			App.err().report(ErrorLevel.Error, Voc.Error_OpenFileIOError, ex, filePath);
		}
		catch (SecurityException ex)
		{
			App.err().report(ErrorLevel.Error, Voc.Error_Security, ex, filePath);
		}
		catch (Exception ex)
		{
			App.err().report(ErrorLevel.Warning, Voc.Error_Unknown, ex, filePath);
		}
	}
	
	
	/**
   * Gets the name and version of the program as a String.
   */
  public String getNameAndVersion()
  {
  	return Zong.getNameAndVersion(getProjectFirstName());
  }
  
  
  /**
   * Gets the name of the program as a String.
   */
  public String getName()
  {
  	return Zong.getName(getProjectFirstName());
  }
  
  
  /**
   * Gets the "first" name of the project, like "Viewer", "Player"
   * or "Editor".
   */
  public String getProjectFirstName()
  {
  	return "Unnamed";
  }


}
