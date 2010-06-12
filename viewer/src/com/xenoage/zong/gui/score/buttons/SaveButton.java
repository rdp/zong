package com.xenoage.zong.gui.score.buttons;

import java.awt.event.MouseEvent;

import com.xenoage.util.language.Lang;
import com.xenoage.util.language.LanguageComponent;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.commands.dialogs.SaveDocumentDialogViewerCommand;
import com.xenoage.zong.gui.score.GUIButton;
import com.xenoage.zong.gui.score.GUIManager;


/**
 * Save-button for the viewer.
 * 
 * @author Andreas Wenger
 */
public class SaveButton
	extends GUIButton
	implements LanguageComponent
{
	
	
	public SaveButton(GUIManager guiManager, Point2i position, Size2i size)
	{
		super(guiManager, position, size, TextureManager.ID_GUI_BUTTON_SAVE);
		Lang.registerComponent(this);
		updateTooltip();
	}
	
	
	@Override public void mouseClicked(MouseEvent e)
	{
		super.mouseClicked(e);
		guiManager.getView().getDocument().getCommandPerformer().execute(
			new SaveDocumentDialogViewerCommand());
	}
	
	
	@Override public void languageChanged()
	{
		updateTooltip();
	}
	
	
	private void updateTooltip()
	{
		if (App.getInstance().isDesktopApp())
		{
			//viewer app: export
			setTooltipText(Lang.get(Voc.ButtonPanel_SaveDocumentAs));
		}
		else
		{
			//viewer applet: download MusicXML or export
			setTooltipText(Lang.get(Voc.ButtonPanel_DownloadDocument));
		}
	}

}
