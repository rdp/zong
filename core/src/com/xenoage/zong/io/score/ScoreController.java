package com.xenoage.zong.io.score;

import static com.xenoage.pdlib.IVector.ivec;
import static com.xenoage.util.math.Fraction._0;
import static com.xenoage.zong.core.music.MP.atMeasure;
import static com.xenoage.zong.core.music.MP.mp0;
import static com.xenoage.zong.core.music.util.BeatE.selectLatest;
import static com.xenoage.zong.core.music.util.BeatInterval.At;
import static com.xenoage.zong.core.music.util.BeatInterval.Before;
import static com.xenoage.zong.core.music.util.BeatInterval.BeforeOrAt;
import static com.xenoage.zong.core.music.util.MPE.mpe;
import static com.xenoage.zong.core.music.util.VoiceElementSelection.Last;
import static com.xenoage.zong.core.music.util.VoiceElementSide.Start;

import java.util.ArrayList;
import java.util.LinkedList;

import com.xenoage.pdlib.PMap;
import com.xenoage.pdlib.PVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.MathTools;
import com.xenoage.util.SortedList;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.format.Break;
import com.xenoage.zong.core.format.StaffLayout;
import com.xenoage.zong.core.format.SystemLayout;
import com.xenoage.zong.core.header.ColumnHeader;
import com.xenoage.zong.core.header.ScoreHeader;
import com.xenoage.zong.core.music.Attachable;
import com.xenoage.zong.core.music.ColumnElement;
import com.xenoage.zong.core.music.Globals;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.Measure;
import com.xenoage.zong.core.music.MeasureElement;
import com.xenoage.zong.core.music.MusicContext;
import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.core.music.Pitch;
import com.xenoage.zong.core.music.Staff;
import com.xenoage.zong.core.music.StavesList;
import com.xenoage.zong.core.music.Voice;
import com.xenoage.zong.core.music.VoiceElement;
import com.xenoage.zong.core.music.barline.Barline;
import com.xenoage.zong.core.music.beam.Beam;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.clef.Clef;
import com.xenoage.zong.core.music.clef.ClefType;
import com.xenoage.zong.core.music.curvedline.CurvedLine;
import com.xenoage.zong.core.music.direction.Tempo;
import com.xenoage.zong.core.music.key.Key;
import com.xenoage.zong.core.music.key.TraditionalKey;
import com.xenoage.zong.core.music.rest.Rest;
import com.xenoage.zong.core.music.time.SenzaMisura;
import com.xenoage.zong.core.music.time.Time;
import com.xenoage.zong.core.music.util.BeatE;
import com.xenoage.zong.core.music.util.BeatInterval;
import com.xenoage.zong.core.music.util.MPE;
import com.xenoage.zong.core.music.volta.Volta;
import com.xenoage.zong.util.exceptions.IllegalMPException;
import com.xenoage.zong.util.exceptions.MeasureFullException;


