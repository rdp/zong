package com.xenoage.zong.data.format;

import com.xenoage.zong.data.music.layout.PageBreak;
import com.xenoage.zong.data.music.layout.SystemBreak;


/**
 * Layout information for a measure column:
 * 
 * Is there a system break or a page break forced
 * or prohibited?
 * 
 * @author Andreas Wenger
 */
public class MeasureLayout
{
	public PageBreak pageBreak = null;
	public SystemBreak systemBreak = null;
}