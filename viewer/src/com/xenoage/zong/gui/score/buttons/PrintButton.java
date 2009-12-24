package com.xenoage.zong.gui.score.buttons;

import java.awt.event.MouseEvent;

import com.xenoage.util.language.Lang;
import com.xenoage.util.language.LanguageComponent;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.commands.document.PrintCommand;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.gui.score.GUIButton;
import com.xenoage.zong.gui.score.GUIManager;


/**
 * Print-button for the viewer.
 * 
 * @author Andreas Wenger
 */
public class PrintButton
	extends GUIButton
	implements LanguageComponent
{
	
	
	public PrintButton(GUIManager guiManager, Point2i position, Size2i size)
	{
		super(guiManager, position, size, TextureManager.ID_GUI_BUTTON_PRINT);
		Lang.registerComponent(this);
		updateTooltip();
	}
	
	
	@Override public void mouseClicked(MouseEvent e)
	{
		super.mouseClicked(e);
		ScoreDocument doc = App.getInstance().getScoreDocument();
		doc.getCommandPerformer().execute(new PrintCommand());
	}
	
	
	@Override public void languageChanged()
	{
		updateTooltip();
	}
	
	
	private void updateTooltip()
	{
		setTooltipText(Lang.get(Voc.ButtonPanel_PrintDocument));
	}

}
