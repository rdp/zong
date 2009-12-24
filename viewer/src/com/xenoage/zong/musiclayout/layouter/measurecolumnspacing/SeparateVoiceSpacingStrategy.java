package com.xenoage.zong.musiclayout.layouter.measurecolumnspacing;

import java.util.ArrayList;


import com.xenoage.util.RAList;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.music.MusicElement;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.key.Key;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.cache.NotationsCache;
import com.xenoage.zong.musiclayout.notations.Notation;
import com.xenoage.zong.musiclayout.spacing.horizontal.ElementWidth;
import com.xenoage.zong.musiclayout.spacing.horizontal.SpacingElement;
import com.xenoage.zong.musiclayout.spacing.horizontal.VoiceSpacing;
import com.xenoage.zong.util.ArrayTools;


/**
 * Strategy to compute the horizontal spacing for a
 * single voice regardless of the spacing of other
 * voices or staves.
 * 
 * This is needed for precomputations needed for the final
 * spacing, which can be done when the {@link BeatOffset}s
 * are known by using the
 * {@link BeatOffsetBasedVoiceSpacingStrategy}.
 * 
 * The barlines at the beginning and the end of the measure
 * and implicit initial clefs and time signatures (because of
 * line breaks) are ignored.
 * 
 * @author Andreas Wenger
 */
public class SeparateVoiceSpacingStrategy
	implements ScoreLayouterStrategy
{

	
	/**
	 * Computes the {@link VoiceSpacing} for the given voice,
	 * which is computed separately, regardless of the spacing of other
	 * voices or staves.
	 * @param voice           the voice to compute
	 * @param leadingSpacing  true, if there is a leading spacing at the beginning
	 *                        of this measure
	 * @param notations       the already computed notations (all elements within the
	 *                        given voice must be included)
	 * @param measureBeats    the time signature of this voice
	 * 
	 * //LAYOUT-PERFORMANCE (needed 2 of 60 seconds)
	 */
  public VoiceSpacing computeVoiceSpacing(Voice voice, boolean leadingSpacing,
  	NotationsCache notations, Fraction measureBeats)
  {
    ArrayList<SpacingElement> ret = new ArrayList<SpacingElement>();
    
    int elementsCount = voice.getElements().size();
    RAList<MusicElement> elements = voice.getElements();
    
    //real offset where the last element ended, and no following
    //elements may be placed behind this line
    float hardOffset = 1; //TODO: always 1 space at beginning? read from config file?
    //hard offset where the last element ended + the rear gap
    //of this element. things like left-suspended notes or
    //accidental may be placed behind this line.
    float softOffset = hardOffset;
    
    //special case: no elements in the measure.
    //use 8 spaces.
    if (elementsCount == 0)
    {
      return new VoiceSpacing(voice, new SpacingElement[]{
        new SpacingElement(null, Fraction._0, 0),
        new SpacingElement(null, measureBeats, 8)});
    }
    
    //* OBSOLETE. No. This is done later. TODO: where?!?
    //add a single space at the beginning (see book, page 75)
    //float startGap = 1; //TODO: 1, 2, other?
    //softOffset += startGap;
    //*/
    
    //iterate through the elements
    Fraction curBeat = new Fraction(0);
    for (MusicElement element : elements)
    {
    	
      //if we are at beat 0 and a leading spacing was used
      //for this voice and the element is a clef or
      //a key signature, do not use it again
      if (curBeat.getNumerator() == 0 && leadingSpacing &&
        (element instanceof Clef || element instanceof Key))
      {
        continue;
      }
      
      //get the notation
      Notation notation = notations.get(element);
      if (notation == null)
      {
      	//element has no notation, proceed with the next one
      	continue;
      }
      
      //get the layout of the element (front gap, symbol's width, rear gap, lyric's width)
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
	      ret.add(new SpacingElement(element, curBeat, softOffset));
	      hardOffset = softOffset + elementWidth.getSymbolWidth();
	      softOffset = hardOffset + elementWidth.getRearGap();
      }
      else
      {
      	//lyric is the dominant part
      	ret.add(new SpacingElement(element, curBeat, softOffset));
      	hardOffset = softOffset + elementWidth.getLyricWidth(); //TODO: correct?
	      softOffset = hardOffset; //TODO: correct?
      }
      //update beat cursor
      curBeat = curBeat.add(element.getDuration());
    }
    ret.add(new SpacingElement(null, curBeat, softOffset));
    
    return new VoiceSpacing(voice, ArrayTools.toSpacingElementArray(ret));
  }

}
