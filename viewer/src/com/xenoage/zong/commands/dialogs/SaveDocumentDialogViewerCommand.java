package com.xenoage.zong.commands.dialogs;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.xenoage.util.settings.Settings;
import com.xenoage.zong.app.App;
import com.xenoage.zong.commands.Command;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.util.filefilter.MidiFileFilter;
import com.xenoage.zong.util.filefilter.MusicXMLFileFilter;
import com.xenoage.util.logging.Log;


/**
 * This command shows a dialog that allows to save
 * the current {@link ScoreDocument}.
 * 
 * In the application, only export to Midi is possible.
 * In the applet, download as MusicXML (original format) and
 * export to Midi is possible.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class SaveDocumentDialogViewerCommand
	extends Command
{
	
	public static enum FileFormat
	{
		Midi,
		MusicXML
	}

	/**
	 * Shows the dialog.
	 */
	@Override
	public void execute()
	{
		JFileChooser fileChooser = new JFileChooser();

		//use last document directory, when running as a desktop application
		String lastDocumentDir = null;
		if (App.getInstance().isDesktopApp())
		{
			lastDocumentDir = Settings.getInstance().getSetting("lastdocumentdirectory", "paths");
			if (lastDocumentDir != null)
			{
				fileChooser.setCurrentDirectory(new File(lastDocumentDir));
			}
			else
	    {
	    	fileChooser.setCurrentDirectory(new File("files"));
	    }
		}

		FileFilter musicXMLFilter = null;
		if (!App.getInstance().isDesktopApp())
		{
			//filter for MusicXML files
			musicXMLFilter = new MusicXMLFileFilter();
			fileChooser.addChoosableFileFilter(musicXMLFilter);
		}
		//filter for MIDI files
		FileFilter midiFilter = new MidiFileFilter();
		fileChooser.addChoosableFileFilter(midiFilter);
		//set default
		FileFilter defaultFilter = null;
		FileFormat defaultFormat = null;
		if (!App.getInstance().isDesktopApp())
		{
			//applet: MusicXML is default
			defaultFilter = musicXMLFilter; 
			defaultFormat = FileFormat.MusicXML;
		}
		else
		{
			//desktop app: Midi is default
			defaultFilter = midiFilter;
			defaultFormat = FileFormat.Midi;
		}
		fileChooser.setFileFilter(defaultFilter);
		
		//show the dialog
		int dialogResult = fileChooser.showSaveDialog(App.getInstance().getMainFrame());
		if (dialogResult == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			Log.log(Log.MESSAGE, this, "Dialog closed (OK), saving file \""
				+ file.getName() + "\"");
			FileFormat format = defaultFormat;
			if (fileChooser.getFileFilter() == musicXMLFilter)
			{
				format = FileFormat.MusicXML;
			}
			else if (fileChooser.getFileFilter() == midiFilter)
			{
				format = FileFormat.Midi;
			}
			App.getInstance().saveFile(
				App.getInstance().getScoreDocument(), file, format);
		}
		else
		{
			Log.log(Log.MESSAGE, this, "Dialog closed (Cancel)");
		}

		//save document directory
		Settings.getInstance().saveSetting("lastdocumentdirectory", "paths",
			fileChooser.getCurrentDirectory().toString());
	}
}
