/**
 * 
 */
package com.xenoage.zong.data.instrument;

/**
 * This Class is necessary for the class Instrument to save the Staffs of an Instrument.
 * 
 * @author Uli
 *
 */
public class InstrumentStaff
{

	private String clef;
	private int lines;
	private String bottomline;
	
	/**
	 * @return the bottomline
	 */
	public String getBottomline()
	{
		return bottomline;
	}
	
	/**
	 * @param bottomline the bottomline to set
	 */
	public void setBottomline(String bottomline)
	{
		this.bottomline = bottomline;
	}
	
	/**
	 * @return the lines
	 */
	public int getLines()
	{
		return lines;
	}
	
	/**
	 * @param lines the lines to set
	 */
	public void setLines(int lines)
	{
		this.lines = lines;
	}
	
	/**
	 * @return the clef
	 */
	public String getClef()
	{
		return clef;
	}
	
	/**
	 * @param clef the clef to set
	 */
	public void setClef(String clef)
	{
		this.clef = clef;
	}
}
