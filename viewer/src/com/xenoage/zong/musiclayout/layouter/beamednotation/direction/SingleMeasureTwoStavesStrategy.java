package com.xenoage.zong.musiclayout.layouter.beamednotation.direction;

import java.util.Iterator;

import com.xenoage.util.iterators.It;
import com.xenoage.zong.data.music.Beam;
import com.xenoage.zong.data.music.BeamWaypoint;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.Staff;
import com.xenoage.zong.data.music.StemDirection;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterContext;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.cache.NotationsCache;
import com.xenoage.zong.musiclayout.layouter.notation.NotationStrategy;
import com.xenoage.zong.musiclayout.notations.ChordNotation;
import com.xenoage.zong.musiclayout.notations.Notation;
import com.xenoage.zong.musiclayout.notations.beam.BeamStemDirections;


/**
 * This strategy recomputes the {@link Notation}s of the chords
 * of the given {@link Beam}, which spans over two adjacent staves
 * and a single measure column, but only, if necessary.
 * 
 * @author Andreas Wenger
 */
public class SingleMeasureTwoStavesStrategy
	implements ScoreLayouterStrategy
{
	
	//used strategies
	private final NotationStrategy notationStrategy;
	
	
	public SingleMeasureTwoStavesStrategy(NotationStrategy notationStrategy)
	{
		this.notationStrategy = notationStrategy;
	}
	

	/**
	 * Returns better {@link Notation}s of the chords connected by the given beam,
	 * using the given notations.
	 * Only changed notations are returned.
	 */
	public NotationsCache computeNotations(Beam beam,
		NotationsCache notations, ScoreLayouterContext lc)
	{
		//do the work
		BeamStemDirections bsd = computeBeamStemDirections(beam);
		
		//return the results as a new NotationsCache
		NotationsCache ret = new NotationsCache();
		Iterator<BeamWaypoint> beamWaypoints = beam.getWaypoints();
		for (int i = 0; i < bsd.getStemDirections().length; i++)
		{
			Chord chord = beamWaypoints.next().getChord();
			ChordNotation oldChordNotation = notations.getChord(chord);
			StemDirection oldStemDir = oldChordNotation.getStemDirection();
			//if stem direction was changed, recompute the notation.
			//the stem lengths are not fitted to the beam already, so this is
			//has to be done later within another strategy
			if (bsd.getStemDirections()[i] != oldStemDir)
			{
				ret.set(notationStrategy.computeChord(chord, bsd.getStemDirections()[i], lc), chord);
			}
		}
		return ret;
	}
	
	
	/**
   * Computes the directions of the stems of beamed chords within a single measure
   * in two adjacent staves.
   * Currently, the stems of the chords of the upper staff always point down, while
   * the stems of the chords of the lower staff always point up.
   */
  BeamStemDirections computeBeamStemDirections(Beam beam)
  {
  	StemDirection[] ret = new StemDirection[beam.getWaypointsCount()];
  	Staff upperStaff = beam.getUpperStaff();
  	It<BeamWaypoint> waypoints = beam.getWaypoints();
  	for (BeamWaypoint waypoint : waypoints)
		{
			Staff staff = waypoint.getChord().getVoice().getMeasure().getStaff();
			ret[waypoints.getIndex()] = (staff == upperStaff ? StemDirection.Down : StemDirection.Up);
		}
  	return new BeamStemDirections(ret);
  }
  

}
