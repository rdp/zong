package com.xenoage.zong.core.music;

import static com.xenoage.util.Range.range;
import static com.xenoage.zong.core.music.MP.mp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.MathTools;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.barline.BarlineGroupStyle;
import com.xenoage.zong.core.music.bracket.BracketGroupStyle;
import com.xenoage.zong.util.exceptions.IllegalMPException;


/**
 * A {@link StavesList} manages the staves of a score
 * and all of its groups, like parts, bracket groups
 * and barline groups.
 *
 * @author Andreas Wenger
 */
public final class StavesList
{
  
  private final PVector<Staff> staves;
  private final PVector<Part> parts;
  private final PVector<StavesGroup<BarlineGroupStyle>> barlineGroups;
  private final PVector<StavesGroup<BracketGroupStyle>> bracketGroups;
  
  private static final StavesList empty =
  	new StavesList(new PVector<Staff>(), new PVector<Part>(),
  		new PVector<StavesGroup<BarlineGroupStyle>>(),
  		new PVector<StavesGroup<BracketGroupStyle>>());
  
  
  /**
   * Creates a new {@link StavesList}.
   */
  public StavesList(PVector<Staff> staves, PVector<Part> parts,
  	PVector<StavesGroup<BarlineGroupStyle>> barlineGroups,
  	PVector<StavesGroup<BracketGroupStyle>> bracketGroups)
  {
    this.staves = staves;
    this.parts = parts;
    this.barlineGroups = barlineGroups;
    this.bracketGroups = bracketGroups;
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
  public PVector<StavesGroup<BracketGroupStyle>> getBracketGroups()
  {
    return bracketGroups;
  }
  
  
  /**
   * Gets the barline groups.
   */
  public PVector<StavesGroup<BarlineGroupStyle>> getBarlineGroups()
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
    PVector<StavesGroup<BracketGroupStyle>> bracketGroups = this.bracketGroups;
    for (int i : range(bracketGroups))
    {
    	StavesGroup<BracketGroupStyle> bracketGroup = bracketGroups.get(i);
    	if (bracketGroup.getStartIndex() >= startStaff)
      {
    		bracketGroups.with(i, bracketGroup.shift(partStavesCount));
      }
      else if (bracketGroup.getEndIndex() >= startStaff)
      {
      	bracketGroups.with(i, bracketGroup.shiftEnd(partStavesCount));
      }
    }
    PVector<StavesGroup<BarlineGroupStyle>> barlineGroups = this.barlineGroups;
    for (int i : range(barlineGroups))
    {
    	StavesGroup<BarlineGroupStyle> barlineGroup = barlineGroups.get(i);
    	if (barlineGroup.getStartIndex() >= startStaff)
      {
    		barlineGroups.with(i, barlineGroup.shift(partStavesCount));
      }
      else if (barlineGroup.getEndIndex() >= startStaff)
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
    BarlineGroupStyle style)
  {
  	if (endStaff >= staves.size())
  		throw new IllegalArgumentException("staves out of range");
  	PVector<StavesGroup<BarlineGroupStyle>> barlineGroups = this.barlineGroups;
  	
    //delete existing groups within the given range, or, if the
  	//given group is within an existing one, ignore it
  	//(we do not support nested barline groups)
    for (int i = 0; i < barlineGroups.size(); i++)
    {
    	StavesGroup<BarlineGroupStyle> group = barlineGroups.get(i);
      if (group.getEndIndex() >= startStaff && group.getStartIndex() <= endStaff)
      {
      	barlineGroups = barlineGroups.minus(i);
        i--;
      }
      //ignore subgroups. then, this staves list remains untouched.
      if (startStaff >= group.getStartIndex() && endStaff <= group.getEndIndex())
      {
      	return this;
      }
    }
    
    //add new group at the right position
    //(the barline groups are sorted by start index)
    int i = 0;
    while (i < barlineGroups.size() && barlineGroups.get(i).getStartIndex() < startStaff)
    {
      i++;
    }
    barlineGroups = barlineGroups.plus(i,
    	new StavesGroup<BarlineGroupStyle>(startStaff, endStaff, style));
    
    return new StavesList(staves, parts, barlineGroups, bracketGroups);
  }
  
  
  /**
   * Adds a bracket group for the given staves with the given style.
   */
  public StavesList plusBracketGroup(int startStaff, int endStaff,
    BracketGroupStyle style)
  {
  	if (endStaff >= staves.size())
  		throw new IllegalArgumentException("staves out of range");
  	PVector<StavesGroup<BracketGroupStyle>> bracketGroups = this.bracketGroups;
  	
    //add new group at the right position
    //(the bracket groups are sorted by start index)
    int i = 0;
    while (i < bracketGroups.size() && bracketGroups.get(i).getStartIndex() > startStaff)
    {
      i++;
    }
    bracketGroups = bracketGroups.plus(i,
    	new StavesGroup<BracketGroupStyle>(startStaff, endStaff, style));
    
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
   * Gets the start index of the given part.
   */
  public int getPartStartIndex(Part part)
  {
  	int ret = 0;
  	for (Part p : parts)
  	{
  		if (part == p) return ret;
  		ret += p.getStavesCount();
  	}
  	throw new IllegalArgumentException("Unknown part");
  }
  
  
  /**
   * Gets the end index of the given part.
   */
  public int getPartEndIndex(Part part)
  {
  	return getPartStartIndex(part) + part.getStavesCount() - 1;
  }
  
  
  /**
	 * Gets the barline group the given staff belongs to.
	 * If no group is found, null is returned.
	 */
	public StavesGroup<BarlineGroupStyle> getBarlineGroup(int staffIndex)
	{
		for (StavesGroup<BarlineGroupStyle> barlineGroup : barlineGroups)
    {
      if (barlineGroup.contains(staffIndex))
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
    return getStaff(getPartStartIndex(parts.get(partIndex)) + partStaffIndex);
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
