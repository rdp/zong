package com.xenoage.zong.musiclayout.layouter;

import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.musiclayout.ScoreFrameLayout;
import com.xenoage.zong.musiclayout.ScoreLayout;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.SystemCursorStamping;


/**
 * This class modifies a given {@link ScoreLayout}
 * so that the given playback situation is shown.
 * 
 * @author Andreas Wenger
 */
public class PlaybackLayouter
{
	
	private final ScoreLayout scoreLayout;
	
	
	/**
	 * Creates a new {@link PlaybackLayouter} for the given {@link ScoreLayout}.
	 */
	public PlaybackLayouter(ScoreLayout scoreLayout)
	{
		this.scoreLayout = scoreLayout;
	}
	
	
	/**
	 * Sets a {@link SystemCursorStamping} at the given {@link ScorePosition},
	 * representing the current playback position.
	 */
	public void setCursorAt(MP mp)
	{
		//remove old playback cursors
		removePlaybackStampings();
		//find frame
		int measure = mp.getMeasure();
		for (ScoreFrameLayout frame : scoreLayout.getScoreFrameLayouts())
		{
			if (measure >= frame.getFrameArrangement().getStartMeasureIndex() &&
				measure <= frame.getFrameArrangement().getEndMeasureIndex())
			{
				Score score = scoreLayout.getScore();
				StaffStamping topStaff = frame.getStaffStamping(0, measure);
				StaffStamping bottomStaff = frame.getStaffStamping(score.getStavesCount() - 1, measure);
				if (topStaff != null && bottomStaff != null)
				{
					frame.getPlaybackStampings().add(new SystemCursorStamping(topStaff, bottomStaff,
						getPositionX(mp, frame)));
				}
				return;
			}
		}
	}
	
	
	/**
	 * Removes all playback stampings.
	 */
	public void removePlaybackStampings()
	{
		for (ScoreFrameLayout frame : scoreLayout.getScoreFrameLayouts())
		{
			frame.getPlaybackStampings().clear();
		}
	}
	
	
	/**
	 * Gets the X-position of the given beat within the given frame,
	 * or 0 if not found (should not happen).
	 * TIDY: performance?
	 */
	private float getPositionX(MP mp, ScoreFrameLayout frame)
	{
		float minX = Float.MAX_VALUE;
		//search all staves for the given musical position, beginning at the top staff
		for (StaffStamping staff : frame.getStaffStampings())
		{
			Float x = staff.getXMmAt(mp);
			if (x != null && x < minX)
				minX = x;
		}
		if (minX == Float.MAX_VALUE)
			return 0;
		else
			return minX;
	}
	

}
