package com.xenoage.zong.musiclayout.layouter.measurecolumnspacing;

import static com.xenoage.util.math.Fraction.fr;

import com.xenoage.util.lang.Tuple2;
import com.xenoage.zong.core.music.MusicContext;
import com.xenoage.zong.core.music.clef.Clef;
import com.xenoage.zong.core.music.key.Key;
import com.xenoage.zong.core.music.key.TraditionalKey;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.cache.NotationsCache;
import com.xenoage.zong.musiclayout.layouter.notation.NotationStrategy;
import com.xenoage.zong.musiclayout.notations.ClefNotation;
import com.xenoage.zong.musiclayout.notations.TraditionalKeyNotation;
import com.xenoage.zong.musiclayout.spacing.horizontal.ElementWidth;
import com.xenoage.zong.musiclayout.spacing.horizontal.MeasureLeadingSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.SpacingElement;


/**
 * Strategy to compute the leading spacing for
 * the given measure.
 * 
 * This spacing contains the current clef and
 * key signature.
 * 
 * The additionally created notations are returned, too.
 * 
 * @author Andreas Wenger
 */
public class MeasureLeadingSpacingStrategy
	implements ScoreLayouterStrategy
{
	
	//used strategies
	private final NotationStrategy notationStrategy;
	
	
	/**
	 * Creates a new {@link MeasureLeadingSpacingStrategy}.
	 */
	public MeasureLeadingSpacingStrategy(NotationStrategy notationStrategy)
	{
		this.notationStrategy = notationStrategy;
	}

	
	/**
   * Computes and returns the leading spacing for the given measure.
   * This spacing contains the current clef and key signature.
   * The additionally created notations are returned, too.
   */
  public Tuple2<MeasureLeadingSpacing, NotationsCache> computeLeadingSpacing(MusicContext musicContext)
  {
    float xOffset = 1f; //TODO
    
    boolean useKey = false;
    Key key = musicContext.getKey();
    if (key instanceof TraditionalKey)
    {
      useKey = true;
    }
    
    SpacingElement[] elements = new SpacingElement[useKey ? 2 : 1];
    NotationsCache notations = new NotationsCache();
    
    Clef clef = new Clef(musicContext.getClef().getType()); //it is not the same element instance, but has the same meaning
    ClefNotation clefNotation = new ClefNotation(
    	clef, new ElementWidth(0, NotationStrategy.clefWidthIS, 0), musicContext.getClef().getType().getLine(), 1);
    notations.set(clefNotation, clef);
    elements[0] = new SpacingElement(clef, fr(0), xOffset);
    xOffset += NotationStrategy.clefWidthIS;
    
    if (useKey)
    {
      TraditionalKeyNotation keyNotation =
      	notationStrategy.computeTraditionalKey((TraditionalKey) key, clef);
      notations.set(keyNotation, key);
      elements[1] = new SpacingElement(key, fr(0), xOffset);
      xOffset += keyNotation.getWidth().getWidth();
    }
    
    return new Tuple2<MeasureLeadingSpacing, NotationsCache>(
    	new MeasureLeadingSpacing(elements, xOffset), notations);
  }

}
