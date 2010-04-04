package com.xenoage.zong.app;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xenoage.util.language.Lang;
import com.xenoage.zong.core.instrument.Instrument;
import com.xenoage.zong.core.instrument.PitchedInstrument;


/**
 * @author Uli Teschemacher
 */
public class InstrumentsPoolTest
{
	
	
	@Test public void InstrumentsPoolTestFunction()
	{
		Lang.loadLanguage("de");
		InstrumentsPool ip = new InstrumentsPool();
		ip.loadPool();
		Instrument b = ip.getInstrument("piano");
		assertTrue(b instanceof PitchedInstrument);
		PitchedInstrument piano = (PitchedInstrument) b;
		assertNotNull(b);
		assertEquals("Klavier", piano.getLocalName());
		assertEquals("Kl", piano.getLocalAbbreviation());
		assertEquals(1, piano.getMidiProgram());
		assertEquals(0, piano.getTranspose());
		//TODO assertEquals("A0", b.getBottomPitch());
		//TODO assertEquals("C8", b.getTopPitch());
		assertEquals(0, piano.getPolyphonic());
		assertEquals("Tasteninstrumente", b.getGroupNames().get(0));
	}
	
}
