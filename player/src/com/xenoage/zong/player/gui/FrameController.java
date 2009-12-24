package com.xenoage.zong.player.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.xenoage.util.FileTools;
import com.xenoage.util.filter.AllFilter;
import com.xenoage.util.iterators.It;
import com.xenoage.util.language.Lang;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.io.midi.out.MidiConverter;
import com.xenoage.zong.io.midi.out.PlaybackListener;
import com.xenoage.zong.io.midi.out.SynthManager;
import com.xenoage.zong.io.musicxml.FileType;
import com.xenoage.zong.io.musicxml.in.FileTypeReader;
import com.xenoage.zong.player.Player;
import com.xenoage.zong.player.language.Voc;
import com.xenoage.zong.util.filefilter.MidiFileFilter;
import com.xenoage.zong.util.filefilter.MusicXMLFileFilter;


/**
 * Controller for the main frame of the Zong! Player.
 * 
 * TIDY: clean up, share more code
 * 
 * @author Andreas Wenger
 */
public class FrameController
	implements PlaybackListener
{
	
	private Controller controller;
	private FrameView frame;
	private String lastPath = "files";
	
	private Timer timer;
	
	
	public FrameController()
	{
		controller = new Controller();
		controller.addPlaybackListener(this);
		frame = new FrameView(this);
		controller.setParentFrame(frame);
		
		//timer for progress display
		timer = new java.util.Timer();
    timer.scheduleAtFixedRate(new PlaybackProgressTask(this), 0, 100);
	}
	
	
	public FrameView getFrame()
	{
		return frame;
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
	
	
	public float getVolume()
	{
		return controller.getPlayer().getVolume();
	}
	
	
	public void setVolume(float volume)
	{
		controller.getPlayer().setVolume(volume);
	}
	
	
	public void openFile()
  {
    JFileChooser fc = new JFileChooser(lastPath);
    fc.addChoosableFileFilter(new MusicXMLFileFilter());
    int ret = fc.showOpenDialog(frame);
    if (ret == JFileChooser.APPROVE_OPTION)
    {
      File file = fc.getSelectedFile();
      lastPath = file.getAbsolutePath();
      openScore(lastPath);
    }
  }
	
	
	public void saveMidiFile()
  {
    Sequence seq = controller.getPlayer().getSequence();
    if (seq == null)
    {
    	controller.showWarning(Voc.Message_NoFileLoaded);
      return;
    }
    JFileChooser fc = new JFileChooser(lastPath);
    fc.addChoosableFileFilter(new MidiFileFilter());
    fc.setAcceptAllFileFilterUsed(false);
    String defaultFileName = new File(lastPath).getName();
    
    //replace .xml and .mxl by .mid, otherwise just add it
    if (defaultFileName.endsWith(".xml") || defaultFileName.endsWith(".mxl"))
      defaultFileName = defaultFileName.substring(0, defaultFileName.length() - 4);
    defaultFileName += ".mid";
    
    fc.setSelectedFile(new File(defaultFileName));
    int ret = fc.showSaveDialog(frame);
    if (ret == JFileChooser.APPROVE_OPTION)
    {
      File file = fc.getSelectedFile();
      lastPath = file.getAbsolutePath();
      try
      {
        MidiSystem.write(seq, 1, file); //TODO: move into midi-out project
      }
      catch (Exception ex)
      {
      	controller.showWarning(Voc.Error_SavingFile);
      }
    }
  }
	
	
	public void convertToMidiFile()
  {
    JFileChooser fc = new JFileChooser(lastPath);
    fc.addChoosableFileFilter(new MusicXMLFileFilter());
    int ret = fc.showOpenDialog(frame);
    if (ret == JFileChooser.APPROVE_OPTION)
    {
      File file = fc.getSelectedFile();
      lastPath = file.getAbsolutePath();
      List<Score> scores = controller.loadScores(lastPath, new AllFilter<String>());
      boolean useNumber = scores.size() > 1;
      It<Score> scoresIt = new It<Score>(scores);
      for (Score score : scoresIt)
      {
        Sequence seq = MidiConverter.convertToSequence(score, false, false).getSequence();
        String newPath = lastPath;
        String number = (useNumber ? "-" + (scoresIt.getIndex() + 1) : "");
        if (newPath.toLowerCase().endsWith(".xml") || //TIDY: share code
          newPath.toLowerCase().endsWith(".mxl"))
          newPath = newPath.substring(0, newPath.length() - 4);
        newPath += number + ".mid";
        try
        {
          MidiSystem.write(seq, 1, new File(newPath));
        }
        catch (Exception ex)
        {
        	controller.showWarning(Voc.Error_SavingFile);
        }
      }
    }
  }
	
	
	public void convertToMidiDir()
  {
    JFileChooser fc = new JFileChooser(lastPath);
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    JCheckBox chkSubdir = new JCheckBox(Lang.get(Voc.Label_IncludeSubdirectories), true);
    JCheckBox chkCancel = new JCheckBox(Lang.get(Voc.Label_CancelAtFirstError), false);
    JPanel pnlOptions = new JPanel();
    pnlOptions.setLayout(new BoxLayout(pnlOptions, BoxLayout.Y_AXIS));
    pnlOptions.add(chkSubdir);
    pnlOptions.add(chkCancel);
    fc.setAccessory(pnlOptions);
    int ret = fc.showOpenDialog(frame);
    if (ret == JFileChooser.APPROVE_OPTION)
    {
      File dir = fc.getSelectedFile();
      lastPath = dir.getAbsolutePath();
      List<File> files = FileTools.listFiles(dir, chkSubdir.isSelected());
      int countOK = 0, countFailed = 0;
      for (File file : files)
      {
      	try
      	{
	      	//only process MusicXML files
	      	FileType fileType = FileTypeReader.getFileType(new FileInputStream(file));
	      	if (fileType != null)
	      	{
	      		String filePath = file.getAbsolutePath();
	      		List<Score> scores = controller.loadScores(filePath, new AllFilter<String>());
	      		if (scores.size() == 0 && chkCancel.isSelected())
	      		{
	      			countFailed++;
	      			break;
	      		}
	      		boolean useNumber = scores.size() > 1;
	          It<Score> scoresIt = new It<Score>(scores);
	          for (Score score : scoresIt)
		      	{
		      		Sequence seq = MidiConverter.convertToSequence(score, false, false).getSequence();
		      		String number = (useNumber ? "-" + (scoresIt.getIndex() + 1) : "");
		      		String newPath = filePath;
		      		if (filePath.toLowerCase().endsWith(".xml") || filePath.toLowerCase().endsWith(".mxl"))
		      			newPath = newPath.substring(0, filePath.length() - 4);
		      		newPath += number + ".mid";
	            MidiSystem.write(seq, 1, new File(newPath));
	            countOK++;
		        }
		      }
      	}
      	catch (IOException ex)
      	{
      		countFailed++;
      		if (chkCancel.isSelected())
      		{
      			break;
      		}
      	}
	    }
      JOptionPane.showMessageDialog(frame,
      	Lang.get(Voc.Message_DirectoryConversionResult, ""+countOK, ""+countFailed),
      	Player.getProjectName(), JOptionPane.INFORMATION_MESSAGE);
    }
  }
	
	
	public long getMicrosecondPosition()
	{
		return controller.getPlayer().getMicrosecondPosition();
	}
	
	
	public long getMicrosecondLength()
	{
		return controller.getPlayer().getMicrosecondLength();
	}
	
	
	public void showAudioSettingsDialog()
	{
		AudioSettingsDialog dlg = new AudioSettingsDialog(frame);
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
	
	
	public void openScore(String file)
	{
		controller.openScore(file);
    if (controller.getScore() != null)
    {
    	frame.setScoreInfo(controller.getScore());
    }
	}

	
	private static class PlaybackProgressTask
	  extends TimerTask
	{
		
		private FrameController frameController;
		
		
		public PlaybackProgressTask(FrameController frameController)
		{
			this.frameController = frameController;
		}
		
	
	  @Override public void run()
	  {
	    try
	    {
	    	long pos = frameController.getMicrosecondPosition();
	    	long len = frameController.getMicrosecondLength();
	    	if (len > 0)
	    	{
	    		frameController.frame.setProgressDisplay(1f * pos / len,
	    			getTime((int) (pos / 1000000)) + " / " + getTime((int) (len / 1000000)));
	    	}
	    	else
	    	{
	    		frameController.frame.setProgressDisplay(0, "");
	    	}
	    }
	    catch (Exception ex)
	    {
	    }
	  }
	  
	  private String getTime(int time)
	  {
	    String mins, secs;
	    mins = String.valueOf(time / 60);
	    secs = String.valueOf(time % 60);
	    if (secs.length() < 2)
	      secs = "0" + secs;
	    return mins + ":" + secs;
	  }
	  
	}


	@Override public void playbackAtEnd()
	{
		controller.stop();
	}


	@Override public void playbackAtScorePosition(ScorePosition position)
	{
	}


	@Override public void playbackStopped(ScorePosition position)
	{
	}
	
	
	public void play()
	{
		controller.play();
	}
	
	
	public void pause()
	{
		controller.pause();
	}
	
	
	public void stop()
	{
		controller.stop();
	}
	
	
	public void showInfoWindow(int tabIndex)
  {
		controller.showInfoWindow(tabIndex);
  }
	
	
	public void showReadme()
  {
		try
		{
			Desktop.getDesktop().open(new File("readme.txt"));
		}
		catch (IOException ex)
		{
		}
  }
	
	

}
