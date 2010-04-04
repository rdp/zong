package com.xenoage.zong.musiclayout.layouter.scoreframelayout;

import com.xenoage.zong.core.music.time.CommonTime;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.notations.ClefNotation;
import com.xenoage.zong.musiclayout.notations.NormalTimeNotation;
import com.xenoage.zong.musiclayout.notations.TraditionalKeyNotation;
import com.xenoage.zong.musiclayout.stampings.ClefStamping;
import com.xenoage.zong.musiclayout.stampings.CommonTimeStamping;
import com.xenoage.zong.musiclayout.stampings.KeySignatureStamping;
import com.xenoage.zong.musiclayout.stampings.NormalTimeStamping;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.Stamping;


/**
 * Strategy to create stampings for key signatures, time signatures,
 * clefs, barlines and so on.
 * 
 * @author Andreas Wenger
 */
public class NoVoiceElementStampingStrategy
	implements ScoreLayouterStrategy
{
	
	
  public ClefStamping createClefStamping(ClefNotation clef, float positionX, StaffStamping staff)
  {
    return new ClefStamping(
      clef.getMusicElement(), staff, positionX, clef.getScaling());
  }
  
  
  public KeySignatureStamping createKeyStamping(TraditionalKeyNotation key, float positionX, StaffStamping staff)
  {
    return new KeySignatureStamping(
      key.getMusicElement(), key.getLinePositionC4(), key.getLinePositionMin(), positionX, staff);
  }
  
  
  public Stamping createTimeStamping(NormalTimeNotation time, float positionX, StaffStamping staff)
  {
  	if (time.getMusicElement() instanceof CommonTime)
  	{
  		return new CommonTimeStamping((CommonTime) time.getMusicElement(), positionX, staff);
  	}
  	else
  	{
	    return new NormalTimeStamping(
	      time.getMusicElement(), positionX, staff,
	      time.getNumeratorOffset(), time.getDenominatorOffset(), time.getDigitGap());
  	}
  }

}
