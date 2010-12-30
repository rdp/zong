package com.xenoage.zong.io.midi.out;

import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.core.music.MP.atStaff;
import static com.xenoage.zong.core.music.MP.mp;
import static com.xenoage.zong.io.midi.out.MidiVelocityConverter.getVelocityAtPosition;
import static com.xenoage.zong.io.midi.out.MidiVelocityConverter.getVoiceforDynamicsInStaff;
import static com.xenoage.zong.io.score.ScoreController.clipToMeasure;
import static com.xenoage.zong.io.score.ScoreController.getDivisions;
import static com.xenoage.zong.io.score.ScoreController.getFilledBeats;
import static com.xenoage.zong.io.score.ScoreController.getMeasureBeats;
import static com.xenoage.zong.io.score.ScoreController.getTimeAtOrBefore;
import static com.xenoage.zong.io.score.ScoreController.getUsedBeats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import com.xenoage.pdlib.Vector;
import com.xenoage.util.SortedList;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.util.settings.Settings;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.instrument.Instrument;
import com.xenoage.zong.core.instrument.PitchedInstrument;
import com.xenoage.zong.core.instrument.Transpose;
import com.xenoage.zong.core.music.Globals;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.Measure;
import com.xenoage.zong.core.music.Part;
import com.xenoage.zong.core.music.Staff;
import com.xenoage.zong.core.music.Voice;
import com.xenoage.zong.core.music.VoiceElement;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.chord.Note;
import com.xenoage.zong.core.music.time.BeatWeight;
import com.xenoage.zong.core.music.time.Time;


