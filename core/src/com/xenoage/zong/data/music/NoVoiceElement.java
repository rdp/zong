package com.xenoage.zong.data.music;

import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * A {@link NoVoiceElement} is the interface for clefs, time signatures,
 * key signatures, barlines and all other elements
 * which do not belong to a certain voice but
 * contain information for all following elements in the whole measure.
 * 
 * Technically, they can belong to a voice, but if so, this is by convention
 * always voice 0 of the measure.
 * 
 * @author Andreas Wenger
 */
public abstract class NoVoiceElement
	extends MusicElement
{
	
	
	/**
	 * Returns a deep copy of this element, using the given
	 * parent voice and {@link DeepCopyCache}.
	 */
	@Override public abstract NoVoiceElement deepCopy(Voice parentVoice, DeepCopyCache cache);

}
