package com.xenoage.zong.io.musicxml.in.readers;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.CollectionUtils.map;
import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.util.lang.Tuple2.t;

import java.util.HashMap;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.MathTools;
import com.xenoage.util.annotations.MaybeEmpty;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.logging.Log;
import com.xenoage.zong.core.instrument.Instrument;
import com.xenoage.zong.core.instrument.PitchedInstrument;
import com.xenoage.zong.core.instrument.UnpitchedInstrument;
import com.xenoage.zong.musicxml.types.MxlMidiInstrument;
import com.xenoage.zong.musicxml.types.MxlScoreInstrument;
import com.xenoage.zong.musicxml.types.MxlScorePart;


/**
 * This class reads the {@link Instrument}s from a
 * given {@link MxlScorePart}.
 * 
 * TODO: more information like midi channel, bank etc.
 * 
 * TODO: include better guesses about the instruments,
 * e.g. which MIDI program to take if nothing is specified,
 * use localized names and so on.
 * 
 * @author Andreas Wenger
 */
public final class InstrumentsReader
{
	
	/**
	 * Reads the instruments from the given {@link ScorePart}.
	 */
	@MaybeEmpty public static PVector<Instrument> read(MxlScorePart mxlScorePart)
	{
		PVector<Instrument> ret = pvec();
    //score-instrument elements
    HashMap<String, Tuple2<String, String>> instrumentBaseValues = map();
    for (MxlScoreInstrument mxlScoreInstr : mxlScorePart.getScoreInstruments())
    {
    	String id = mxlScoreInstr.getID();
    	String name = mxlScoreInstr.getInstrumentName(); //never null
    	String abbr = mxlScoreInstr.getInstrumentAbbreviation();
    	instrumentBaseValues.put(id, t(name, abbr));
    }
    //midi-instrument elements
    for (MxlMidiInstrument mxlMidiInstr : mxlScorePart.getMidiInstruments())
    {
      String id = mxlMidiInstr.getID();
      Tuple2<String, String> baseValues = instrumentBaseValues.get(id);
      if (baseValues == null)
      {
      	Log.log(Log.WARNING, "Unknown midi-instrument: " + id);
      	continue;
      }
      Integer midiChannel = mxlMidiInstr.getMidiChannel();
      if (midiChannel != null && midiChannel.equals(10))
      {
      	//unpitched instrument
      	ret = ret.plus(new UnpitchedInstrument(id, baseValues.get1(), baseValues.get2(), null));
      }
      else
      {
      	//pitched instrument
      	int midiProgram = notNull(mxlMidiInstr.getMidiProgram(), 0); //TODO: find value that matches instrument name
      	midiProgram = MathTools.clamp(midiProgram, 0, 127);
      	ret = ret.plus(new PitchedInstrument(id, baseValues.get1(), baseValues.get2(), null,
      		midiProgram, null, null, null, 0)); //TODO
      }
    }
    return ret;
	}
	

}
