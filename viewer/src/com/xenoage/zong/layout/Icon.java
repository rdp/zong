package com.xenoage.zong.layout;

import com.xenoage.zong.app.tools.Tool;
import com.xenoage.zong.commands.Command;


public class Icon
{

	private String texturePath;
	private String hoverDescription;
	private Command command;
	private Tool tool;


	public Icon(String texturePath, String hoverDescription, Command command)
	{
		this.texturePath = texturePath;
		this.hoverDescription = hoverDescription;
		this.command = command;
	}


	public Icon(String texturePath, String hoverDescription, Tool tool)
	{
		this.texturePath = texturePath;
		this.hoverDescription = hoverDescription;
		this.tool = tool;
	}


	public Icon(String texturePath, String hoverDescription)
	{
		this.texturePath = texturePath;
		this.hoverDescription = hoverDescription;
	}
	
	public String getHoverDescription()
	{
		return hoverDescription;
	}
	
	public Command getCommand()
	{
		return command;
	}
	
	public Tool getTool()
	{
		return tool;
	}
	
	public String getTexturePath()
	{
		return texturePath;
	}
}
