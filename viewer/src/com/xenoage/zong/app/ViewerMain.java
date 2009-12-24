package com.xenoage.zong.app;


import javax.swing.JOptionPane;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.error.ErrorProcessing;


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
		    	new ErrorProcessing().report(ErrorLevel.Fatal, "Unknown program error.", error);
		    	JOptionPane.showMessageDialog(null, "Unknown program error"); //TODO
		    }
		  }
		});
  }

}
