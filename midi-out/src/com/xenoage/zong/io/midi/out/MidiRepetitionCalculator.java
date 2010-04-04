package com.xenoage.zong.io.midi.out;

import static com.xenoage.util.math.Fraction._0;
import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.core.music.MP.mp;
import static com.xenoage.zong.io.score.ScoreController.getMeasureBeats;

import java.util.ArrayList;
import java.util.HashMap;

import com.xenoage.util.Range;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.header.ColumnHeader;
import com.xenoage.zong.core.header.ScoreHeader;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.barline.Barline;
import com.xenoage.zong.core.music.barline.BarlineRepeat;
import com.xenoage.zong.core.music.util.BeatE;
import com.xenoage.zong.core.music.util.BeatEList;
import com.xenoage.zong.core.music.volta.Volta;


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
	private static ArrayList<Tuple2<MP, MP>> createJumpList(
		Score score)
	{
		ArrayList<Tuple2<MP, MP>> list = new ArrayList<Tuple2<MP, MP>>();

		HashMap<Integer, VoltaBlock> voltaBlocks = createVoltaBlocks(score);

		MP pos = mp(1, 0, -1, fr(0, 1));

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
					MP stopPosition = mp(-1, range.getStop(), -1,
						getMeasureBeats(score, i));
					if (i != range.getStart())
					{
						MP currentPosition = mp(-1, i, -1, _0);
						MP startPosition = mp(-1, range.getStart(), -1, _0);
						list.add(new Tuple2<MP, MP>(currentPosition, startPosition));
					}
					if (!voltaBlock.isLastTime(voltatime))
					{
						list.add(new Tuple2<MP, MP>(stopPosition, pos));
					}
					voltatime++;
				}

				i += voltaBlock.getBlockLength() - 1;
				pos = mp(-1, i, -1, _0);
			}
			else
			// normal Barline repetition
			{
				ColumnHeader columnHeader = score.getScoreHeader().getColumnHeader(i);

				BeatEList<Barline> barlines = columnHeader.getMiddleBarlines();
				if (barlines == null)
				{
					barlines = new BeatEList<Barline>();
				}
				if (columnHeader.getStartBarline() != null)
				{
					barlines = barlines.plus(columnHeader.getStartBarline(), _0);
				}
				if (columnHeader.getEndBarline() != null)
				{
					barlines = barlines.plus(columnHeader.getEndBarline(), getMeasureBeats(score, i));
				}

				for (BeatE<Barline> barline : barlines.getElements())
				{
					BarlineRepeat repeat = barline.getElement().getRepeat();
					if (repeat == BarlineRepeat.Backward || repeat == BarlineRepeat.Both)
					{
						// Jump back to last forward repeat sign or beginning
						MP barlinePosition = mp(-1, i, -1, barline.getBeat());
						for (int a = 0; a < barline.getElement().getRepeatTimes(); a++)
						{
							list.add(new Tuple2<MP, MP>(
								barlinePosition, pos));
						}
					}
					if (repeat == BarlineRepeat.Forward || repeat == BarlineRepeat.Both)
					{
						pos = mp(-1, i, -1, barline.getBeat());
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
	public static ArrayList<Tuple2<MP, MP>> createPlayList(Score score)
	{
		ArrayList<Tuple2<MP, MP>> playlist = new ArrayList<Tuple2<MP, MP>>();

		MP start = mp(-1, 0, -1, _0);
		Fraction lastMeasure = getMeasureBeats(score, score.getMeasuresCount() - 1);
		MP end = mp(-1, score.getMeasuresCount() - 1, -1, lastMeasure);

		ArrayList<Tuple2<MP, MP>> jumplist = createJumpList(score);
		int size = jumplist.size();

		if (size == 0)
		{
			playlist.add(new Tuple2<MP, MP>(start, end));
		}

		if (size > 0)
		{
			MP pos1 = jumplist.get(0).get1();
			playlist.add(new Tuple2<MP, MP>(start, pos1));
		}
		for (int i = 1; i < size; i++)
		{
			MP pos1 = jumplist.get(i - 1).get2();
			MP pos2 = jumplist.get(i).get1();
			playlist.add(new Tuple2<MP, MP>(pos1, pos2));
		}
		if (size > 0)
		{
			MP pos1 = jumplist.get(size - 1).get2();
			playlist.add(new Tuple2<MP, MP>(pos1, end));
		}

		return playlist;
	}


	/**
	 * Looks for {@link VoltaBlock}s in the score and creates a list of them
	 */
	private static HashMap<Integer, VoltaBlock> createVoltaBlocks(Score score)
	{
		HashMap<Integer, VoltaBlock> map = new HashMap<Integer, VoltaBlock>();
		ScoreHeader scoreHeader = score.getScoreHeader();
		for (int i = 0; i < score.getMeasuresCount(); i++)
		{
			if (scoreHeader.getColumnHeader(i).getVolta() != null)
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
			Volta volta = scoreHeader.getColumnHeader(iMeasure).getVolta();
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
