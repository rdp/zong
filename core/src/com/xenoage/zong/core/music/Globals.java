package com.xenoage.zong.core.music;

import static com.xenoage.util.Range.range;

import com.xenoage.pdlib.PMap;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.beam.Beam;
import com.xenoage.zong.core.music.beam.Beams;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.curvedline.CurvedLine;
import com.xenoage.zong.core.music.tuplet.Tuplet;
import com.xenoage.zong.core.music.tuplet.Tuplets;
import com.xenoage.zong.util.exceptions.IllegalMPException;


/**
 * Information that can not be assigned to a staff, measure
 * or voice. These are:
 * 
 * <ul>
 *  <li>Set of tuplets</li>
 * 	<li>Set of beams</li>
 * 	<li>Set of slurs and ties</li>
 * 	<li>Set of elements attached to other ones</li>
 * 	<li>Lookup table for the {@link MP}s of musical elements</li>
 * </ul>
 * 
 * @author Andreas Wenger
 */
public class Globals
{
	
	private final Tuplets tuplets;
	private final Beams beams;
	private final CurvedLines curvedLines;
	private final Attachments attachments;
	private final PMap<MusicElement, MP> mps;
	
	private static final Globals empty =
		new Globals(Tuplets.empty(), Beams.empty(), CurvedLines.empty(),
			Attachments.empty(), new PMap<MusicElement, MP>());
	
	
	/**
	 * Creates a new {@link Globals} using the given information.
	 * @param tuplets        the set of all tuplets
	 * @param beams        the set of all beams
	 * @param curvedLines  the set of all slurs and ties
	 * @param attachments  the set of elements attached to other ones
	 * @param mps          the musical positions for all voice elements and measure elements
	 */
	private Globals(Tuplets tuplets, Beams beams, CurvedLines curvedLines,
		Attachments attachments, PMap<MusicElement, MP> mps)
	{
		this.tuplets = tuplets;
		this.beams = beams;
		this.curvedLines = curvedLines;
		this.attachments = attachments;
		this.mps = mps;
	}
	
	
	/**
	 * Gets an empty {@link Globals}.
	 */
	public static Globals empty()
	{
		return empty;
	}
	
	
	/**
	 * Gets all tuplets of the score.
	 */
	public Tuplets getTuplets()
	{
		return tuplets;
	}
	
	
	/**
	 * Gets all beams of the score.
	 */
	public Beams getBeams()
	{
		return beams;
	}
	
	
	/**
	 * Attaches the given element to the given anchor element.
	 */
	public Globals plusAttachment(MusicElement anchor, Attachable attachment)
	{
		Attachments attachments = this.attachments.plus(anchor, attachment);
		return new Globals(tuplets, beams, curvedLines, attachments, mps);
	}
	
	
	/**
	 * Adds a beam to the score.
	 */
	public Globals plusBeam(Beam beam)
	{
		Beams beams = this.beams.plus(beam);
		return new Globals(tuplets, beams, curvedLines, attachments, mps);
	}
	
	
	/**
	 * Gets all curved lines of the score.
	 */
	public CurvedLines getCurvedLines()
	{
		return curvedLines;
	}
	
	
	/**
	 * Adds a curved line to the score.
	 */
	public Globals plusCurvedLine(CurvedLine curvedLine)
	{
		CurvedLines curvedLines = this.curvedLines.plus(curvedLine);
		return new Globals(tuplets, beams, curvedLines, attachments, mps);
	}
	
	
	/**
	 * Gets all attached elements of the score.
	 */
	public Attachments getAttachments()
	{
		return attachments;
	}
	
	
	/**
	 * Gets the musical position of the given element, or returns
	 * null if unknown.
	 * Attached elements are ignored. Call {@link #getMPWithAttachments(MusicElement)}
	 * instead to query also attached elements.
	 */
	public MP getMP(MusicElement element)
	{
		return mps.get(element);
	}
	
	
	/**
	 * Gets the musical position of the given element, or returns
	 * null if unknown. If the given element is attached a second element,
	 * the musical position of the second element is returned.
	 * If unknown, null is returned.
	 */
	public MP getMPWithAttachments(MusicElement element)
	{
		MP mp = mps.get(element);
		if (mp == null && element instanceof Attachable)
		{
			MusicElement anchor = attachments.getAnchor((Attachable) element);
			if (anchor != null)
			{
				mp = getMP(anchor);
			}
		}
		return mp;
	}
	
	
	/**
	 * Removes the given attached element. 
	 */
	public Globals minusAttachment(Attachable attachment)
	{
		Attachments attachments = this.attachments.minusAttachment(attachment);
		return new Globals(tuplets, beams, curvedLines, attachments, mps);
	}
	
	
	/**
	 * Removes all cached MPs, beams, curved lines and attachments
	 * that are found in the given measure. 
	 */
	public Globals minusMeasure(Measure measure)
	{
		Globals globals = this;
		for (Voice voice : measure.getVoices())
		{
			globals = globals.minusVoice(voice);
		}
		return globals;
	}
	
	
	/**
	 * Removes all cached MPs, beams, curved lines and attachments
	 * that are found in the given staff. 
	 */
	public Globals minusStaff(Staff staff)
	{
		Globals globals = this;
		for (Measure measure : staff.getMeasures())
		{
			globals = globals.minusMeasure(measure);
		}
		return globals;
	}
	
	
	/**
	 * Removes all cached MPs, tuplets, beams, curved lines and attachments
	 * that are found in the given voice. 
	 */
	public Globals minusVoice(Voice voice)
	{
		Globals globals = this;
		for (VoiceElement e : voice.getElements())
		{
			globals = globals.minusMusicElement(e);
		}
		return globals;
	}
	
	
	/**
	 * Removes the cached MP, beam, and all curved lines and attachments
	 * that belong to the given MusicElement.
	 * Do not call this method for attached elements. Call
	 * {@link #minusAttachment(Attachable)} instead.
	 */
	public Globals minusMusicElement(MusicElement element)
	{
		Tuplets tuplets = this.tuplets;
		Beams beams = this.beams;
		CurvedLines curvedLines = this.curvedLines;
		if (element instanceof Chord)
		{
			Chord chord = (Chord) element;
			//remove tuplet
			Tuplet tuplet = tuplets.get(chord);
			if (tuplet != null)
			{
				tuplets = tuplets.minus(tuplet);
			}
			//remove beam
			Beam beam = beams.get(chord);
			if (beam != null)
			{
				beams = beams.minus(beam);
			}
			//remove curved lines
			curvedLines = curvedLines.minusAll(chord);
		}
		//remove attachments
		Attachments attachments = this.attachments.minusAnchor(element);
		//remove MPs
		PMap<MusicElement, MP> mps = this.mps.minus(element);
		return new Globals(tuplets, beams, curvedLines, attachments, mps);
	}
	
	
	/**
	 * Adds the elements in the given measure at the given {@link MP}
	 * to the cached {@link MP}s. 
	 */
	public Globals plusMeasure(MP mp, Measure measure)
	{
		Globals globals = this;
		for (int i : range(measure.getVoices()))
		{
			globals = globals.plusVoice(mp.withVoice(i), measure.getVoices().get(i));
		}
		return globals;
	}
	
	
	/**
	 * Adds the elements in the given staff at the given {@link MP}
	 * to the cached {@link MP}s. 
	 */
	public Globals plusStaff(MP mp, Staff staff)
	{
		Globals globals = this;
		for (int i : range(staff.getMeasures()))
		{
			globals = globals.plusMeasure(mp.withMeasure(i), staff.getMeasures().get(i));
		}
		return globals;
	}
	
	
	/**
	 * Adds the elements in the given voice at the given {@link MP}
	 * to the cached {@link MP}s. 
	 */
	public Globals plusVoice(MP mp, Voice voice)
	{
		//staff, measure and voice may not be unknown
		if (mp.isStaffOrMeasureOrVoiceUnknown())
		{
			throw new IllegalMPException(mp, "staff, measure and voice must be given");
		}
		//add MP
		PMap<MusicElement, MP> mps = this.mps;
		Fraction beat = Fraction._0;
		for (VoiceElement e : voice.getElements())
		{
			mps = mps.plus(e, mp.withBeat(beat));
			beat = beat.add(e.getDuration());
		}
		return new Globals(tuplets, beams, curvedLines, attachments, mps);
	}
	
	
	/**
	 * Adds the element at the given {@link MP} to the cached {@link MP}s. 
	 */
	public Globals plusMusicElement(MP mp, MusicElement element)
	{
		//staff, measure, voice and beat may not be unknown
		if (mp.isStaffOrMeasureOrVoiceOrBeatUnknown())
		{
			throw new IllegalMPException(mp, "staff, measure, voice and beat must be given");
		}
		//add MP
		PMap<MusicElement, MP> mps = this.mps.plus(element, mp);
		return new Globals(tuplets, beams, curvedLines, attachments, mps);
	}
	
	
	/**
	 * Replaced the given chord by the other given one.
	 * They are considered to be on the same {@link MP} and the new
	 * chord will have the same tuplet, beam, curved lines and attachments
	 * as the old one.
	 */
	public Globals replaceChord(Chord oldChord, Chord newChord)
	{
		//update MPs
		PMap<MusicElement, MP> mps = this.mps;
		MP mp = mps.get(oldChord);
		if (mp == null)
		{
			throw new IllegalArgumentException("Unknown old chord");
		}
		mps = mps.minus(oldChord);
		mps = mps.plus(newChord, mp);
		//update tuplets, beams, curved lines and attachments
		Tuplets tuplets = this.tuplets.replaceChord(oldChord, newChord);
		Beams beams = this.beams.replaceChord(oldChord, newChord);
		CurvedLines curvedLines = this.curvedLines.replaceChord(oldChord, newChord);
		Attachments attachments = this.attachments.replaceChord(oldChord, newChord);
		return new Globals(tuplets, beams, curvedLines, attachments, mps);
	}
	

}
