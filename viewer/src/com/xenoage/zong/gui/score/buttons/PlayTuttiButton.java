package com.xenoage.zong.gui.score.buttons;

import java.awt.event.MouseEvent;

import com.xenoage.util.language.Lang;
import com.xenoage.util.language.LanguageComponent;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.app.tools.PlaybackTool;
import com.xenoage.zong.commands.Command;
import com.xenoage.zong.commands.io.StartMidiPlaybackCommand;
import com.xenoage.zong.commands.io.StopMidiPlaybackCommand;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.gui.score.GUIButton;
import com.xenoage.zong.gui.score.GUIManager;


/**
 * Button for the viewer, that starts the playback of
 * all staves and voices.
 * 
 * @author Andreas Wenger
 */
public class PlayTuttiButton
	extends GUIButton
	implements LanguageComponent
{
	
	
	public PlayTuttiButton(GUIManager guiManager, Point2i position, Size2i size)
	{
		super(guiManager, position, size, 
			TextureManager.ID_GUI_BUTTON_PLAY_TUTTI);
		Lang.registerComponent(this);
		updateTooltip();
	}
	
	
	@Override public void mouseClicked(MouseEvent e)
	{
		super.mouseClicked(e);
		ScoreDocument doc = App.getInstance().getScoreDocument();
		Command command;
		if (!(doc.getSelectedTool() instanceof PlaybackTool))
		{
			command = new StartMidiPlaybackCommand();
		}
		else
		{
			command = new StopMidiPlaybackCommand();
		}
		App.getInstance().getScoreDocument().getCommandPerformer().execute(command);
	}
	
	
	@Override public void languageChanged()
	{
		updateTooltip();
	}
	
	
	private void updateTooltip()
	{
		setTooltipText(Lang.get(Voc.ButtonPanel_Playback));
	}

}
