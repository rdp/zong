package com.xenoage.zong.io.musicxml.in;

import com.xenoage.zong.data.info.*;

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
	
	private ScoreInfo scoreInfo;
  
  
  /**
   * Reads general information about a score
   * from the given MusicXML 2.0 document.
   */
  public MxlScoreInfo(ScorePartwise doc)
  {
  	scoreInfo = new ScoreInfo();
    
  	scoreInfo.setMovementNumber(doc.getMovementNumber());
  	scoreInfo.setMovementTitle(doc.getMovementTitle());
    
    Work mxlWork = doc.getWork();
    if (mxlWork != null)
    {
    	scoreInfo.setWorkNumber(mxlWork.getWorkNumber());
    	scoreInfo.setWorkTitle(mxlWork.getWorkTitle());
    }
    
    Identification mxlIdentification = doc.getIdentification();
    if (mxlIdentification != null)
    {
    	List<TypedText> mxlCreators = mxlIdentification.getCreator();
    	for (TypedText creator : mxlCreators)
    	{
    		scoreInfo.addCreator(new Creator(creator.getValue(), creator.getType()));
    	}
    	List<TypedText> mxlRights = mxlIdentification.getRights();
    	for (TypedText right : mxlRights)
    	{
    		scoreInfo.addRights(new Rights(right.getValue(), right.getType()));
    	}
    }
  }
  
  
  /**
   * Gets the score information.
   */
  public ScoreInfo getScoreInfo()
  {
    return scoreInfo;
  }
  

}
