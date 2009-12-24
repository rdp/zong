package com.xenoage.zong.musiclayout.layouter.scoreframelayout.util;

import java.util.ArrayList;

import com.xenoage.zong.musiclayout.stampings.StaffTextStamping;


/**
 * This class remembers the last stamped lyrics for each staff and each verse.
 * 
 * @author Andreas Wenger
 */
public class LastLyrics
{
	
	public ArrayList<ArrayList<StaffTextStamping>> lastLyrics = new ArrayList<ArrayList<StaffTextStamping>>();
	
	
	/**
	 * Returns the last stamped lyric within the given staff and verse, or null if unset.
	 */
	public StaffTextStamping get(int staff, int verse)
	{
		if (staff < lastLyrics.size())
		{
			ArrayList<StaffTextStamping> verses = lastLyrics.get(staff);
			if (verses != null && verse < verses.size())
			{
				return verses.get(verse);
			}
		}
		return null;
	}
	
	
	/**
	 * Sets the last stamped lyric within the given staff and verse, or null if unset.
	 */
	public void set(int staff, int verse, StaffTextStamping lyric)
	{
		//ensure that staff array is big enough
		while (staff >= lastLyrics.size())
		{
			lastLyrics.add(null);
		}
		//ensure that verse array is existing and big enough
		if (lastLyrics.get(staff) == null)
		{
			lastLyrics.set(staff, new ArrayList<StaffTextStamping>());
		}
		ArrayList<StaffTextStamping> verses = lastLyrics.get(staff);
		while (verse >= verses.size())
		{
			verses.add(null);
		}
		//set lyric
		verses.set(verse, lyric);
	}
	

}
