package com.xenoage.zong.app.tools;

import java.awt.event.KeyEvent;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Rectangle2f;
import com.xenoage.util.math.Rectangle2i;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.commands.io.StopMidiPlaybackCommand;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.gui.event.ScoreKeyEvent;
import com.xenoage.zong.io.midi.out.MidiScorePlayer;
import com.xenoage.zong.io.midi.out.PlaybackListener;
import com.xenoage.zong.musiclayout.ScoreLayout;
import com.xenoage.zong.musiclayout.layouter.PlaybackLayouter;
import com.xenoage.zong.view.ScorePageView;


/**
 * This tool controls the playback of a score.
 * 
 * It starts and stops the MIDI playback and,
 * if the score frame chain of the current document
 * is current, shows a cursor at the current playback position.
 * 
 * Optionally, the assigned view can follow the playback,
 * that means, the current system is always visible on the
 * screen.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class PlaybackTool
  extends Tool
  implements PlaybackListener
{
	
	private ScoreDocument doc;
	private Score score;
	private ScoreLayout scoreLayout = null;
	private PlaybackLayouter layouter = null;
	private ScorePageView view = null;
	
	private MidiScorePlayer player;
	
	//follow playback
	private boolean followPlayback = true; //TODO: editable
	private int lastMeasureIndex = -1;
	private int lastFrameIndex = -1;
	private int lastSystemIndex = -1;
	
	
	/**
	 * Creates a {@link PlaybackTool} for the given {@link ViewerScore}.
	 */
	public PlaybackTool(ScoreDocument doc, Score score)
	{
		this.doc = doc;
		this.score = score;
	}
	
	
	/**
   * Call this method when this tool is activated, i.e. when
   * it became the selected tool of a document.
   */
  @Override public void activated()
  {
  	//cursor
  	ScoreDocument doc = App.getInstance().getScoreDocument();
  	if (doc != null && doc.getCurrentScoreFrameChain().getScore() == score)
  	{
  		this.scoreLayout = doc.getCurrentScoreFrameChain().getScoreLayout();
  		this.layouter = new PlaybackLayouter(this.scoreLayout);
  		this.view = (ScorePageView) doc.getCurrentView(); //TODO
  	}
  	//follow playback
  	lastMeasureIndex = -1;
  	lastFrameIndex = -1;
		lastSystemIndex = -1;
  	//midi
		try
		{
		  player = new MidiScorePlayer();
		  player.openScore(score);
		  player.addPlaybackListener(this);
		  player.play();
		}
		catch (Exception ex)
		{
			App.err().report(ErrorLevel.Warning, Voc.Error_MidiNotAvailable, ex);
		}
  }
  
  
  /**
   * TODO: localization
   * Returns the name of this tool.
   */
  @Override public String getName()
  {
    return "MIDI Playback";
  }
  
  
  /**
   * This method is called when the user pressed a key
   * while the tool is active.
   */
  @Override public boolean keyPressed(ScoreKeyEvent e)
  {
  	if (e.getKeyCode() == KeyEvent.VK_Q) //TODO
  	{
	  	stopPlayback();
	  	return true;
  	}
  	else
  	{
  		return false;
  	}
  }


	@Override public void playbackAtMP(MP position)
	{
		//TEST
		//App.getInstance().showMessageDialog(position.toString());
		
		if (layouter != null)
		{
			layouter.setCursorAt(position);
			view.repaint();
			//follow playback?
			if (followPlayback)
			{
				if (position.getMeasure() != lastMeasureIndex)
				{
					lastMeasureIndex = position.getMeasure();
					Tuple2<Integer, Integer> pos = scoreLayout.getFrameAndSystemIndex(lastMeasureIndex);
					if (pos != null && (lastFrameIndex != pos.get1() || lastSystemIndex != pos.get2()))
					{
						//next system was reached.
						lastFrameIndex = pos.get1();
						lastSystemIndex = pos.get2();
						//system completely visible on screen?
						Tuple2<Integer, Rectangle2f> systemRect = view.getLayout().computeSystemBoundingRect(
							scoreLayout, lastFrameIndex, lastSystemIndex);
						int page = systemRect.get1();
						Rectangle2i systemRectPx = view.computeScreenPosition(page, systemRect.get2());
						//TEST
						//System.out.println(systemRect);
						Rectangle2i viewRect = new Rectangle2i(new Point2i(0, 0), view.getSize());
						if (viewRect.contains(systemRectPx))
						{
							//yes, system is completely visible. everything is fine.
						}
						else
						{
							//no, system is not completely visible.
							//is scrolling enough, or is also zooming necessary?
							if (systemRectPx.size.width <= viewRect.size.width &&
								systemRectPx.size.height <= viewRect.size.height)
							{
								//scrolling suffices
							}
							else
							{
								//zoom out as much as needed (+10% more, so it looks nicer)
								float factor = Math.min(1f * viewRect.size.width / systemRectPx.size.width,
									1f * viewRect.size.height / systemRectPx.size.height) / 1.1f;
								view.zoomTo(view.getCurrentScaling() * factor);
							}
							//scroll
							Point2f goalMm = view.getScrollPosition();
							if (systemRectPx.position.x < 0 ||
								systemRectPx.position.x + systemRectPx.size.width > viewRect.size.width)
							{
								//horizontal scrolling required
								goalMm = new Point2f(
									view.getPageDisplayOffset(page).x + systemRect.get2().getCenterX(), goalMm.y);
							}
							if (systemRectPx.position.y < 0 ||
								systemRectPx.position.y + systemRectPx.size.height > viewRect.size.height)
							{
								//vertical scrolling required
								goalMm = new Point2f(
									goalMm.x, view.getPageDisplayOffset(page).y + systemRect.get2().getCenterY());
							}
							view.scrollToSmooth(goalMm);
						}
					}
				}
			}
		}
	}


	public void playbackAtEnd()
	{
		//TEST
		//App.getInstance().showMessageDialog("end");
		App.getInstance().getScoreDocument().getCommandPerformer().execute(
			new StopMidiPlaybackCommand());
	}


	public void playbackStopped(MP position)
	{
		//TEST
		//App.getInstance().showMessageDialog("stopped at " + position);
	}
	
	
	@Override public void deactivated()
  {
		stopPlayback();
  }
	
	
	private void stopPlayback()
	{
		if (player != null)
		{
			player.stop();
		}
  	if (layouter != null)
  	{
  		layouter.removePlaybackStampings();
	  	view.repaint();
	  }
  	doc.setSelectedTool(null);
	}


}
