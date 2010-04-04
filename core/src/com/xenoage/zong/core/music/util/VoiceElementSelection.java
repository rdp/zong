package com.xenoage.zong.core.music.util;

import com.xenoage.zong.core.music.VoiceElement;


/**
 * This enumeration is used to filter a single element
 * when searching for {@link VoiceElement}s.
 * 
 * @author Andreas Wenger
 */
public enum VoiceElementSelection
{
	
	/**
	 * The first element (at the lowest beat) that matches a condition.
	 */
	First,
	
	/**
	 * The last element (at the highest beat) that matches a condition.
	 */
	Last;
	
}
