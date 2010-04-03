package com.xenoage.zong.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.io.IO;
import com.xenoage.util.language.Lang;
import com.xenoage.util.language.LanguageComponent;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.DesktopApp;
import com.xenoage.zong.app.language.Voc;


/**
 * Tabbed pane with close buttons, that can show a score
 * in the content pane (but also other components are possible,
 * like web pages (not yet)).
 * 
 * @author Andreas Wenger
 */
public class TabbedPane
	extends JTabbedPane
	implements MouseListener, LanguageComponent, ChangeListener
{
	
	private Component scorePanel;
	private int currentScorePanelTabIndex = -1;
	
	private ArrayList<JLabel> closeLabels = new ArrayList<JLabel>();
  private Icon iconCloseButtonHover = null;
  private Icon iconCloseButton = null;
  
  
  /**
   * Creates a new {@link TabbedPane}.
   * @param scorePanel  the single score panel, that is reused for each
   *                    content pane where it is needed
   */
  public TabbedPane(Component scorePanel)
  {
  	super(JTabbedPane.TOP);
  	this.scorePanel = scorePanel;
  	//load tab close buttons
    try
    {
      iconCloseButtonHover = new ImageIcon(ImageIO.read(
        IO.openInputStream("data/img/menu/16/fileclose.png")));
      iconCloseButton = new ImageIcon(ImageIO.read(
        IO.openInputStream("data/img/menu/16/fileclose2.png")));
    }
    catch (Exception ex)
    {
    	App.err().report(ErrorLevel.Remark, "Close icons are missing", ex);
    }
    this.addChangeListener(this);
  }
  
  
  /**
   * Adds a tab.
   */
  public void addTab(String title)
  {
    super.addTab(title, new JPanel(new BorderLayout()));
    //create custom tab content with close button
    int tabIndex = getTabCount() - 1;
    JPanel tabPanel = new JPanel();
    tabPanel.setBackground(new Color(0, 0, 0, 0));
    tabPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
    JLabel tabLabelText = new JLabel(title);
    tabPanel.add(tabLabelText);
    JLabel tabLabelIcon = new JLabel(iconCloseButton);
    closeLabels.add(tabLabelIcon);
    tabLabelIcon.addMouseListener(this);
    Lang.registerComponent(this);
    tabLabelIcon.setToolTipText(Lang.get(Voc.Label_CloseDocument));
    tabLabelIcon.setVisible(false);
    tabPanel.add(tabLabelIcon);
    setTabComponentAt(tabIndex, tabPanel);
  }
  
  
  /**
   * Removes the tab with the given index.
   */
  public void removeTab(int index)
  {
    closeLabels.remove(index);
    remove(index);
  }


  public void mouseClicked(MouseEvent e)
  {
    //close button?
    int tabIndex = closeLabels.indexOf(e.getSource());
    if (tabIndex >= 0)
    {
    	DesktopApp.getInstance().requestCloseDocument(tabIndex);
    	if (changeListener != null)
    		changeListener.stateChanged(new ChangeEvent(this));
    }
  }


  public void mouseEntered(MouseEvent e)
  {
  	//close button?
    int tabIndex = closeLabels.indexOf(e.getSource());
    if (tabIndex >= 0)
    {
    	((JLabel) e.getSource()).setIcon(iconCloseButtonHover);
    }
  }


  public void mouseExited(MouseEvent e)
  {
  	int tabIndex = closeLabels.indexOf(e.getSource());
    if (tabIndex >= 0)
    {
    	((JLabel) e.getSource()).setIcon(iconCloseButton);
    }
  }


  public void mousePressed(MouseEvent arg0)
  {
  }


  public void mouseReleased(MouseEvent arg0)
  {
  }


	@Override public void languageChanged()
	{
		for (JLabel closeLabel : closeLabels)
		{
			closeLabel.setToolTipText(Lang.get(Voc.Label_CloseDocument));
		}
	}


	@Override public void stateChanged(ChangeEvent e)
	{
		DesktopApp app = DesktopApp.getInstance();
		app.setActiveDocument(getSelectedIndex());
		if (app.getScoreDocument() != null && getSelectedIndex() != currentScorePanelTabIndex)
  	{
			if (currentScorePanelTabIndex != -1)
				((JPanel) getComponentAt(getSelectedIndex())).remove(scorePanel);
			((JPanel) getComponentAt(getSelectedIndex())).add(scorePanel, BorderLayout.CENTER);
			currentScorePanelTabIndex = getSelectedIndex();
			scorePanel.repaint();
  	}
		//if there is just one tab left, don't show close button
		boolean onlyOneLabel = (closeLabels.size() == 1);
		for (JLabel label : closeLabels)
		{
			label.setVisible(!onlyOneLabel);
		}
	}
  

}
