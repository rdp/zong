package com.xenoage.zong.player.gui;

import static com.xenoage.util.NullTools.notNull;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;

import com.xenoage.util.AppletTools;
import com.xenoage.util.io.IO;
import com.xenoage.util.language.Lang;
import com.xenoage.util.language.LanguageInfo;
import com.xenoage.util.logging.Log;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.io.midi.out.PlaybackListener;
import com.xenoage.zong.io.midi.out.SynthManager;
import com.xenoage.zong.player.Player;
import com.xenoage.zong.player.language.Voc;

import java.awt.*;
import java.awt.event.*;

import java.io.IOException;
import java.util.List;


/**
 * The applet version of the Zong! Player.
 *
 * @author Andreas Wenger
 */
public class PlayerApplet
  extends JApplet
  implements PlaybackListener
{

  private JLabel textBox;
  private JButton btnOpen;
  private JButton btnPlay;
  private JButton btnPause;
  private JButton btnStop;
  private JButton btnInfo;
  private ImageIcon[][] imgButtons;
  
  private Controller controller;


  @Override public void init()
  {
  	setSize(384, 36);
    setLayout(null);
  	
  	//init logging, IO, language, audio and GUI
    Log.initAppletLog();
		IO.initApplet(getCodeBase());
		try
  	{
	  	//get available languages
	  	List<LanguageInfo> languages = LanguageInfo.getAvailableLanguages(Lang.defaultLangPath);
	  	//use system's default (TODO: config)
	  	String langID = LanguageInfo.getDefaultID(languages);
			Lang.loadLanguage(langID);
  	}
  	catch (Exception ex)
  	{
  		Log.log(Log.WARNING, "Could not load language", ex);
  		Lang.loadLanguage("en");
  	}
		try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception ex)
    {
    }
    try
		{
			SynthManager.init(false);
		}
		catch (MidiUnavailableException ex)
		{
			JOptionPane.showMessageDialog(null, Lang.get(Voc.Error_Midi),
				Player.getProjectName(), JOptionPane.ERROR_MESSAGE);
			return;
		}
  	controller = new Controller();
  	controller.setParentFrame(AppletTools.getParentFrame(this));
  	controller.addPlaybackListener(this);

    //load background color
    try
    {
      String col = notNull(getParameter("bgcolor")).toLowerCase();
      if (!col.startsWith("#"))
        col = "#" + col;
      getContentPane().setBackground(Color.decode(col));
    }
    catch (NumberFormatException ex)
    {
      getContentPane().setBackground(Color.WHITE);
    }

    //load background image
    ImageIcon img = null;
    try
    {
      img = loadSkinImage("frame.png");
    }
    catch (Exception ex)
    {
    	controller.showWarning(Voc.Error_Skin);
      return;
    }

    //load button graphics
    imgButtons = new ImageIcon[2][5];
    imgButtons[0][0] = loadSkinImage("open.png");
    imgButtons[1][0] = loadSkinImage("open2.png");
    imgButtons[0][1] = loadSkinImage("play.png");
    imgButtons[1][1] = loadSkinImage("play2.png");
    imgButtons[0][2] = loadSkinImage("pause.png");
    imgButtons[1][2] = loadSkinImage("pause2.png");
    imgButtons[0][3] = loadSkinImage("stop.png");
    imgButtons[1][3] = loadSkinImage("stop2.png");
    imgButtons[0][4] = loadSkinImage("info.png");
    imgButtons[1][4] = loadSkinImage("info2.png");

    btnOpen = new JButton(imgButtons[0][0]);
    btnOpen.setBounds(47, 9, 18, 18);
    add(btnOpen);
    btnOpen.addMouseListener(new MouseAdapter()
    {

      @Override public void mouseClicked(MouseEvent e)
      {
        String filename = "";
        String paramFiles = getParameter("files");
        if (paramFiles == null)
        {
        	controller.showWarning(Voc.Error_Files);
        	return;
        }
        String[] files = paramFiles.split(";");
        filename = (String) JOptionPane.showInputDialog(PlayerApplet.this,
        	Lang.get(Voc.Label_SelectDocument), Player.getProjectName(), JOptionPane.PLAIN_MESSAGE,
        	null, files, files[0]);
        if (filename != null)
        {
          openFile(filename);
        }
        else
        {
          textBox.setText(Player.getProjectName());
        }
      }


      @Override public void mousePressed(MouseEvent e)
      {
        btnOpen.setIcon(imgButtons[1][0]);
      }


      @Override public void mouseReleased(MouseEvent e)
      {
        btnOpen.setIcon(imgButtons[0][0]);
      }
    });


    btnPlay = new JButton(imgButtons[0][1]);
    btnPlay.setBounds(68, 9, 18, 18);
    add(btnPlay);
    btnPlay.addMouseListener(new MouseAdapter()
    {

      @Override public void mouseClicked(MouseEvent e)
      {
        controller.play();
      }


      @Override public void mousePressed(MouseEvent e)
      {
        btnPlay.setIcon(imgButtons[1][1]);
      }


      @Override public void mouseReleased(MouseEvent e)
      {
        btnPlay.setIcon(imgButtons[0][1]);
      }

    });
    

    btnPause = new JButton(imgButtons[0][2]);
    btnPause.setBounds(89, 9, 18, 18);
    add(btnPause);
    btnPause.addMouseListener(new MouseAdapter()
    {

      @Override public void mouseClicked(MouseEvent e)
      {
        controller.pause();
      }


      @Override public void mousePressed(MouseEvent e)
      {
        btnPause.setIcon(imgButtons[1][2]);
      }


      @Override public void mouseReleased(MouseEvent e)
      {
        btnPause.setIcon(imgButtons[0][2]);
      }

    });

    btnStop = new JButton(imgButtons[0][3]);
    btnStop.setBounds(110, 9, 18, 18);
    add(btnStop);
    btnStop.addMouseListener(new MouseAdapter()
    {

      @Override public void mouseClicked(MouseEvent e)
      {
        controller.stop();
      }


      @Override public void mousePressed(MouseEvent e)
      {
        btnStop.setIcon(imgButtons[1][3]);
      }


      @Override public void mouseReleased(MouseEvent e)
      {
        btnStop.setIcon(imgButtons[0][3]);
      }

    });

    btnInfo = new JButton(imgButtons[0][4]);
    btnInfo.setBounds(361, 9, 18, 18);
    add(btnInfo);
    btnInfo.addMouseListener(new MouseAdapter()
    {

      @Override public void mouseClicked(MouseEvent e)
      {
        controller.showInfoWindow(0);
      }


      @Override public void mousePressed(MouseEvent e)
      {
        btnInfo.setIcon(imgButtons[1][4]);
      }


      @Override public void mouseReleased(MouseEvent e)
      {
        btnInfo.setIcon(imgButtons[0][4]);
      }

    });

    textBox = new JLabel(Player.getProjectName());
    textBox.setBounds(131, 9, 225, 18);
    textBox.setOpaque(false);
    textBox.setFont(new Font("Dialog", Font.PLAIN, 12));
    add(textBox);
    
    JLabel imgFrame = new JLabel(img);
    imgFrame.setBounds(0, 0, getWidth(), getHeight());
    add(imgFrame);
    
    //if start-argument was set, try to open that file
    String paramStart = this.getParameter("start");
    try
    {
      if (paramStart != null && paramStart.length() > 0)
        openFile(paramStart);
    }
    catch (Exception ex)
    {
      textBox.setText("Could not load \"" + paramStart + "\"!");
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this,
        "Could not load \"" + paramStart + "\"!\n\n" + ex.toString());
    }
  }


  @Override public void destroy()
  {
  	if (controller != null)
  		controller.stop();
    SynthManager.close();
  }
  
  
  private ImageIcon loadSkinImage(String filename)
  {
    try
    {
    	return new ImageIcon(ImageIO.read(IO.openDataFile(filename)));
    }
    catch (IOException ex)
    {
    	return null;
    }
  }


  /**
   * Opens the file with the given filename.
   */
  private void openFile(String filename)
  {
  	String dir = "";
    if (getParameter("path") != null)
    {
      String p = getParameter("path");
      if (!p.endsWith("/"))
        p += "/";
      dir = p;
    }
    showStatus(filename);
    
    controller.openScore(dir + filename);
    if (controller.getScore() != null)
    	textBox.setText(controller.getScore().getScoreInfo().getTitle());
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
  

}
