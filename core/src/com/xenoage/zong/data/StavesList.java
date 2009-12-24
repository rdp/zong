package com.xenoage.zong.data;

import com.xenoage.zong.data.music.BracketGroupStyle;
import com.xenoage.zong.data.music.Staff;
import com.xenoage.zong.data.music.barline.BarlineGroupStyle;
import com.xenoage.zong.data.music.util.MeasureColumn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A staves list manages the staves of a score
 * and all of its groups, like parts, bracket groups
 * and barline groups.
 *
 * @author Andreas Wenger
 */
public class StavesList
{
  
  protected Score parentScore;
  
  protected ArrayList<Staff> staves;
  protected ArrayList<Part> parts;
  protected ArrayList<BarlineGroup> barlineGroups;
  protected ArrayList<BracketGroup> bracketGroups;
  
  
  /**
   * Creates a new empty staves list.
   */
  public StavesList(Score parentScore)
  {
    this.parentScore = parentScore;
    staves = new ArrayList<Staff>();
    parts = new ArrayList<Part>(); 
    barlineGroups = new ArrayList<BarlineGroup>(); 
    bracketGroups = new ArrayList<BracketGroup>(); 
  }
  
  
  /**
   * Gets the parent score of this staves list.
   */
  protected Score getParentScore()
  {
    return parentScore;
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
  public List<Staff> getStaves()
  {
  	return Collections.unmodifiableList(staves);
  }
  
  
  /**
   * Gets the staff with the given index.
   */
  public Staff getStaff(int staffIndex)
  {
    if (staffIndex >= 0 && staffIndex < staves.size())
    {
      return staves.get(staffIndex);
    }
    return null;
  }
  
  
  /**
   * Gets the staff of the given part with the given
   * part-internal index.
   */
  public Staff getStaff(int partIndex, int partStaffIndex)
  {
    if (partIndex >= 0 && partIndex < parts.size())
    {
      return getStaff(getPartStartIndex(parts.get(partIndex)) + partStaffIndex);
    }
    return null;
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
   * Gets the list of parts.
   */
  public List<Part> getParts()
  {
    return Collections.unmodifiableList(parts);
  }
  
  
  /**
   * Gets the number of bracket groups.
   */
  public int getBracketGroupsCount()
  {
    return bracketGroups.size();
  }
  
  
  /**
   * Gets the bracket group with the given index.
   */
  public BracketGroup getBracketGroup(int index)
  {
    return bracketGroups.get(index);
  }
  
  
  /**
   * Adds the given part at the end of the other parts.
   */
  public void addPart(Part part, int measuresCount)
  {
    addPart(part, parts.size(), measuresCount);
  }
  
  
  /**
   * Adds a new part with the given number of staves
   * at the given position.
   * Each added staff will have the given number of
   * empty measures.
   */
  public void addPart(Part part, int partIndex, int measuresCount)
  {
    if (partIndex < 0)
      partIndex = 0;
    else if (partIndex > parts.size())
      partIndex = parts.size();
    
    //add the part
    parts.add(partIndex, part);
    
    //add the given number of staves
    int startStaff = getPartStartIndex(part);
    for (int i = 0; i < part.getStavesCount(); i++)
    {
      Staff staff = new Staff(parentScore);
      staff.addEmptyMeasures(measuresCount);
      staves.add(startStaff + i, staff);
    }
    
    //shift the staff indexes of the parts and groups
    //beginning at the start index by the given number of staves
    shiftAllPartsAndGroups(startStaff, part.getStavesCount());
    
    //TODO: add the default elements from score.getScoreHeader()
  }
  
  
  /**
   * Adds a new part using the given staves at the end of the other parts.
   */
  public void addPart(Part part, List<Staff> staves)
  {
    //add the part
    parts.add(part);
    
    //add the staves
    int startStaff = getPartStartIndex(part);
    for (int i = 0; i < part.getStavesCount(); i++)
    {
      this.staves.add(startStaff + i, staves.get(i));
    }
  }
  
  
  /**
   * Adds a barline group for the given staves
   * with the given style. Since a staff may only
   * have one barline group, existing barline groups
   * at the given positions are removed.
   */
  public void addBarlineGroup(int startStaff, int endStaff,
    BarlineGroupStyle style)
  {
  	if (endStaff >= staves.size())
  		throw new IllegalArgumentException("staves out of range");
    //delete existing groups within the given range, or, if the
  	//given group is within an existing one, ignore it
  	//(we do not support nested barline groups)
    for (int i = 0; i < barlineGroups.size(); i++)
    {
      BarlineGroup group = barlineGroups.get(i);
      if (group.startIndex >= startStaff && group.endIndex <= endStaff)
      {
        barlineGroups.remove(i);
        i--;
      }
      if (startStaff >= group.startIndex && endStaff <= group.endIndex)
      {
      	return; //ignore this subgroup
      }
    }
    //add new group at the right position
    //(the barline groups are sorted by start index)
    int i = 0;
    while (i < barlineGroups.size() && barlineGroups.get(i).startIndex < startStaff)
    {
      i++;
    }
    barlineGroups.add(i, new BarlineGroup(startStaff, endStaff, style));
  }
  
  
  /**
   * Adds a bracket group for the given staves
   * with the given style.
   */
  public void addBracketGroup(int startStaff, int endStaff,
    BracketGroupStyle style)
  {
  	if (endStaff >= staves.size())
  		throw new IllegalArgumentException("staves out of range");
    //add new group at the right position
    //(the bracket groups are sorted by start index)
    int i = 0;
    while (i < bracketGroups.size() && bracketGroups.get(i).startIndex > startStaff)
    {
      i++;
    }
    bracketGroups.add(i, new BracketGroup(startStaff, endStaff, style));
  }
  
  
  /**
   * Adds the given number of empty measures at the end
   * of each staff.
   */
  public void addEmptyMeasures(int measuresCount)
  {
    for (Staff staff : staves)
    {
      staff.addEmptyMeasures(measuresCount);
    }
  }
  
  
  /**
   * Gets the measure of the given index of all staves.
   * PERFORMANCE: don't create new list, but return an iterator over the
   * measures.
   */
  public MeasureColumn getMeasureColumn(int index)
  {
  	MeasureColumn ret = new MeasureColumn(staves.size());
    for (Staff staff : staves)
    {
    	ret.add(staff.getMeasures().get(index));
    }
    return ret;
  }
  
  
  /**
   * Gets the number of barline groups.
   */
  public int getBarlineGroupsCount()
  {
    return barlineGroups.size();
  }
  
  
  /**
   * Gets the barline group the given staff belongs to.
   * At the moment only the first registered group of
   * the staff is returned.
   * If no group is found, null is returned.
   */
  public BarlineGroup getBarlineGroup(int staffIndex)
  {
    for (BarlineGroup barlineGroup : barlineGroups)
    {
      if (barlineGroup.contains(staffIndex))
        return barlineGroup;
    }
    return null;
  }
  
  
  /**
   * Shifts the staff indices of all parts and groups
   * beginning at the given index by the given number of staves.
   */
  private void shiftAllPartsAndGroups(int index, int stavesCount)
  {
    for (BracketGroup bracketGroup: bracketGroups)
    {
      shiftGroup(bracketGroup, index, stavesCount);
    }
    for (BarlineGroup barlineGroup: barlineGroups)
    {
      shiftGroup(barlineGroup, index, stavesCount);
    }
  }
  
  
  /**
   * Shifts the staff indices of the given group
   * beginning at the given index by the given number of staves.
   */
  private void shiftGroup(StavesGroup group, int index, int stavesCount)
  {
    if (group.startIndex >= index)
    {
      group.startIndex += stavesCount;
      group.endIndex += stavesCount;
    }
    else if (group.endIndex >= index)
    {
      group.endIndex += stavesCount;
    }
  }
  
  
  /**
   * Gets the number of measures of the first staff.
   */
  public int getMeasuresCount()
  {
    if (staves.size() > 0)
      return staves.get(0).getMeasures().size();
    else
      return 0;
  }
  
  
  /**
   * Gets the start index of the given part.
   * TODO: performance? but should not be so bad, since we do not have
   * 1000 staves!
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
  

}
