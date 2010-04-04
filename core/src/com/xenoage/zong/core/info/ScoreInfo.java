package com.xenoage.zong.core.info;

import static com.xenoage.util.NullTools.notNull;

import com.xenoage.pdlib.PVector;


/**
 * Information about a single score,
 * like title or composer.
 *
 * @author Andreas Wenger
 */
public final class ScoreInfo
{
  
  private final String workTitle;
  private final String workNumber;
  private final String movementTitle;
  private final String movementNumber;
  private final PVector<Creator> creators;
  private final PVector<Rights> rights;
  
  private static final ScoreInfo empty =
  	new ScoreInfo(null, null, null, null, new PVector<Creator>(), new PVector<Rights>());

  
  /**
   * Creates a new {@link ScoreInfo}.
	 * @param workTitle       title of the work, or null
	 * @param workNumber      number of the work, or null
	 * @param movementTitle   title of the movement, or null
	 * @param movementNumber  number of the movement, or null
	 * @param creators        list of creators (composers, arrangers, ...)
	 * @param rights          list of rights rights
	 */
	public ScoreInfo(String workTitle, String workNumber, String movementTitle,
		String movementNumber, PVector<Creator> creators, PVector<Rights> rights)
	{
		this.workTitle = workTitle;
		this.workNumber = workNumber;
		this.movementTitle = movementTitle;
		this.movementNumber = movementNumber;
		this.creators = creators;
		this.rights = rights;
	}
	
	
	/**
	 * Gets an empty {@link ScoreInfo}.
	 */
	public static ScoreInfo empty()
	{
		return empty;
	}


	public String getMovementNumber()
  {
    return movementNumber;
  }
	
	
	public ScoreInfo withMovementNumber(String movementNumber)
  {
    return new ScoreInfo(workTitle, workNumber, movementTitle,
    	movementNumber, creators, rights);
  }

  
  public String getMovementTitle()
  {
    return movementTitle;
  }
  
  
  public ScoreInfo withMovementTitle(String movementTitle)
  {
  	return new ScoreInfo(workTitle, workNumber, movementTitle,
    	movementNumber, creators, rights);
  }

  
  public String getWorkNumber()
  {
    return workNumber;
  }
  
  
  public ScoreInfo withWorkNumber(String workNumber)
  {
  	return new ScoreInfo(workTitle, workNumber, movementTitle,
    	movementNumber, creators, rights);
  }

  
  public String getWorkTitle()
  {
    return workTitle;
  }
  
  
  public ScoreInfo withWorkTitle(String workTitle)
  {
  	return new ScoreInfo(workTitle, workNumber, movementTitle,
    	movementNumber, creators, rights);
  }
  
  
  public PVector<Creator> getCreators()
  {
    return creators;
  }
  
  
  public PVector<Rights> getRights()
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
