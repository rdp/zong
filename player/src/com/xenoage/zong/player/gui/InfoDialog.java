package com.xenoage.zong.player.gui;

import static com.xenoage.util.NullTools.notNull;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.xenoage.util.iterators.It;
import com.xenoage.util.language.Lang;
import com.xenoage.zong.Zong;
import com.xenoage.zong.data.Part;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.info.Creator;
import com.xenoage.zong.data.info.ScoreInfo;
import com.xenoage.zong.player.Player;
import com.xenoage.zong.player.language.Voc;


/**
 * This dialog shows information on
 * the application and the open score.
 * 
 * TODO: rework completely.
 * 
 * @author Andreas Wenger
 */
public class InfoDialog
  extends JDialog
{

  TitledBorder titledBorder1;
  JPanel pnlMain = new JPanel();
  JPanel pnlApp = new JPanel();
  JPanel pnlConsole = new JPanel();

  JPanel pnlScore = new JPanel();
  JTabbedPane tabMain = new JTabbedPane();
  Border border1;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel pnlButtons = new JPanel();
  JButton btnClose = new JButton();
  Border border2;
  Border border3;
  JScrollPane scroll_txtScore = new JScrollPane();
  JTextArea txtScore = new JTextArea();
  JScrollPane scroll_txtApp = new JScrollPane();
  JTextArea txtApp = new JTextArea();


  public InfoDialog(Frame parentFrame, String title, boolean modal)
  {
    super(parentFrame, title, modal);
    try
    {
      init();
      setIconImages(parentFrame.getIconImages());
      pack();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }


  public void setInformation(Score score)
  {
  	String sScore = "";
    String sTemp = "";

    //score info
    if (score != null)
    {
    	ScoreInfo info = score.getScoreInfo();
	    sTemp = notNull(score.getScoreInfo().getTitle(), Lang.get(Voc.Label_UnnamedScore));
	    sScore += sTemp + "\n" + stars(sTemp.length()) + "\n\n";
	
	    sScore += fillWithSpaces(Lang.get(Voc.Label_WorkNumber), 24)
	      + notNull(info.getWorkNumber()) + "\n";
	    sScore += fillWithSpaces(Lang.get(Voc.Label_WorkTitle), 24)
	      + notNull(info.getWorkTitle()) + "\n";
	    sScore += fillWithSpaces(Lang.get(Voc.Label_MovementNumber), 24)
	      + notNull(info.getMovementNumber()) + "\n";
	    sScore += fillWithSpaces(Lang.get(Voc.Label_MovementTitle), 24)
	      + notNull(info.getMovementTitle()) + "\n";
	    sScore += "\n";
	
	    sScore += Lang.get(Voc.Label_Creators) + "\n";
	    if (info.getCreators().size() > 0)
	    {
	      for (Creator creator : info.getCreators())
	      {
	        sScore += fillWithSpaces("   " + notNull(creator.getType()) + ":", 24) +
	        notNull(creator.getName()) + "\n";
	      }
	    }
	    else
	      sScore += "   -\n";
	    sScore += "\n";
	
	    if (info.getRights().size() > 0)
	    {
	      sScore += fillWithSpaces(Lang.get(Voc.Label_Rights), 24) + notNull(info.getRights().get(0).getText()) + "\n";
	      for (int i = 1; i < info.getRights().size(); i++)
	      {
	        sScore += fillWithSpaces("", 24) + notNull(info.getRights().get(1).getText()) + "\n";
	      }
	    }
	    else
	      sScore += fillWithSpaces(Lang.get(Voc.Label_Rights), 24) + "-\n";
	    sScore += "\n";
	
	    /* TODO
	    sScore += fillWithSpaces("encoding-date:", 24)
	      + MusicXMLDocumentInfo.getEncodingDate(doc) + "\n";
	    sScore += fillWithSpaces("encoder:", 24) + MusicXMLDocumentInfo.getEncoder(doc)
	      + "\n";
	    sScore += fillWithSpaces("software:", 24) + MusicXMLDocumentInfo.getSoftware(doc)
	      + "\n";
	    sScore += fillWithSpaces("encoding-description:", 24)
	      + MusicXMLDocumentInfo.getEncodingDescription(doc) + "\n";
	    sScore += "\n";
	    */
	
	    sScore += Lang.get(Voc.Label_Parts) + "\n";
	    It<Part> parts = new It<Part>(score.getParts());
      for (Part part : parts)
      {
        sScore += fillWithSpaces("   " + parts.getIndex() + ":", 24) + part.getName() + "\n";
      }
	    
    }
    else
    {
      sScore = Lang.get(Voc.Label_NoFileLoaded);
    }
    txtScore.setText(sScore);

    //program info
    String sApp =
    	Player.getProjectName() + "\n" + stars(Player.getProjectName().length())
      + "\n\n" + fillWithSpaces(Lang.get(Voc.Label_Version), 16) + Zong.PROJECT_VERSION + "." + Zong.PROJECT_ITERATION + "\n"
      + fillWithSpaces(Lang.get(Voc.Label_Description), 16)
      + fillWithSpacesAfterLineBreaks(Lang.get(Voc.Label_DescriptionContent), 16) + "\n\n"
      + fillWithSpaces(Lang.get(Voc.Label_License), 16) + "GPL" + "\n"
      + fillWithSpaces(Lang.get(Voc.Label_Copyright), 16)
      + fillWithSpacesAfterLineBreaks(Lang.get(Voc.Label_CopyrightContent), 16) + "\n\n"
      + Lang.get(Voc.Label_MoreInfoContent);
    txtApp.setText(sApp);
  }
  
  
  private String stars(int len)
  {
  	String res = "";
    for (int i = 0; i < len; i++)
      res += "*";
    return res;
  }


  private String fillWithSpaces(String s, int len)
  {
    String res = s;
    for (int i = 0; i < len - s.length(); i++)
      res += " ";
    return res;
  }
  
  
  private String fillWithSpacesAfterLineBreaks(String s, int len)
  {
    String spaces = "\n";
    for (int i = 0; i < len; i++)
    	spaces += " ";
    return s.replaceAll("\\n", spaces);
  }


  private void init() throws Exception
  {
  	setLocationByPlatform(true);
    titledBorder1 = new TitledBorder("");
    border1 = BorderFactory.createEmptyBorder(4, 4, 4, 4);
    border2 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(
      BevelBorder.LOWERED, Color.white, Color.white, new Color(124, 124, 124), new Color(
        178, 178, 178)), BorderFactory.createEmptyBorder(2, 2, 2, 2));
    border3 = BorderFactory.createEmptyBorder(0, 2, 0, 2);
    pnlMain.setBorder(border1);
    pnlMain.setMinimumSize(new Dimension(423, 379));
    pnlMain.setLayout(borderLayout1);
    this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    this.setModal(false);
    this.setTitle(Player.getProjectName() + " - " + Lang.get(Voc.Label_Info));
    btnClose.setText(Lang.get(Voc.Label_Close));
    btnClose.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e)
    	{
    		InfoDialog.this.dispose();
    	}
    });
    txtScore.setFont(new java.awt.Font("Monospaced", 0, 12));
    txtScore.setBorder(border3);
    txtScore.setRequestFocusEnabled(true);
    txtScore.setVerifyInputWhenFocusTarget(true);
    txtScore.setEditable(false);
    txtScore.setMargin(new Insets(1, 1, 1, 1));
    txtScore.setText("");
    txtScore.setColumns(0);
    txtScore.setLineWrap(false);
    scroll_txtScore.setMaximumSize(new Dimension(400, 300));
    scroll_txtScore.setMinimumSize(new Dimension(400, 300));
    scroll_txtScore.setPreferredSize(new Dimension(400, 300));
    txtApp.setLineWrap(false);
    txtApp.setColumns(0);
    txtApp.setText("");
    txtApp.setMargin(new Insets(1, 1, 1, 1));
    txtApp.setEditable(false);
    txtApp.setVerifyInputWhenFocusTarget(true);
    txtApp.setRequestFocusEnabled(true);
    txtApp.setBorder(border3);
    txtApp.setFont(new java.awt.Font("Monospaced", 0, 12));
    scroll_txtApp.setMaximumSize(new Dimension(400, 300));
    scroll_txtApp.setMinimumSize(new Dimension(400, 300));
    scroll_txtApp.setPreferredSize(new Dimension(400, 300));
    this.getContentPane().add(pnlMain, BorderLayout.NORTH);
    tabMain.add(pnlScore, Lang.get(Voc.Label_ScoreInfo));
    pnlScore.add(scroll_txtScore, null);
    tabMain.add(pnlApp, Lang.get(Voc.Label_ProgramInfo));
    pnlApp.add(scroll_txtApp, null);
    scroll_txtApp.getViewport().add(txtApp, null);
    pnlMain.add(pnlButtons, BorderLayout.SOUTH);
    pnlMain.add(tabMain, BorderLayout.CENTER);
    pnlButtons.add(btnClose, null);
    scroll_txtScore.getViewport().add(txtScore, null);
  }


	public void setActivePage(int tabIndex)
	{
	  tabMain.setSelectedIndex(tabIndex);
	}
  

}
