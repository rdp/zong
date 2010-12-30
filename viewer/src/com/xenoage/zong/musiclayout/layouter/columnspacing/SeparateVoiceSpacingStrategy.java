package com.xenoage.zong.musiclayout.layouter.columnspacing;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.musiclayout.LayoutSettings.layoutSettings;

import com.xenoage.pdlib.PVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.Measure;
import com.xenoage.zong.core.music.MeasureElement;
import com.xenoage.zong.core.music.Voice;
import com.xenoage.zong.core.music.VoiceElement;
import com.xenoage.zong.musiclayout.BeatOffset;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.cache.NotationsCache;
import com.xenoage.zong.musiclayout.notations.Notation;
import com.xenoage.zong.musiclayout.spacing.horizontal.ElementWidth;
import com.xenoage.zong.musiclayout.spacing.horizontal.SpacingElement;
import com.xenoage.zong.musiclayout.spacing.horizontal.VoiceSpacing;


/**
 * Strategy to compute the horizontal spacing for a
 * single voice regardless of the spacing of other
 * voices or staves.
 * 
 * This is needed for precomputations needed for the final
 * spacing, which can be done when the {@link BeatOffset}s
 * are known by using the {@link BeatOffsetBasedVoiceSpacingStrategy}.
 * 
 * The barlines at the beginning and the end of the measure
 * and implicit initial clefs and time signatures (because of
 * line breaks) are ignored. Also {@link MeasureElement}s like
 * inner clefs or inner barlines are ignored.
 * 
 * @author Andreas Wenger
 */
public class SeparateVoiceSpacingStrategy
	implements ScoreLayouterStrategy
{

	
	/**
	 * Computes the {@link VoiceSpacing} for the given voice,
	 * which is computed separately, regardless of the spacing of other
	 * voices or staves. If a parent measure is given, its clefs and
	 * key signatures are considered for the computation, too.
	 * @param voice           the voice to compute
	 * @param measure         the parent measure of the voice, or null.
	 *                        if not null, its clefs and key signatures are considered
	 *                        for the computation, too. if leadingSpacing is true,
	 *                        clefs and key signatures at beat 0 are ignored.
	 * @param interlineSpace  interline space of the voice
	 * @param leadingSpacing  true, if there is a leading spacing at the beginning
	 *                        of this measure
	 * @param notations       the already computed notations (all elements within the
	 *                        given voice must be included)
	 * @param measureBeats    the time signature of this voice
	 */
  public VoiceSpacing computeVoiceSpacing(Voice voice, Measure measure, float interlineSpace,
  	boolean leadingSpacing, NotationsCache notations, Fraction measureBeats)
  {
    PVector<SpacingElement> ret = pvec();
    
    int elementsCount = voice.getElements().size();
    Vector<VoiceElement> elements = voice.getElements();
    
    //TODO: Task #36
    //get clefs and keys
    //measure = notNull(measure, Measure.minimal);
    //@MaybeNull BeatEList<Clef> clefs = measure.getClefs();
    //@MaybeNull BeatEList<Key> keys = measure.getKeys();
    //...
    
    //hard offset:
    //real offset where the last element ended, and no following
    //elements may be placed before this line
    float hardOffset = layoutSettings().offsetMeasureStart;
    
    //soft offset:
    //hard offset where the last element ended + the rear gap
    //of this element. things like left-suspended notes,
    //accidentals, clefs or keys may be placed before this line.
    float softOffset = hardOffset;
    
    //special case: no elements in the measure.
    if (elementsCount == 0)
    {
      return new VoiceSpacing(voice, interlineSpace, pvec(
        new SpacingElement(null, Fraction._0, 0),
        new SpacingElement(null, measureBeats, layoutSettings().widthMeasureEmpty)));
    }
    
    //iterate through the elements
    Fraction curBeat = fr(0);
    for (VoiceElement element : elements)
    {
      
      //get the notation
      Notation notation = notations.get(element);
      if (notation == null)
      {
      	throw new IllegalStateException("No notation for element " + element);
      }
      
      //get the width of the element (front gap, symbol's width, rear gap, lyric's width)
      ElementWidth elementWidth = notation.getWidth();
      //spacing dependent on the presence of lyrics
      if (elementWidth.getWidth() > elementWidth.getLyricWidth())
      {
      	//no lyrics or element itself needs more space then lyrics
	      //try to place the front gap within the last rear gap
	      if (softOffset - elementWidth.getFrontGap() >= hardOffset)
	      {
	        //no problem. there is enough space in the last rear gap.
	      }
	      else
	      {
	        //the last rear gap is not big enough for the current front gap.
	        //use new soft offset: hard offset + current front gap
	        softOffset = hardOffset + elementWidth.getFrontGap();
	      }
	      ret = ret.plus(new SpacingElement(element, curBeat, softOffset));
	      hardOffset = softOffset + elementWidth.getSymbolWidth();
	      softOffset = hardOffset + elementWidth.getRearGap();
      }
      else
      {
      	//lyric is the dominant part
      	ret = ret.plus(new SpacingElement(element, curBeat, softOffset));
      	//TODO: TASK #35
      	hardOffset = softOffset + elementWidth.getLyricWidth(); //TODO: correct?
	      softOffset = hardOffset; //TODO: correct?
      }
      //update beat cursor
      curBeat = curBeat.add(element.getDuration());
    }
    ret = ret.plus(new SpacingElement(null, curBeat, softOffset));
    
    return new VoiceSpacing(voice, interlineSpace, ret);
  }

}
