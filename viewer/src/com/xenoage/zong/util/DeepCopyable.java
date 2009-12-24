package com.xenoage.zong.util;


/**
 * Interface for all classes for which deep copies
 * can be created.
 * 
 * Deep copy means, that all children are deeply copied
 * too instead of reusing references to them.
 * 
 * @deprecated Not used, because classes with backward
 * linking (e.g. a voice has a parent measure) need
 * additional parameters for deepCopy().
 * 
 * @author Andreas Wenger
 */
public interface DeepCopyable<T>
{
	
	/**
	 * Returns a deep copy of this object.
	 */
	public T deepCopy();

}
