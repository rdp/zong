package com.xenoage.zong.player.gui;


import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.xenoage.util.io.IO;
import com.xenoage.util.language.Lang;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.player.Player;
import com.xenoage.zong.player.language.Voc;


/**
 * View for the main frame of the Zong! Player.
 *
 * @author Andreas Wenger
 */
public class FrameView
  extends JFrame
  implements ActionListener
{
  
	private FrameController controller;
	
	private JMenuBar mnuBar;
  private JMenu mnuFile;
  private JMenuItem mnuFileOpen, mnuFileSave, mnuFileInfo, mnuFileExit;
  private JMenu mnuConvert;
  private JMenuItem mnuConvertFile, mnuConvertDir;
  private JMenu mnuSettings;
  private JMenuItem mnuSettingsAudio;
  private JMenu mnuHelp;
  private JMenuItem mnuHelpReadme, mnuHelpAbout;

  private JLabel lblTitle;
  private JProgressBar progress;

  private JButton btnOpen, btnPlay, btnPause, btnStop, btnSave, btnInfo;

  private JSlider sldVolume;


  public FrameView(FrameController controller)
  {
  	this.controller = controller;
  	
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setMinimumSize(new Dimension(380, 135));
    this.setLocationRelativeTo(null);
    try
    {
    	List<Image> icons = new ArrayList<Image>();
    	icons.add(ImageIO.read(IO.openDataFile("data/img/gui/logo512.png")));
    	icons.add(ImageIO.read(IO.openDataFile("data/img/gui/logo256.png")));
    	icons.add(ImageIO.read(IO.openDataFile("data/img/gui/logo128.png")));
    	icons.add(ImageIO.read(IO.openDataFile("data/img/gui/logo64.png")));
    	icons.add(ImageIO.read(IO.openDataFile("data/img/gui/logo48.png")));
    	icons.add(ImageIO.read(IO.openDataFile("data/img/gui/logo32.png")));
    	icons.add(ImageIO.read(IO.openDataFile("data/img/gui/logo16.png")));
      this.setIconImages(icons);
    }
    catch (IOException ex)
    {
    }
    this.setTitle(Player.getProjectName());
    this.setResizable(false);

    mnuBar = new JMenuBar();

    mnuFile = new JMenu(Lang.get(Voc.Menu_File));
    mnuBar.add(mnuFile);
    mnuFileOpen = new JMenuItem(Lang.get(Voc.Menu_Open));
    mnuFileOpen.addActionListener(this);
    mnuFile.add(mnuFileOpen);
    mnuFileSave = new JMenuItem(Lang.get(Voc.Menu_SaveAs));
    mnuFileSave.addActionListener(this);
    mnuFile.add(mnuFileSave);
    mnuFile.addSeparator();
    mnuFileInfo = new JMenuItem(Lang.get(Voc.Menu_Info));
    mnuFileInfo.addActionListener(this);
    mnuFile.add(mnuFileInfo);
    mnuFile.addSeparator();
    mnuFileExit = new JMenuItem(Lang.get(Voc.Menu_Exit));
    mnuFileExit.addActionListener(this);
    mnuFile.add(mnuFileExit);

    mnuConvert = new JMenu(Lang.get(Voc.Menu_Convert));
    mnuBar.add(mnuConvert);
    mnuConvertFile = new JMenuItem(Lang.get(Voc.Menu_FileToMidi));
    mnuConvertFile.addActionListener(this);
    mnuConvert.add(mnuConvertFile);
    mnuConvertDir = new JMenuItem(Lang.get(Voc.Menu_DirToMidi));
    mnuConvertDir.addActionListener(this);
    mnuConvert.add(mnuConvertDir);
    
    mnuSettings = new JMenu(Lang.get(Voc.Menu_Settings));
    mnuBar.add(mnuSettings);
    mnuSettingsAudio = new JMenuItem(Lang.get(Voc.Menu_Audio));
    mnuSettingsAudio.addActionListener(this);
    mnuSettings.add(mnuSettingsAudio);
    
    mnuHelp = new JMenu(Lang.get(Voc.Menu_Help));
    mnuBar.add(mnuHelp);
    mnuHelpReadme = new JMenuItem(Lang.get(Voc.Menu_ReadMe));
    mnuHelpReadme.addActionListener(this);
    mnuHelp.add(mnuHelpReadme);
    mnuHelp.addSeparator();
    mnuHelpAbout = new JMenuItem(Lang.get(Voc.Menu_About));
    mnuHelpAbout.addActionListener(this);
    mnuHelp.add(mnuHelpAbout);

    this.setJMenuBar(mnuBar);
    
    int border = 4;
    
    FlowLayout contentPaneLayout = new FlowLayout(
      FlowLayout.CENTER, border, border);
    this.getContentPane().setLayout(contentPaneLayout);
    
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
    this.add(mainPanel);

    JPanel pnlTitle = new JPanel();
    pnlTitle.setBorder(BorderFactory.createEmptyBorder(
      border, border, border, border));
    pnlTitle.setLayout(new GridLayout(1, 1));
    mainPanel.add(pnlTitle);
    
    lblTitle = new JLabel(" ");
    pnlTitle.add(lblTitle);

    JPanel pnlProgress = new JPanel();
    pnlProgress.setBorder(BorderFactory.createEmptyBorder(
      border, border, border, border));
    pnlProgress.setLayout(new GridLayout(1, 1));
    mainPanel.add(pnlProgress);
    
    progress = new JProgressBar();
    progress.setMaximum(10000);
    progress.setStringPainted(true);
    progress.addMouseListener(new MouseAdapter()
    {
      @Override public void mousePressed(MouseEvent e)
      {
      	FrameView.this.controller.setPlaybackPosition(1f * e.getX() / progress.getWidth());
      }
    });
    pnlProgress.add(progress);
    
    FlowLayout buttonsLayout = new FlowLayout(
      FlowLayout.CENTER, border / 2, border);
    JPanel pnlButtons = new JPanel(buttonsLayout);
    mainPanel.add(pnlButtons);

    btnOpen = new JButton(loadIcon("open.png"));
    btnOpen.setPreferredSize(new Dimension(22, 22));
    btnOpen.addActionListener(this);
    pnlButtons.add(btnOpen);

    btnPlay = new JButton(loadIcon("play.png"));
    btnPlay.setPreferredSize(new Dimension(22, 22));
    btnPlay.addActionListener(this);
    pnlButtons.add(btnPlay);

    btnPause = new JButton(loadIcon("pause.png"));
    btnPause.setPreferredSize(new Dimension(22, 22));
    btnPause.addActionListener(this);
    pnlButtons.add(btnPause);

    btnStop = new JButton(loadIcon("stop.png"));
    btnStop.setPreferredSize(new Dimension(22, 22));
    btnStop.addActionListener(this);
    pnlButtons.add(btnStop);

    JLabel lblVolume = new JLabel(loadIcon("volume.png"));
    lblVolume.setPreferredSize(new Dimension(22, 22));
    pnlButtons.add(lblVolume);

    sldVolume = new JSlider(JSlider.HORIZONTAL, 0, 100,
      (int) (controller.getVolume() * 100));
    sldVolume.setPaintLabels(false);
    sldVolume.setMajorTickSpacing(1);
    sldVolume.setMinorTickSpacing(1);
    sldVolume.setPaintTicks(false);
    sldVolume.setMinimumSize(new Dimension(200, 22));
    sldVolume.addChangeListener(new ChangeListener(){
			@Override public void stateChanged(ChangeEvent e)
			{
				FrameView.this.controller.setVolume(0.01f * sldVolume.getValue());
			}
    });
    pnlButtons.add(sldVolume);

    btnSave = new JButton(loadIcon("save.png"));
    btnSave.setPreferredSize(new Dimension(22, 22));
    btnSave.addActionListener(this);
    pnlButtons.add(btnSave);

    btnInfo = new JButton(loadIcon("info.png"));
    btnInfo.setPreferredSize(new Dimension(22, 22));
    btnInfo.addActionListener(this);
    pnlButtons.add(btnInfo);
    
    this.pack();
  }


  public void setScoreInfo(Score score)
  {
    String creator = score.getScoreInfo().getComposer();
    if (creator != null)
      creator += " - ";
    else
    	creator = "";
    lblTitle.setText("<html><b>" + creator
     + score.getScoreInfo().getTitle() + "</b></html>");
    this.pack();
  }


  public void actionPerformed(ActionEvent e)
  {
  	Object s = e.getSource();
  	if (s == btnPlay)
  		controller.play();
  	else if (s == btnPause)
  		controller.pause();
  	else if (s == btnStop)
  	{
  		controller.stop();
  		progress.setValue(0);
  	}
  	else if (s == mnuFileOpen || s == btnOpen)
      controller.openFile();
    else if (s == mnuFileSave || s == btnSave)
      controller.saveMidiFile();
    else if (s == mnuFileInfo || s == btnInfo)
      controller.showInfoWindow(0);
    else if (s == mnuFileExit)
      System.exit(0);
    else if (s == mnuConvertFile)
      controller.convertToMidiFile();
    else if (s == mnuConvertDir)
    	controller.convertToMidiDir();
    else if (s == mnuSettingsAudio)
    	controller.showAudioSettingsDialog();
    else if (s == mnuHelpReadme)
    	controller.showReadme();
    else if (s == mnuHelpAbout)
    	controller.showInfoWindow(1);
  }

  
  private ImageIcon loadIcon(String filename)
  {
  	try
  	{
  		return new ImageIcon(ImageIO.read(IO.openDataFile("data/img/gui/" + filename)));
  	}
  	catch (IOException ex)
  	{
  		return null;
  	}
  }


  public void setProgressDisplay(float progressValue, String progressText)
  {
  	progress.setValue((int) (10000 * progressValue));
  	progress.setString(progressText);
  }
  

}
