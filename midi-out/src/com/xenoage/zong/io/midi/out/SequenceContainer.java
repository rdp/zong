/**
 * 
 */
package com.xenoage.zong.io.midi.out;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.sound.midi.Sequence;

import com.xenoage.util.lang.Tuple2;
import com.xenoage.zong.data.ScorePosition;


/**
 * @author Uli Teschemacher
 *
 */
public class SequenceContainer
{

	private Sequence sequence;
	private int metronomeBeatTrackNumber;
	private LinkedList<Tuple2<Long, ScorePosition>> scorePositionTicks;
	private ArrayList<Long> measureStartTicks;
	private ArrayList<Integer> staffTracks;

	public void setSequence(Sequence sequence)
	{
		this.sequence = sequence;
	}

	public Sequence getSequence()
	{
		return sequence;
	}

	public void setMetronomeTrackNumber(int metronomeBeatTrackNumber)
	{
		this.metronomeBeatTrackNumber = metronomeBeatTrackNumber;
		
	}
	
	public int getMetronomeTrackNumber()
	{
		return metronomeBeatTrackNumber;
	}

	public void setScorePositionTicks(
		LinkedList<Tuple2<Long, ScorePosition>> scorePositionTicks)
	{
		this.scorePositionTicks = scorePositionTicks;
	}
	
	public LinkedList<Tuple2<Long, ScorePosition>> getScorePositionTicks()
	{
		return scorePositionTicks;
	}

	public void setMeasureStartTicks(ArrayList<Long> measureStartTicks)
	{
		this.measureStartTicks = measureStartTicks;	
	}
	
	public ArrayList<Long> getMeasureStartTicks()
	{
		return measureStartTicks;
	}

	public void setStaffTracks(ArrayList<Integer> staffTracks)
	{
		this.staffTracks = staffTracks;
	}

	public ArrayList<Integer> getStaffTracks()
	{
		return staffTracks;
	}
}
