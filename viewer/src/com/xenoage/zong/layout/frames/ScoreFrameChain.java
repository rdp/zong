package com.xenoage.zong.layout.frames;

import java.util.ArrayList;
import java.util.List;

import com.xenoage.util.Range;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.app.App;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.io.score.ScoreInput;
import com.xenoage.zong.musiclayout.ScoreLayout;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouter;


/**
 * A list of connected score frames. It also manages the layouter of the frames.
 * 
 * This class is also used for unconnected, single score frames, because it
 * manages the layouter.
 * 
 * A chain can be "limited", that means, the layouter is requested to only compute
 * the layout of the score up to the last existing score frame.
 * On the other hand, it may be "complete", which means,
 * if the really existing frames are not enough, the layouter is requested
 * to call the <code>addAdditionalFrame</code> method for each additional
 * needed frame, using the size returned by <code>getAdditionalFrameSize</code>.
 * Before the layouting process, <code>clearAdditionalFrames</code> has to be
 * called.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class ScoreFrameChain
{

	//the list of really existing frames
	private ArrayList<ScoreFrame> frames = new ArrayList<ScoreFrame>();
	
	//the list of additional frames, of the really existing frames don't have
	//enough space for the score. only used if isCompleteChain is true.
	private boolean isCompleteChain = false;
	private ArrayList<ScoreFrame> additionalFrames = new ArrayList<ScoreFrame>();
	private Size2f additionalFramesSize = null;
	
	private Score score;
	private ScoreLayouter layouter = null;


	/**
	 * Creates a new list of conntected score frames, showing the given score,
	 * beginning with the given frame. If the frames don't have enough space for
	 * the whole score, the layouter will simply stop after the last frame.
	 */
	public static ScoreFrameChain createLimitedChain(Score score, ScoreFrame firstFrame)
	{
		return new ScoreFrameChain(score, firstFrame, false, null);
	}
	
	
	/**
	 * Creates a new list of conntected score frames, showing the given score,
	 * beginning with the given frame. If the frames don't have enough space for
	 * the whole score, the layouter will create additional frames with the
	 * given size which contain the rest of the score.
	 */
	public static ScoreFrameChain createCompleteChain(Score score, ScoreFrame firstFrame,
		Size2f additionalFramesSize)
	{
		ScoreFrameChain ret = new ScoreFrameChain(score, firstFrame, true, additionalFramesSize);
		firstFrame.setScoreFrameChain(ret);
		return ret;
	}
	
	
	private ScoreFrameChain(Score score, ScoreFrame firstFrame, boolean isCompleteChain,
		Size2f additionalFramesSize)
	{
		this.score = score;
		frames.add(firstFrame);
		this.isCompleteChain = isCompleteChain;
		this.additionalFramesSize = additionalFramesSize;
	} 


	/**
	 * Gets the number of really existing frames in this list of connected score frames.
	 */
	public int getLimitedFramesCount()
	{
		return frames.size();
	}
	
	
	/**
	 * Gets the number of all frames (including additional frames)
	 * in this list of connected score frames.
	 */
	public int getCompleteFramesCount()
	{
		return frames.size() + additionalFrames.size();
	}


	/**
	 * Gets the frame with the given index. If the index is higher than the
	 * highest index of each existing frame, an additional frame is meant.
	 * If there is no such frame, an {@link IndexOutOfBoundsException} is thrown.
	 */
	public ScoreFrame getFrame(int index)
	{
		if (index < frames.size())
			return frames.get(index);
		else
			return additionalFrames.get(index - frames.size());
	}


	// TIDY
	public ArrayList<ScoreFrame> getFrames()
	{
		return frames;
	}


	/**
	 * Adds the given frame to the end of the chain, if it is not already part
	 * of the chain.
	 */
	public void addFrame(ScoreFrame frame)
	{
		if (frames.contains(frame))
		{
			throw new IllegalArgumentException(
				"Score frame is already part of the chain!");
		}
		frame.setScoreFrameChain(this);
		frames.add(frame);
	}


	/**
	 * Adds the given frame in the middle of the chain, after the given Frame.
	 * If the Frame is already part of the chain, it is moved to the new
	 * position.
	 */
	public void addFrame(ScoreFrame frame, ScoreFrame newFrame)
	{
		if (!frames.contains(frame))
		{
			throw new IllegalArgumentException("ScoreFrame 1 is not existing!");
		}
		// If the new Frame exists already, it is removed
		frames.remove(newFrame);

		newFrame.setScoreFrameChain(this);
		int pos = frames.indexOf(frame);
		frames.add(pos + 1, newFrame);
	}


	/**
	 * Adds tje given Frame at the given position. If the frame already exists,
	 * an exception is thrown.
	 * @param newFrame
	 * @param position
	 */
	public void addFrame(ScoreFrame newFrame, int position)
	{
		if (frames.contains(newFrame))
		{
			throw new IllegalArgumentException("ScoreFrame is already part of the chain!");
		}
		frames.add(position, newFrame);
		newFrame.setScoreFrameChain(this);
	}


	/**
	 * Removes a Frame from the chain.
	 */
	public int removeFrame(ScoreFrame frame)
	{
		int index = frames.indexOf(frame);
		frames.remove(frame);
		frame.setScoreFrameChain(null);
		return index;
	}


	/**
	 * Gets the score these score frames are showing.
	 */
	public Score getScore()
	{
		return score;
	}


	/**
	 * Updates the layout of all frames in this chain.
	 */
	public void updateLayout()
	{
		ensureLayouterInitialized();
		ScoreLayout layout = layouter.createLayout();
		// fill frames as long layouts are available
		for (int i = 0; i < frames.size() && i < layout.getScoreFrameLayouts().size(); i++)
		{
			frames.get(i).setLayout(layout.getScoreFrameLayouts().get(i));
		}
		// the remaining frames are empty
		for (int i = layout.getScoreFrameLayouts().size(); i < frames.size(); i++)
		{
			frames.get(i).setLayout(null);
		}
	}


	/**
	 * Updates the layout of at least the given measures, if possible. This will
	 * be more performant than <code>updateLayout()</code>.
	 */
	public void updateLayout(Range measures)
	{
		// TODO
		updateLayout();
	}


	/**
	 * Updates the selection stampings in this score frame chain.
	 */
	public void updateSelections(ScoreInput input)
	{
		for (int i = 0; i < frames.size(); i++)
		{
			frames.get(i).getScoreLayout().updateSelections(input);
		}
	}


	public ScoreLayout getScoreLayout()
	{
		ensureLayouterInitialized();
		return layouter.getLayout();
	}


	public void ensureLayouterInitialized()
	{
		if (layouter == null)
		{
			layouter = new ScoreLayouter(this, App.getInstance().getSymbolPool());
			layouter.createLayout();
		}
	}
	
	
	/**
	 * Returns true, if this chain requests the layouter to compute the layout
	 * of the whole score, independent of the number of really existing frames.
	 */
	public boolean isCompleteChain()
	{
		return isCompleteChain;
	}
	
	
	/**
	 * Returns the size of the additional frames. This is always <code>null</code> when
	 * <code>isCompleteChain</code> returns false.
	 */
	public Size2f getAdditionalFrameSize()
	{
		return additionalFramesSize;
	}
	
	
	/**
	 * Clears the additional frames. This method has to be called by the layouter
	 * before it recomputes the layouts of the frames.
	 */
	public void clearAdditionalFrames()
	{
		additionalFrames.clear();
	}
	
	
	/**
	 * Adds the given additional frame.
	 */
	public void addAdditionalFrame(ScoreFrame additionalFrame)
	{
		additionalFrames.add(additionalFrame);
	}
	
	
	/**
	 * Gets the list of additional frames. This may be the empty list,
	 * but never <code>null</code>.
	 */
	public List<ScoreFrame> getAdditionalFrames()
	{
		return additionalFrames;
	}


}
