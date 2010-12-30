package com.xenoage.zong.io.musicxml.in.readers;

import com.xenoage.pdlib.PVector;
import com.xenoage.zong.core.instrument.Instrument;
import com.xenoage.zong.core.music.Part;
import com.xenoage.zong.musicxml.types.MxlScorePart;


/**
 * Reads a {@link Part} from a given {@link MxlScorePart}.
 * 
 * @author Andreas Wenger
 */
public final class PartReader
{
	
	public static Part readPart(MxlScorePart mxlScorePart)
	{
		PVector<Instrument> instruments = InstrumentsReader.read(mxlScorePart);
		Instrument instrument = instruments.size() > 0 ?
			instruments.get(0) : Instrument.defaultValue;
		return new Part(mxlScorePart.getName(), mxlScorePart.getAbbreviation(),
			1, instrument);
	}

}
