package com.xenoage.zong.player.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.swing.JFileChooser;

import com.xenoage.util.filter.AllFilter;
import com.xenoage.util.iterators.It;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.player.language.Voc;
import com.xenoage.zong.util.filefilter.MidiFileFilter;
import com.xenoage.zong.util.filefilter.MusicXMLFileFilter;


/**
 * Controller for the main frame of the Zong! Player.
 *
 * @author Andreas Wenger
 */
public class ZongFrameController
	extends BasicFrameController
{

	private String lastPath = "files";


	/**
	 * Creates a new {@link ZongFrameView} object.
	 * @param view the related view
	 */
	public ZongFrameController(ZongFrameView view)
	{
		super(view);
	}


	@Override public ZongFrameView getFrameView()
	{
		return (ZongFrameView) view;
	}


	public void convertToMidiDir()
	{
		/* TODO
		JFileChooser fc = new JFileChooser(lastPath);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		JCheckBox chkSubdir = new JCheckBox(Lang.get(Voc.Label_IncludeSubdirectories), true);
		JCheckBox chkCancel = new JCheckBox(Lang.get(Voc.Label_CancelAtFirstError), false);
		JPanel pnlOptions = new JPanel();
		pnlOptions.setLayout(new BoxLayout(pnlOptions, BoxLayout.Y_AXIS));
		pnlOptions.add(chkSubdir);
		pnlOptions.add(chkCancel);
		fc.setAccessory(pnlOptions);

		int ret = fc.showOpenDialog(view.getFrame());

		if (ret == JFileChooser.APPROVE_OPTION)
		{
			File dir = fc.getSelectedFile();
			lastPath = dir.getAbsolutePath();

			List<File> files = FileTools.listFiles(dir, chkSubdir.isSelected());
			int countOK = 0;
			int countFailed = 0;

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

						if ((scores.size() == 0) && chkCancel.isSelected())
						{
							countFailed++;

							break;
						}

						boolean useNumber = scores.size() > 1;
						It<Score> scoresIt = new It<Score>(scores);

						for (Score score : scoresIt)
						{
							Sequence seq = MidiConverter.convertToSequence(score, false, false)
								.getSequence();
							String number = (useNumber ? ("-" + (scoresIt.getIndex() + 1)) : "");
							String newPath = filePath;

							if (filePath.toLowerCase().endsWith(".xml")
								|| filePath.toLowerCase().endsWith(".mxl"))
							{
								newPath = newPath.substring(0, filePath.length() - 4);
							}

							newPath += (number + ".mid");
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

			JOptionPane.showMessageDialog(view.getFrame(), Lang.get(
				Voc.Message_DirectoryConversionResult, "" + countOK, "" + countFailed), Player
				.getProjectName(), JOptionPane.INFORMATION_MESSAGE);
		}
		*/
	}


	public void convertToMidiFile()
	{
		JFileChooser fc = new JFileChooser(lastPath);
		fc.addChoosableFileFilter(new MusicXMLFileFilter());

		int ret = fc.showOpenDialog(view.getFrame());

		if (ret == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			lastPath = file.getAbsolutePath();

			List<Score> scores = controller.loadScores(lastPath, new AllFilter<String>());
			boolean useNumber = scores.size() > 1;
			It<Score> scoresIt = new It<Score>(scores);

			for (Score score : scoresIt)
			{
				//TODO Sequence seq = MidiConverter.convertToSequence(score, false, false).getSequence();
				/*String newPath = lastPath;
				String number = (useNumber ? ("-" + (scoresIt.getIndex() + 1)) : "");

				if (newPath.toLowerCase().endsWith(".xml") || //TIDY: share code
					newPath.toLowerCase().endsWith(".mxl"))
				{
					newPath = newPath.substring(0, newPath.length() - 4);
				}

				newPath += (number + ".mid");

				try
				{
					MidiSystem.write(seq, 1, new File(newPath));
				}
				catch (Exception ex)
				{
					controller.showWarning(Voc.Error_SavingFile);
				}*/
			}
		}
	}


	public void openFile()
	{
		JFileChooser fc = new JFileChooser(lastPath);
		fc.addChoosableFileFilter(new MusicXMLFileFilter());

		int ret = fc.showOpenDialog(view.getFrame());

		if (ret == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			lastPath = file.getAbsolutePath();
			openScore(lastPath);
		}
	}


	public void openScore(String file)
	{
		controller.openScore(file);

		if (controller.getScore() != null)
		{
			view.setScoreInfo(controller.getScore());
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
		{
			defaultFileName = defaultFileName.substring(0, defaultFileName.length() - 4);
		}

		defaultFileName += ".mid";

		fc.setSelectedFile(new File(defaultFileName));

		int ret = fc.showSaveDialog(view.getFrame());

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
