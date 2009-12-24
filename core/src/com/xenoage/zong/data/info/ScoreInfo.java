package com.xenoage.zong.data.info;

import static com.xenoage.util.NullTools.notNull;

import java.util.*;


/**
 * Information about a single score,
 * like title or composer.
 *
 * @author Andreas Wenger
 */
public class ScoreInfo
{
  
  //title and number of the work
  private String workTitle = null;
  private String workNumber = null;
  
  //title and number of the movement
  private String movementTitle = null;
  private String movementNumber = null;
  
  //creators (composers, arrangers, ...)
  private ArrayList<Creator> creators = new ArrayList<Creator>();
  
  //rights
  private ArrayList<Rights> rights = new ArrayList<Rights>();

  
  public String getMovementNumber()
  {
    return movementNumber;
  }

  
  public void setMovementNumber(String movementNumber)
  {
    this.movementNumber = movementNumber;
  }

  
  public String getMovementTitle()
  {
    return movementTitle;
  }

  
  public void setMovementTitle(String movementTitle)
  {
    this.movementTitle = movementTitle;
  }

  
  public String getWorkNumber()
  {
    return workNumber;
  }

  
  public void setWorkNumber(String workNumber)
  {
    this.workNumber = workNumber;
  }

  
  public String getWorkTitle()
  {
    return workTitle;
  }

  
  public void setWorkTitle(String workTitle)
  {
    this.workTitle = workTitle;
  }
  
  
  public void addCreator(Creator creator)
  {
    creators.add(creator);
  }
  
  
  public List<Creator> getCreators()
  {
    return creators;
  }
  
  
  public void addRights(Rights rights)
  {
    this.rights.add(rights);
  }
  
  
  public List<Rights> getRights()
  {
    return rights;
  }
  
  
  /**
   * Gets the first mentioned composer of this score, or null
   * if unavailable.
   */
  public String getComposer()
  {
  	for (Creator creator : creators)
  	{
  		if (creator.getType() != null &&
  			creator.getType().toLowerCase().equals("composer"))
  			return creator.getName();
  	}
  	return null;
  }
  
  
  /**
   * Gets the title of the score. This is the movement-title, or if unknown,
   * the work-title. If both are unknown, null is returned.
   */
  public String getTitle()
  {
  	return notNull(movementTitle, workTitle);
  }
  
}
