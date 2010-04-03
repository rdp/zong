package com.xenoage.zong.gui.cursor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.zong.app.App;


/**
 * Manager for mouse cursors.
 * 
 * @author Andreas Wenger
 */
public class CursorManager
{
	
	private static CursorManager instance = null;
	
	private HashMap<Cursor, java.awt.Cursor> cursors = new HashMap<Cursor, java.awt.Cursor>();
	
	
	/**
	 * Initializes the manager and loads custom cursors.
	 */
	private CursorManager()
	{
		//load cursors
		Dimension cursorSize = Toolkit.getDefaultToolkit().getBestCursorSize(16, 16);
		for (Cursor cursor : Cursor.values())
		{
			java.awt.Cursor awtCursor = null;
			if (cursor.getAWTCursorType() != -1)
			{
				awtCursor = new java.awt.Cursor(cursor.getAWTCursorType());
			}
			else
			{
				String filepath = cursor.getFilepath();
				try
				{
					BufferedImage img = new BufferedImage(
						cursorSize.width, cursorSize.height, BufferedImage.TYPE_INT_ARGB);
					BufferedImage imgRaw = ImageIO.read(com.xenoage.util.io.IO.openInputStream(filepath));
					Graphics2D imgGr = img.createGraphics();
					imgGr.drawImage(imgRaw, img.getWidth() / 2 - imgRaw.getWidth() / 2,
						img.getHeight() / 2 - imgRaw.getHeight() / 2, null);
					imgGr.dispose();
					awtCursor = Toolkit.getDefaultToolkit().createCustomCursor(img,
					  new Point(img.getWidth() / 2, img.getHeight() / 2), cursor.toString());
				}
				catch (IOException ex)
				{
					App.err().report(ErrorLevel.Remark, "Can not read cursor file: \"" + filepath + "\"", ex);
					awtCursor = java.awt.Cursor.getDefaultCursor();
					//TEST
					ex.printStackTrace();
				}
			}
			cursors.put(cursor, awtCursor);
		}
	}
	
	
	public static CursorManager getInstance()
	{
		if (instance == null)
		{
			instance = new CursorManager();
		}
		return instance;
	}
	
	
	public void setCursor(Cursor cursor, Component component)
	{
		component.setCursor(cursors.get(cursor));
	}
	

}
