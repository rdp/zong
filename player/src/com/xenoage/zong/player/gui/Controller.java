package com.xenoage.zong.player.gui;

import java.awt.Frame;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.JOptionPane;

import com.xenoage.util.exceptions.InvalidFormatException;
import com.xenoage.util.filter.Filter;
import com.xenoage.util.language.Lang;
import com.xenoage.util.language.VocabularyID;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.io.midi.out.MidiScorePlayer;
import com.xenoage.zong.io.midi.out.PlaybackListener;
import com.xenoage.zong.io.musicxml.in.FileReader;
import com.xenoage.zong.io.musicxml.in.MusicXMLScoreFileInput;
import com.xenoage.zong.musicxml.types.MxlScorePartwise;
import com.xenoage.zong.player.Player;
import com.xenoage.zong.player.language.Voc;


/**
 * Controller for both the application version and
 * the applet version.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 * @author Hervé Bitteur
 */
public class Controller
{
	
	private Frame parentFrame = null;
	private Score score;
	private MidiScorePlayer player;
	
	
	public Controller()
	{
		try
		{
			player = new MidiScorePlayer();
		}
		catch (MidiUnavailableException ex)
		{
			JOptionPane.showMessageDialog(parentFrame, Lang.get(Voc.Error_Midi),
				Player.getProjectName(), JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	
	public void setParentFrame(Frame parentFrame)
	{
		this.parentFrame = parentFrame;
	}
	
	
	public MidiScorePlayer getPlayer()
	{
		return player;
	}
	
	
	public Score getScore()
	{
		return score;
	}
	
	
	public void addPlaybackListener(PlaybackListener listener)
	{
		player.addPlaybackListener(listener);
	}
	
	
	public void removePlaybackListener (PlaybackListener listener)
  {
		player.removePlaybackListener(listener);
  }
	
	
	/**
	 * Loads a score from the given file. XML scores,
	 * XML opera and compressed MusicXML files are supported.
	 * When a compressed MusicXML file contains an opus,
	 * the user can select a file in a small dialog. If the action is cancelled,
	 * null is returned.
	 * If an error occurs, an error dialog is shown.
	 */
	public Score loadScore(String path)
	{
		List<Score> scores = loadScores(path, new FilenameFilterDialog());
		if (scores.size() > 0)
			return scores.get(0);
		else
			return null;
	}
	
	
	/**
   * Loads a score from the given {@link ScorePartwise} document.
   * @param doc           the provided document
   * @param ignoreErrors  if true, try to ignore errors (like overfull measures) as long
   *                      as a consistent state can be guaranteed, or false, to cancel
   *                      loading as soon as something is wrong
   */
  public void loadScore(MxlScorePartwise doc, boolean ignoreErrors)
  	throws InvalidFormatException
  {
		Score score = new MusicXMLScoreFileInput().read(doc, null, ignoreErrors);

		if (score != null)
		{
			this.score = score;
			player.openScore(score);
		}
  }
  
  
  /**
	 * Loads a list of scores from the given file. XML scores,
	 * XML opera and compressed MusicXML files are supported.
	 * The given filter is used to select scores, if there are
	 * more than one (like in an opus). If the action is cancelled,
	 * null is returned.
	 * If an error occurs, an error dialog is shown.
	 */
	public List<Score> loadScores(String path, Filter<String> filter)
	{
		try
		{
			return FileReader.loadScores(path, filter);
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(parentFrame, Lang.get(Voc.Error_NoValidMusicXMLFile, path)
				+ "\n\n" + ex.toString(), Player.getProjectName(), JOptionPane.ERROR_MESSAGE);
			return new LinkedList<Score>();
		}
	}
	
	
	/**
	 * Opens a score from the given file.
	 */
	public void openScore(String path)
	{
		Score score = loadScore(path);
    if (score != null)
    {
    	this.score = score;
    	player.openScore(score);
    	play();
    }
	}
	
	
	public void play()
	{
		player.play();
	}
	
	
	public void pause()
	{
		player.pause();
	}
	
	
	public void stop()
	{
		player.stop();
	}
	
	
	public void showWarning(VocabularyID voc)
	{
		JOptionPane.showMessageDialog(parentFrame, Lang.get(voc),
			Player.getProjectName(), JOptionPane.WARNING_MESSAGE);
	}
	
	
	public void showInfoWindow(int tabIndex)
  {
    InfoDialog frmInfo = new InfoDialog(parentFrame, Player.getProjectName(), true);
    frmInfo.setInformation(score);
    frmInfo.setActivePage(tabIndex);
    frmInfo.setVisible(true);
  }

}
