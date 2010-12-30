package com.xenoage.zong.app;

import static com.xenoage.pdlib.PVector.pvec;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.FileTools;
import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.xml.XMLReader;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.core.instrument.Instrument;
import com.xenoage.zong.core.instrument.InstrumentGroup;
import com.xenoage.zong.core.instrument.PitchedInstrument;
import com.xenoage.zong.core.instrument.Transpose;
import com.xenoage.zong.core.instrument.UnpitchedInstrument;
import com.xenoage.util.logging.Log;


/**
 * This class loads all instruments from the xml-files
 * into the instruments folder in an ArrayList.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class InstrumentsPool
{
	private final String instrumentspath="data/instruments/";
	private Map<String, InstrumentGroup> groups = new HashMap<String, InstrumentGroup>();
	private ArrayList<Instrument> instruments = new ArrayList<Instrument>();
	
	
	public void loadPool()
	{
		String[] files= new File(instrumentspath).list(FileTools.getXMLFilter());
		for (String file: files)
		{
			Log.log(Log.MESSAGE,"Loading instrument file \"" + file + "\"...");
			Document doc=null;
			try
			{
				doc=XMLReader.readFile(instrumentspath +"/" + file);
			}
			catch (Exception e)
			{
				Log.log(Log.ERROR,
				        "Instrument file \"" + file + "\" could not be loaded! ");
				App.err().report(ErrorLevel.Fatal, Voc.Error_Unknown, e); //TODO: not unknown error!
			}
			Element root= XMLReader.root(doc);
			//load groups
			Element eGroupsList = XMLReader.element(root, "groups");
			List<Element> listGroups = XMLReader.elements(eGroupsList, "group");
			for (int i = 0; i < listGroups.size(); i++)
			{
				Element eGroup = listGroups.get(i);
				String gID = XMLReader.attribute(eGroup, "id");
				String gName = XMLReader.attribute(eGroup, "name");
				if (groups.containsKey(gID))
				{
					Log.log(Log.WARNING, "Group \"" + gID + "\" already defined. Not overwritten, but reused.");
				}
				else
				{
					groups.put(gID, new InstrumentGroup(gID, gName));
				}
			}
			//load instruments
			List<Element> eEntries = XMLReader.elements(root, "instrument");
			for (int i=0; i<eEntries.size();i++)
			{
				Element eInstrument = eEntries.get(i);
				String iID = XMLReader.attribute(eInstrument, "id");
				String iName= XMLReader.elementTextNotNull(eInstrument, "name");
				String iAbbr = XMLReader.elementText(eInstrument, "abbr");
				
				//assign groups
				List<Element> eGroups = XMLReader.elements(
          XMLReader.element(eInstrument, "groups"), ("group"));
				PVector<InstrumentGroup> iGroups = pvec();
				for (int a=0; a<eGroups.size();a++)
				{
					Element eGroup = eGroups.get(a);
					InstrumentGroup group = groups.get(XMLReader.text(eGroup));
					if (group == null)
					{
						Log.log(Log.WARNING, "Unknown group \"" + XMLReader.text(eGroup) + "\" in instrument \"" + iID + "\". Ignored.");
					}
					else
					{
						iGroups = iGroups.plus(group);
					}
				}
				
				Instrument instrument;
				
				//pitched or unpitched instrument?
				boolean unpitched = (XMLReader.element(eInstrument, "unpitched") != null);
				
				if (!unpitched)
				{
					//pitched instrument
					int iMidiProgram = Integer.parseInt(
	          XMLReader.elementText(eInstrument, "midi-program"));
					String iPitchBottom = XMLReader.elementText(eInstrument, "pitch-bottom");
					String iPitchTop = XMLReader.elementText(eInstrument, "pitch-top");
					int iPolyphon = Integer.parseInt(
	          XMLReader.elementText(eInstrument, "polyphonic"));
					int iTranspose = Integer.parseInt(
	          XMLReader.elementText(eInstrument, "transpose"));
				
					//TODO: parse iPitchBottom and iPitchTop, diatonic
					instrument = new PitchedInstrument(iID, iName, iAbbr, iGroups,
						iMidiProgram, new Transpose(iTranspose, 0, 0, false), null, null, iPolyphon);
				}
				else
				{
					//unpitched instrument
					instrument = new UnpitchedInstrument(iID, iName, iAbbr, iGroups);
				}
				
				/*
				 * TODO: discuss which of the elements within <staves> are necessary and
				 * how to save them
				List<Element> eStaves = XMLReader.elements(
          XMLReader.element(eInstrument, "staves"), "staff");
				for (int a=0;a<eStaves.size();a++)
				{
					Element eStaff =eStaves.get(a);
					InstrumentStaff is = new InstrumentStaff();
					is.setClef(XMLReader.attribute(eStaff, "clef"));
					is.setLines(Integer.parseInt(XMLReader.attribute(eStaff, "lines")));
					is.setBottomline(XMLReader.attribute(eStaff, "bottomline"));
					instr.addStaves(is);
				}
				*/
				
				instruments.add(instrument);
			}
		}
	}

	/**
	 * @return the instruments
	 */
	public ArrayList<Instrument> getInstruments()
	{
		return instruments;
	}


	public Instrument getInstrument(String id)
	{
		for (Instrument instrument : instruments)
		{
			if (instrument.getID().equals(id))
				return instrument;
		}
		return null;
	}

	
}
