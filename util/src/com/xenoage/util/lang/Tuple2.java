package com.xenoage.util.lang;


/**
 * This class combines two instances of two given classes
 * to one instance.
 * 
 * This is especially useful for multiple return values.
 * 
 * @author Andreas Wenger
 */
public final class Tuple2<T1, T2>
{
	
	private final T1 e1;
	private final T2 e2;
	
	
	public Tuple2(T1 e1, T2 e2)
	{
		this.e1 = e1;
		this.e2 = e2;
	}
	
	
	public static <T1, T2> Tuple2<T1, T2> t(T1 e1, T2 e2)
	{
		return new Tuple2<T1, T2>(e1, e2);
	}
	
	
	public T1 get1()
	{
		return e1;
	}
	
	
	public T2 get2()
	{
		return e2;
	}
	

}
