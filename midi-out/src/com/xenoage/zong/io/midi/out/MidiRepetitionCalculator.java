/**
 * 
 */
package com.xenoage.zong.io.midi.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.xenoage.util.Range;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.header.MeasureColumnHeader;
import com.xenoage.zong.data.header.ScoreHeader;
import com.xenoage.zong.data.music.Volta;
import com.xenoage.zong.data.music.barline.Barline;
import com.xenoage.zong.data.music.barline.BarlineRepeat;


/**
 * Class to calculate a playback list for the midi converter.
 * 
 * @author Uli Teschemacher
 * 
 */
public class MidiRepetitionCalculator
{

	/**
	 * Creates a "JumpList" of the Score. The returned Tuples have the form
	 * <jumpfrom, jumpto>
	 */
	private static ArrayList<Tuple2<ScorePosition, ScorePosition>> createJumpList(
		Score score)
	{
		ArrayList<Tuple2<ScorePosition, ScorePosition>> list = new ArrayList<Tuple2<ScorePosition, ScorePosition>>();

		HashMap<Integer, VoltaBlock> voltaBlocks = createVoltaBlocks(score);

		ScorePosition pos = new ScorePosition(1, 0, new Fraction(0, 1), -1);

		int measuresCount = score.getMeasuresCount();
		for (int i = 0; i < measuresCount; i++)
		{
			if (voltaBlocks.containsKey(i)) // Volta
			{
				VoltaBlock voltaBlock = voltaBlocks.get(i);

				int voltatime = 1;
				while (voltatime <= voltaBlock.getRepeatCount())
				{
					Range range = voltaBlock.getRange(voltatime);
					ScorePosition stopPosition = new ScorePosition(-1, range.getStop(),
						score.getController().getMeasureBeats(i), -1);
					if (i != range.getStart())
					{
						ScorePosition currentPosition = new ScorePosition(-1, i,
							new Fraction(0, 1), -1);
						ScorePosition startPosition = new ScorePosition(-1, range
							.getStart(), new Fraction(0, 1), -1);
						list.add(new Tuple2<ScorePosition, ScorePosition>(
							currentPosition, startPosition));
					}
					if (!voltaBlock.isLastTime(voltatime))
					{
						list.add(new Tuple2<ScorePosition, ScorePosition>(stopPosition,
							pos));
					}
					voltatime++;
				}

				i += voltaBlock.getBlockLength() - 1;
				pos = new ScorePosition(-1, i, new Fraction(0, 1), -1);
			}
			else
			// normal Barline repetition
			{
				MeasureColumnHeader measureHeader = score.getScoreHeader().getMeasureColumnHeader(i);

				List<Tuple2<Fraction, Barline>> barlines = measureHeader
					.getMiddleBarlines();
				if (barlines == null)
				{
					barlines = new ArrayList<Tuple2<Fraction, Barline>>();
				}
				if (measureHeader.getStartBarline() != null)
				{
					barlines.add(0, new Tuple2<Fraction, Barline>(new Fraction(0, 1),
						measureHeader.getStartBarline()));
				}
				if (measureHeader.getEndBarline() != null)
				{
					barlines.add(new Tuple2<Fraction, Barline>(score.getController()
						.getMeasureBeats(i), measureHeader.getEndBarline()));
				}

				for (Tuple2<Fraction, Barline> tuple : barlines)
				{
					BarlineRepeat repeat = tuple.get2().getRepeat();
					if (repeat == BarlineRepeat.Backward || repeat == BarlineRepeat.Both)
					{
						// Jump back to last forward repeat sign or beginning
						ScorePosition barlinePosition = new ScorePosition(-1, i, tuple
							.get1(), -1);
						for (int a = 0; a < tuple.get2().getRepeatTimes(); a++)
						{
							list.add(new Tuple2<ScorePosition, ScorePosition>(
								barlinePosition, pos));
						}
					}
					if (repeat == BarlineRepeat.Forward || repeat == BarlineRepeat.Both)
					{
						pos = new ScorePosition(-1, i, tuple.get1(), -1);
					}
				}
			}
		}

		return list;
	}


	/**
	 * Creates a playlist from the given score. the tuples contain <playfrom,
	 * playto>
	 */
	public static ArrayList<Tuple2<ScorePosition, ScorePosition>> createPlayList(
		Score score)
	{
		ArrayList<Tuple2<ScorePosition, ScorePosition>> playlist = new ArrayList<Tuple2<ScorePosition, ScorePosition>>();

		ScorePosition start = new ScorePosition(-1, 0, new Fraction(0, 1), -1);
		Fraction lastMeasure = score.getController().getMeasureBeats(
			score.getMeasuresCount() - 1);
		ScorePosition end = new ScorePosition(-1, score.getMeasuresCount() - 1,
			lastMeasure, -1);

		ArrayList<Tuple2<ScorePosition, ScorePosition>> jumplist = createJumpList(score);
		int size = jumplist.size();

		if (size == 0)
		{
			playlist.add(new Tuple2<ScorePosition, ScorePosition>(start, end));
		}

		if (size > 0)
		{
			ScorePosition pos1 = jumplist.get(0).get1();
			playlist.add(new Tuple2<ScorePosition, ScorePosition>(start, pos1));
		}
		for (int i = 1; i < size; i++)
		{
			ScorePosition pos1 = jumplist.get(i - 1).get2();
			ScorePosition pos2 = jumplist.get(i).get1();
			playlist.add(new Tuple2<ScorePosition, ScorePosition>(pos1, pos2));
		}
		if (size > 0)
		{
			ScorePosition pos1 = jumplist.get(size - 1).get2();
			playlist.add(new Tuple2<ScorePosition, ScorePosition>(pos1, end));
		}

		return playlist;
	}


	/**
	 * Looks for VoltaBlocks in the score and creates a list of them
	 */
	private static HashMap<Integer, VoltaBlock> createVoltaBlocks(Score score)
	{
		HashMap<Integer, VoltaBlock> map = new HashMap<Integer, VoltaBlock>();
		ScoreHeader scoreHeader = score.getScoreHeader();
		for (int i = 0; i < score.getMeasuresCount(); i++)
		{
			if (scoreHeader.getMeasureColumnHeader(i).getVolta() != null)
			{
				VoltaBlock voltaBlock = createVoltaBlock(score, i);
				map.put(i, voltaBlock);
				i += voltaBlock.getBlockLength() - 1;
			}
		}
		return map;
	}


	/**
	 * Creates a {@link VoltaBlock} at the given measureID.
	 */
	private static VoltaBlock createVoltaBlock(Score score, int startMeasure)
	{
		ScoreHeader scoreHeader = score.getScoreHeader();
		VoltaBlock block = new VoltaBlock();
		int iMeasure = startMeasure;
		while (iMeasure < score.getMeasuresCount())
		{
			Volta volta = scoreHeader.getMeasureColumnHeader(iMeasure).getVolta();
			if (volta != null)
			{
				block.addVolta(volta, iMeasure);
				iMeasure += volta.getLength();
			}
			else
			{
				break;
			}
		}
		return block;
	}
}
