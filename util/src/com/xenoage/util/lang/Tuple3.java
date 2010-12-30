package com.xenoage.util.lang;


/**
 * This class combines three instances of three given classes
 * to one instance.
 * 
 * This is especially useful for multiple return values.
 * 
 * @author Andreas Wenger
 */
public class Tuple3<T1, T2, T3>
{
	
	private final T1 e1;
	private final T2 e2;
	private final T3 e3;
	
	
	public Tuple3(T1 e1, T2 e2, T3 e3)
	{
		this.e1 = e1;
		this.e2 = e2;
		this.e3 = e3;
	}
	
	
	public static <P1, P2, P3> Tuple3<P1, P2, P3> t3(P1 e1, P2 e2, P3 e3)
	{
		return new Tuple3<P1, P2, P3>(e1, e2, e3);
	}
	
	
	public T1 get1()
	{
		return e1;
	}
	
	
	public T2 get2()
	{
		return e2;
	}
	
	
	public T3 get3()
	{
		return e3;
	}
	

}
