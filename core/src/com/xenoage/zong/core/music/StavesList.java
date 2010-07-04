package com.xenoage.zong.core.music;

import static com.xenoage.pdlib.PMap.pmap;
import static com.xenoage.util.Range.range;
import static com.xenoage.zong.core.music.MP.mp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.xenoage.pdlib.PMap;
import com.xenoage.pdlib.PVector;
import com.xenoage.util.MathTools;
import com.xenoage.util.Range;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.group.BarlineGroup;
import com.xenoage.zong.core.music.group.BracketGroup;
import com.xenoage.zong.core.music.group.StavesRange;
import com.xenoage.zong.util.exceptions.IllegalMPException;


/**
 * A {@link StavesList} manages the staves of a score
 * and all of its parts, and bracket and barline groups.
 *
 * @author Andreas Wenger
 */
public final class StavesList
{
  
  private final PVector<Staff> staves;
  private final PVector<Part> parts;
  private final PVector<BarlineGroup> barlineGroups;
  private final PVector<BracketGroup> bracketGroups;
  
  private static final StavesList empty =
  	new StavesList(new PVector<Staff>(), new PVector<Part>(),
  		new PVector<BarlineGroup>(), new PVector<BracketGroup>());
  
  //cache
  private final PMap<Part, Range> partStaffIndices;
  private final Part[] stavesToParts;
  
  
  /**
   * Creates a new {@link StavesList}.
   */
  public StavesList(PVector<Staff> staves, PVector<Part> parts,
  	PVector<BarlineGroup> barlineGroups, PVector<BracketGroup> bracketGroups)
  {
    this.staves = staves;
    this.parts = parts;
    this.barlineGroups = barlineGroups;
    this.bracketGroups = bracketGroups;
    
    //cache
    PMap<Part, Range> partStaffIndices = pmap();
    Part[] stavesToParts = new Part[staves.size()];
    int staffIndex = 0;
    for (Part part : parts)
    {
    	Range r = range(staffIndex, staffIndex + part.getStavesCount() - 1);
    	partStaffIndices = partStaffIndices.plus(part, r);
    	for (int i : r)
    		stavesToParts[i] = part;
    	staffIndex += part.getStavesCount();
    }
    this.partStaffIndices = partStaffIndices;
    this.stavesToParts = stavesToParts;
  }
  
  
  /**
   * Gets an empty {@link StavesList}.
   */
  public static StavesList empty()
  {
  	return empty;
  }
  
  
  /**
   * Gets the number of staves.
   */
  public int getStavesCount()
  {
    return staves.size();
  }
  
  
  /**
   * Gets a list of all staves.
   */
  public PVector<Staff> getStaves()
  {
  	return staves;
  }
  
  
  /**
   * Gets the list of parts.
   */
  public PVector<Part> getParts()
  {
    return parts;
  }
  
  
  /**
   * Gets the bracket groups.
   */
  public PVector<BracketGroup> getBracketGroups()
  {
    return bracketGroups;
  }
  
  
  /**
   * Gets the barline groups.
   */
  public PVector<BarlineGroup> getBarlineGroups()
  {
    return barlineGroups;
  }
  
  
  /**
   * Adds the given part at the end of the other parts.
   */
  public StavesList plusPart(Part part, int measuresCount)
  {
  	//create empty staves
  	LinkedList<Staff> staves = new LinkedList<Staff>();
  	for (int i = 0; i < part.getStavesCount(); i++)
    {
      Staff staff = new Staff(measuresCount, 5, null); //TODO
      staves.add(staff);
    }
    return plusPart(part, parts.size(), staves);
  }
  
  
  /**
   * Adds a new part using the given staves at the end of the other parts.
   */
  public StavesList plusPart(Part part, List<Staff> staves)
  {
  	return plusPart(part, parts.size(), staves);
  }
  
  
  /**
   * Adds a new part with the given staves at the given position.
   */
  public StavesList plusPart(Part part, int partIndex, List<Staff> partStaves)
  {
    if (partIndex < 0 || partIndex > parts.size())
      throw new IllegalArgumentException("Invalid part index");
    if (part.getStavesCount() != partStaves.size())
    	throw new IllegalArgumentException("Part has different number of staves than given");
    int partStavesCount = part.getStavesCount();
    
    //add the part
    PVector<Part> parts = this.parts.plus(partIndex, part);
    
    //add the given number of staves
    PVector<Staff> staves = this.staves;
    int startStaff = 0;
    for (int iPart = 0; iPart < partIndex; iPart++)
  	{
    	startStaff += parts.get(iPart).getStavesCount();
  	}
    staves = staves.plusAll(startStaff, partStaves);
    
    //shift the staff indexes of the parts and groups
    //beginning at the start index by the given number of staves
    PVector<BracketGroup> bracketGroups = this.bracketGroups;
    for (int i : range(bracketGroups))
    {
    	BracketGroup bracketGroup = bracketGroups.get(i);
    	if (bracketGroup.getStavesRange().getStartIndex() >= startStaff)
      {
    		bracketGroups.with(i, bracketGroup.shift(partStavesCount));
      }
      else if (bracketGroup.getStavesRange().getEndIndex() >= startStaff)
      {
      	bracketGroups.with(i, bracketGroup.shiftEnd(partStavesCount));
      }
    }
    PVector<BarlineGroup> barlineGroups = this.barlineGroups;
    for (int i : range(barlineGroups))
    {
    	BarlineGroup barlineGroup = barlineGroups.get(i);
    	if (barlineGroup.getStavesRange().getStartIndex() >= startStaff)
      {
    		barlineGroups.with(i, barlineGroup.shift(partStavesCount));
      }
      else if (barlineGroup.getStavesRange().getEndIndex() >= startStaff)
      {
      	barlineGroups.with(i, barlineGroup.shiftEnd(partStavesCount));
      }
    }
    
    return new StavesList(staves, parts, barlineGroups, bracketGroups);
  }
  
  
  /**
   * Adds a staff group for the given staves
   * with the given style. Since a staff may only
   * have one barline group, existing barline groups
   * at the given positions are removed.
   */
  public StavesList plusBarlineGroup(int startStaff, int endStaff,
    BarlineGroup.Style style)
  {
  	if (endStaff >= staves.size())
  		throw new IllegalArgumentException("staves out of range");
  	PVector<BarlineGroup> barlineGroups = this.barlineGroups;
  	
    //delete existing groups within the given range, or, if the
  	//given group is within an existing one, ignore it
  	//(we do not support nested barline groups)
    for (int i = 0; i < barlineGroups.size(); i++)
    {
    	BarlineGroup group = barlineGroups.get(i);
      if (group.getStavesRange().getEndIndex() >= startStaff &&
      	group.getStavesRange().getStartIndex() <= endStaff)
      {
      	barlineGroups = barlineGroups.minus(i);
        i--;
      }
      //ignore subgroups. then, this staves list remains untouched.
      if (startStaff >= group.getStavesRange().getStartIndex() && endStaff <=
      	group.getStavesRange().getEndIndex())
      {
      	return this;
      }
    }
    
    //add new group at the right position
    //(the barline groups are sorted by start index)
    int i = 0;
    while (i < barlineGroups.size() &&
    	barlineGroups.get(i).getStavesRange().getStartIndex() < startStaff)
    {
      i++;
    }
    barlineGroups = barlineGroups.plus(i,
    	new BarlineGroup(new StavesRange(startStaff, endStaff), style));
    
    return new StavesList(staves, parts, barlineGroups, bracketGroups);
  }
  
  
  /**
   * Adds a bracket group for the given staves with the given style.
   */
  public StavesList plusBracketGroup(int startStaff, int endStaff,
    BracketGroup.Style style)
  {
  	if (endStaff >= staves.size())
  		throw new IllegalArgumentException("staves out of range");
  	PVector<BracketGroup> bracketGroups = this.bracketGroups;
  	
    //add new group at the right position
    //(the bracket groups are sorted by start index)
    int i = 0;
    while (i < bracketGroups.size() &&
    	bracketGroups.get(i).getStavesRange().getStartIndex() > startStaff)
    {
      i++;
    }
    bracketGroups = bracketGroups.plus(i,
    	new BracketGroup(new StavesRange(startStaff, endStaff), style));
    
    return new StavesList(staves, parts, barlineGroups, bracketGroups);
  }
  
  
  /**
   * Adds the given number of empty measures at the end
   * of each staff.
   */
  public StavesList plusEmptyMeasures(int measuresCount)
  {
    PVector<Staff> staves = this.staves;
    for (int i : range(staves))
    {
    	staves = staves.with(i, staves.get(i).plusEmptyMeasures(measuresCount));
    }
    return new StavesList(staves, parts, barlineGroups, bracketGroups);
  }
  
  
  /**
   * Gets the measure of the given index of all staves.
   */
  public ArrayList<Measure> getMeasureColumn(int index)
  {
  	ArrayList<Measure> ret = new ArrayList<Measure>(staves.size());
    for (Staff staff : staves)
    {
    	ret.add(staff.getMeasures().get(index));
    }
    return ret;
  }
  
  
  /**
   * Gets the part the given staff belongs to.
   */
  public Part getPartByStaffIndex(int staffIndex)
  {
  	return stavesToParts[staffIndex];
  }
  
  
  /**
   * Gets the range of staff indices of the given part.
   */
  public Range getPartStaffIndices(Part part)
  {
  	return partStaffIndices.get(part);
  }
  
  
  /**
	 * Gets the barline group the given staff belongs to.
	 * If no group is found, null is returned.
	 */
	public BarlineGroup getBarlineGroupByStaff(int staffIndex)
	{
		for (BarlineGroup barlineGroup : barlineGroups)
    {
      if (barlineGroup.getStavesRange().contains(staffIndex))
        return barlineGroup;
    }
    return null;
	}
	
	
	/**
	 * Returns the number of Divisions of a quarter note. This is needed for
	 * Midi and MusicXML Export.
	 */
	public int computeDivisions()
	{
		int actualdivision = 4;
		for (Staff staff : staves)
		{
			for (Measure measure : staff.getMeasures())
			{
				for (Voice voice : measure.getVoices())
				{
					for (VoiceElement me : voice.getElements())
					{
						if (me.getDuration() != null)
						{
							actualdivision = MathTools.lcm(actualdivision, me.getDuration().getDenominator());
						}
					}
				}
			}
		}
		return actualdivision / 4;
	}
	
	
	/**
	 * Gets the {@link Staff} at the given {@link MP} (only the
	 * staff index is read).
	 */
	public Staff getStaff(MP mp)
	{
		return getStaff(mp.getStaff());
	}
	
	
	/**
	 * Gets the {@link Staff} at the given global index.
	 */
	public Staff getStaff(int staffIndex)
	{
		if (staffIndex >= 0 && staffIndex < staves.size())
		{
			return staves.get(staffIndex);
		}
		else
		{
			throw new IllegalMPException(mp(staffIndex, -1, -1, null));
		}
	}
	
	
	/**
   * Gets the staff of the given part with the given
   * part-internal index.
   */
  public Staff getStaff(int partIndex, int partStaffIndex)
  {
    return getStaff(getPartStaffIndices(parts.get(partIndex)).getStart() + partStaffIndex);
  }
  
  
  /**
	 * Gets the global index of the given {@link Staff}, or -1 if the staff
	 * is not part of this staves list.
	 */
	public int getStaffIndex(Staff staff)
	{
		return staves.indexOf(staff);
	}
	
	
	/**
	 * Gets the {@link Measure} with the given index at the staff with the given
	 * global index.
	 */
	public Measure getMeasure(MP mp)
	{
		return getStaff(mp).getMeasure(mp);
	}
	
	
	/**
	 * Gets the {@link Voice} at the given {@link MP} (beat is ignored).
	 */
	public Voice getVoice(MP mp)
	{
		return getMeasure(mp).getVoice(mp);
	}
  

	/**
   * Gets the filled beats for each measure column, that
   * means, the first beat in each column where there is no music
   * element following any more.
   */
  public Fraction[] getFilledBeats()
  {
  	int measuresCount = staves.getFirst().getMeasures().size();
  	Fraction[] ret = new Fraction[measuresCount];
  	for (int iMeasure = 0; iMeasure < measuresCount; iMeasure++)
  	{
	  	Fraction maxBeat = Fraction._0;
	  	for (Staff staff : staves)
	  	{
	  		Fraction beat = staff.getMeasures().get(iMeasure).getFilledBeats();
	  		if (beat.compareTo(maxBeat) > 0)
	  			maxBeat = beat;
	  	}
	  	ret[iMeasure] = maxBeat;
  	}
  	return ret;
  }
  
  
  /**
   * Sets the staff with the given index.
   */
  public StavesList withStaff(int index, Staff staff)
  {
  	PVector<Staff> staves = this.staves.with(index, staff);
  	return new StavesList(staves, parts, barlineGroups, bracketGroups);
  }
  
  
}
