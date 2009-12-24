package com.xenoage.zong.data.music.time;

import java.util.ArrayList;

import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Class for a senza-misura time signature.
 * 
 * A senza-misura element explicitly
 * indicates that there is no time signature.
 * Thus as many beats as needed are
 * allowed for each measure.
 *
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public final class SenzaMisura
  extends Time
{
  
  
  public SenzaMisura()
  {
  }
  
  
  /**
	 * Return this {@link SenzaMisura}. No deep copy is needed since everything is final.
	 */
	@Override public SenzaMisura deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}
  
  
  /**
   * Returns null, because as many beats as needed are
   * allowed in a senza-misura measure.
   */
	@Override public Fraction getBeatsPerMeasure()
  {
    return null;
  }
	
	
	@Override public boolean equals(Object o)
	{
		return (o instanceof SenzaMisura);
	}

	/**
	 * As the SenzaMisura time has no real beats an empty list is returned.
	 */
	@Override
	public ArrayList<Tuple2<Fraction, BeatWeight>> getBeatWeights()
	{
		ArrayList<Tuple2<Fraction, BeatWeight>> list = new ArrayList<Tuple2<Fraction, BeatWeight>>();
		return list;
	}


}
