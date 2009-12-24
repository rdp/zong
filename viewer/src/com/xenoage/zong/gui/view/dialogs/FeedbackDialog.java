package com.xenoage.zong.gui.view.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.xenoage.util.FileTools;
import com.xenoage.util.language.Lang;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.gui.controller.dialogs.FeedbackDialogController;


/**
 * This dialog allows the user to send in error reports
 * and feature requests.
 * 
 * If a path to a document is given, a checkbox to send
 * this document is shown.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class FeedbackDialog
	extends JDialog
{
	
	private FeedbackDialogController controller;
	private JLabel lblBugtracker;
	
	private JTextArea area, email;
	private JCheckBox chkLog, chkDoc = null;
	

	public FeedbackDialog(FeedbackDialogController controller, String documentPath, String text)
	{
		this.controller = controller;
		init(documentPath, text);
	}


	private void init(String documentPath, String text)
	{
		//set title
		setTitle(Lang.get(Voc.Feedback_Title));
		
		//icons
		try
		{
			setIconImages(App.getInstance().getMainFrame().getIconImages());
		}
		catch (Exception ex)
		{
		}

		//set sizes
		//setMinimumSize(new Dimension(300, 200));
		setResizable(false);

		//create Layout
		JPanel panel = new JPanel();

		int border=10;
		double pref = TableLayout.PREFERRED;
		double table[][] = {
			{ border, TableLayout.FILL , border},		//columns
			{ border, pref, border, pref, border, pref, border, 200, border, pref, pref,
				border, pref, border, pref, border}};//rows
		panel.setLayout(new TableLayout(table));
		int ypos = 1;
		
		//Box panel = Box.createVerticalBox();
		//panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		//Description
		JLabel label1 = new JLabel(Lang.get(Voc.Feedback_Description));
		label1.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(label1, "1,"+ypos);
		ypos += 2;

		//link to bugtracker
		lblBugtracker = new JLabel(Lang.get(Voc.Feedback_ExperiencedUsers));
		lblBugtracker.setForeground(new Color(0, 0, 150));
		lblBugtracker.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblBugtracker.addMouseListener(new MouseAdapter() {
			@Override public void mouseEntered(MouseEvent e)
			{
				lblBugtracker.setText("<html><u>" + Lang.get(Voc.Feedback_ExperiencedUsers) + "</u></html>");
			}
			@Override public void mouseExited(MouseEvent e)
			{
				lblBugtracker.setText(Lang.get(Voc.Feedback_ExperiencedUsers));
			}
			@Override public void mouseClicked(MouseEvent e)
			{
				controller.openBugtrackerWebsite();
			}
		});
		panel.add(lblBugtracker, "1,"+ypos);
		ypos+=2;

		//headline textarea
		JLabel label3 = new JLabel(Lang.get(Voc.Feedback_HeadlineTextarea));
		panel.add(label3, "1,"+ypos);
		ypos+=2;
		
		//Textarea
		area = new JTextArea();
		area.setWrapStyleWord(true);
		area.setLineWrap(true);
		area.setEditable(true);
		if (text != null)
		{
			area.setText(text);
		}
		panel.add(area, "1,"+ypos);
		ypos += 2;

		//attach log
		chkLog = new JCheckBox(Lang.get(Voc.Feedback_AttachLog));
		chkLog.setSelected(true);
		panel.add(chkLog, "1,"+ypos);
		ypos+=1;

		if (documentPath != null)
		{
			//attach file
			chkDoc = new JCheckBox(Lang.get(Voc.Feedback_AttachFile, FileTools.getFileName(documentPath)));
			chkDoc.setSelected(true);
			panel.add(chkDoc, "1,"+ypos);
		}
		ypos+=2;
		
		//Email Panel
		JPanel emailPanel = new JPanel();
		emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.LINE_AXIS));
		JLabel labelMail = new JLabel(Lang.get(Voc.Feedback_LabelMail));
		emailPanel.add(labelMail);
		emailPanel.add(new JLabel(" ")); //space
		email = new JTextArea();
		emailPanel.add(email);
		panel.add(emailPanel, "1,"+ypos);
		ypos+=2;
		
		//button panel
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout(FlowLayout.RIGHT, border,0));
		JButton btnSend = new JButton(Lang.get(Voc.Feedback_Send));
		btnSend.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				FeedbackDialog.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				if (controller.send())
					setVisible(false);
				FeedbackDialog.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		buttons.add(btnSend);
		JButton btnCancel = new JButton(Lang.get(Voc.Feedback_Cancel));
		btnCancel.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		buttons.add(btnCancel);
		panel.add(buttons, "1,"+ypos);

		getContentPane().add(panel);
		pack();
	}
	
	
	public String getText()
	{
		return area.getText();
	}
	
	
	public boolean isSendLog()
	{
		return chkLog.isSelected();
	}
	
	
	public boolean isSendDoc()
	{
		return chkLog.isSelected();
	}
	
	
	public String getEmail()
	{
		return email.getText();
	}
	
	
}
