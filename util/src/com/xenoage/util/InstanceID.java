package com.xenoage.util;


/**
 * Helper class for hashmapping.
 * 
 * If you want to use some class as a key in a hash table,
 * but do not want to override the equals method (because you
 * want to use it for testing equal content, not equal reference),
 * use a instance of this class as a class member and
 * use it for the key.
 * 
 * @author Andreas Wenger
 */
public class InstanceID
{
	
	//since this class has no content, its hashCode will be
	//a unique number and the equals method will test for
	//reference equality.
	//this is ideal for use in hash tables.
	
	
	/*/TEST
	public InstanceID()
	{
		System.out.println(hashCode());
	} //*/

}
