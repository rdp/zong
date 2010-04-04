package com.xenoage.zong.core.music.chord;


/**
 * Articulation like staccato or tenuto.
 * 
 * @author Andreas Wenger
 */
public final class Articulation
{
	
	public enum Type
	{
		Accent,
		Staccato,
		Staccatissimo,
		StrongAccent,
		Tenuto
	}
	
	private final Type type;
	
	
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
