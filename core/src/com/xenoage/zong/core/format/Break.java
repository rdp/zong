package com.xenoage.zong.core.format;

import com.xenoage.zong.core.music.ColumnElement;
import com.xenoage.zong.core.music.layout.PageBreak;
import com.xenoage.zong.core.music.layout.SystemBreak;


/**
 * Break information for a measure column:
 * Is there a system break or a page break forced
 * or prohibited?
 * 
 * @author Andreas Wenger
 */
public final class Break
	implements ColumnElement
{
	
	private final PageBreak pageBreak;
	private final SystemBreak systemBreak;
	
	
	public Break(PageBreak pageBreak, SystemBreak systemBreak)
	{
		this.pageBreak = pageBreak;
		this.systemBreak = systemBreak;
	}

	
	public PageBreak getPageBreak()
	{
		return pageBreak;
	}

	
	public SystemBreak getSystemBreak()
	{
		return systemBreak;
	}
	
	
}