package com.xenoage.zong.util.demo;

import com.xenoage.util.annotations.Demo;
import com.xenoage.util.font.FontInfo;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.app.App;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.format.PageFormat;
import com.xenoage.zong.core.format.PageMargins;
import com.xenoage.zong.core.text.Alignment;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.data.text.FormattedTextParagraph;
import com.xenoage.zong.data.text.FormattedTextString;
import com.xenoage.zong.data.text.FormattedTextStyle;
import com.xenoage.zong.data.text.FormattedTextSymbol;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.gui.controller.panels.ScorePanelController;
import com.xenoage.zong.layout.Page;
import com.xenoage.zong.layout.frames.ScoreFrame;
import com.xenoage.zong.layout.frames.ScoreFrameChain;
import com.xenoage.zong.layout.frames.TextFrame;


/**
 * Some demo methods for the {@link ScoreDocument} class.
 * 
 * @author Andreas Wenger
 */
public class ScoreDocumentDemo
{
	
	/**
   * Creates a demo score document.
   */
  @Demo public static ScoreDocument createDemoScoreDocument(
  	ScorePanelController scorePanelController)
  {
		//128 measures on 10 pages
		int pages = 10;
		Score score = ScoreRevolutionary.createScore(); // ScoreDemo.createDemoScore(128);
		ScoreDocument document = new ScoreDocument(score, null, scorePanelController);
		ScoreFrameChain sfc = null;
		for (int i = 0; i < pages; i++)
		{
			Page page = document.getCurrentLayout().addPage(
				new PageFormat(new Size2f(220, 250), new PageMargins(20, 20, 20, 20)));
			// formatted text
			int fontSize = (int) (Math.random() * 30) + 10;
			FormattedTextStyle style = new FormattedTextStyle(
				new FontInfo("Times New Roman", (float) fontSize, null));
			FormattedTextParagraph paragraph = new FormattedTextParagraph();
			paragraph.setAlignment(Alignment.Center);
			paragraph.addElement(new FormattedTextString("Ein " + fontSize + "pt Text (",
				style));
			String sym = "";
			switch ((int) (Math.random() * 4))
			{
				case 0:
					sym = "dynamics-p";
					break;
				case 1:
					sym = "dynamics-m";
					break;
				case 2:
					sym = "dynamics-f";
					break;
				case 3:
					sym = "clef-g";
					break;
			}
			paragraph.addElement(new FormattedTextSymbol(App.getInstance().getSymbolPool().getSymbol(sym),
				fontSize));
			paragraph.addElement(new FormattedTextString(") mit Symbol drin", style));
			FormattedText text = new FormattedText();
			text.addParagraph(paragraph);
			page.addFrame(new TextFrame(text, new Point2f(110, 20), new Size2f(100, 20)));
			// musical content
			ScoreFrame scoreFrame;
			if (i == 0)
			{
				scoreFrame = new ScoreFrame(new Point2f(110, 125), new Size2f(180, 210),
					score);
				sfc = scoreFrame.getScoreFrameChain();
			}
			else
			{
				scoreFrame = new ScoreFrame(new Point2f(110, 125), new Size2f(180, 210));
				sfc.addFrame(scoreFrame);
			}
			page.addFrame(scoreFrame);
		}
		document.setCurrentScoreFrameChain(sfc);
		return document;
  }
  
}