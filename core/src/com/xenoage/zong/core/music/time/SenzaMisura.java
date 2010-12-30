package com.xenoage.zong.core.music.time;

import com.xenoage.pdlib.IVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;


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
  implements Time
{
  
  
  public SenzaMisura()
  {
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
	@Override public Vector<Tuple2<Fraction, BeatWeight>> getBeatWeights()
	{
		return new IVector<Tuple2<Fraction, BeatWeight>>();
	}


}
