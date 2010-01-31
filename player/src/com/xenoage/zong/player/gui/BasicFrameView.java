package com.xenoage.zong.player.gui;

import com.xenoage.util.io.IO;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.player.Player;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * View for a basic frame of the Zong! Player,
 * containing components for play, pause, stop, volume, progress,
 * audio settings and info dialog.
 * 
 * It is useful not only for Zong!, but can also be used by other
 * projects which want to provide a basic GUI for the player.
 * 
 * This abstract class must me customized to provide the desired
 * sequence of buttons via the {@link #getButtonSequence()} method.
 *
 * @author Andreas Wenger
 * @author Hervé Bitteur
 */
public abstract class BasicFrameView
	implements FrameView
{

	protected enum Components
		implements ComponentID
	{
		Play,
		Pause,
		Stop,
		VolumeImage,
		VolumeSlider,
		AudioSettings,
		InfoDialog;
	}

	/** The border value to use constantly */
	protected static final int border = 4;

	protected final JFrame frame;
	private FrameController frameController;

	//entities that need a direct access
	protected JLabel lblTitle;
	protected JProgressBar progress;
	protected JSlider sldVolume;

	/** Map:  ComponentID => Action */
	protected Map<ComponentID, MidiAction> actionMap = new HashMap<ComponentID, MidiAction>();

	/** Map:  ComponentID => Component */
	protected Map<ComponentID, Component> buttonMap = new HashMap<ComponentID, Component>();


	/**
	 * Creates a new {@link BasicFrameView}.
	 */
	public BasicFrameView()
	{
		this.frame = new JFrame();

		frame.setTitle(Player.getProjectName());

		this.defineActions();
		this.defineButtons();
		this.defineLayout();

		frame.pack();
	}


	public JFrame getFrame()
	{
		return frame;
	}


	public void setFrameController(FrameController frameController)
	{
		this.frameController = frameController;
		sldVolume.setValue((int) (frameController.getVolume() * 100));
	}


	public void setProgressDisplay(float progressValue, String progressText)
	{
		progress.setValue((int) (10000 * progressValue));
		progress.setString(progressText);
	}


	public void setScoreInfo(Score score)
	{
		String creator = score.getScoreInfo().getComposer();

		if (creator != null)
		{
			creator += " - ";
		}
		else
		{
			creator = "";
		}

		String title = score.getScoreInfo().getTitle();

		if (title == null)
		{
			title = "";
		}

		lblTitle.setText("<html><b>" + creator + title + "</b></html>");
		frame.pack();
	}


	/**
	 * Override this method to specify a different button dimension
	 * @return the dimension to be used for any button
	 */
	protected Dimension getButtonDimension()
	{
		return new Dimension(22, 22);
	}


	/**
	 * Provide an implementation for this method to define the proper sequence
	 * of buttons to use
	 * @return the sequence of button names
	 */
	protected abstract List<ComponentID> getButtonSequence();


	protected FrameController getFrameController()
	{
		return frameController;
	}


	protected JButton createButton(MidiAction action)
	{
		JButton button = new JButton(action);
		button.setPreferredSize(getButtonDimension());

		return button;
	}


	/**
	 * Define and name every action and store the definitions into the
	 * actionMap. The same actions can be used for a button and/or a menu item.
	 */
	protected void defineActions()
	{
		//play 
		bindAction(Components.Play, new MidiAction("play.png")
		{
			public void actionPerformed(ActionEvent e)
			{
				getFrameController().play();
			}
		});

		//pause
		bindAction(Components.Pause, new MidiAction("pause.png")
		{
			public void actionPerformed(ActionEvent e)
			{
				getFrameController().pause();
			}
		});

		//stop
		bindAction(Components.Stop, new MidiAction("stop.png")
		{
			public void actionPerformed(ActionEvent e)
			{
				getFrameController().stop();
			}
		});

		//audio settings
		bindAction(Components.AudioSettings, new MidiAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				getFrameController().showAudioSettingsDialog();
			}
		});

		//info dialog
		bindAction(Components.InfoDialog, new MidiAction("info.png")
		{
			public void actionPerformed(ActionEvent e)
			{
				getFrameController().showScoreInfo();
			}
		});
	}


	/**
	 * Define the various buttons into the buttonMap
	 */
	protected void defineButtons()
	{
		//all (standard) action-based buttons
		for (Entry<ComponentID, MidiAction> entry : actionMap.entrySet())
		{
			JButton button = createButton(entry.getValue());
			button.setName(entry.getKey().toString());
			buttonMap.put(entry.getKey(), button);
		}

		//add Volume icon
		JLabel lblVolume = new JLabel(loadIcon("volume.png"));
		lblVolume.setPreferredSize(getButtonDimension());
		lblVolume.setName(Components.VolumeImage.toString());
		buttonMap.put(Components.VolumeImage, lblVolume);

		//add Volume slider
		sldVolume = new JSlider(0, 100);
		sldVolume.setPaintLabels(false);
		sldVolume.setMajorTickSpacing(1);
		sldVolume.setMinorTickSpacing(1);
		sldVolume.setPaintTicks(false);
		sldVolume.setMinimumSize(new Dimension(200, 22));
		sldVolume.addChangeListener(new ChangeListener()
		{
			@Override public void stateChanged(ChangeEvent e)
			{
				getFrameController().setVolume(0.01f * sldVolume.getValue());
			}
		});
		sldVolume.setName(Components.VolumeSlider.toString());
		buttonMap.put(Components.VolumeSlider, sldVolume);
	}


	


	protected void defineLayout()
	{
		FlowLayout contentPaneLayout = new FlowLayout(FlowLayout.CENTER, border, border);
		frame.getContentPane().setLayout(contentPaneLayout);

		//main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		frame.add(mainPanel);

		//title panel
		JPanel pnlTitle = new JPanel();
		pnlTitle.setBorder(BorderFactory.createEmptyBorder(border, border, border, border));
		pnlTitle.setLayout(new GridLayout(1, 1));
		mainPanel.add(pnlTitle);

		lblTitle = new JLabel(" ");
		pnlTitle.add(lblTitle);

		//progress panel
		JPanel pnlProgress = new JPanel();
		pnlProgress
			.setBorder(BorderFactory.createEmptyBorder(border, border, border, border));
		pnlProgress.setLayout(new GridLayout(1, 1));
		mainPanel.add(pnlProgress);

		progress = new JProgressBar();
		progress.setMaximum(10000);
		progress.setStringPainted(true);
		progress.addMouseListener(new MouseAdapter()
		{

			@Override public void mousePressed(MouseEvent e)
			{
				getFrameController().setPlaybackPosition((1f * e.getX()) / progress.getWidth());
			}
		});
		pnlProgress.add(progress);

		//buttons panel
		FlowLayout buttonsLayout = new FlowLayout(FlowLayout.CENTER, border / 2, border);
		JPanel pnlButtons = new JPanel(buttonsLayout);
		mainPanel.add(pnlButtons);

		//add the buttons using the desired sequence
		for (ComponentID id : getButtonSequence())
		{
			Component button = buttonMap.get(id);

			if (button != null)
			{
				button.setName(id.toString());
				pnlButtons.add(button);
			}
			else
			{
				System.err.println("Cannot find button '" + id + "'");
			}
		}
	}


	protected ImageIcon loadIcon(String filename)
	{
		if (filename == null)
		{
			return null;
		}

		try
		{
			return new ImageIcon(loadImage(filename));
		}
		catch (IOException ex)
		{
			return null;
		}
	}


	protected BufferedImage loadImage(String filename) throws IOException
	{
		return ImageIO.read(IO.openDataFile("data/img/gui/" + filename));
	}

	
	/**
	 * Binds a new action to the given component.
	 */
	protected void bindAction(ComponentID id, MidiAction action)
	{
		actionMap.put(id, action);
	}
	

	/**
	 * Base class for all actions in the player.
	 * 
	 * @author Hervé Bitteur
	 * @author Andreas Wenger
	 */
	protected abstract class MidiAction
		extends AbstractAction
	{

		
		public MidiAction(String iconFilename)
		{
			super(null, loadIcon(iconFilename));
		}


		public MidiAction()
		{
		}
		
	}
	
}
