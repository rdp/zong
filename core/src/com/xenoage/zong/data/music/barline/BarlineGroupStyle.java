package com.xenoage.zong.data.music.barline;


/**
 * Style of the barline of a group of staves.
 * 
 * A group of staves can not only have single barlines,
 * but also connected barlines or Mensurstriche.
 * 
 * @author Andreas Wenger
 */
public enum BarlineGroupStyle
{
  
  /** Each staff has its own barlines. */
  Single,
  
  /** The barlines of the staves are connected. */
  Common,
  
  /** Barlines are shown between the staves but not on them. */
  Mensurstrich;

}
