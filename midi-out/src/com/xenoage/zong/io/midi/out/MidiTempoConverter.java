package com.xenoage.zong.io.midi.out;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Track;

import com.xenoage.util.RAList;
import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.error.ErrorProcessing;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.MusicElement;
import com.xenoage.zong.data.music.directions.Tempo;


/**
 * This class provides the ability to calculate the tempo for the midifile.
 * 
 * @author Uli Teschemacher
 */
public class MidiTempoConverter
{

	private static final int type = 0x51; //Midi Meta Event Type for setting Tempo


	/*public static ArrayList<MidiElement> getTempo(Score score,
		ArrayList<Tuple2<ScorePosition, ScorePosition>> playList, int resolution)*/
	public static void getTempoTrack(Score score,
		ArrayList<Tuple2<ScorePosition, ScorePosition>> playList, int resolution, Track track)
	{
		//ArrayList<MidiElement> messages = new ArrayList<MidiElement>();
		//score.getScoreHeader() //TODO
		for (int i = 0; i < score.getStavesCount(); i++)
		{
			long measurestarttick = 0;
			for (Tuple2<ScorePosition, ScorePosition> tuple : playList)
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
						start = new Fraction(0, 1);
					}
					if (tuple.get2().getMeasure() == iMeasure)
					{
						end = tuple.get2().getBeat();
					}
					else
					{
						end = score.getController().getMeasureBeats(iMeasure);
					}

					Measure measure = score.getMeasure(i, iMeasure);
					RAList<MusicElement> elements = measure.getVoices().get(0).getElements();
					for (MusicElement musicElement : elements)
					{
						if (musicElement instanceof Tempo && musicElement.getBeat().compareTo(start)!=-1 && musicElement.getBeat().compareTo(end)!=1)
						{
							Tempo tempo = (Tempo) musicElement;
							MetaMessage message = createMetaMessage(tempo);
							MidiEvent event = new MidiEvent(message, measurestarttick
								+ MidiConverter.calculateTickFromFraction(tempo.getBeat().sub(start), resolution));
							track.add(event);
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
