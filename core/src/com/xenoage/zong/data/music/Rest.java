package com.xenoage.zong.data.music;

import java.util.List;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Class for a rest.
 *
 * @author Andreas Wenger
 */
public class Rest
	extends VoiceElement
  implements DurationElement
{
	
	private RestData data;
  
  
  public Rest(RestData data)
  {
    this.data = data;
  }
  
  
  /**
	 * Returns a deep copy of this {@link Rest}, using the given
	 * parent voice.
	 */
	@Override public Rest deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		Rest ret = new Rest(data);
		ret.parentVoice = parentVoice;
		return ret;
	}

  
	@Override public Fraction getDuration()
  {
    return data.getDuration();
  }
	
	
	/**
   * Gets a list of the {@link Direction}s attached to this rest,
   * or null if there are none.
   */
  public List<Direction> getDirections()
  {
  	return null;
  }


}
