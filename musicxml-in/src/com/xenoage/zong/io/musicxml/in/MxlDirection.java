package com.xenoage.zong.io.musicxml.in;

import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.util.math.Fraction.fr;
import proxymusic.Sound;

import com.xenoage.util.font.FontInfo;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.music.directions.Crescendo;
import com.xenoage.zong.data.music.directions.Diminuendo;
import com.xenoage.zong.data.music.directions.Dynamics;
import com.xenoage.zong.data.music.directions.DynamicsType;
import com.xenoage.zong.data.music.directions.Pedal;
import com.xenoage.zong.data.music.directions.Tempo;
import com.xenoage.zong.data.music.directions.Wedge;
import com.xenoage.zong.data.music.directions.Words;
import com.xenoage.zong.data.music.format.Position;
import com.xenoage.util.exceptions.InvalidFormatException;


/**
 * This class reads a {@link proxymusic.Direction}.
 * 
 * Currently, only the types dynamics, pedal, wedges and words
 * are supported, as well as the tempo attribute of the sound
 * element.
 * 
 * @author Andreas Wenger
 */
class MxlDirection
{
	
	//input
	private MxlScoreData parentData;
	private MxlScoreDataContext context;
	
	
	/**
   * Reads the given direction, and writes
   * it to context of the given parent {@link MxlScoreData}.
   */
	public static void readDirection(proxymusic.Direction mxlDirection, MxlScoreData parentData)
		throws InvalidFormatException
	{
		new MxlDirection(parentData).doReadDirection(mxlDirection);
	}
	
	
	private MxlDirection(MxlScoreData parentData)
	{
		this.parentData = parentData;
		this.context = parentData.getContext();
	}
	

	/**
   * Does the work.
   */
  private void doReadDirection(proxymusic.Direction mxlDirection)
    throws InvalidFormatException
  {
  	
  	//staff
  	int staff = notNull(mxlDirection.getStaff(), 1).intValue() - 1;
  	
  	//direction-type. currently, only one element is supported.
  	proxymusic.DirectionType mxlType = mxlDirection.getDirectionType().get(0);
  	
  	Words words = null;
  	if (mxlType.getDynamics() != null)
  	{
  		//dynamics
  		DynamicsType type = Util.getDynamicsType(mxlType.getDynamics());
  		if (type != null)
  		{
  			Dynamics dynamics = new Dynamics(type);
  			parentData.writeNoVoiceElement(dynamics, staff);
  		}
  	}
  	if (mxlType.getPedal() != null)
  	{
  		//pedal
  		proxymusic.Pedal mxlPedal = (proxymusic.Pedal) mxlType.getPedal();
  		Pedal.Type type = null;
  		switch (mxlPedal.getType())
  		{
  			case START:
  				type = Pedal.Type.Start;
  				break;
  			case STOP:
  				type = Pedal.Type.Stop;
  				break;
  			case CHANGE:
  				break;
  		}
  		if (type != null)
  		{
  			Pedal pedal = new Pedal(type, Util.readPosition(mxlPedal, context.getTenthMm(),
  				context.getCurrentMusicContext(0).getLines()));
  			parentData.writeNoVoiceElement(pedal, staff);
  		}
  	}
  	else if (mxlType.getWedge() != null)
  	{
  		//wedge
  		proxymusic.Wedge mxlWedge = mxlType.getWedge();
  		int number = notNull(mxlWedge.getNumber(), 1);
  		Position pos = Util.readPosition(mxlWedge, context.getTenthMm(), context.getCurrentMusicContext(0).getLines());
  		switch (mxlWedge.getType())
  		{
  			case CRESCENDO:
  				Wedge crescendo = new Crescendo(null, pos);
  				parentData.writeNoVoiceElement(crescendo, staff);
  				context.openWedge(number, crescendo);
  				break;
  			case DIMINUENDO:
  				Wedge diminuendo = new Diminuendo(null, pos);
  				parentData.writeNoVoiceElement(diminuendo, staff);
  				context.openWedge(number, diminuendo);
  				break;	
  			case STOP:
  				Wedge wedge = context.closeWedge(number);
  				if (wedge == null)
  					throw new InvalidFormatException("Wedge " + (number+1) + " is not open!");
  				parentData.writeNoVoiceElement(wedge.getWedgeEnd(), staff);
  				break;	
  		}
  	}
  	else if (mxlType.getWords().size() > 0)
  	{
  		//words (currently only one element is supported)
  		proxymusic.FormattedText mxlWords = mxlType.getWords().get(0);
  		FontInfo fontInfo = new MxlFontInfo(mxlWords).getFontInfo();
  		Position position = Util.readPosition(mxlWords,
  			context.getTenthMm(), context.getCurrentMusicContext(0).getLines());
  		words = new Words(mxlWords.getValue(), fontInfo, position);
  	}
  	
  	//sound
  	if (mxlDirection.getSound() != null)
  	{
  		Sound mxlSound = mxlDirection.getSound();
  		//tempo
  		if (mxlSound.getTempo() != null)
  		{
  			//always expressed in quarter notes per minute
  			int quarterNotesPerMinute = mxlSound.getTempo().intValue();
  			//if there were words found, use them for the tempo
  			Tempo tempo;
  			if (words != null)
  			{
  				tempo = new Tempo(fr(1, 4), quarterNotesPerMinute, words.getText(), words.getPosition());
  				words = null; //words were used now
  			}
  			else
  			{
  				tempo = new Tempo(fr(1, 4), quarterNotesPerMinute, null, null);
  			}
  			parentData.writeTempo(tempo, staff);
  		}
  	}
  	
  	//if words have been found, that were not added yet, do it
  	if (words != null)
  	{
  		parentData.writeNoVoiceElement(words, staff);
  	}
  	
  }
  
}
  