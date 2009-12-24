package com.xenoage.zong.musiclayout.layouter.scoreframelayout.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.xenoage.zong.musiclayout.ScoreFrameLayout;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;


/**
 * The staff stampings on a {@link ScoreFrameLayout}.
 * 
 * @author Andreas Wenger
 */
public class StaffStampings
{
	
	//lists of staves, if we are interested in a certain score staff: staves[globalstaffindex][systemindex]
	private List<List<StaffStamping>> stavesByStaff;
	//lists of staves, if we are interested in a certain system: staves[systemindex][globalstaffindex]
	private List<List<StaffStamping>> stavesBySystem;
	
	
	/**
	 * Creates a new list of staff stampings for the given number of
	 * systems and staves per system.
	 */
	public StaffStampings(int systemsCount, int stavesCount)
	{
		stavesByStaff = new ArrayList<List<StaffStamping>>(stavesCount);
		for (int i = 0; i < stavesCount; i++)
		{
			stavesByStaff.add(Arrays.asList(new StaffStamping[systemsCount]));
		}
		stavesBySystem = new ArrayList<List<StaffStamping>>(systemsCount);
		for (int i = 0; i < systemsCount; i++)
		{
			stavesBySystem.add(Arrays.asList(new StaffStamping[stavesCount]));
		}
	}
	
	
	/**
	 * Gets the staff stamping for the given global staff index and
	 * system index, or null if unset.
	 */
	public StaffStamping get(int system, int staff)
	{
		return stavesByStaff.get(staff).get(system);
	}
	
	
	/**
	 * Sets the staff stamping for the given global staff index and
	 * system index.
	 */
	public void set(int system, int staff, StaffStamping staffStamping)
	{
		stavesByStaff.get(staff).set(system, staffStamping);
		stavesBySystem.get(system).set(staff, staffStamping);
	}
	
	
	/**
	 * Gets the staves of the given global staff index (one per system).
	 */
	public List<StaffStamping> getAllOfStaff(int staff)
	{
		return stavesByStaff.get(staff);
	}
	
	
	/**
	 * Gets the staves of the given system.
	 */
	public List<StaffStamping> getAllOfSystem(int system)
	{
		return stavesBySystem.get(system);
	}
	
	
	/**
	 * Adds all staves to the given list, system after system.
	 */
	public void addAllTo(List<StaffStamping> list)
	{
		for (int i = 0; i < stavesBySystem.size(); i++)
			list.addAll(stavesBySystem.get(i));
	}
	

}
