package com.xenoage.zong.io.musicxml.in;

import static com.xenoage.util.NullTools.notNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import proxymusic.MidiInstrument;
import proxymusic.ScoreInstrument;
import proxymusic.ScorePart;

import com.xenoage.zong.core.instrument.Instrument;
import com.xenoage.zong.core.instrument.PitchedInstrument;
import com.xenoage.zong.core.instrument.UnpitchedInstrument;
import com.xenoage.util.MathTools;
import com.xenoage.util.exceptions.InvalidFormatException;
import com.xenoage.util.logging.Log;


/**
 * This class reads the {@link Instrument}s from a
 * given {@link ScorePart}.
 * 
 * TODO: more information like midi channel, bank etc.
 * 
 * TODO: include better guesses about the instruments,
 * e.g. which MIDI program to take if nothing is specified,
 * use localized names and so on.
 * 
 * @author Andreas Wenger
 */
class MxlInstruments
{
	
	private List<Instrument> instruments = new LinkedList<Instrument>();
	
	
	/**
	 * Reads the instruments from the given {@link ScorePart}.
	 */
	public MxlInstruments(ScorePart mxlScorePart)
		throws InvalidFormatException
	{
    //score-instrument elements
    List<ScoreInstrument> mxlScoreInstruments = mxlScorePart.getScoreInstrument();
    HashMap<String, List<String>> instrumentBaseValues = 
    	new HashMap<String, List<String>>();
    for (ScoreInstrument mxlScoreInstr : mxlScoreInstruments)
    {
    	String id = mxlScoreInstr.getId();
    	String name = mxlScoreInstr.getInstrumentName(); //never null
    	String abbr = mxlScoreInstr.getInstrumentAbbreviation(); //may be null
    	instrumentBaseValues.put(id, Arrays.asList(name, abbr));
    }
    //midi-instrument elements
    List<MidiInstrument> mxlMidiInstruments = mxlScorePart.getMidiInstrument();
    for (MidiInstrument mxlMidiInstr : mxlMidiInstruments)
    {
      String id = ((ScoreInstrument) mxlMidiInstr.getId()).getId();
      List<String> baseValues = instrumentBaseValues.get(id);
      if (baseValues == null)
      {
      	Log.log(Log.WARNING, "Unknown midi-instrument: " + id);
      	continue;
      }
      Integer midiChannel = mxlMidiInstr.getMidiChannel();
      if (midiChannel != null && midiChannel.equals(10))
      {
      	//unpitched instrument
      	instruments.add(new UnpitchedInstrument(id, baseValues.get(0), baseValues.get(1), null));
      }
      else
      {
      	//pitched instrument
      	int midiProgram = notNull(mxlMidiInstr.getMidiProgram(), 0); //TODO: find value that matches instrument name
      	midiProgram = MathTools.clamp(midiProgram, 0, 127);
      	instruments.add(new PitchedInstrument(id, baseValues.get(0), baseValues.get(1), null,
      		midiProgram, 0, null, null, 0)); //TODO
      }
    }
	}

	
	/**
	 * Gets the list of read {@link Instrument}s.
	 */
	public List<Instrument> getInstruments()
	{
		return instruments;
	}
	

}
