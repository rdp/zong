package com.xenoage.zong.commands.help;

import java.net.MalformedURLException;
import java.net.URL;

import com.xenoage.zong.app.App;
import com.xenoage.zong.commands.Command;


/**
 * This command opens the Zong! Viewer website.
 *
 * @author Andreas Wenger
 */
public class BlogCommand
  extends Command
{

  @Override public void execute()
  {
  	//open blog.zong-music.com
		try
		{
			URL url = new URL("http://blog.zong-music.com");
			App.getInstance().openWebsite(url, true);
		}
		catch (MalformedURLException ex) {}
  }
	
}
