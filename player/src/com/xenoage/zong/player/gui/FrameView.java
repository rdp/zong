package com.xenoage.zong.player.gui;

import javax.swing.JFrame;

import com.xenoage.zong.core.Score;


/**
 * Viewer for a frame with contains a MIDI player.
 *
 * @author Herv&eacute Bitteur
 */
public interface FrameView
{
	

	/**
	 * Gets the Swing frame.
	 */
	JFrame getFrame();


	/**
	 * Link sthis view to the proper {@link FrameController} companion.
	 */
	void setFrameController(FrameController frameController);


	/**
	 * Updates the display of the progress bar.
	 * @param progressValue  new numerical value between 0 and 1
	 * @param progressText   new text content
	 */
	void setProgressDisplay(float progressValue, String progressText);


	/**
	 * Sets information about the score being played.
	 */
	void setScoreInfo(Score score);
	
}
