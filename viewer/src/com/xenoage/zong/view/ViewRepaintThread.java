package com.xenoage.zong.view;

import com.xenoage.zong.app.App;


/**
 * This thread repaints the assigned {@link View} when requested.
 * 
 * It is useful to avoid a congestion of repaint-calls,
 * since this thread takes care that obsolete repaint requests
 * are ignored.
 * 
 * @author Andreas Wenger
 */
public class ViewRepaintThread
	extends Thread
{
	
	private final View view;
	
	//true for a single repaint request
	private boolean request = false;
	
	//time up to which continuous repaints are requested
	private long requestEndTime = 0;
	
	
	public ViewRepaintThread(View view)
	{
		this.view = view;
	}
	
	
	@Override public void run()
	{
		while(!isInterrupted())
		{
			if (request || System.currentTimeMillis() < requestEndTime)
			{
				request = false;
				view.doRepaint();
			}
			try
			{
				Thread.sleep(10); //~100 FPS should be enough for everyone
			}
			catch (InterruptedException e)
			{
			}
		}
		
		//TEST
		//TODO: never reached!
		App.getInstance().showMessageDialog("ViewRepaintThread ends");
	}
	
	
	/**
	 * Requests a single repaint at the next occasion.
	 */
	public void requestRepaint()
	{
		request = true;
	}
	
	
	/**
	 * Requests continuous repaints for the given time in ms.  
	 */
	public void requestRepaintsForTime(int ms)
	{
		requestEndTime = System.currentTimeMillis() + ms;
	}
	

}
