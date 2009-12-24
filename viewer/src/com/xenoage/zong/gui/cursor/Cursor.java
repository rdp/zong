package com.xenoage.zong.gui.cursor;


/**
 * Enumeration for all mouse cursors.
 * 
 * @author Andreas Wenger
 */
public enum Cursor
{

	/**
	 * Default cursor (arrow on Windows and Linux).
	 */
	Default(java.awt.Cursor.DEFAULT_CURSOR),

	/**
	 * Text cursor.
	 */
	Text(java.awt.Cursor.TEXT_CURSOR),
	
	/**
	 * Crosshair cursor.
	 */
	Crosshair(java.awt.Cursor.CROSSHAIR_CURSOR),
	
	/**
	 * Hand with index finger.
	 */
	IndexFinger(java.awt.Cursor.HAND_CURSOR),

	/**
	 * Open hand.
	 */
	OpenHand("data/img/cursor/hand-open.png"),

	/**
	 * Closed hand (when dragging).
	 */
	ClosedHand("data/img/cursor/hand-closed.png");
	
	
	private int awtCursorType = -1;
	private String filepath = null;
	
	
	private Cursor(int awtCursorType)
	{
		this.awtCursorType = awtCursorType;
	}
	
	
	private Cursor(String filepath)
	{
		this.filepath = filepath;
	}

	
	protected int getAWTCursorType()
	{
		return awtCursorType;
	}
	
	
	protected String getFilepath()
	{
		return filepath;
	}

}
