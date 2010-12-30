package com.xenoage.zong.gui.view.dialogs;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import info.clearthought.layout.TableLayout;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.xenoage.util.language.Lang;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.gui.controller.dialogs.ErrorDialogController;
import com.xenoage.zong.util.ImageTools;
import com.xenoage.zong.util.LookAndFeel;
import com.xenoage.zong.util.SwingTools;


/**
 * This dialog shows an error or warning message
 * together with an icon.
 * 
 * Optionally, the stack trace of the error can be shown.
 * Optionally, the error can be mailed to the Score development team.
 * Optionally, a file can be attached to this e-mail.
 * 
 * @author Andreas Wenger
 */
public class ErrorDialog
	extends JDialog
{
	
	private ErrorDialogController controller;
	
	
	/**
	 * Creates an {@link ErrorDialog} with the given options.
	 */
	public ErrorDialog(ErrorDialogController controller, String title, String message, Icon icon,
		boolean showDetails, boolean showErrorReport, boolean code, Frame parentFrame)
	{
		super(parentFrame, true);
		init(controller, title, message, icon, showDetails, showErrorReport, code);
	}
	
	
	/**
	 * Creates an {@link ErrorDialog} with the given options.
	 */
	public ErrorDialog(ErrorDialogController controller, String title, String message, Icon icon,
		boolean showDetails, boolean showErrorReport, boolean code, Dialog parentDialog)
	{
		super(parentDialog, true);
		init(controller, title, message, icon, showDetails, showErrorReport, code);
	}


	private void init(ErrorDialogController controller, String title, String message,
		Icon icon, boolean showDetails, boolean showErrorReport, boolean code)
	{
		this.controller = controller;
		//create layout
		setPreferredSize(new Dimension(600, 240));
		int border = 10;
    double table[][] =
        {{0, 80, TableLayout.FILL, border},  //columns
         {2 * border, 80, TableLayout.FILL, border, TableLayout.PREFERRED, border}}; //rows
    int buttonWidth = 120;
    setLayout(new TableLayout(table));
		//title
		setTitle(title);
		//icon
		JLabel labelIcon = new JLabel(icon);
		labelIcon.setVerticalAlignment(JLabel.TOP);
		add(labelIcon, "1, 1, 1, 1");
		//message
		JTextArea labelMessage = new JTextArea(message);
		if (code)
		{
			labelMessage.setFont(new Font("Monospaced", Font.PLAIN, labelMessage.getFont().getSize()));
		}
		else
		{
			labelMessage.setWrapStyleWord(true);
			labelMessage.setLineWrap(true);
		}
		labelMessage.setEditable(false);
		JScrollPane scrollMessage = new JScrollPane(labelMessage);
		add(scrollMessage, "2, 1, 2, 2");
		//buttons
		JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, border, 0));
		if (showErrorReport)
		{
			JButton btnEMail = new JButton(Lang.get(Voc.Buttons_ReportError), ImageTools.tryToLoadIcon("data/img/icons/email16.png"));
			SwingTools.setMinimumWidth(btnEMail, buttonWidth);
			btnEMail.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					getController().reportError();
				}});
			panelButtons.add(btnEMail);
		}
		if (showDetails)
		{
			JButton btnDetails = new JButton(Lang.get(Voc.Buttons_Details), ImageTools.tryToLoadIcon("data/img/icons/details16.png"));
			SwingTools.setMinimumWidth(btnDetails, buttonWidth);
			btnDetails.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					getController().showDetails();
				}});
			panelButtons.add(btnDetails);
		}
		//TODO: ok16 is no good icon for an error message... we hate error messages, we don't want to see a green checkmark
		JButton btnOK = new JButton(Lang.get(Voc.Buttons_Close), ImageTools.tryToLoadIcon("data/img/icons/ok16.png"));
		SwingTools.setMinimumWidth(btnOK, buttonWidth);
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}});
		panelButtons.add(btnOK);
		add(panelButtons, "1, 4, 3, 4");
		//pack and position
		pack();
    setLocationRelativeTo(null);
	}
	
	
	private ErrorDialogController getController()
	{
		return controller;
	}
}
