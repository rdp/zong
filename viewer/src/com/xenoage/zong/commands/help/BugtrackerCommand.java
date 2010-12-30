package com.xenoage.zong.commands.help;

import java.net.MalformedURLException;
import java.net.URL;

import com.xenoage.zong.Zong;
import com.xenoage.zong.app.App;
import com.xenoage.zong.commands.Command;


/**
 * This command opens the bugtracker website.
 *
 * @author Andreas Wenger
 */
public class BugtrackerCommand
  extends Command
{

  @Override public void execute()
  {
  	//open www.xenoage.com/zongviewer.html?version=<iteration>
		try
		{
			URL url = new URL("http://tracker.zong-music.com");
			App.getInstance().openWebsite(url, true);
		}
		catch (MalformedURLException ex) {}
  }
	
}
