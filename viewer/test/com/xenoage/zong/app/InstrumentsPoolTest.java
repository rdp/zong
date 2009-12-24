/**
 * 
 */
package com.xenoage.zong.app;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.xenoage.util.language.Lang;
import com.xenoage.zong.data.instrument.Instrument;
import com.xenoage.zong.data.instrument.InstrumentStaff;
import com.xenoage.zong.data.instrument.PitchedInstrument;

/**
 * @author Uli
 *
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
		InstrumentStaff is = new InstrumentStaff();
		is.setClef("clef-g");
		is.setLines(5);
		is.setBottomline("E4");
		InstrumentStaff is2 = new InstrumentStaff();
		is2.setClef("clef-f");
		is2.setLines(5);
		is2.setBottomline("G2");
		ArrayList<InstrumentStaff> al = new ArrayList<InstrumentStaff>();
		al.add(is);
		al.add(is2);
		//TODO assertEquals(al.get(1).getClef(),b.getStaves().get(1).getClef());
		//TODO assertEquals(al.get(1).getLines(),b.getStaves().get(1).getLines());
		//TODO assertEquals(al.get(1).getBottomline(),b.getStaves().get(1).getBottomline());
	}
}
