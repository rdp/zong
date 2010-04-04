package com.xenoage.zong.util.demo;

import com.xenoage.util.annotations.Demo;
import com.xenoage.util.font.FontInfo;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.format.PageFormat;
import com.xenoage.zong.core.text.Alignment;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.data.text.FormattedTextParagraph;
import com.xenoage.zong.data.text.FormattedTextString;
import com.xenoage.zong.data.text.FormattedTextStyle;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.gui.controller.panels.ScorePanelController;
import com.xenoage.zong.layout.Page;
import com.xenoage.zong.layout.frames.ImageFrame;
import com.xenoage.zong.layout.frames.ScoreFrame;
import com.xenoage.zong.layout.frames.ScoreFrameChain;
import com.xenoage.zong.layout.frames.TextFrame;
import com.xenoage.zong.layout.frames.TextFrame.LineBreakStyle;
import com.xenoage.zong.musiclayout.layouter.horizontalsystemfilling.NoHorizontalSystemFillingStrategy;


/**
 * @author Uli Teschemacher
 *
 */
public class ScoreDocumentAlphaVersionWelcomeScreen
{

	@Demo public static ScoreDocument createScore(ScorePanelController scorePanelController)
	{
		Score score = ScoreRevolutionary.createScore();//createDemoScore(128);
		score = score.withScoreInfo(score.getScoreInfo().withWorkTitle("Welcome to Zong!"));
		
		ScoreDocument document = new ScoreDocument(score, null, scorePanelController);

		Page page = document.getCurrentLayout().addPage(new PageFormat());
		ScoreFrameChain sfc = null;
		
		//Show Logo
		ImageFrame image = new ImageFrame(new Point2f(105,65), new Size2f(156,104), "data/demo/logo.png");
		page.addFrame(image);
		
		//Show text box
		int fontSizeTitle = 25;
		int fontSizeSubtitle = 21;
		FormattedTextStyle style1 = new FormattedTextStyle(
			new FontInfo("Arial", (float) fontSizeTitle, null));
		FormattedTextStyle style2 = new FormattedTextStyle(
			new FontInfo("Arial", (float) fontSizeSubtitle, null));

		FormattedText text = new FormattedText();

		FormattedTextParagraph paragraph = new FormattedTextParagraph();
		paragraph.setAlignment(Alignment.Center);
		paragraph.addElement(new FormattedTextString("Welcome to the Zong! Viewer ALPHA",
			style1));
		text.addParagraph(paragraph);
		
		paragraph = new FormattedTextParagraph();
		paragraph.addElement(new FormattedTextString("The revolution is just about to begin!", style2));
		paragraph.setAlignment(Alignment.Center);
		text.addParagraph(paragraph);
		page.addFrame(new TextFrame(text, new Point2f(105, 130), new Size2f(100, 20)));
		

		FormattedTextStyle style3 = new FormattedTextStyle(
			new FontInfo("Times New Roman", (float) fontSizeSubtitle, null));

		text = new FormattedText();
		paragraph = new FormattedTextParagraph();
		paragraph.setAlignment(Alignment.Center);
		paragraph.addElement(new FormattedTextString("Revolutionary Etude (Beginning)", style3));
		text.addParagraph(paragraph);
		page.addFrame(new TextFrame(text, new Point2f(105,162),new Size2f(200,20)));
		
		FormattedTextStyle style4 = new FormattedTextStyle(
			new FontInfo("Times New Roman", (float) 11, null));

		text = new FormattedText();
		paragraph = new FormattedTextParagraph();
		paragraph.setAlignment(Alignment.Center);
		paragraph.addElement(new FormattedTextString("Frédéric Chopin", style4));
		text.addParagraph(paragraph);
		page.addFrame(new TextFrame(text, new Point2f(181,175),new Size2f(200,20)));
		
		ScoreFrame scoreFrame = new ScoreFrame(new Point2f(110, 265), new Size2f(180, 210),
			score);
		scoreFrame.setHorizontalSystemFillingStrategy(NoHorizontalSystemFillingStrategy.getInstance());
		sfc = scoreFrame.getScoreFrameChain();
		page.addFrame(scoreFrame);

		document.setCurrentScoreFrameChain(sfc);
		
		FormattedTextStyle style5 = new FormattedTextStyle(
			new FontInfo("Arial", (float) 11, null));
		FormattedTextStyle style6 = new FormattedTextStyle(
			new FontInfo("Arial", (float) fontSizeSubtitle-4, null));

		text = new FormattedText();
		paragraph = new FormattedTextParagraph();
		paragraph.setAlignment(Alignment.Left);
		paragraph.addElement(new FormattedTextString("We need you!", style6));
		text.addParagraph(paragraph);
		paragraph = new FormattedTextParagraph();
		paragraph.addElement(new FormattedTextString(" ", style5));
		text.addParagraph(paragraph);
		paragraph = new FormattedTextParagraph();
		paragraph.addElement(new FormattedTextString("If you find a bug in the program, if you have a good idea how to improve the program or if you want to join our team, please contact us.", style5));
		text.addParagraph(paragraph);
		paragraph = new FormattedTextParagraph();
		paragraph.addElement(new FormattedTextString(" ", style5));
		text.addParagraph(paragraph);
		paragraph = new FormattedTextParagraph();
		paragraph.addElement(new FormattedTextString("www.zong-music.com", style5));
		text.addParagraph(paragraph);
		TextFrame textFrame = new TextFrame(text, new Point2f(158,254),new Size2f(70,50));
		textFrame.setLineBreakStyle(LineBreakStyle.Automatic);
		page.addFrame(textFrame);
		
		
		return document;
	}
}
