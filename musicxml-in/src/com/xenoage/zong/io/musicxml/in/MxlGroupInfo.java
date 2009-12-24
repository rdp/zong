package com.xenoage.zong.io.musicxml.in;

import proxymusic.PartGroup;


/**
 * This class stores a part-group element of a MusicXML 2.0 document
 * together with its beginning and ending part indices.
 *
 * @author Andreas Wenger
 */
class MxlGroupInfo
{
	
  private final PartGroup mxlPartGroup;
  
  private int startPartIndex = -1;
  private int endPartIndex = -1;
  
  
  public MxlGroupInfo(PartGroup mxlPartGroup)
  {
    this.mxlPartGroup = mxlPartGroup;
  }

  
  public PartGroup getMxlPartGroup()
  {
    return mxlPartGroup;
  }

  
  public int getStartPartIndex()
  {
    return startPartIndex;
  }

  
  public void setStartPartIndex(int startPartIndex)
  {
    this.startPartIndex = startPartIndex;
  }
  
  
  public int getEndPartIndex()
  {
    return endPartIndex;
  }

  
  public void setEndPartIndex(int endPartIndex)
  {
    this.endPartIndex = endPartIndex;
  }


}
