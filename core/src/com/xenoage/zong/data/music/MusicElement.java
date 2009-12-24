package com.xenoage.zong.data.music;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.ScoreEntity;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.music.util.DeepCopyCache;
import com.xenoage.util.InstanceID;


/**
 * Base class for all musical elements,
 * like notes, rests, barlines or directions.
 *
 * @author Andreas Wenger
 */
public abstract class MusicElement
  implements ScoreEntity
{
	
	//the parent voice of this element, or null
  protected Voice parentVoice = null;
	
  //the unique ID of this element, used for hashing
  protected final InstanceID instanceID = new InstanceID();
  
  
	/**
	 * Returns a deep copy of this element, using the given
	 * parent voice and {@link DeepCopyCache}.
	 */
	public abstract MusicElement deepCopy(Voice parentVoice, DeepCopyCache cache);
	
	
  /**
   * Gets the duration of this element.
   * Elements with no duration (like a time signature
   * or a slur) must return null.
   */
  public Fraction getDuration()
  {
  	return null;
  }
  
  
  /**
   * Gets the {@link ScorePosition} of this element. Notice,
   * that the result may contain information that is unset (null or -1).
   * If the parent voice is unknown, an {@link IllegalStateException} is thrown.
   */
  public ScorePosition getScorePosition()
  {
  	if (parentVoice != null)
  	{
  		Measure measure = parentVoice.getMeasure();
			return new ScorePosition(measure.getStaff().getIndex(), measure.getIndex(),
				getBeat(), getVoiceIndex());
  	}
		else
			throw new IllegalStateException("Element has no parent voice");
  }
  
  
  /**
	 * Gets the parent {@link Voice} of the element, or null
	 * if unknown.
	 */
	public Voice getVoice()
	{
		return parentVoice;
	}
	
	
	/**
   * Sets the parent {@link Voice} of this element.
   * This method should only be called by the voice adding this chord
   * or by the {@link ScoreController}.
   */
	public void setVoice(Voice voice)
  {
  	this.parentVoice = voice;
  }
  
  
  /**
   * Gets the {@link InstanceID} of this object, used
   * for hashmapping.
   */
  public InstanceID getInstanceID()
  {
  	return instanceID;
  }
  
  
  /**
	 * Gets the beat of this chord. This is only known if a parent
	 * voice exist. Otherwise an {@link IllegalStateException} is thrown.
	 */
	public Fraction getBeat()
	{
		if (parentVoice != null)
			return parentVoice.getBeat(this);
		else
			throw new IllegalStateException("Element has no parent voice");
	}
  
	
	/**
	 * Gets the voice index of this chord. This is only known if a parent
	 * voice exist.
	 */
	public int getVoiceIndex()
	{
		if (parentVoice != null)
			return parentVoice.getMeasure().getVoices().indexOf(parentVoice);
		else
			throw new IllegalStateException("Element has no parent voice");
	}
  

}
