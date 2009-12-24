package com.xenoage.zong.util;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.xenoage.util.io.IO;


/**
 * Useful methods for handling with images.
 * 
 * @author Andreas Wenger
 */
public class ImageTools
{
	
	
	/**
	 * Returns an {@link ImageIcon} with the image at the given path.
	 * If loading fails, return null. Failing is not even logged.
	 */
	public static ImageIcon tryToLoadIcon(String path)
	{
		try
		{
			return new ImageIcon(ImageIO.read(IO.openDataFile(path)));
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	

}
