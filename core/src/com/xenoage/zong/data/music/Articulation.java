package com.xenoage.zong.data.music;


/**
 * Articulation like staccato or tenuto.
 * 
 * @author Andreas Wenger
 */
public class Articulation
{
	
	public enum Type
	{
		Accent,
		Staccato,
		Staccatissimo,
		StrongAccent,
		Tenuto
	}
	
	private Type type;
	
	
	/**
	 * Creates a new {@link Articulation} with the given {@link Type}.
	 */
	public Articulation(Type type)
	{
		this.type = type;
	}
	
	
	/**
	 * Gets the {@link Type} of this articulation.
	 */
	public Type getType()
	{
		return type;
	}
	

}
