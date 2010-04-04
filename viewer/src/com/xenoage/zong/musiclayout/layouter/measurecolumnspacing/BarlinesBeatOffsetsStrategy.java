package com.xenoage.zong.musiclayout.layouter.measurecolumnspacing;

import static com.xenoage.util.lang.Tuple2.t;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.header.ColumnHeader;
import com.xenoage.zong.core.music.barline.Barline;
import com.xenoage.zong.core.music.barline.BarlineRepeat;
import com.xenoage.zong.core.music.util.BeatE;
import com.xenoage.zong.musiclayout.spacing.horizontal.BeatOffset;


/**
 * This strategy creates {@link BeatOffset}s for the barlines
 * and the notes of the given measure column, based on the
 * given {@link BeatOffset}s (that were created without
 * respect to barlines).
 * 
 * @author Andreas Wenger
 */
public class BarlinesBeatOffsetsStrategy
{
	
	//additional 1 IS when using a repeat sign
	public static final float REPEAT_SPACE = 1;
	//2 IS after a mid-measure barline
	public static final float MID_BARLINE_SPACE = 2;
	
	
	/**
	 * Computes and returns updated {@link BeatOffset}s. The first component are
	 * the note offsets, the second one the barline offsets.
	 */
	public Tuple2<PVector<BeatOffset>, PVector<BeatOffset>> computeBeatOffsets(
		PVector<BeatOffset> baseOffsets, ColumnHeader columnHeader, float interlineSpace)
	{
		PVector<BeatOffset> retNotes = baseOffsets;
		PVector<BeatOffset> retBarlines = new PVector<BeatOffset>();
		//start barline
		retBarlines = retBarlines.plus(new BeatOffset(Fraction._0, 0));
		Barline startBarline = columnHeader.getStartBarline();
		if (startBarline != null && startBarline.getRepeat() == BarlineRepeat.Forward)
		{
			//forward repeat: move all beats REPEAT_SPACE IS backward
			float move = REPEAT_SPACE * interlineSpace;
			for (int i = 0; i < retNotes.size(); i++)
			{
				BeatOffset oldOffset = retNotes.get(i);
				retNotes = retNotes.with(i,
					new BeatOffset(oldOffset.getBeat(), oldOffset.getOffsetMm() + move));
			}
		}
		//mid-measure barlines: add MID_BARLINE_SPACE IS for each
		if (columnHeader.getMiddleBarlinesCount() > 0)
		{
			for (BeatE<Barline> midBarline : columnHeader.getMiddleBarlines())
			{
				//get beat of barline, find it in the note offsets and move the following ones
				Fraction beat = midBarline.getBeat();
				int i = 0;
				float move = 0;
				for (; i < retNotes.size(); i++)
				{
					if (retNotes.get(i).getBeat().compareTo(beat) >= 0)
					{
						BarlineRepeat repeat = midBarline.getElement().getRepeat();
						if (repeat == BarlineRepeat.Backward)
						{
							//backward repeat: additional space before barline
							move += REPEAT_SPACE * interlineSpace;
							BeatOffset oldOffset = retNotes.get(i);
							retBarlines = retBarlines.plus(
								new BeatOffset(oldOffset.getBeat(), oldOffset.getOffsetMm() + move));
						}
						else if (repeat == BarlineRepeat.Forward)
						{
							//forward repeat: additional space after barline
							BeatOffset oldOffset = retNotes.get(i);
							retBarlines = retBarlines.plus(
								new BeatOffset(oldOffset.getBeat(), oldOffset.getOffsetMm() + move));
							move += REPEAT_SPACE * interlineSpace;
						}
						else if (repeat == BarlineRepeat.Both)
						{
							//forward and backward repeat: additional space before and after barline
							move += REPEAT_SPACE * interlineSpace;
							BeatOffset oldOffset = retNotes.get(i);
							retBarlines = retBarlines.plus(
								new BeatOffset(oldOffset.getBeat(), oldOffset.getOffsetMm() + move));
							move += REPEAT_SPACE * interlineSpace;
						}
						else
						{
							retBarlines = retBarlines.plus(retNotes.get(i));
						}
						move += MID_BARLINE_SPACE * interlineSpace;
						break;
					}
				}
				for (; i < retNotes.size(); i++)
				{
					//move following notes
					BeatOffset oldOffset = retNotes.get(i);
					retNotes = retNotes.with(i,
						new BeatOffset(oldOffset.getBeat(), oldOffset.getOffsetMm() + move));
				}
			}
		}
		//end barline
		BeatOffset lastOffset = retNotes.get(retNotes.size() - 1);
		Barline endBarline = columnHeader.getEndBarline();
		if (endBarline != null && endBarline.getRepeat() == BarlineRepeat.Backward)
		{
			//backward repeat: additional space before end barline
			float move = REPEAT_SPACE * interlineSpace;
			retBarlines = retBarlines.plus(
				new BeatOffset(lastOffset.getBeat(), lastOffset.getOffsetMm() + move));
		}
		else
		{
			retBarlines = retBarlines.plus(lastOffset);
		}
		//return result
		return t(retNotes, retBarlines);
	}
	

}
