package com.xenoage.zong.view;

import com.xenoage.util.math.Point2f;
import com.xenoage.zong.app.App;


/**
 * {@link Thread} for smooth zooming and scrolling in the
 * assigned {@link View}.
 * 
 * The thread is always running, trying to update the
 * current zoom and scroll position of the view to the
 * target zoom and target scroll position.
 * 
 * @author Andreas Wenger
 */
public class ViewZoomThread
	extends Thread
{
	
	private final View view;
	
	//zoom
	private final float zoomTime = 300f;
	private float targetZoom = 0;
	private float beginZoom = 0;
	private long beginZoomTime = 0;
	private boolean zooming;
	
	//scroll
	private final float scrollTime = 300f;
	private Point2f beginScroll = null;
	private long beginScrollTime = 0;
	private boolean scrolling;
	
	
	public ViewZoomThread(View view)
	{
		this.view = view;
		this.targetZoom = view.targetZoom;
		this.beginZoom = this.targetZoom;
	}
	
	
	@Override public void run()
	{
		while (!isInterrupted())
		{
			long nowTime = System.currentTimeMillis();
			zoom(nowTime);
			scroll(nowTime);
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
		App.getInstance().showMessageDialog("ViewZoomThread ends");
	}


	private void zoom(long nowTime)
	{
		//every time the target zoom has changed, use 200 ms to
		//zoom to the desired level
		if (view.targetZoom != targetZoom)
		{
			targetZoom = view.targetZoom;
			beginZoom = view.currentZoom;
			beginZoomTime = nowTime;
			zooming = true;
		}
		//if neccessary, zoom
		if (zooming)
		{
			float timediff = nowTime - beginZoomTime;
			if (timediff < zoomTime)
			{
				view.setCurrentZoom(beginZoom + (timediff / zoomTime) * (targetZoom - beginZoom));
			}
			else
			{
				view.setCurrentZoom(targetZoom);
				zooming = false;
			}
		}
	}
	
	
	private void scroll(long nowTime)
	{
		//when a target scroll position is set and smooth scrolling is not
		//done at this moment, begin a new smooth scroll.
		//TODO: when a smooth scroll is interrupted by another smooth scroll,
		//currently there is a jump
		Point2f targetPosition = view.getTargetScrollPosition();
		if (!scrolling && targetPosition != null)
		{
			beginScroll = view.getScrollPosition();
			beginScrollTime = nowTime;
			scrolling = true;
		}
		//if neccessary, scroll
		if (scrolling)
		{
			float timediff = nowTime - beginScrollTime;
			float t = (timediff / scrollTime); //between 0 and 1
			if (timediff < scrollTime)
			{
				view.setCurrentScrollPosition(new Point2f(
					beginScroll.x + t * (targetPosition.x - beginScroll.x),
					beginScroll.y + t * (targetPosition.y - beginScroll.y)));
			}
			else
			{
				view.setScrollPosition(targetPosition); //end smooth scrolling
				scrolling = false;
			}
		}
	}



}
