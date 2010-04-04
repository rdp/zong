package com.xenoage.zong.player.gui;

import java.util.Timer;
import java.util.TimerTask;

import javax.sound.midi.Sequence;

import proxymusic.ScorePartwise;

import com.xenoage.util.exceptions.InvalidFormatException;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.io.midi.out.MidiScorePlayer;
import com.xenoage.zong.io.midi.out.PlaybackListener;
import com.xenoage.zong.io.midi.out.SynthManager;


/**
 * Controller for a basic frame of the Zong! Player.
 * 
 * It is useful not only for Zong!, but can also be used by other
 * projects which want to provide a basic GUI for the player.
 *
 * @author Andreas Wenger
 * @author Herv&eacute Bitteur
 */
public class BasicFrameController
	implements FrameController
{

	
	protected final Controller controller;
	protected final FrameView view;
	protected final Timer timer;


	/**
	 * Creates a new {@link BasicFrameController}.
	 * @param view  the related view
	 */
	public BasicFrameController(FrameView view)
	{
		this.view = view;

		controller = new Controller();
		view.setFrameController(this);
		controller.addPlaybackListener(this);
		controller.setParentFrame(view.getFrame());

		//timer for progress display
		timer = new java.util.Timer();
		timer.scheduleAtFixedRate(new PlaybackProgressTask(), 0, 100);
	}


	public FrameView getFrameView()
	{
		return view;
	}


	public long getMicrosecondLength()
	{
		return controller.getPlayer().getMicrosecondLength();
	}


	public long getMicrosecondPosition()
	{
		return controller.getPlayer().getMicrosecondPosition();
	}


	public MidiScorePlayer getMidiPlayer()
	{
		return controller.getPlayer();
	}


	/**
	 * Sets the current position of the playback cursor.
	 * @param position  a value between 0 (beginning) and 1 (ending)
	 */
	public void setPlaybackPosition(float position)
	{
		controller.getPlayer().setMicrosecondPosition(
			(int) (position * controller.getPlayer().getMicrosecondLength()));
	}


	public Sequence getSequence()
	{
		return controller.getPlayer().getSequence();
	}


	public void setVolume(float volume)
	{
		controller.getPlayer().setVolume(volume);
	}


	public float getVolume()
	{
		return controller.getPlayer().getVolume();
	}


	public void addPlaybackListener(PlaybackListener listener)
	{
		controller.addPlaybackListener(listener);
	}


	public void loadDocument(ScorePartwise doc) throws InvalidFormatException
	{
		controller.loadScore(doc);
		view.setScoreInfo(controller.getScore());
	}


	public void openDocument(ScorePartwise doc) throws InvalidFormatException
	{
		loadDocument(doc);
		controller.play();
	}


	public void pause()
	{
		controller.pause();
	}


	public void play()
	{
		controller.play();
	}


	@Override public void playbackAtEnd()
	{
		controller.stop();
	}


	@Override public void playbackAtMP(MP position)
	{
	}


	@Override public void playbackStopped(MP position)
	{
	}


	public void removePlaybackListener(PlaybackListener listener)
	{
		controller.removePlaybackListener(listener);
	}


	public void showAudioSettingsDialog()
	{
		AudioSettingsDialog dlg = new AudioSettingsDialog(view.getFrame());
		dlg.setVisible(true);

		if (dlg.isOK())
		{
			//reinit midi
			try
			{
				SynthManager.init(true);
			}
			catch (Exception ex)
			{
				//TODO
			}
		}
	}


	public void showScoreInfo()
	{
		controller.showInfoWindow(0);
	}


	public void stop()
	{
		controller.stop();
	}


	private class PlaybackProgressTask
		extends TimerTask
	{

		@Override public void run()
		{
			try
			{
				long pos = getMicrosecondPosition();
				long len = getMicrosecondLength();

				if (len > 0)
				{
					view.setProgressDisplay((1f * pos) / len, getTime((int) (pos / 1000000))
						+ " / " + getTime((int) (len / 1000000)));
				}
				else
				{
					view.setProgressDisplay(0, "");
				}
			}
			catch (Exception ex)
			{
			}
		}


		private String getTime(int time)
		{
			String mins = String.valueOf(time / 60);
			String secs = String.valueOf(time % 60);

			if (secs.length() < 2)
			{
				secs = "0" + secs;
			}

			return mins + ":" + secs;
		}
		
	}
	
}