/**
 * This class creates a {@link SequenceContainer} containing
 * a MIDI sequence from a given {@link Score}.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class MidiConverter
{


	/**
	 * Converts a {@link Score} to a {@link Sequence}.
	 * @param score        the score to convert
	 * @param addMPEvents  true, if controller events containing the current musical position
	 *                     should be inserted in the sequence, otherwise false
	 * @param metronome    true to add metronome ticks, otherwise false
	 */
	public static SequenceContainer convertToSequence(Score score,
		boolean addMPEvents, boolean metronome)
	{

		ArrayList<Integer> staffTracks = new ArrayList<Integer>();
		int resolution = getDivisions(score);
		ArrayList<Long> measureStartTick = new ArrayList<Long>();
		int metronomeBeatTrackNumber = 0;
		LinkedList<Tuple2<Long, MP>> scorePositionTicks = new LinkedList<Tuple2<Long, MP>>();
		int stavesCount = score.getStavesList().getStavesCount();
		Track[] tracks = new Track[stavesCount];

		//compute mapping of staff indices to channel numbers
		int[] channelMap = createChannelMap(score);

		//create sequence
		Sequence seq = null;
		try
		{
			seq = new Sequence(Sequence.PPQ, resolution);
			Track tempoTrack = seq.createTrack();
			ArrayList<Tuple2<MP, MP>> playList = MidiRepetitionCalculator.createPlayList(score);

			//create tracks
			for (int iStaff = 0; iStaff < stavesCount; iStaff++)
			{
				tracks[iStaff] = seq.createTrack();
			}

			//activate MIDI programs
			int partFirstStaff = 0;
			for (Part part : score.getStavesList().getParts())
			{
				Instrument instrument = part.getInstrument();
				if (instrument instanceof PitchedInstrument)
				{
					PitchedInstrument pitchedInstrument = (PitchedInstrument) instrument;
					int channel = channelMap[partFirstStaff];
					if (channel > -1)
					{
						ShortMessage prgMsg = new ShortMessage();
						prgMsg.setMessage(ShortMessage.PROGRAM_CHANGE, channel, (pitchedInstrument)
							.getMidiProgram(), 0);
						tracks[0].add(new MidiEvent(prgMsg, 0)); //do all program changes in track 0
					}
				}
				partFirstStaff += part.getStavesCount();
			}
			
			//used beats in each measure column
			Vector<Fraction> realMeasureColumnBeats = getFilledBeats(score);

			//fill tracks
			for (int iStaff = 0; iStaff < stavesCount; iStaff++)
			{
				int channel = channelMap[iStaff];
				if (channel == -1)
				{
					continue; //no MIDI channel left for this staff
				}

				long currenttickinstaff = 0;
				Staff staff = score.getStaff(atStaff(iStaff));
				Track track = tracks[iStaff];

				Vector<Measure> measures = staff.getMeasures();
				int numberOfVoices = MidiVelocityConverter.getNumberOfVoicesInStaff(staff);
				int velocity[] = new int[numberOfVoices];
				for (int i = 0; i < velocity.length; i++)
				{
					velocity[i] = 90;
				}

				int tracknumber = iStaff;

				int[] voiceforDynamicsInStaff = getVoiceforDynamicsInStaff(staff, score.getGlobals());
				for (Tuple2<MP, MP> tuple : playList)
				{
					int transposing = 0;
					for (int iMeasure = tuple.get1().getMeasure(); iMeasure <= tuple.get2()
						.getMeasure(); iMeasure++)
					{
						Measure measure = measures.get(iMeasure);
						measureStartTick.add(currenttickinstaff);

						//TODO: transposition changes can happen everywhere in the measure
						Transpose t = null; //measure.getInstrumentChanges()...
						if (t != null)
						{
							transposing = t.getChromatic();
						}
						
						Fraction start, end;
						start = clipToMeasure(score, iMeasure, tuple.get1()).getBeat();
						end = clipToMeasure(score, iMeasure, tuple.get2()).getBeat();

						if (realMeasureColumnBeats.get(iMeasure).compareTo(end) < 0)
							end = realMeasureColumnBeats.get(iMeasure);

						for (int iVoice = 0; iVoice < measure.getVoices().size(); iVoice++)
						{
							Voice voice = measure.getVoices().get(iVoice);
							int currentVelocity = velocity[voiceforDynamicsInStaff[iVoice]];
							velocity[iVoice] = generateMidi(resolution, channel, currenttickinstaff,
								staff, track, currentVelocity, iVoice, voice, start, end, tracknumber,
								transposing, score.getGlobals());
						}
						Fraction measureduration = end.sub(start);
						currenttickinstaff += calculateTickFromFraction(measureduration, resolution);

					}
					staffTracks.add(tracknumber);
				}
			}


			if (addMPEvents)
			{
				createControlEventChannel(score, resolution, measureStartTick,
					scorePositionTicks, seq, 0, playList); //score position events in channel 0
				addPlaybackAtEndControlEvent(seq);
			}

			if (metronome)
			{
				metronomeBeatTrackNumber = createMetronomeTrack(score, resolution, seq, playList,
					measureStartTick);
			}

			//Add Tempo Changes
			/*ArrayList<MidiElement> tempo = MidiTempoConverter.getTempo(score, playList);
			for (MidiElement midiElement : tempo)
			{
				long startTick = measureStartTick.get(midiElement.getMeasure());
				long beat = startTick + (long)midiElement.getPosition().toFloat() * resolution;
				MidiEvent event = new MidiEvent(midiElement.getMidiMessage(), beat);
				tempoTrack.add(event);
			}*/
			MidiTempoConverter.getTempoTrack(score, playList, resolution, tempoTrack);
		}
		catch (InvalidMidiDataException e)
		{
			e.printStackTrace();
		}

		SequenceContainer container = new SequenceContainer();
		container.setSequence(seq);
		container.setStaffTracks(staffTracks);
		container.setMetronomeTrackNumber(metronomeBeatTrackNumber);
		container.setMPTicks(scorePositionTicks);
		container.setMeasureStartTicks(measureStartTick);

		return container;
	}


	/**
	 * generates the MidiSequence from a MP. returns the current
	 * velocity
	 * @param transposing 
	 */
	private static int generateMidi(int resolution, int channel, long currenttickinstaff,
		Staff staff, Track track, int currentVelocity, int iVoice, Voice voice,
		Fraction start, Fraction end, int tracknumber, int transposing, Globals globals)
		throws InvalidMidiDataException
	{
		long currenttickinvoice = currenttickinstaff;
		for (VoiceElement element : voice.getElements())
		{
			Fraction duration = element.getDuration();
			Fraction elementBeat = globals.getMP(element).getBeat();
			if (!isInRange(elementBeat, duration, start, end))
				continue;
			Fraction startBeat = elementBeat;
			Fraction endBeat = startBeat.add(duration);
			long starttick = currenttickinvoice;
			long endtick = calculateEndTick(startBeat, endBeat, start, end, currenttickinvoice,
				resolution);
			if (starttick != endtick && element instanceof Chord)
			{
				Chord chord = (Chord) element;
				for (Note note : chord.getNotes())
				{
					currentVelocity = addNoteToTrack(channel, staff, track, currentVelocity,
						iVoice, starttick, endtick, chord, note, transposing, globals);
				}
			}
			MetaMessage m = null;
			//TODO Timidity doesn't like it
			/*if (musicelement instanceof Clef)
			{
				Clef c = (Clef) musicelement;
				m = createMidiEvent(c, tracknumber);
			}
			else if (musicelement instanceof NormalTime)
			{
				NormalTime t = (NormalTime) musicelement;
				m = createMidiEvent(t, resolution, tracknumber);
			}
			else if (musicelement instanceof Key)
			{
				Key k = (Key) musicelement;
				m = createMidiEvent(k, tracknumber);
			}*/
			/*else if (musicelement instanceof Tempo)
			{
				Tempo tempo = (Tempo)musicelement;
				m = MidiTempoConverter.createMetaMessage(tempo);
			}*/
			if (m != null)
			{
				MidiEvent event = new MidiEvent(m, starttick);
				track.add(event);
			}
			currenttickinvoice = endtick;
		}
		return currentVelocity;
	}


	/* UNUSED
	private static MetaMessage createMidiEvent(Key k, int tracknumber)
		throws InvalidMidiDataException
	{
		MetaMessage m = new MetaMessage();
		int type = 0x59;
		byte mm = (byte) tracknumber;
		byte sf = 0;
		for (int a : k.getAlterations())
		{
			if (a != 0)
			{
				sf++;
			}
		}
		byte mi = 0;
		byte[] data = { mm, sf, mi };
		m.setMessage(type, data, data.length);
		return null;
	}


	/**
	 * Creates the MetaMessage for a Time Signature
	 * @param t
	 * @return
	 * @throws InvalidMidiDataException
	 *-/
	private static MetaMessage createMidiEvent(NormalTime t, int resolution, int tracknumber)
		throws InvalidMidiDataException
	{
		MetaMessage m = new MetaMessage();
		int type = 0x58;
		byte mm = (byte) tracknumber;
		byte nn = (byte) t.getNumerator();
		byte dd = (byte) t.getDenominator();
		byte cc = (byte) (resolution * 4 / dd);
		byte bb = (byte) (32 / dd);
		byte[] data = { mm, nn, dd, cc, bb };
		m.setMessage(type, data, data.length);
		return m;
	}


	/**
	 * Creates the MetaMessage for a {@link Clef}
	 * @param clef
	 * @return
	 * @throws InvalidMidiDataException
	 *-/
	private static MetaMessage createMidiEvent(Clef clef, int tracknumber)
		throws InvalidMidiDataException
	{
		MetaMessage m = new MetaMessage();
		int type = 0x57; // TODO ????
		byte mm = (byte) tracknumber;
		byte cl = -1; // clef type
		byte li = -1; // clef position on staff (line position)
		byte oc = 0; // octave transposition
		if (clef.getType() == ClefType.G)
		{
			cl = 1;
			li = (byte) (clef.getType().getLine() + 1);
			oc = (byte) clef.getType().getOctaveChange();
		}
		else if (clef.getType() == ClefType.F)
		{
			cl = 2;
			li = (byte) (clef.getType().getLine() + 1);
			oc = (byte) clef.getType().getOctaveChange();
		}
		if (cl != -1)
		{
			byte[] data = { mm, cl, li, oc };
			m.setMessage(type, data, data.length);
		}
		return m;
	}
	*/
	

	private static boolean isInRange(Fraction startBeat, Fraction duration, Fraction start,
		Fraction end)
	{
		Fraction endBeat = startBeat.add(duration);
		if (endBeat.compareTo(start) == -1 || startBeat.compareTo(end) == 1)
		{
			return false;
		}
		return true;
	}


	private static long calculateEndTick(Fraction startBeat, Fraction endBeat,
		Fraction start, Fraction end, long currentTickInVoice, int resolution)
	{
		Fraction begin;
		Fraction finish;
		if (startBeat.compareTo(start) == -1)
		{
			begin = start;
		}
		else
		{
			begin = startBeat;
		}
		if (endBeat.compareTo(end) == 1)
		{
			finish = end;
		}
		else
		{
			finish = endBeat;
		}
		Fraction duration = finish.sub(begin);
		return currentTickInVoice + calculateTickFromFraction(duration, resolution);
	}


	private static int addNoteToTrack(int channel, Staff staff, Track track,
		int currentVelocity, int iVoice, long starttick, long endtick, Chord chord,
		Note note, int transposing, Globals globals) throws InvalidMidiDataException
	{
		int[] velocityAtPosition = getVelocityAtPosition(staff, iVoice,
			globals.getMP(chord), currentVelocity, globals);
		int midiNote = MidiTools.getNoteNumberFromPitch(note.getPitch()) + transposing;
		addEventToTrack(track, starttick, ShortMessage.NOTE_ON, channel, midiNote,
			velocityAtPosition[0]);
		addEventToTrack(track, endtick, ShortMessage.NOTE_OFF, channel, midiNote, 0);
		currentVelocity = velocityAtPosition[1];
		return currentVelocity;
	}


	private static void addPlaybackAtEndControlEvent(Sequence seq)
		throws InvalidMidiDataException
	{
		Track t = seq.createTrack();
		addEventToTrack(t, seq.getTickLength(), ShortMessage.CONTROL_CHANGE, 0, 117, 0);
	}


	/**
	 * Creates the ControlEvents for the Playback cursor. Channel 119 is used
	 * because it has no other meaning.
	 */
	private static void createControlEventChannel(Score score, int resolution,
		ArrayList<Long> measureStartTick,
		LinkedList<Tuple2<Long, MP>> scorePositionTicks, Sequence sequence,
		int channel, ArrayList<Tuple2<MP, MP>> playlist)
		throws InvalidMidiDataException
	{
		Vector<SortedList<Fraction>> usedBeatsMeasures = getUsedBeats(score);
		// Add ControlEvents for Listener

		Track controltrack = sequence.createTrack();

		int imeasure = 0;
		for (Tuple2<MP, MP> tuple : playlist)
		{

			for (int i = tuple.get1().getMeasure(); i <= tuple.get2().getMeasure(); i++)
			{
				SortedList<Fraction> usedBeats = usedBeatsMeasures.get(i);

				Fraction start, end;
				start = clipToMeasure(score, tuple.get1().getMeasure(), tuple.get1()).getBeat();
				end = clipToMeasure(score, tuple.get2().getMeasure(), tuple.get2()).getBeat();

				for (Fraction fraction : usedBeats)
				{
					//only add, if beats are between start and end
					if (fraction.compareTo(start) > -1 && fraction.compareTo(end) < 1)
					{
						long tick = measureStartTick.get(imeasure)
							+ calculateTickFromFraction(fraction.sub(start), resolution);
						addEventToTrack(controltrack, tick, ShortMessage.CONTROL_CHANGE, channel,
							119, 0);

						MP pos = mp(1, i, -1, fraction);
						Tuple2<Long, MP> positionTick = new Tuple2<Long, MP>(
							tick, pos);
						scorePositionTicks.add(positionTick);
					}
				}
				imeasure++;
			}
		}
	}


	/**
	 * Creates the track in the sequence with the beats of the metronome.
	 * Additional it adds a second track that is used for the controlevents to
	 * unmute the metronome in the correct moment. Message 118 is used. It
	 * returns the number of the track in the sequencer.
	 * @param score
	 * @param resolution the midi resolution
	 * @param seq the midi sequence where the metronome should be added
	 * @param playList the Playlist
	 * @param measureStartTick
	 * @return the number of the metronome track in the sequence
	 * @throws InvalidMidiDataException
	 */
	private static int createMetronomeTrack(Score score, int resolution, Sequence seq,
		ArrayList<Tuple2<MP, MP>> playList,
		ArrayList<Long> measureStartTick) throws InvalidMidiDataException
	{
		int metronomeBeatTrackNumber;

		// Load Settings
		int metronomeStrongBeatNote = Settings.getInstance().getSetting(
			"MetronomeStrongBeat", "playback", 36);
		int metronomeWeakBeatNote = Settings.getInstance().getSetting("MetronomeWeakBeat",
			"playback", 50);

		// Create Tracks
		metronomeBeatTrackNumber = seq.getTracks().length;
		Track metronomeTrack = seq.createTrack();
		Track metronomeControlTrack = seq.createTrack();

		int imeasure = 0;
		for (Tuple2<MP, MP> tuple : playList)
		{

			for (int i = tuple.get1().getMeasure(); i <= tuple.get2().getMeasure(); i++)
			{
				Time time = getTimeAtOrBefore(score, i);

				Fraction start, end;
				if (tuple.get1().getMeasure() == i)
				{
					start = tuple.get1().getBeat();
				}
				else
				{
					start = fr(0, 1);
				}
				if (tuple.get2().getMeasure() == i)
				{
					end = tuple.get2().getBeat();
				}
				else
				{
					end = getMeasureBeats(score, i);
				}

				if (time != null)
				{
					Vector<Tuple2<Fraction, BeatWeight>> beatWeights = time.getBeatWeights();
					for (Tuple2<Fraction, BeatWeight> beatWeight : beatWeights)
					{
						if (!isInRange(beatWeight.get1(), fr(0), start, end))
							continue;
						long currenttick = measureStartTick.get(imeasure)
							+ calculateTickFromFraction(beatWeight.get1().sub(start), resolution);
						int note;
						if (beatWeight.get2() == BeatWeight.StrongBeat)
						{
							note = metronomeStrongBeatNote;
						}
						else
						{
							note = metronomeWeakBeatNote;
						}

						int velocity = 127;
						addEventToTrack(metronomeTrack, currenttick, ShortMessage.NOTE_ON, 9, note,
							velocity);
						addEventToTrack(metronomeControlTrack, currenttick,
							ShortMessage.CONTROL_CHANGE, 9, 118, 0);
					}
				}
				imeasure++;
			}
		}
		return metronomeBeatTrackNumber;
	}


	/**
	 * This method calculates the tick of a {@link Fraction}. This is necessary
	 * to get the current beat in a measure or to get the duration of a
	 * {@link Fraction} in midi ticks.
	 */
	public static int calculateTickFromFraction(Fraction fraction, int resolution)
	{
		return fraction.getNumerator() * 4 * resolution / fraction.getDenominator();
	}


	/**
	 * Adds an event to a midi track.
	 * @param Track
	 * @param tick
	 * @param type insert the constants of ShortMessage, e.g.
	 * ShortMessage.NOTE_ON
	 * @param channel the midi channel
	 * @param note
	 * @param velocity
	 * @throws InvalidMidiDataException
	 */
	private static void addEventToTrack(Track Track, long tick, int type, int channel,
		int note, int velocity) throws InvalidMidiDataException
	{
		ShortMessage message = new ShortMessage();
		message.setMessage(type, channel, note, velocity);
		MidiEvent midievent = new MidiEvent(message, tick);
		Track.add(midievent);
	}


	/**
	 * Creates the mapping from staff indices to MIDI channel numbers.
	 * If possible, each part gets its own channel. If there are too many parts,
	 * the parts with the same MIDI program share the device. If there are still
	 * too many parts, the remaining parts are ignored (i.e. they are mapped to
	 * the device number -1).
	 * Channel 10 is always reserved for drums/percussion/metronome.
	 */
	private static int[] createChannelMap(Score score)
	{
		int[] ret = new int[score.getStavesCount()];
		//get number of parts which have not channel 10
		int melodicPartsCount = 0;
		for (Part part : score.getStavesList().getParts())
		{
			if (part.getInstrument() instanceof PitchedInstrument)
			{
				melodicPartsCount++;
			}
		}
		//find out if there are enough channels for all parts
		if (melodicPartsCount <= 15)
		{
			//ok, each pitched part can have its own channel.
			//all unpitched parts get channel 10.
			int partFirstStave = 0;
			int iChannel = 0;
			for (Part part : score.getStavesList().getParts())
			{
				boolean pitched = (part.getInstrument() instanceof PitchedInstrument);
				int channel = (pitched ? iChannel : 10);
				for (int iStaff = partFirstStave; iStaff < partFirstStave + part.getStavesCount(); iStaff++)
				{
					ret[iStaff] = channel;
				}
				//after pitched part: increment channel number
				if (pitched)
				{
					iChannel = iChannel + 1 + (iChannel + 1 == 10 ? 1 : 0); //don't use channel 10
				}
				partFirstStave += part.getStavesCount();
			}
		}
		else
		{
			//too many parts to have a device for each part.
			//share devices for parts with the same instrument. if none is left, ignore part.
			//all unpitched parts get device 10.
			int partFirstStave = 0;
			int iChannel = 0;
			HashMap<Integer, Integer> programToDeviceMap = new HashMap<Integer, Integer>();
			for (Part part : score.getStavesList().getParts())
			{
				boolean pitched = (part.getInstrument() instanceof PitchedInstrument);
				//find device
				int channel = -1;
				int program = 0;
				boolean isChannelReused = false;
				if (pitched && iChannel != -1)
				{
					PitchedInstrument pitchedInstr = (PitchedInstrument) part.getInstrument();
					program = pitchedInstr.getMidiProgram();
					Integer channelReused = programToDeviceMap.get(program);
					if (channelReused != null)
					{
						isChannelReused = true;
						channel = channelReused;
					}
					else
					{
						channel = iChannel;
					}
				}
				else if (!pitched)
				{
					channel = 10;
				}
				for (int iStaff = partFirstStave; iStaff < partFirstStave + part.getStavesCount(); iStaff++)
				{
					ret[iStaff] = channel;
				}
				//after pitched part with new device number: increment device number and remember program
				if (pitched && !isChannelReused && iChannel != -1)
				{
					iChannel = iChannel + 1 + (iChannel + 1 == 10 ? 1 : 0); //don't use channel 10
					if (iChannel > 15)
					{
						iChannel = -1; //no channels left
					}
					programToDeviceMap.put(program, channel);
				}
				partFirstStave += part.getStavesCount();
			}
		}
		return ret;
	}

}
