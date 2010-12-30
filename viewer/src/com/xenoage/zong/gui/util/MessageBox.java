package com.xenoage.zong.gui.util;

import java.awt.Component;

import javax.swing.JOptionPane;

import com.xenoage.zong.Zong;
import com.xenoage.zong.app.App;


/**
 * Helper class to show a simple message box.
 * 
 * @author Andreas Wenger
 */
public class MessageBox
{
	
	
	public static void show(String message)
	{
		JOptionPane.showMessageDialog(App.getInstance().getMainFrame(), message, App.getInstance().getName(), JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	public static void show(String message, Component parentComponent)
	{
		JOptionPane.showMessageDialog(parentComponent, message, App.getInstance().getName(), JOptionPane.INFORMATION_MESSAGE);
	}

}
