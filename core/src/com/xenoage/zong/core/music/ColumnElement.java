package com.xenoage.zong.core.music;

import com.xenoage.zong.core.format.Break;
import com.xenoage.zong.core.music.barline.Barline;
import com.xenoage.zong.core.music.direction.Tempo;
import com.xenoage.zong.core.music.key.Key;
import com.xenoage.zong.core.music.time.Time;
import com.xenoage.zong.core.music.volta.Volta;


/**
 * Column elements are {@link MusicElement}s, which can
 * appear in a whole measure column.
 * 
 * This is the case for {@link Time}, {@link Barline}, {@link Volta},
 * {@link Key}, {@link Tempo} and {@link Break}.
 * 
 * @author Andreas Wenger
 */
public interface ColumnElement
	extends MusicElement
{

}
