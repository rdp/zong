package com.xenoage.zong.gui.score;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.xenoage.util.math.Point2i;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * This class manages the waiting and open {@link GUITooltip}s
 * of a {@link GUIManager}.
 * 
 * @author Andreas Wenger
 */
public class TooltipManager
{
	
	private static final int waitingTimeNormalMs = 800;
	private static final int waitingTimeBusyMs = 100;
	private static final int busyTime = 500;
	
	private int waitingTimeMs = waitingTimeNormalMs;
	private Long lastTimeWithActiveTooltip = 0l;
	
	//list of waiting tooltips, together with the first request time
	private HashMap<GUITooltip, Long> waiting = new HashMap<GUITooltip, Long>();
	
	//list of open tooltips
	private LinkedList<GUITooltip> open = new LinkedList<GUITooltip>();
	
	private GUIManager guiManager;
	
	
	public TooltipManager(GUIManager guiManager)
	{
		this.guiManager = guiManager;
	}
	
	
	/**
	 * Registers the tooltip as waiting.
	 */
	public void registerTooltip(GUITooltip tooltip)
	{
		if (!waiting.containsKey(tooltip) && !open.contains(tooltip))
		{
			waiting.put(tooltip, System.currentTimeMillis());
			guiManager.requestRepaintsForTime(waitingTimeMs + 500); //+ 500 additional ms
		}
	}
	
	
	/**
	 * Unregisters the given tooltip or does nothing, if
	 * the given tooltip is not waiting.
	 */
	public void unregisterTooltip(GUITooltip tooltip)
	{
		waiting.remove(tooltip);
		if (open.contains(tooltip))
		{
			tooltip.die();
		}
	}
	
	
	/**
	 * Paints the tooltips and activates the waiting tooltips whose
	 * waiting time is over.
	 */
	public void paint(GLGraphicsContext context)
  {
		long time = System.currentTimeMillis();
  	//activate tooltips
		Iterator<GUITooltip> waitingIt = waiting.keySet().iterator();
  	while (waitingIt.hasNext())
  	{
  		GUITooltip t = waitingIt.next();
  		if (waiting.get(t) + waitingTimeMs < time)
  		{
  			waitingIt.remove();
  			t.revive();
  			open.add(t);
  		}
  	}
  	//paint open tooltips
  	for (GUITooltip t : open)
  	{
  		context.getGL().glPushMatrix();
  		if (t.getParent() != null)
  		{
  			Point2i absPos = t.getParent().getAbsolutePosition();
  			context.getGL().glTranslatef(absPos.x, absPos.y, 0);
  		}
  		t.paint(context);
  		context.getGL().glPopMatrix();
  	}
  	//if a tooltip is open, the activation time for another tooltip is
  	//less than normal
  	if (open.size() > 0)
  	{
  		lastTimeWithActiveTooltip = time;
  	}
  	this.waitingTimeMs = (time - lastTimeWithActiveTooltip < busyTime ?
  		waitingTimeBusyMs : waitingTimeNormalMs);
  	//remove dead tooltips
  	Iterator<GUITooltip> openIt = open.iterator();
  	while (openIt.hasNext())
  	{
  		GUITooltip t = openIt.next();
  		if (t.isDead())
  		{
  			openIt.remove();
  		}
  	}
  }
	

}
