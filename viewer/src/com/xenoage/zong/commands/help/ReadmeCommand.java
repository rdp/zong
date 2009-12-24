package com.xenoage.zong.commands.help;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.xenoage.zong.commands.Command;


/**
 * This command opens the readme.txt file.
 *
 * @author Andreas Wenger
 */
public class ReadmeCommand
  extends Command
{

  @Override public void execute()
  {
  	//open readme.txt
		try
		{
			Desktop.getDesktop().open(new File("readme.txt"));
		}
		catch (IOException ex)
		{
		}
  }
	
}
