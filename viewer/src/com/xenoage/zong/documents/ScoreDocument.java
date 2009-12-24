package com.xenoage.zong.documents;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import com.xenoage.util.font.FontInfo;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.tools.Tool;
import com.xenoage.zong.commands.CommandPerformer;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.event.ScoreChangedEvent;
import com.xenoage.zong.data.format.PageFormat;
import com.xenoage.zong.data.format.PageMargins;
import com.xenoage.zong.data.text.Alignment;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.data.text.FormattedTextParagraph;
import com.xenoage.zong.data.text.FormattedTextString;
import com.xenoage.zong.data.text.FormattedTextStyle;
import com.xenoage.zong.gui.controller.panels.ScorePanelController;
import com.xenoage.zong.gui.controller.panels.ScorePanelController.CursorLevel;
import com.xenoage.zong.gui.cursor.Cursor;
import com.xenoage.zong.io.score.ViewerScoreInput;
import com.xenoage.zong.layout.Layout;
import com.xenoage.zong.layout.Page;
import com.xenoage.zong.layout.frames.ScoreFrame;
import com.xenoage.zong.layout.frames.ScoreFrameChain;
import com.xenoage.zong.layout.frames.TextFrame;
import com.xenoage.zong.print.PrintProcess;
import com.xenoage.zong.view.ScorePageView;
import com.xenoage.zong.view.ScoreView;
import com.xenoage.zong.view.View;


/**
 * Class for a score document.
 * 
 * A score document may contain multiple scores.
 * It has at least one layout.
 * 
 * At the moment a score document has only one
 * layout and page view.
 *
 * @author Andreas Wenger
 */
