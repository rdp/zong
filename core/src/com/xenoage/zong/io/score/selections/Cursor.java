package com.xenoage.zong.io.score.selections;

import static com.xenoage.util.NullTools.throwNullArg;
import static com.xenoage.util.math.Fraction._0;
import static com.xenoage.zong.core.music.beam.Beam.beam;
import static com.xenoage.zong.io.score.ScoreController.ensureMeasureExists;
import static com.xenoage.zong.io.score.ScoreController.ensureVoiceExists;
import static com.xenoage.zong.io.score.ScoreController.getTimeAtOrBefore;
import static com.xenoage.zong.io.score.ScoreController.writeBeam;
import static com.xenoage.zong.io.score.ScoreController.writeColumnElement;
import static com.xenoage.zong.io.score.ScoreController.writeCurvedLine;
import static com.xenoage.zong.io.score.ScoreController.writeMeasureElement;
import static com.xenoage.zong.io.score.ScoreController.writeVoiceElementTimeAware;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.*;
import com.xenoage.zong.core.music.beam.BeamWaypoint;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.chord.Note;
import com.xenoage.zong.core.music.curvedline.CurvedLine;
import com.xenoage.zong.core.music.curvedline.CurvedLineWaypoint;
import com.xenoage.zong.core.music.util.MPE;
import com.xenoage.zong.io.score.ScoreController;
import com.xenoage.zong.io.score.ScoreInputOptions;
import com.xenoage.zong.io.score.ScoreInputOptions.WriteMode;
import com.xenoage.zong.util.exceptions.IllegalMPException;
import com.xenoage.zong.util.exceptions.MeasureFullException;


