package com.xenoage.zong.player.gui;

import com.xenoage.util.language.Lang;
import com.xenoage.zong.player.language.Voc;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


/**
 * View for the main frame of the Zong! Player.
 *
 * @author Andreas Wenger
 * @author Hervé Bitteur
 */
public class ZongFrameView
	extends BasicFrameView
{
	
	protected enum ZongComponents
		implements ComponentID
	{
		Open,
		Save,
		Exit,
		FileToMidi,
		DirToMidi,
		ReadMe,
		About
	}


	/**
	 * Creates a new FullFrameView object, with both buttons and pull-down menus
	 */
	public ZongFrameView()
	{
		//specific location and behavior
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setMinimumSize(new Dimension(380, 135));
		frame.setResizable(false);

		this.defineIcons();
		this.defineMenuBar();

		frame.pack();
	}


	@Override protected List<ComponentID> getButtonSequence()
	{
		return Arrays.asList((ComponentID) ZongComponents.Open, Components.Play, Components.Pause,
			Components.Stop, Components.VolumeImage, Components.VolumeSlider,
			ZongComponents.Save, Components.InfoDialog);
	}


	@Override protected ZongFrameController getFrameController()
	{
		return (ZongFrameController) super.getFrameController();
	}


	protected JMenuItem createMenuItem(Voc voc, ComponentID id)
	{
		JMenuItem item = new JMenuItem(actionMap.get(id));
		item.setText(Lang.get(voc));

		return item;
	}


	@Override protected void defineActions()
	{
		super.defineActions();

		//open 
		bindAction(ZongComponents.Open, new MidiAction("open.png")
		{
			public void actionPerformed(ActionEvent e)
			{
				getFrameController().openFile();
			}
		});

		//save 
		bindAction(ZongComponents.Save, new MidiAction("save.png")
		{
			public void actionPerformed(ActionEvent e)
			{
				getFrameController().saveMidiFile();
			}
		});

		//exit 
		bindAction(ZongComponents.Exit, new MidiAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});

		//file to Midi
		bindAction(ZongComponents.FileToMidi, new MidiAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				getFrameController().convertToMidiFile();
			}
		});

		//dir to Midi
		bindAction(ZongComponents.DirToMidi, new MidiAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				getFrameController().convertToMidiDir();
			}
		});

		//readme
		bindAction(ZongComponents.ReadMe, new MidiAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				getFrameController().showReadme();
			}
		});

		//about
		bindAction(ZongComponents.About, new MidiAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				getFrameController().showInfoWindow(1);
			}
		});
	}


	protected void defineMenuBar()
	{
		JMenuBar mnuBar = new JMenuBar();

		//file menu
		JMenu mnuFile = new JMenu(Lang.get(Voc.Menu_File));
		mnuBar.add(mnuFile);

		mnuFile.add(createMenuItem(Voc.Menu_Open, ZongComponents.Open));
		mnuFile.add(createMenuItem(Voc.Menu_SaveAs, ZongComponents.Save));
		mnuFile.addSeparator();
		mnuFile.add(createMenuItem(Voc.Menu_Info, Components.InfoDialog));
		mnuFile.addSeparator();
		mnuFile.add(createMenuItem(Voc.Menu_Exit, ZongComponents.Exit));

		//convert menu
		JMenu mnuConvert = new JMenu(Lang.get(Voc.Menu_Convert));
		mnuBar.add(mnuConvert);

		mnuConvert.add(createMenuItem(Voc.Menu_FileToMidi, ZongComponents.FileToMidi));
		mnuConvert.add(createMenuItem(Voc.Menu_DirToMidi, ZongComponents.DirToMidi));

		//settings menu
		JMenu mnuSettings = new JMenu(Lang.get(Voc.Menu_Settings));
		mnuBar.add(mnuSettings);

		mnuSettings.add(createMenuItem(Voc.Menu_Audio, Components.AudioSettings));

		//help menu
		JMenu mnuHelp = new JMenu(Lang.get(Voc.Menu_Help));
		mnuBar.add(mnuHelp);

		mnuHelp.add(createMenuItem(Voc.Menu_ReadMe, ZongComponents.ReadMe));
		mnuHelp.addSeparator();
		mnuHelp.add(createMenuItem(Voc.Menu_About, ZongComponents.About));

		frame.setJMenuBar(mnuBar);
	}


	protected void defineIcons()
	{
		try
		{
			List<Image> icons = new ArrayList<Image>();
			icons.add(loadImage("logo512.png"));
			icons.add(loadImage("logo256.png"));
			icons.add(loadImage("logo128.png"));
			icons.add(loadImage("logo64.png"));
			icons.add(loadImage("logo48.png"));
			icons.add(loadImage("logo32.png"));
			icons.add(loadImage("logo16.png"));
			frame.setIconImages(icons);
		}
		catch (IOException ex)
		{
		}
	}

}
