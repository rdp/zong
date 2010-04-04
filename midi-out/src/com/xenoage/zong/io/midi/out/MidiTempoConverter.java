package com.xenoage.zong.io.midi.out;

import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.core.music.MP.atMeasure;
import static com.xenoage.zong.io.midi.out.MidiConverter.calculateTickFromFraction;
import static com.xenoage.zong.io.score.ScoreController.getMeasureBeats;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Track;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.error.ErrorProcessing;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.Attachable;
import com.xenoage.zong.core.music.Globals;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.Measure;
import com.xenoage.zong.core.music.Voice;
import com.xenoage.zong.core.music.VoiceElement;
import com.xenoage.zong.core.music.direction.Tempo;


/**
 * This class provides the ability to calculate the tempo for the midifile.
 * 
 * @author Uli Teschemacher
 */
public class MidiTempoConverter
{

	private static final int type = 0x51; //Midi Meta Event Type for setting Tempo


	/*public static ArrayList<MidiElement> getTempo(Score score,
		ArrayList<Tuple2<MP, MP>> playList, int resolution)*/
	public static void getTempoTrack(Score score,
		ArrayList<Tuple2<MP, MP>> playList, int resolution, Track track)
	{
		//ArrayList<MidiElement> messages = new ArrayList<MidiElement>();
		//score.getScoreHeader() //TODO
		for (int iStaff = 0; iStaff < score.getStavesCount(); iStaff++)
		{
			long measurestarttick = 0;
			for (Tuple2<MP, MP> tuple : playList)
			{
				for (int iMeasure = tuple.get1().getMeasure(); iMeasure <= tuple.get2()
					.getMeasure(); iMeasure++)
				{
					Fraction start, end;
					if (tuple.get1().getMeasure() == iMeasure)
					{
						start = tuple.get1().getBeat();
					}
					else
					{
						start = fr(0, 1);
					}
					if (tuple.get2().getMeasure() == iMeasure)
					{
						end = tuple.get2().getBeat();
					}
					else
					{
						end = getMeasureBeats(score, iMeasure);
					}

					//TODO: no... we _must_ save tempo changes in the column headers...
					Globals globals = score.getGlobals();
					Measure measure = score.getMeasure(atMeasure(iStaff, iMeasure));
					for (Voice voice : measure.getVoices())
					{
						for (VoiceElement element : voice.getElements())
						{
							Fraction elementBeat = globals.getMP(element).getBeat();
							if (elementBeat.compareTo(start) > -1 && elementBeat.compareTo(end) < 1)
							{
								for (Attachable attachment : globals.getAttachments().get(element))
								{
									if (attachment instanceof Tempo)
									{
										Tempo tempo = (Tempo) attachment;
										MetaMessage message = createMetaMessage(tempo);
										MidiEvent event = new MidiEvent(message, measurestarttick +
											calculateTickFromFraction(elementBeat.sub(start), resolution));
										track.add(event);
									}
								}
							}
						}
					}
					Fraction measureDuration = end.sub(start);
					measurestarttick += MidiConverter.calculateTickFromFraction(measureDuration,
						resolution);
				}
			}
		}
	}


	public static MetaMessage createMetaMessage(Tempo tempo)
	{
		int bpm = tempo.getBeatsPerMinute();
		byte[] data = toByteArray(getMicroSecondsPerQuarterNote(bpm));
		MetaMessage message = new MetaMessage();
		try
		{
			message.setMessage(type, data, data.length);
		}
		catch (InvalidMidiDataException e)
		{
			ErrorProcessing errorProcessing = new ErrorProcessing();
			errorProcessing.report(ErrorLevel.Warning, "Could not convertetempo to midi");
		}
		return message;
	}


	private static int getMicroSecondsPerQuarterNote(int bpm)
	{
		final int MICROSECONDS_PER_MINUTE = 60000000;
		return MICROSECONDS_PER_MINUTE / bpm;
	}


	/**
	 * Returns the last three bytes of the integer.
	 * @return
	 */
	private static byte[] toByteArray(int val)
	{
		byte[] res = new byte[3];
		res[0] = (byte) (val / 0x10000);
		res[1] = (byte) ((val - res[0] * 0x10000) / 0x100);
		res[2] = (byte) (val - res[0] * 0x10000 - res[1] * 0x100);
		return res;
	}

}
