package com.xenoage.zong.layout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.xenoage.util.iterators.It;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Rectangle2f;
import com.xenoage.zong.data.event.ScoreChangedEvent;
import com.xenoage.zong.data.format.LayoutFormat;
import com.xenoage.zong.data.format.PageFormat;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.layout.frames.FrameHandle;
import com.xenoage.zong.layout.frames.FramePosition;
import com.xenoage.zong.layout.frames.ScoreFrame;
import com.xenoage.zong.layout.frames.ScoreFrameChain;
import com.xenoage.zong.layout.observer.LayoutObserver;
import com.xenoage.zong.musiclayout.ScoreFrameLayout;
import com.xenoage.zong.musiclayout.ScoreLayout;


/**
 * Class for the layout of a score document.
 * 
 * It consists of several pages, each containing several frames.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class Layout
{

	private ScoreDocument parentScoreDocument;

	private ArrayList<Page> pages = new ArrayList<Page>();

	private LayoutFormat format;

	// list of all objects observing this layout
	private ArrayList<LayoutObserver> observers = new ArrayList<LayoutObserver>();


	/**
	 * Initializes a new Layout. The parameter parent sets the ScoreDocument
	 * that contains this Layout. The page layout uses the default page format.
	 */
	public Layout(ScoreDocument parentScoreDocument)
	{
		this.parentScoreDocument = parentScoreDocument;
		this.format = new LayoutFormat();
	}


	/**
	 * Gets the parent score document.
	 */
	public ScoreDocument getParentScoreDocument()
	{
		return parentScoreDocument;
	}


	/**
	 * Gets the list of pages.
	 */
	public List<Page> getPages()
	{
		return pages;
	}


	/**
	 * Adds a new page with the given format to this layout and returns it.
	 */
	public Page addPage(PageFormat pageFormat)
	{
		Page page = new Page(pageFormat, this);
		pages.add(page);

		// notify the observers
		for (LayoutObserver observer : observers)
		{
			observer.pageAdded(page);
		}

		return page;
	}


	/**
	 * Adds a new page with the default format to this layout and returns it.
	 */
	public Page addPage()
	{
		PageFormat pageFormat = format.getPageFormat(pages.size());
		return addPage(pageFormat);
	}


	/**
	 * Transforms the given layout coordinates to a frame position, that is a
	 * reference to a frame and the position within that frame in mm. If there
	 * is no frame, null is returned.
	 */
	public FramePosition computeFramePosition(LayoutPosition layoutPosition)
	{
		if (layoutPosition == null)
			return null;
		Page page = pages.get(layoutPosition.getPageIndex());
		return page.computeFramePosition(layoutPosition.getPosition());
	}


	/**
	 * Call this method when the given score has been changed.
	 */
	public void scoreChanged(ScoreChangedEvent event)
	{
		for (Page page : pages)
		{
			page.scoreChanged(event);
		}
	}


	/**
	 * Gets a copy of the default format of this layout.
	 */
	public LayoutFormat getFormat()
	{
		return new LayoutFormat(format);
	}


	/**
	 * Sets the default format of this layout and applies the format to all
	 * pages.
	 */
	public void setFormat(LayoutFormat format)
	{
		this.format = format;
		// apply to all pages
		for (int i = 0; i < pages.size(); i++)
		{
			// TODO: this not not undoable...
			pages.get(i).setFormat(format.getPageFormat(i));
		}
	}


	/**
	 * Computes the adjustment handle at the given position and returns it. If
	 * there is none, null is returned.
	 * @param layoutPosition the position where to look for a handle
	 * @param scaling the current scaling factor
	 */
	public FrameHandle computeFrameHandleAt(LayoutPosition layoutPosition, float scaling)
	{
		if (layoutPosition == null)
			return null;
		// find handle on the given page
		FrameHandle handle = null;
		Page givenPage = pages.get(layoutPosition.getPageIndex());
		handle = givenPage.computeFrameHandleAt(layoutPosition.getPosition(), scaling);
		return handle;
	}


	/**
	 * Gets a LinkedList of the selected frames on this layout
	 */
	public LinkedList<Frame> getSelectedFrames()
	{
		LinkedList<Frame> frames = new LinkedList<Frame>();
		for (Page page : pages)
		{
			for (Frame frame : page.getFrames())
			{
				if (frame.isSelected())
				{
					frames.add(frame);
				}
			}
		}
		return frames;
	}


	/**
	 * Adds an observer to this page layout, that gets notified when something
	 * is changed.
	 */
	public void addObserver(LayoutObserver observer)
	{
		observers.add(observer);
	}


	/**
	 * This method is called from a page when its size has been changed.
	 */
	public void pageFormatChanged(Page page)
	{
		// notify the observers
		for (LayoutObserver observer : observers)
		{
			observer.pageFormatChanged(page);
		}
	}


	/**
	 * Gets the axis-aligned bounding rectangle of the given system, specified
	 * by its {@link ScoreLayout}, the index of the frame and the index of the
	 * system (relative to the frame) in page coordinates together with the
	 * index of the page. If not found, null is returned.
	 */
	public Tuple2<Integer, Rectangle2f> computeSystemBoundingRect(
		ScoreLayout scoreLayout, int frameIndex, int systemIndex)
	{
		// TODO: performance ok? (seldom called)
		// find the page and the frame
		ScoreFrameLayout searchedLayout = scoreLayout.getScoreFrameLayouts().get(
			frameIndex);
		for (int iPage = 0; iPage < pages.size(); iPage++)
		{
			Page page = pages.get(iPage);
			for (Frame frame : page.getFrames())
			{
				if (frame instanceof ScoreFrame)
				{
					ScoreFrame scoreFrame = (ScoreFrame) frame;
					if (scoreFrame.getLayout() == searchedLayout)
					{
						// frame found
						// get system boundaries in mm
						Rectangle2f rectMm = searchedLayout
							.computeSystemBoundaries(systemIndex);
						if (rectMm == null)
							return null;
						// compute corner points (because frame may be rotated)
						// and transform them
						float x = rectMm.position.x - frame.getSize().width / 2;
						float y = rectMm.position.y - frame.getSize().height / 2;
						float w = rectMm.size.width;
						float h = rectMm.size.height;
						Point2f nw = frame.computePagePosition(new Point2f(x, y));
						Point2f ne = frame.computePagePosition(new Point2f(x + w, y));
						Point2f se = frame.computePagePosition(new Point2f(x + w, y + h));
						Point2f sw = frame.computePagePosition(new Point2f(x, y + h));
						// compute axis-aligned bounding box and return it
						Rectangle2f ret = new Rectangle2f(nw.x, nw.y, 0, 0);
						ret = ret.extend(ne);
						ret = ret.extend(se);
						ret = ret.extend(sw);
						return new Tuple2<Integer, Rectangle2f>(iPage, ret);
					}
				}
			}
		}
		// not found
		return null;
	}


	/**
	 * Gets all {@link ScoreFrameChain}s of the Layout
	 * @return
	 */
	// TODO TIDY!!!
	public ArrayList<ScoreFrameChain> getScoreFrameChains()
	{
		ArrayList<ScoreFrameChain> chains = new ArrayList<ScoreFrameChain>();
		for (Page page : pages)
		{
			It<Frame> frames = page.getFrames();
			for (Frame frame : frames)
			{
				if (frame instanceof ScoreFrame)
				{
					ScoreFrame scoreFrame = (ScoreFrame)frame;
					ScoreFrameChain chain = scoreFrame.getScoreFrameChain();
					if (!chains.contains(chain))
					{
						chains.add(chain);
					}
				}
			}
		}
		return chains;
	}

}
