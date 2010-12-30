package com.xenoage.zong.app;


import javax.swing.JOptionPane;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.error.ErrorProcessing;
import com.xenoage.util.exceptions.ThrowableTools;
import com.xenoage.zong.Zong;


/**
 * Start class of the Zong! Viewer desktop application.
 *
 * @author Andreas Wenger
 */
public class ViewerMain
{
	
	private static String[] args;
	

  /**
   * Starts the application.
   */
  public static void main(String[] args)
  {
		ViewerMain.args = args;
		//schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
		  public void run()
		  {
		  	try
		  	{
		  		new ViewerApplication(ViewerMain.args);
		  	}
		    catch (Throwable error)
		    {
		      //here all uncaught exceptions and errors are caught.
		    	try
		    	{
		    		new ErrorProcessing().report(ErrorLevel.Fatal, "Unknown program error.", error);
		    	}
		    	catch (Throwable error2)
		    	{
		    		//ignore (we can not do something)
		    	}
		    	if (JOptionPane.showConfirmDialog(null, "Unknown program error!\nShow stack trace?",
		    		Zong.PROJECT_FAMILY_NAME, JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION)
		    	{
		    		String stackTrace = ThrowableTools.getStackTrace(error);
		    		if (stackTrace.length() > 2000)
		    			stackTrace = stackTrace.substring(0, 2000) + "...";
		    		JOptionPane.showMessageDialog(null, stackTrace);
		    	}
		    }
		  }
		});
  }

}
