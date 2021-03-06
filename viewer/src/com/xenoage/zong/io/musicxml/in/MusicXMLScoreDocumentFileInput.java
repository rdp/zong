package com.xenoage.zong.io.musicxml.in;

import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import com.xenoage.util.FileTools;
import com.xenoage.util.exceptions.InvalidFormatException;
import com.xenoage.util.filter.AllFilter;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.app.App;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.format.LayoutFormat;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.io.ScoreDocumentFileInput;
import com.xenoage.zong.layout.Page;
import com.xenoage.zong.layout.frames.ScoreFrame;
import com.xenoage.zong.layout.frames.ScoreFrameChain;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouter;
import com.xenoage.zong.musicxml.types.MxlScorePartwise;


/**
 * This class reads a MusicXML 2.0 file
 * into a instance of the {@link ScoreDocument} class.
 *
 * @author Andreas Wenger
 */
public class MusicXMLScoreDocumentFileInput
  implements ScoreDocumentFileInput
{
	
	
	/**
	 * Creates a new MusicXML 2.0 reader.
	 */
	public MusicXMLScoreDocumentFileInput()
	{
	}
  
  
  /**
   * Gets the localized name of the supported file format.
   */
  public String getFileFormatName()
  {
    return "MusicXML 2.0";
  }
  
  
  /**
   * Gets the file filter of the supported format.
   */
  public FilenameFilter getFilenameFilter()
  {
    return FileTools.getXMLFilter();
  }
  
  
  /**
   * Creates a {@link ScoreDocument} instance from the document
   * at the given path.
   * 
   * If none is opened, null is returned.
   */
  @Override public ScoreDocument read(String filePath)
    throws InvalidFormatException, IOException
  {
  	Score score;
  	
  	List<Score> scores = FileReader.loadScores(filePath, new AllFilter<String>());
		if (scores.size() > 0)
			score = scores.get(0);
		else
			return null;

    //create the document
    ScoreDocument ret = new ScoreDocument(score, filePath, null);
    
    //page format
    Object layoutFormat = score.getMetaData().get("layoutformat");
    if (layoutFormat != null && layoutFormat instanceof LayoutFormat)
    {
    	ret.getDefaultLayout().setFormat((LayoutFormat) layoutFormat);
    }
    
    //create and fill at least one page
    Page page = ret.getDefaultLayout().addPage();
    Size2f frameSize = new Size2f(page.getFormat().getUseableWidth(),
    	page.getFormat().getUseableHeight());
    Point2f framePos = new Point2f(page.getFormat().getMargins().left + frameSize.width/2,
    	page.getFormat().getMargins().top + frameSize.height/2);
    
    ScoreFrame frame = new ScoreFrame(framePos, frameSize);
    ScoreFrameChain chain = ScoreFrameChain.createCompleteChain(score, frame, frameSize);
    page.addFrame(frame);
    ret.setCurrentScoreFrameChain(chain);
    
    //layout the score. if one page is not enough, add a page for each
    //additional score frame
    ScoreLayouter layouter = new ScoreLayouter(chain, App.getInstance().getSymbolPool());
    layouter.createLayout();
    for (ScoreFrame additionalFrame : chain.getAdditionalFrames())
    {
    	chain.addFrame(additionalFrame); //create "real" frame from this "additional" frame
    	Page additionalPage = ret.getDefaultLayout().addPage();
    	additionalFrame.setRelativePosition(framePos);
    	additionalPage.addFrame(additionalFrame);
    }
    chain.clearAdditionalFrames();
    
    //add credit elements
    Object o = score.getMetaData().get("mxldoc");
    if (o != null && o instanceof MxlScorePartwise)
    {
    	MxlScorePartwise doc = (MxlScorePartwise) o;
    	CreditsReader.read(doc, ret, score.getScoreFormat());
    }
    
    return ret;
  }
  

}