/**
 * This class provides basic read and write operations for
 * a score.
 * 
 * These methods are safe, i.e. the data within the score
 * should remain consistent, whatever is done. This is not the case,
 * if the music classes are modified directly. Thus, it is
 * strongly recommended to use this class whenever possible.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class ScoreController
{
	
	
	/**
	 * Attaches the given element to the given anchor.
	 */
	public static Score attachElement(Score score, MusicElement anchor, Attachable attachment)
	{
		Globals globals = score.getGlobals().plusAttachment(anchor, attachment);
		return score.withGlobals(globals);
	}
	
	
	/**
	 * Clips a {@link MP} to a measure. If the {@link MP} is before or at the
	 * beginning of the measure with measureIndex, the first beat is returned.
	 * If it is after the measure, the last beat is returned.
	 * If the ScorePosition is in the measure, this position is returned.
	 * @param score          the score
	 * @param measureIndex	 the index of the measure that to which the scorePosition is clipped
	 * @param scorePosition	 the position which is clipped
	 */
	public static MP clipToMeasure(Score score, int measureIndex, MP mp)
	{
		Fraction beat = null;
		if (mp.getMeasure() < measureIndex)
		{
			beat = Fraction._0;
		}
		else if (measureIndex == mp.getMeasure())
		{
			Fraction endBeat = getMeasureBeats(score, measureIndex);
			beat = (endBeat.compareTo(mp.getBeat()) < 0 ? endBeat : mp.getBeat());
		}
		else
		{
			beat = getMeasureBeats(score, measureIndex);
		}
		return mp.withMeasure(measureIndex).withBeat(beat);
	}
	
	
	/**
	 * Gets the accidentals at the given {@link MP} that are
	 * valid before or at the given beat (depending on the given interval),
	 * looking at all voices.
	 */
	public static PMap<Pitch, Byte> getAccidentals(Score score, MP mp, BeatInterval interval)
	{
		MPE<? extends Key> key = getKey(score, mp, interval);
		//if key change is in same measure, start at that beat. otherwise start at beat 0.
		Fraction keyBeat = (key.getMP().getMeasure() == mp.getMeasure() ?
			key.getMP().getBeat() : _0);
		Measure measure = score.getMeasure(mp);
		return measure.getAccidentals(mp.getBeat(), interval, keyBeat, key.getElement());
	}
	
	
	/**
	 * Gets the last {@link Clef} that is defined before (or at,
	 * dependent on the given {@link BeatInterval}) the given
	 * {@link MP}, also over measure boundaries. If there is
	 * none, a default g clef is returned.
	 */
	public static Clef getClef(Score score, MP mp, BeatInterval interval)
	{
		if (!interval.isPrecedingOrAt())
		{
			throw new IllegalArgumentException("Illegal interval for this method");
		}
		//begin with the given measure. if there is one, return it.
		Measure measure = score.getMeasure(mp);
		BeatE<Clef> ret = measure.getLastClef(interval, mp.getBeat());
		if (ret != null)
			return ret.getElement();
		if (interval != At)
		{
			//search in the preceding measures
			for (int iMeasure = mp.getMeasure() - 1; iMeasure >= 0; iMeasure--)
			{
				measure = score.getMeasure(atMeasure(mp.getStaff(), iMeasure));
				ret = measure.getLastClef();
				if (ret != null)
					return ret.getElement();
			}
		}
		//no clef found. return default clef.
		return new Clef(ClefType.G);
	}
	
	
	/**
	 * Returns the number of divisions of a quarter note within
	 * the whole score.
	 * This is needed for Midi and MusicXML Export.
	 */
	public static int getDivisions(Score score)
	{
		int actualdivision = 4;
		for (Staff staff : score.getStavesList().getStaves())
		{
			for (Measure measure : staff.getMeasures())
			{
				for (Voice voice : measure.getVoices())
				{
					for (VoiceElement e : voice.getElements())
					{
						actualdivision = MathTools.lcm(actualdivision, e.getDuration().getDenominator());
					}
				}
			}
		}
		return actualdivision / 4;
	}
	
	
	/**
	 * Gets the measures of the column with the given index.
	 */
	public static Vector<Measure> getColumn(Score score, int measureIndex)
	{
		LinkedList<Measure> ret = new LinkedList<Measure>();
		for (Staff staff : score.getStavesList().getStaves())
		{
			ret.add(staff.getMeasures().get(measureIndex));
		}
		return ivec(ret);
	}
	
	
	/**
   * Gets the filled beats for each measure column, that
   * means, the first beat in each column where the is no music
   * element following any more.
   */
  public static Vector<Fraction> getFilledBeats(Score score)
  {
  	Fraction[] ret = new Fraction[score.getMeasuresCount()];
  	StavesList stavesList = score.getStavesList();
  	for (int iMeasure = 0; iMeasure < score.getMeasuresCount(); iMeasure++)
  	{
	  	Fraction maxBeat = Fraction._0;
	  	for (Staff staff : stavesList.getStaves())
	  	{
	  		Fraction beat = staff.getMeasures().get(iMeasure).getFilledBeats();
	  		if (beat.compareTo(maxBeat) > 0)
	  			maxBeat = beat;
	  	}
	  	ret[iMeasure] = maxBeat;
  	}
  	return ivec(ret);
  }
  
  
  /**
   * Gets the interline space of the staff with the given index.
   * If unspecified, the default value of the score is returned.
   */
  public static float getInterlineSpace(Score score, MP mp)
  {
  	Float custom = score.getStaff(mp).getInterlineSpace();
  	if (custom != null)
  	{
  		return custom;
  	}
  	else
  	{
  		return score.getScoreFormat().getInterlineSpace();
  	}
  }
	
	
	/**
	 * Gets the last {@link Key} that is defined before (or at,
	 * dependent on the given {@link BeatInterval}) the given
	 * {@link MP}, also over measure boundaries. If there is
	 * none, a default C major time signature is returned.
	 * Private keys (in measure) override public keys (in measure column header). 
	 */
	@SuppressWarnings("unchecked") public static MPE<? extends Key> getKey(
		Score score, MP mp, BeatInterval interval)
	{
		if (!interval.isPrecedingOrAt())
		{
			throw new IllegalArgumentException("Illegal interval for this method");
		}
		//begin with the given measure. if there is one, return it.
		ScoreHeader scoreHeader = score.getScoreHeader();
		BeatE<Key> columnKey = scoreHeader.getColumnHeader(
			mp.getMeasure()).getLastKey(interval, mp.getBeat());
		BeatE<Key> measureKey = score.getMeasure(mp).getLastKey(interval, mp.getBeat());
		BeatE<Key> ret = selectLatest(columnKey, measureKey);
		if (ret != null)
			return mpe(ret.getElement(), mp.withBeat(ret.getBeat()));
		if (interval != At)
		{
			//search in the preceding measures
			for (int iMeasure = mp.getMeasure() - 1; iMeasure >= 0; iMeasure--)
			{
				columnKey = scoreHeader.getColumnHeader(iMeasure).getLastKey();
				measureKey = score.getMeasure(MP.atMeasure(mp.getStaff(), iMeasure)).getLastKey();
				ret = selectLatest(columnKey, measureKey);
				if (ret != null)
					return mpe(ret.getElement(), mp.withMeasure(iMeasure).withBeat(ret.getBeat()));
			}
		}
		//no key found. return default key.
		return mpe(new TraditionalKey(0), mp0);
	}

	
	/**
	 * Gets the number of beats in the given measure column.
	 * If a time signature is defined, its beats are returned.
	 * If the time signature is unknown (senza-misura), the beats of the
	 * voice with the most beats are returned.
	 */
	public static Fraction getMeasureBeats(Score score, int measureIndex)
	{
		Fraction ret = getTimeAtOrBefore(score, measureIndex).getBeatsPerMeasure();
		if (ret == null)
		{
			for (Measure measure : getColumn(score, measureIndex))
			{
				for (Voice voice : measure.getVoices())
				{
					Fraction usedBeats = voice.getFilledBeats();
					if (ret == null || usedBeats.compareTo(ret) > 0)
					{
						ret = usedBeats;
					}
				}
			}
		}
		return ret;
	}
	
	
	/**
	 * Gets the {@link MusicContext} that is defined before (or at,
	 * dependent on the given {@link BeatInterval}) the given
	 * {@link MP}, also over measure boundaries.
	 * 
	 * If an accidental appears at the given beat, it is regarded
	 * if the given {@link BeatInterval} or {@link BeatInterval#BeforeOrAt},
	 * but if it is  {@link BeatInterval#Before}, it is ignored.
	 *
	 * Calling this method can be quite expensive, so call only when neccessary.
	 */
	public static MusicContext getMusicContext(Score score, MP mp, BeatInterval interval)
	{
		Clef clef = getClef(score, mp, interval);
		Key key = getKey(score, mp, interval).getElement();
		PMap<Pitch, Byte> accidentals = getAccidentals(score, mp, interval);
		return new MusicContext(clef, key, accidentals);
	}
	
	
	/**
	 * Gets the last {@link Time} that is defined at or before the measure
	 * with the given index. If there is none, a default {@link SenzaMisura}
	 * time signature is returned.
	 */
	public static Time getTimeAtOrBefore(Score score, int measureIndex)
	{
		//search for last time
		for (int iMeasure = measureIndex; iMeasure >= 0; iMeasure--)
		{
			ColumnHeader measure =
				score.getScoreHeader().getColumnHeader(iMeasure);
			Time time = measure.getTime();
			if (time != null)
				return time;
		}
		//no key found. return default time.
		return new SenzaMisura();
	}
	
	
	/**
	 * Gets the used beats within the whole score.
	 * For each measure, a sorted list of beats is returned.
	 */
	public static Vector<SortedList<Fraction>> getUsedBeats(Score score)
	{
		ArrayList<SortedList<Fraction>> ret = new ArrayList<SortedList<Fraction>>(
			score.getMeasuresCount());
		for (int iColumn = 0; iColumn < score.getMeasuresCount(); iColumn++)
		{
			SortedList<Fraction> columnBeats = new SortedList<Fraction>(false);
			for (int iStaff = 0; iStaff < score.getStavesCount(); iStaff++)
			{
				columnBeats = columnBeats.merge(score.getMeasure(
					atMeasure(iStaff, iColumn)).getUsedBeats(), false);
			}
			ret.add(columnBeats);
		}
		return ivec(ret);
	}
	
	
	/**
	 * Checks if the given staff exists. If not, an {@link IllegalMPException}
	 * is thrown.
	 */
	public static void assertStaffExists(Score score, int staff)
	{
		if (score.getStavesList().getStavesCount() <= staff)
		{
			throw new IllegalMPException(MP.atStaff(staff));
		}
	}
	
	
	/**
	 * Creates the measures up to the given measure index.
	 * If already existing, nothings happens.
	 */
	public static Score ensureMeasureExists(Score score, MP mp)
	{
		if (mp.isMeasureUnknown())
		{
			throw new IllegalMPException(mp);
		}
		assertStaffExists(score, mp.getStaff());
		Score ret = score;
		int measuresCount = score.getMeasuresCount();
		int measuresToAdd = mp.getMeasure() - measuresCount + 1;
		if (measuresToAdd > 0)
		{
			StavesList stavesList = ret.getStavesList().plusEmptyMeasures(measuresToAdd);
			ret = ret.withStavesList(stavesList);
			ret = ret.withScoreHeader(ret.getScoreHeader().plusEmptyMeasures(measuresToAdd));
		}
		return ret;
	}
	
	
	/**
	 * Creates the voices up to the given voice index at the given measure.
	 * If already existing, nothings happens.
	 */
	public static Score ensureVoiceExists(Score score, MP mp)
	{
		if (mp.isStaffOrMeasureOrVoiceUnknown())
		{
			throw new IllegalMPException(mp);
		}
		score = ensureMeasureExists(score, mp);
		Measure measure = score.getMeasure(mp);
		int voicesToAdd = mp.getVoice() - measure.getVoices().size() + 1;
		if (voicesToAdd > 0)
		{
			for (int i = 0; i < voicesToAdd; i++)
			{
				measure = measure.plusVoice(Voice.empty);
			}
			score = score.withMeasureUnchecked(mp, measure, score.getGlobals());
		}
		return score;
	}
	
	
	/**
	 * Replaces the chord at the given position. It must have the same duration
	 * like the chord which was already there.
	 * If the old chord had a beam, slur, directions or lyrics, they will be used
	 * again.
	 */
	public static Score replaceChord(Score score, MP mp, Chord chord)
	{
		Voice voice = score.getVoice(mp);
		VoiceElement e = voice.getElementAt(mp.getBeat());
		if (e == null || !(e instanceof Chord))
		{
			throw new IllegalArgumentException("No chord starting at " + mp);
		}
		else
		{
			Chord oldChord = (Chord) e;
			if (!oldChord.getDuration().equals(chord.getDuration()))
			{
				throw new IllegalArgumentException("New chord has different duration than the old one");
			}
			else
			{
				voice = voice.replaceElement(oldChord, chord); 
				Globals globals = score.getGlobals().replaceChord(oldChord, chord);
				return score.withVoiceUnchecked(mp, voice, globals);
			}
		}
	}
	
	
	/**
   * Sets layout information for the staff with the given (global) index
   * within the system with the given index, or null if undefined.
   */
  public static Score withStaffLayout(Score score, int systemIndex, int staffIndex,
  	StaffLayout staffLayout)
  {
  	SystemLayout systemLayout = score.getScoreHeader().getSystemLayout(systemIndex);
		PVector<StaffLayout> staffLayouts = systemLayout.getStaffLayouts();
		if (staffLayouts == null)
		{
			staffLayouts = new PVector<StaffLayout>();
		}
		while (systemIndex >= staffLayouts.size())
		{
			staffLayouts = staffLayouts.plus(null);
		}
		staffLayouts = staffLayouts.with(staffIndex, staffLayout);
		return score.withScoreHeader(score.getScoreHeader().withSystemLayout(
			systemIndex, systemLayout.withStaffLayouts(staffLayouts)));
  }
	
	
	/**
	 * Writes the given {@link Beam} to the score.
	 */
	public static Score writeBeam(Score score, Beam beam)
	{
		return score.withGlobals(score.getGlobals().plusBeam(beam));
	}
	
	
	/**
	 * Writes the given {@link CurvedLine} to the score.
	 */
	public static Score writeCurvedLine(Score score, CurvedLine curvedLine)
	{
		return score.withGlobals(score.getGlobals().plusCurvedLine(curvedLine));
	}
	
	
	/**
	 * Writes the given {@link MeasureElement} at the given position
	 * into a single measure (not into the whole measure column!).
	 * Dependent on its type, it may replace elements of the same type.
	 */
	public static Score writeMeasureElement(Score score, MP mp, MeasureElement element)
	{
		score = ensureMeasureExists(score, mp);
		Fraction beat = mp.getBeat();
		Measure measure = score.getMeasure(mp);
		Globals globals = score.getGlobals();
		//insert new element
		Tuple2<Measure, ? extends MeasureElement> e = measure.plusMeasureElement(element, beat);
		measure = e.get1();
		MeasureElement replacedElement = e.get2();
		if (replacedElement != null)
		{
			globals = globals.minusMusicElement(replacedElement);
		}
		globals = globals.plusMusicElement(mp, element);
		//return modified measure
		return score.withMeasureUnchecked(mp, measure, globals);
	}
	
	
	/**
	 * Writes the given {@link Barline} as the end barline
	 * at the given measure column.
	 */
	public static Score writeColumnEndBarline(Score score, int measureIndex,
		Barline endBarline)
	{
		score = ensureMeasureExists(score, atMeasure(measureIndex));
		ScoreHeader scoreHeader = score.getScoreHeader();
		ColumnHeader mch = scoreHeader.getColumnHeader(measureIndex);
		mch = mch.withEndBarline(endBarline);
		return score.withScoreHeader(scoreHeader.withMeasureColumnHeader(mch, measureIndex));
	}
	
	
	/**
	 * Writes the given {@link ColumnElement} at the given position
	 * into the whole measure column. Existing elements of the same type at
	 * the same position are replaced.
	 * Use writeMeasureColumnStartBarline and
	 * writeMeasureColumnStartBarline instead to write {@link Barline}s
	 * that are not in the middle of a measure.
	 */
	public static Score writeColumnElement(Score score, MP mp,
		ColumnElement element)
	{
		score = ensureMeasureExists(score, mp);
		Fraction beat = mp.getBeat();
		ScoreHeader scoreHeader = score.getScoreHeader();
		ColumnHeader mch = scoreHeader.getColumnHeader(mp.getMeasure());
		//insert new element
		if (element instanceof Barline)
		{
			mch = mch.withMiddleBarline((Barline) element, beat);
		}
		else if (element instanceof Break)
		{
			mch = mch.withBreak((Break) element);
		}
		else if (element instanceof Key)
		{
			mch = mch.withKey((Key) element, beat);
		}
		else if (element instanceof Tempo)
		{
			mch = mch.withTempo((Tempo) element, beat);
		}
		else if (element instanceof Time)
		{
			mch = mch.withTime((Time) element);
		}
		else if (element instanceof Volta)
		{
			mch = mch.withVolta((Volta) element);
		}
		//return modified measure column
		return score.withScoreHeader(scoreHeader.withMeasureColumnHeader(mch, mp.getMeasure()));
	}
	
	
	/**
	 * Writes the given {@link Barline} as the start barline
	 * at the given measure column.
	 */
	public static Score writeColumnStartBarline(Score score, int measureIndex,
		Barline startBarline)
	{
		score = ensureMeasureExists(score, atMeasure(measureIndex));
		ScoreHeader scoreHeader = score.getScoreHeader();
		ColumnHeader mch = scoreHeader.getColumnHeader(measureIndex);
		mch = mch.withStartBarline(startBarline);
		return score.withScoreHeader(scoreHeader.withMeasureColumnHeader(mch, measureIndex));
	}
	

	/**
	 * Replaces the {@link VoiceElement}s between the given {@link MP}
	 * and the given duration (if any) by the given {@link VoiceElement}.
	 * If the start or end position is within an element, these elements will
	 * be cut correspondingly.
	 * All affected elements (slurs, beams, ...) will be removed
	 * from the score, so the returned score is guaranteed to be in a
	 * consistent state.
	 */
	public static Score writeVoiceElement(Score score, MP startMP, VoiceElement element)
	{
		score = ensureVoiceExists(score, startMP);
		Fraction startBeat = startMP.getBeat();
		Fraction endBeat = startBeat.add(element.getDuration());
		Voice oldVoice = score.getVoice(startMP);
		Voice voice = oldVoice;
		Globals globals = score.getGlobals();
		//if necessary, cut the first element in the range
		BeatE<VoiceElement> startPVE = voice.getElement(Last, Start, BeforeOrAt, startBeat);
		if (startPVE != null)
		{
			VoiceElement startVE = startPVE.getElement();
			Fraction startVENewLength = startPVE.getBeat().add(
				startVE.getDuration()).sub(startBeat);
			if (startVENewLength.isGreater0())
			{
				VoiceElement newStartVE = startVE.withDuration(startVENewLength);
				MP newStartMP = startMP.withBeat(startPVE.getBeat());
				voice = voice.replaceElement(startVE, newStartVE,
					new Rest(startVE.getDuration().sub(startVENewLength)));
				globals = globals.minusMusicElement(startVE);
				globals = globals.plusMusicElement(newStartMP, startVE);
			}
		}
		//if necessay, cut the last element in the range
		BeatE<VoiceElement> endPVE = voice.getElement(Last, Start, Before, endBeat);
		if (endPVE != null)
		{
			VoiceElement endVE = endPVE.getElement();
			Fraction endVENewLength = endPVE.getBeat().add(endVE.getDuration()).sub(endBeat);
			if (endVENewLength.isGreater0())
			{	
				VoiceElement newEndVE = endVE.withDuration(endVENewLength);
				MP newEndMP = startMP.withBeat(endBeat);
				voice = voice.replaceElement(endVE,
					new Rest(endVE.getDuration().sub(endVENewLength)), newEndVE);
				globals = globals.minusMusicElement(endVE);
				globals = globals.plusMusicElement(newEndMP, newEndVE);
			}
		}
		//remove elements between the two beats
		LinkedList<VoiceElement> middleElements = voice.getElementsInRange(startBeat, endBeat);
		for (VoiceElement middleElement : middleElements)
		{
			voice = voice.minusElement(middleElement);
			globals = globals.minusMusicElement(middleElement);
		}
		//insert new element
		voice = voice.insertElement(startBeat, element);
		globals = globals.plusMusicElement(startMP, element);
		//return modified voice
		return score.withVoiceUnchecked(startMP, voice, globals);
	}
	
	
	/**
	 * Like {@link #writeVoiceElement(Score, MP, VoiceElement)}, but also checks
	 * if there is enough musical space left for the element (according to the
	 * current time signature).
	 */
	public static Score writeVoiceElementTimeAware(Score score, MP startMP,
		VoiceElement element)
		throws MeasureFullException
	{
		Time time = getTimeAtOrBefore(score, startMP.getMeasure());
		Fraction duration = time.getBeatsPerMeasure();
		if (duration != null &&
			startMP.getBeat().add(element.getDuration()).compareTo(duration) > 0)
		{
			throw new MeasureFullException(startMP, element.getDuration());
		}
		return writeVoiceElement(score, startMP, element);
	}

}
