package com.xenoage.zong.player.gui;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import com.xenoage.util.filter.Filter;
import com.xenoage.util.language.Lang;
import com.xenoage.zong.player.Player;
import com.xenoage.zong.player.language.Voc;


/**
 * Filter dialog which allows selecting a single filename.
 * When there is only one file given, it is chosen without
 * showing a dialog.
 * 
 * @author Andreas Wenger
 */
public class FilenameFilterDialog
	implements Filter<String>
{
	

	@Override public List<String> filter(List<String> values)
	{
		List<String> ret = new LinkedList<String>();
		//when there is only one file, select it. when there is no file, also don't
		//show a dialog.
		if (values.size() < 2)
		{
			ret.addAll(values);
		}
		else
		{
			String[] scoreFilesArray = values.toArray(new String[0]);
			String selectedFile = (String) JOptionPane.showInputDialog(null, Lang.get(Voc.Label_SelectDocument),
				Player.getProjectName(), JOptionPane.PLAIN_MESSAGE, null, scoreFilesArray, scoreFilesArray[0]);
			//open file
			if (selectedFile != null)
			{
				ret.add(selectedFile);
			}
		}
		return ret;
	}
	

}
