package com.xenoage.zong.io.musicxml.in;

import com.xenoage.pdlib.PVector;
import com.xenoage.zong.core.info.*;

import java.util.List;

import proxymusic.Identification;
import proxymusic.ScorePartwise;
import proxymusic.TypedText;
import proxymusic.Work;


/**
 * This class reads information about a score
 * from a MusicXML 2.0 document.
 *
 * @author Andreas Wenger
 */
class MxlScoreInfo
{
	
	private final ScoreInfo scoreInfo;
  
  
  /**
   * Reads general information about a score
   * from the given MusicXML 2.0 document.
   */
  public MxlScoreInfo(ScorePartwise doc)
  {
  	String movementNumber = doc.getMovementNumber();
  	String movementTitle = doc.getMovementTitle();
    
    Work mxlWork = doc.getWork();
    String workNumber = null;
    String workTitle = null;
    if (mxlWork != null)
    {
    	workNumber = mxlWork.getWorkNumber();
    	workTitle = mxlWork.getWorkTitle();
    }
    
    Identification mxlIdentification = doc.getIdentification();
    PVector<Creator> creators = PVector.pvec();
    PVector<Rights> rights = PVector.pvec();
    if (mxlIdentification != null)
    {
    	List<TypedText> mxlCreators = mxlIdentification.getCreator();
    	for (TypedText creator : mxlCreators)
    	{
    		creators = creators.plus(new Creator(creator.getValue(), creator.getType()));
    	}
    	List<TypedText> mxlRights = mxlIdentification.getRights();
    	for (TypedText right : mxlRights)
    	{
    		rights = rights.plus(new Rights(right.getValue(), right.getType()));
    	}
    }
    
    scoreInfo = new ScoreInfo(workTitle, workNumber, movementTitle, movementNumber,
    	creators, rights);
  }
  
  
  /**
   * Gets the score information.
   */
  public ScoreInfo getScoreInfo()
  {
    return scoreInfo;
  }
  

}
