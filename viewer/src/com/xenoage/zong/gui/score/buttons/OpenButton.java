package com.xenoage.zong.gui.score.buttons;

import java.awt.event.MouseEvent;

import com.xenoage.util.language.Lang;
import com.xenoage.util.language.LanguageComponent;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.ViewerApplet;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.commands.dialogs.OpenDocumentDialogCommand;
import com.xenoage.zong.gui.score.GUIButton;
import com.xenoage.zong.gui.score.GUIManager;


/**
 * Open-button for the viewer.
 * 
 * @author Andreas Wenger
 */
public class OpenButton
	extends GUIButton
	implements LanguageComponent
{
	
	
	public OpenButton(GUIManager guiManager, Point2i position, Size2i size)
	{
		super(guiManager, position, size, TextureManager.ID_GUI_BUTTON_OPEN);
		Lang.registerComponent(this);
		updateTooltip();
	}
	
	
	@Override public void mouseClicked(MouseEvent e)
	{
		super.mouseClicked(e);
		this.hover = false;
		if (App.getInstance().isDesktopApp())
		{
			guiManager.getView().getDocument().getCommandPerformer().execute(
				new OpenDocumentDialogCommand());
		}
		else
		{
			((ViewerApplet) App.getInstance()).showFileOpenDialog();
		}
	}


	@Override public void languageChanged()
	{
		updateTooltip();
	}
	
	
	private void updateTooltip()
	{
		setTooltipText(Lang.get(Voc.ButtonPanel_OpenDocument));
	}


}
