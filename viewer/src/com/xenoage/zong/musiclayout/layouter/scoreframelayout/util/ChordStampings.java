package com.xenoage.zong.musiclayout.layouter.scoreframelayout.util;

import java.util.LinkedList;
import java.util.List;

import com.xenoage.zong.musiclayout.layouter.cache.util.BeamedStemStampings.OpenBeamMiddleStem;
import com.xenoage.zong.musiclayout.stampings.AccidentalStamping;
import com.xenoage.zong.musiclayout.stampings.ArticulationStamping;
import com.xenoage.zong.musiclayout.stampings.FlagsStamping;
import com.xenoage.zong.musiclayout.stampings.LegerLineStamping;
import com.xenoage.zong.musiclayout.stampings.NoteheadStamping;
import com.xenoage.zong.musiclayout.stampings.ProlongationDotStamping;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.Stamping;
import com.xenoage.zong.musiclayout.stampings.StemStamping;


/**
 * The stampings belonging to a chord: a list of noteheads,
 * leger lines, dots, accidentals and a stem and flag.
 * 
 * @author Andreas Wenger
 */
public class ChordStampings
{
	
	//TIDY
	public final float positionX;
	public final StaffStamping staffStamping;
	
	public LinkedList<NoteheadStamping> noteheads = new LinkedList<NoteheadStamping>();
	public LinkedList<LegerLineStamping> legerLines = new LinkedList<LegerLineStamping>();
	public LinkedList<ProlongationDotStamping> dots = new LinkedList<ProlongationDotStamping>();
	public LinkedList<AccidentalStamping> accidentals = new LinkedList<AccidentalStamping>();
	public LinkedList<ArticulationStamping> articulations = new LinkedList<ArticulationStamping>();
	public FlagsStamping flags = null;
	
	//stamped or open stem
	public StemStamping stem = null;
	public OpenBeamMiddleStem openStem = null;
	
	
	public ChordStampings(float positionX, StaffStamping staffStamping)
	{
		this.positionX = positionX;
		this.staffStamping = staffStamping;
	}
	
	
	/**
	 * //LAYOUT-PERFORMANCE (needed 1 of 60 seconds)
	 */
	public void addAllTo(List<Stamping> list)
	{
		list.addAll(noteheads);
		list.addAll(legerLines);
		list.addAll(dots);
		list.addAll(accidentals);
		list.addAll(articulations);
    if (stem != null)
    	list.add(stem);
    if (flags != null)
    	list.add(flags);
	}
	
	
}
