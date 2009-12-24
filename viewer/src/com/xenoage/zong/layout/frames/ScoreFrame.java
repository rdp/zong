package com.xenoage.zong.layout.frames;

import com.xenoage.util.Range;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.musiclayout.ScoreFrameLayout;
import com.xenoage.zong.musiclayout.ScoreLayout;
import com.xenoage.zong.musiclayout.layouter.horizontalsystemfilling.HorizontalSystemFillingStrategy;
import com.xenoage.zong.musiclayout.layouter.horizontalsystemfilling.StretchHorizontalSystemFillingStrategy;
import com.xenoage.zong.musiclayout.layouter.verticalframefilling.NoVerticalFrameFillingStrategy;
import com.xenoage.zong.musiclayout.layouter.verticalframefilling.VerticalFrameFillingStrategy;
import com.xenoage.zong.renderer.GLGraphicsContext;
import com.xenoage.zong.renderer.SwingGraphicsContext;
import com.xenoage.zong.renderer.frames.GLScoreFrameRenderer;
import com.xenoage.zong.renderer.frames.SwingScoreFrameRenderer;


/**
 * A score frame is a frame that contains
 * a musical score.
 * 
 * A score frame can be linked to another
 * score frame, where the score goes on,
 * if it does not have enough space in this
 * one.
 * 
 * @author Andreas Wenger
 */
public class ScoreFrame
  extends Frame
{
  
  private ScoreFrameChain chain;
  
  private ScoreFrameLayout layout = null;
  
  //alignment of the systems
  private HorizontalSystemFillingStrategy horizontalFillingStrategy = null;
  private VerticalFrameFillingStrategy verticalFillingStrategy = null;
  
  
  
  /**
   * Creates a new score frame showing the given score.
   */
  public ScoreFrame(Point2f position, Size2f size, Score score)
  {
    super(position, size);
    initFillingStrategies();
    this.chain = ScoreFrameChain.createLimitedChain(score, this);
  }
  
  
  /**
   * Creates a new score frame without any content. If must be linked
   * to a {@link ScoreFrameChain} to be usable.
   */
  public ScoreFrame(Point2f position, Size2f size)
  {
    super(position, size);
    initFillingStrategies();
    this.chain = null;
  }
  
  
  /**
   * Initializes the horizontal and vertical filling strategy.
   */
  private void initFillingStrategies()
  {
  	//TODO: put default setting into XML file
  	horizontalFillingStrategy = StretchHorizontalSystemFillingStrategy.getInstance();
  	verticalFillingStrategy = NoVerticalFrameFillingStrategy.getInstance();
  }

  
  /**
   * Paints this frame with the given OpenGL context.
   */
  @Override public void paint(GLGraphicsContext context)
  {
    //paint this frame
    GLScoreFrameRenderer.getInstance().paint(this, context);
  }
  
  
  /**
   * Paints this frame with the given Swing context.
   */
  @Override public void paint(SwingGraphicsContext context)
  {
    //paint this frame
    SwingScoreFrameRenderer.getInstance().paint(this, context);
  }
  
  
  /**
   * Converts the given position from frame space into
   * score layout space.
   * Both spaces use mm units, the difference is the origin:
   * While frames have their origin in the center, the
   * origin of a score layout is in the upper left corner.
   */
  public Point2f computeScoreLayoutPosition(Point2f framePosition)
  {
    Point2f ret = framePosition;
    ret = ret.add(size.width / 2, size.height / 2);
    return ret;
  }
  
  
  /**
   * Gets the score frame layout of this score frame.
   * If it is not up to date, it is recomputed before
   * it is returned.
   */
  public ScoreFrameLayout getLayout()
  {
  	if (chain != null)
  	{
	  	chain.ensureLayouterInitialized();
	    return layout;
  	}
  	else
  	{
  		return null;
  	}
  }
  
  
  public void setLayout(ScoreFrameLayout layout)
  {
    this.layout = layout;
  }

  
  public Score getScore()
  {
  	if (chain != null)
  		return chain.getScore();
  	else
  		return null;
  }


  public ScoreLayout getScoreLayout()
  {
  	if (chain != null)
  		return chain.getScoreLayout();
  	else
  		return null;
  }


	/**
	 * Gets the horizontal system filling strategy for this score frame.
	 */
	public HorizontalSystemFillingStrategy getHorizontalSystemFillingStrategy()
	{
		return horizontalFillingStrategy;
	}


	/**
	 * Sets the horizontal system filling strategy for this score frame.
	 */
	public void setHorizontalSystemFillingStrategy(
		HorizontalSystemFillingStrategy horizontalFillingStrategy)
	{
		this.horizontalFillingStrategy = horizontalFillingStrategy;
		this.updateLayout();
	}


	/**
	 * Gets the vertical frame filling strategy for this score frame.
	 */
	public VerticalFrameFillingStrategy getVerticalFrameFillingStrategy()
	{
		return verticalFillingStrategy;
	}


	/**
	 * Sets the vertical frame filling strategy for this score frame.
	 */
	public void setVerticalFrameFillingStrategy(VerticalFrameFillingStrategy verticalFillingStrategy)
	{
		this.verticalFillingStrategy = verticalFillingStrategy;
		this.updateLayout();
	}
	
	
	/**
	 * Returns true, if this score frame is the first one in its
	 * score frame chain.
	 */
	public boolean isLeading()
	{
		if (chain != null)
			return (chain.getFrame(0) == this);
		else
			return false;
	}
	
	
	/**
	 * Updates the layout of the whole {@link ScoreFrameChain}.
	 */
	public void updateLayout()
	{
		if (chain != null)
			this.chain.updateLayout();
	}
	
	
	/**
	 * Updates the layout within the given range of measures,
	 * which is usually faster than <code>updateLayout()</code>.
	 */
	public void updateLayout(Range measures)
	{
		if (chain != null)
			this.chain.updateLayout(measures);
	}


	
	public ScoreFrameChain getScoreFrameChain()
	{
		return chain;
	}
	
	
	void setScoreFrameChain(ScoreFrameChain chain)
	{
		this.chain = chain;
	}
	

}
