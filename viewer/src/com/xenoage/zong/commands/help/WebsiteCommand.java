package com.xenoage.zong.commands.help;

import java.net.MalformedURLException;
import java.net.URL;

import com.xenoage.zong.Zong;
import com.xenoage.zong.app.App;
import com.xenoage.zong.commands.Command;


/**
 * This command opens the Zong! Viewer website.
 *
 * @author Andreas Wenger
 */
public class WebsiteCommand
  extends Command
{

  @Override public void execute()
  {
  	//open www.xenoage.com/zongviewer.html?version=<iteration>
		try
		{
			URL url = new URL("http://www.xenoage.com/zongviewer.html?version=" + Zong.PROJECT_ITERATION);
			App.getInstance().openWebsite(url, true);
		}
		catch (MalformedURLException ex) {}
  }
	
}