/**
 * Cursor for a score.
 * 
 * This is the most often used selection, since it is also useful for
 * sequential input like needed when reading from a file.
 * 
 * It contains the current position within the score and provides many methods
 * to write or remove elements and move the cursor.
 * 
 * If the move flag is set, the cursor jumps to the end of written elements
 * instead of staying at its old position. Then it can be at a {@link MP} which
 * still does not exist (e.g. at the end of the score), which isn't a problem
 * since it is created as soon as needed.
 * 
 * There is also the possibility to open and close beams and curved lines.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public final class Cursor
	implements Selection
{

	private final Score score;
	private final MP mp;
	private final boolean moving;
	
	private final PVector<BeamWaypoint> openBeamWaypoints;
	private final PVector<CurvedLineWaypoint> openCurvedLineWaypoints;
	private final CurvedLine.Type openCurvedLinesType;

	
	/**
	 * Creates a new {@link Cursor}.
	 * @param score   the score to work on
	 * @param pos     the musical position of the cursor, which may still not exist
	 *                in the score (the measure and voice will be created on demand)
	 * @param moving  move with input?
	 */
	public Cursor(Score score, MP mp, boolean moving)
	{
		this(score, mp, moving, null, null, null);
	}
	
	
	/**
	 * Creates a new {@link Cursor}.
	 * @param score                    the score to work on
	 * @param pos                      the musical position of the cursor
	 * @param moving                   move with input?
	 * @param openBeamWaypoints        list of open beam waypoints, or null
	 * @param openCurvedLineWaypoints  list of open curved line waypoints, or null
	 * @param openCurvedLinesType      type of open curved line, or null
	 */
	private Cursor(Score score, MP mp, boolean moving, PVector<BeamWaypoint> openBeamWaypoints,
		PVector<CurvedLineWaypoint> openCurvedLineWaypoints, CurvedLine.Type openCurvedLinesType)
	{
		if (mp.getBeat().getNumerator() < 0)
			throw new IllegalArgumentException("Beat may not be negative");
		this.score = score;
		this.mp = mp;
		this.moving = moving;
		this.openBeamWaypoints = openBeamWaypoints;
		this.openCurvedLineWaypoints = openCurvedLineWaypoints;
		this.openCurvedLinesType = openCurvedLinesType;
	}


	/**
	 * {@inheritDoc}
	 */
	public Score getScore()
	{
		return score;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public Cursor withScore(Score score)
	{
		return new Cursor(score, mp, moving, openBeamWaypoints,
			openCurvedLineWaypoints, openCurvedLinesType);
	}


	/**
	 * Gets the position of the cursor.
	 */
	public MP getMP()
	{
		return mp;
	}
	
	
	/**
	 * Sets the position of the cursor.
	 */
	public Cursor withMP(MP mp)
	{
		return new Cursor(score, mp, moving);
	}
	
	
	/**
	 * Writes the given {@link ColumnElement} at the current position.
	 * Dependent on its type, it may replace elements of the same type.
	 */
	@Override public Cursor write(ColumnElement element)
	{
		//create the measure, if needed
		Score score = ensureMeasureExists(this.score, mp);
		//write the element
		score = writeColumnElement(score, mp, element);
		return new Cursor(score, mp, moving, openBeamWaypoints,
			openCurvedLineWaypoints, openCurvedLinesType);
	}
	
	
	/**
	 * See {@link #write(ColumnElement)}.
	 */
	@Override public Cursor write(ColumnElement element, ScoreInputOptions options)
	{
		return write(element);
	}


	/**
	 * Writes the given pitches as a chord. The position and overwrite mode
	 * depends on the given write mode.
	 */
	@Override public Cursor write(Pitch[] pitches, ScoreInputOptions options)
		throws IllegalMPException, MeasureFullException
	{
		WriteMode wm = options.getWriteMode();
		if (wm == WriteMode.ChordBeforeCursor)
		{
			//add the given pitches to the chord before the cursor
			return addPitchesToPrecedingChord(pitches);
		}
		else
		{
			//default behaviour: write chord after cursor
			Chord chord = new Chord(Note.createNotes(pitches), options.getFraction(), null, null);
			return write(chord, options);
		}
	}	


	/**
	 * Adds the given pitches to the chord before the cursor. If there is no
	 * chord, nothing is done.
	 */
	private Cursor addPitchesToPrecedingChord(Pitch[] pitches)
	{
		//find the last voice element starting before the current beat
		MPE<VoiceElement> pve = score.getStaff(mp).getVoiceElementEndingAtOrBefore(mp);

		//if the target element is a chord, add the given pitches to it
		if (pve != null && pve.getElement() instanceof Chord)
		{
			Chord chord = (Chord) pve.getElement();
			for (Pitch pitch : pitches)
			{
				chord = chord.plusPitch(pitch);
			}
			Score score = ScoreController.replaceChord(this.score, pve.getMP(), chord);
			return new Cursor(score, mp, moving, openBeamWaypoints,
				openCurvedLineWaypoints, openCurvedLinesType);
		}
		else
		{
			return this;
		}
	}


	/**
	 * Writes the given {@link VoiceElement} at the current position and,
	 * if the moving flag is set, moves the cursor forward according to
	 * the duration of the element.
	 * 
	 * This method overwrites the elements overlapping the current cursor
	 * position (but not {@link NoVoiceElement}s like key or time signatures at
	 * the cursor position) and overlapping the current cursor position plus the
	 * duration of the given element.
	 * 
	 * Thus, if gaps appear before or after the written element, the corresponding
	 * elements are cut.
	 */
	@Override public Cursor write(VoiceElement element)
		throws MeasureFullException
	{
		//create the voice, if needed
		Score score = ensureVoiceExists(this.score, mp);

		//write the element
		score = writeVoiceElementTimeAware(score, mp, element);
		
		//if a beam is open and it is a chord, at it
		PVector<BeamWaypoint> openBeamWaypoints = this.openBeamWaypoints;
		if (openBeamWaypoints != null && element instanceof Chord)
		{
			Chord chord = (Chord) element;
			openBeamWaypoints = openBeamWaypoints.plus(new BeamWaypoint(chord, false));
		}

		//if move flag is set, move cursor forward, also over measure boundaries
		MP newMP = mp;
		if (moving)
		{
			Fraction newBeat = mp.getBeat().add(element.getDuration());
			//if this beat is behind the end of the measure, jump into the next measure
			Fraction measureBeats = getTimeAtOrBefore(score, mp.getMeasure()).getBeatsPerMeasure();
			if (measureBeats != null && newBeat.compareTo(measureBeats) >= 0)
			{
				//begin new measure
				newMP = mp.withMeasure(mp.getMeasure() + 1).withBeat(_0);
			}
			else
			{
				//stay in the current measure
				newMP = mp.withBeat(newBeat);
			}
		}
		
		return new Cursor(score, newMP, moving, openBeamWaypoints,
			openCurvedLineWaypoints, openCurvedLinesType);
	}
	
	
	/**
	 * See {@link #write(VoiceElement)}.
	 */
	@Override public Cursor write(VoiceElement element, ScoreInputOptions options)
		throws MeasureFullException
	{
		return write(element);
	}


	/**
	 * Writes the given {@link MeasureElement} at the current position.
	 * Dependent on its type, it may replace elements of the same type.
	 */
	@Override public Cursor write(MeasureElement element)
	{
		//create the measure, if needed
		Score score = ensureMeasureExists(this.score, mp);
		//write the element
		score = writeMeasureElement(score, mp, element);
		return new Cursor(score, mp, moving, openBeamWaypoints,
			openCurvedLineWaypoints, openCurvedLinesType);
	}
	
	
	/**
	 * See {@link #write(MeasureElement)}.
	 */
	@Override public Cursor write(MeasureElement element, ScoreInputOptions options)
	{
		return write(element);
	}
	

	/**
	 * Returns whether the cursor is moved when the write method is executed
	 */
	public boolean isMoving()
	{
		return moving;
	}
	
	
	/**
	 * Opens a beam. All following chords will be added to it.
	 */
	public Cursor openBeam()
	{
		if (openBeamWaypoints != null)
		{
			throw new IllegalStateException("Beam is already open");
		}
		return new Cursor(score, mp, moving, new PVector<BeamWaypoint>(),
			openCurvedLineWaypoints, openCurvedLinesType);
	}
	
	
	/**
	 * Closes a beam and adds it to the score.
	 */
	public Cursor closeBeam()
	{
		if (openBeamWaypoints == null)
		{
			throw new IllegalStateException("No beam is open");
		}
		Score score = writeBeam(this.score, beam(openBeamWaypoints));
		return new Cursor(score, mp, moving, null, openCurvedLineWaypoints, openCurvedLinesType);
	}
	
	
	/**
	 * Opens a curved line of the given type. All following chords will be added to it.
	 */
	public Cursor openCurvedLine(CurvedLine.Type type)
	{
		if (openCurvedLineWaypoints != null)
		{
			throw new IllegalStateException("Curved line is already open");
		}
		throwNullArg(type);
		return new Cursor(score, mp, moving, openBeamWaypoints,
			new PVector<CurvedLineWaypoint>(), type);
	}
	
	
	/**
	 * Closes a curved line and adds it to the score.
	 */
	public Cursor closeCurvedLine()
	{
		if (openCurvedLineWaypoints == null)
		{
			throw new IllegalStateException("No curved line is open");
		}
		Score score = writeCurvedLine(this.score, new CurvedLine(openCurvedLinesType,
			openCurvedLineWaypoints, null));
		return new Cursor(score, mp, moving, openBeamWaypoints, null, null);
	}


}