public class ScoreDocument
  extends ViewerDocument
{
  
  //contained scores and their input interfaces
  private ArrayList<Score> scores = new ArrayList<Score>();
  private HashMap<Score, ViewerScoreInput> scoresInputs =
  	new HashMap<Score, ViewerScoreInput>(); //TODO: really save input objects in this class?
  
  //layouts. layout 0 is always the default layout.
  private ArrayList<Layout> layouts = new ArrayList<Layout>();
  
  //views. view 0 is always the default page view.
  private ArrayList<ScoreView> views = new ArrayList<ScoreView>();
  
  //the index of the current view
  private int currentViewIndex = 0;
  
  //the file this document belongs to (String, because may be a file or an URL)
  private String filePath = null;
  
  //the current tool
  private Tool selectedTool = null;
  
  //the command performer
  private CommandPerformer commandPerformer;
  
  //the current edited score and it's score frame chain
  private Score currentScore = null;
  private ScoreFrameChain currentScoreFrameChain = null;
  
  
  /**
   * Creates an empty score document.
   */
  public ScoreDocument()
    throws IllegalArgumentException
  {
    //create an empty layout
    Layout layout = new Layout(this);
    layouts.add(layout);
    //create command performer
    commandPerformer = new CommandPerformer(this);
  }
  
  
  
  /**
   * Creates a DEMO score document.
   * @param score     The score of the document. May not be null.
   * @param filePath  The corresponding path of the file/URL, if available, otherwise null.
   * @param scorePanelController  The controller of the score panel that
   *   is used to display the document, or null if no views are needed. - TIDY this is bad
   */
  public ScoreDocument(Score score, String filePath,
    ScorePanelController scorePanelController)
    throws IllegalArgumentException
  {
    if (score == null)
      throw new IllegalArgumentException("score may not be null!");
    addScore(score);
    
    this.filePath = filePath;
    
    Layout layout = new Layout(this);
    layouts.add(layout);
    
    /*
    //DEMO
    Page page = layout.addPage(new PageFormat(new Size2f(220, 250), new PageMargins(20, 20, 20, 20)));
    //GroupFrame frame = FrameTest.createFrameWithChildren();
    //page.addFrame(frame);
    
    //DEMO: score frame
    ScoreFrame scoreFrame = new ScoreFrame(new Point2f(110, 125), new Size2f(180, 210), score);
    page.addFrame(scoreFrame);
    //scoreFrame.setBackground(new ColorBackground(new Color(1, 1, 0, 0.5f)));
    //scoreFrame.setRelativeRotation(40 * (int) (Math.random() * 1.8));
    
    //TEST
    //PageTest.createPageWithRotatedFrames(layout);
    
    /* //DEMO: page 2 with images
    if (scorePanelController != null)
    {
      ImageFrameTest.createPageWithImageFrames(
        layout, ((GLScorePanelController) scorePanelController).getTextureManager());
    }//*/
    
    //create a page view for this layout
    if (scorePanelController != null)
    {
      ScorePageView pageView = App.getInstance().createScorePageView(this, layout);
      views.add(pageView);
    }
    
    //create command performer
    commandPerformer = new CommandPerformer(this);

  }
  
  
  /**
   * Creates a DEMO score document, with one page for each
   * given score.
   */
  public ScoreDocument(Score[] scores,
    ScorePanelController scorePanelController)
    throws IllegalArgumentException
  {
  	
  	this.filePath = null;
  	for (Score score : scores)
  	{
  		addScore(score);
  	}
    
    //DEMO
    Layout layout = new Layout(this);
    layouts.add(layout);
    
    for (Score score : scores)
    {
    	Page page = layout.addPage(new PageFormat(new Size2f(210, 297), new PageMargins(10, 10, 10, 10)));
    	ScoreFrame scoreFrame = new ScoreFrame(new Point2f(105, 145), new Size2f(190, 210), score);
    	page.addFrame(scoreFrame);
    	
    	//DEMO
      FormattedText ft = new FormattedText();
      FormattedTextStyle style = new FormattedTextStyle(
      	new FontInfo("SansSerif", 24f, null), Color.black, null);
      FormattedTextStyle style2 = new FormattedTextStyle(
      	new FontInfo("Monospaced", 16f, null), Color.blue, null);
      FormattedTextParagraph ftp = new FormattedTextParagraph();
      ftp.addElement(new FormattedTextString("We use ", style));
      ftp.addElement(new FormattedTextString("JTextPane", style2));
      ftp.addElement(new FormattedTextString("s to display and print text frames", style));
      ftp.setAlignment(Alignment.Center);
      ft.addParagraph(ftp);
      TextFrame tf = new TextFrame(ft, new Point2f(105, 20), new Size2f(190, 20));
      page.addFrame(tf);
    }
    
    /*
    //* DEMO
    Page page = layout.addPage(new PageFormat(new Size2f(220, 250), new PageMargins(20, 20, 20, 20)));
    TextFrame textFrame = new TextFrame(FormattedTextTest.getMixedStyleText(),
    	new Point2f(100, 50), new Size2f(100, 20));
    page.addFrame(textFrame);
    //*
    textFrame = new TextFrame(FormattedTextTest.getMixedStyleText(),
    	new Point2f(120, 80), new Size2f(80, 10));
    page.addFrame(textFrame);
    textFrame = new TextFrame(FormattedTextTest.getMixedStyleText(),
    	new Point2f(120, 120), new Size2f(80, 10));
    page.addFrame(textFrame);
    textFrame = new TextFrame(FormattedTextTest.getMixedStyleText(),
    	new Point2f(120, 160), new Size2f(80, 10));
    page.addFrame(textFrame);
    textFrame = new TextFrame(FormattedTextTest.getMixedStyleText(),
    	new Point2f(120, 200), new Size2f(80, 10));
    page.addFrame(textFrame);
    textFrame = new TextFrame(FormattedTextTest.getMixedStyleText(),
    	new Point2f(120, 240), new Size2f(80, 10));
    page.addFrame(textFrame);//*/
    
    //create a page view for this layout
    if (scorePanelController != null)
    {
      ScorePageView pageView = App.getInstance().createScorePageView(this, layout);
      views.add(pageView);
    }

  }
  
  
  private void addScore(Score score)
  {
  	scores.add(score);
  	scoresInputs.put(score, new ViewerScoreInput(score, App.getInstance().getScoreInputOptions()));
  }
  
  
  /**
   * Sets the score panel controller for this document.
   */
  public void setScorePanelController(ScorePanelController ctrl)
  {
    //create a page view for this layout
    ScorePageView pageView = App.getInstance().createScorePageView(this, getDefaultLayout());
    views.add(pageView);
    pageView.repaint();
  }
  
  
  
  /**
   * Gets the number of scores in this document.
   */
  public int getScoreCount()
  {
    return scores.size();
  }
  
  
  /**
   * Gets the score with the given index.
   */
  public Score getScore(int index)
  {
    return scores.get(index);
  }
  
  
  /**
   * Gets the corresponding file, or null.
   */
  public String getFilePath()
  {
    return filePath;
  }
  
  
  /**
   * Requests to print the layout of the
   * current view of this document.
   */
  @Override public void requestPrint()
  {
    Layout currentLayout = getCurrentLayout();
    PrintProcess printProcess = new PrintProcess();
    printProcess.requestPrint(currentLayout);
  }
  
  
  /**
   * Call this method when the given layout was changed.
   * This will redraw the changed layout, if it is active
   * at the moment.
   */
  public void layoutChanged(Layout layout)
  {
    if (layout == getCurrentLayout())
    {
      //repaint the current view
      View currentView = getCurrentView();
      if (currentView != null)
      {
        currentView.repaint();
      }
    }
  }
  
  
  /**
   * Call this method when the given score has been changed.
   * This will recompute and redraw the whole layout, or portions
   * of it, dependent on the implementation.
   */
  public void scoreChanged(ScoreChangedEvent event)
  {
    //tell all layouts that the given score was changed
    for (Layout layout : layouts)
    {
      layout.scoreChanged(event);
    }
    //repaint the current view
    View currentView = getCurrentView();
    if (currentView != null)
    {
      currentView.repaint();
    }
  }
  
  
  /**
   * Gets the current view, or null, if there is none.
   */
  public ScoreView getCurrentView()
  {
    if (views.size() == 0)
      return null;
    else
      return views.get(currentViewIndex);
  }
  
  
  /**
   * Gets the currently viewed layout, or null,
   * if there is none.
   */
  public Layout getCurrentLayout()
  {
    if (views.size() == 0)
      return null;
    else
      return views.get(currentViewIndex).getLayout();
  }
  
  
  /**
   * Gets the default layout of the document.
   * Often this layout is the only layout of a score,
   * but there may exist more (e.g. extracts).
   * This one is for example used by importers reading files that only
   * know one layout of a score.
   */
  public Layout getDefaultLayout()
  {
    return layouts.get(0);
  }
  
  
  /**
   * Changes the currently active score frame chain.
   */
  public void setCurrentScoreFrameChain(ScoreFrameChain scoreFrameChain)
  {
  	Score score = scoreFrameChain.getScore();
  	if (!scores.contains(score))
  		throw new IllegalStateException("Score is unknown to the document");
  	this.currentScore = score;
  	this.currentScoreFrameChain = scoreFrameChain;
  }
  
  
  /**
   * Gets the current input interface.
   */
  public ViewerScoreInput getCurrentScoreInput()
  {
  	if (currentScore == null)
  		throw new IllegalStateException("No score is current");
    return scoresInputs.get(currentScore);
  }
  
  
  /**
   * Gets the input interface of the given score.
   */
  public ViewerScoreInput getScoreInput(Score score)
  {
    return scoresInputs.get(score);
  }
  
  
  /**
   * Gets the current score frame chain.
   */
  public ScoreFrameChain getCurrentScoreFrameChain()
  {
  	if (currentScoreFrameChain == null)
  		throw new IllegalStateException("No score frame chain is current");
    return currentScoreFrameChain;
  }

  
  /**
   * Repaints the current view of the document, if available.
   */
  public void repaint()
  {
    View view = getCurrentView();
    if (view != null)
    {
      view.repaint();
    }
  }
  
  
  /**
   * Call this method when this document is activated, that means
   * if it was hidden and is now visible.
   */
  public void activated()
  {
  	updateToolMouseCursor();
  }
  
  
  /**
   * Gets the currently selected tool, or null.
   */
  public Tool getSelectedTool()
  {
    return selectedTool;
  }

  
  /**
   * Sets the currently selected tool, or null.
   */
  public void setSelectedTool(Tool selectedTool)
  {
  	//deactivate old tool
  	Tool oldTool = this.selectedTool;
  	this.selectedTool = null;
  	if (oldTool != null)
    {
  		oldTool.deactivated();
    }
  	//activate new tool
  	this.selectedTool = selectedTool;
    if (selectedTool != null)
    {
    	selectedTool.activated();
    }
    //update mouse cursor
    updateToolMouseCursor();
  }
  
  
  /**
   * Updates the mouse cursor of the {@link ScoreFrame}
   * corresponding to the currently selected tool.
   */
  public void updateToolMouseCursor()
  {
  	Cursor cursor = (selectedTool != null ? selectedTool.getScorePanelCursor() : null);
		App.getInstance().getScorePanelController().setCursor(cursor, CursorLevel.Tool);
  }
  
  
  /**
   * Gets the {@link CommandPerformer} for this document.
   */
  public CommandPerformer getCommandPerformer()
  {
  	return commandPerformer;
  }

}
