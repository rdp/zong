package com.xenoage.zong.io.musicxml.in;

import static org.junit.Assert.*;

import java.io.FileInputStream;

import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.StavesList;
import com.xenoage.zong.data.music.BracketGroupStyle;
import com.xenoage.zong.data.music.barline.BarlineGroupStyle;
import com.xenoage.zong.io.musicxml.in.MxlStavesList;
import com.xenoage.zong.util.xml.ZongMarshalling;

import org.junit.Test;

import proxymusic.ScorePartwise;


/**
 * Test cases for a {@link MxlStavesList} class.
 *
 * @author Andreas Wenger
 */
public class MxlStavesListTest
{
  
  /**
   * Reads and checks the parts and staves of the file "BeetAnGeSample.xml"
   * from the MusicXML 1.1 sample files.
   */
  @Test public void testStavesList1()
  {
    StavesList sl = createStavesList("../shared/data/test/musicxml11/BeetAnGeSample.xml");
    //parts and staves
    assertEquals(2, sl.getParts().size());
    assertEquals(3, sl.getStavesCount());
    assertEquals(0, sl.getPartStartIndex(sl.getParts().get(0)));
    assertEquals(0, sl.getPartEndIndex(sl.getParts().get(0)));
    assertEquals(1, sl.getPartStartIndex(sl.getParts().get(1)));
    assertEquals(2, sl.getPartEndIndex(sl.getParts().get(1)));
    assertEquals(15, sl.getMeasuresCount());
    //barline groups (implicitly added)
    assertNull(sl.getBarlineGroup(0));
    assertNotNull(sl.getBarlineGroup(1));
    assertNotNull(sl.getBarlineGroup(2));
    assertEquals(1, sl.getBarlineGroup(1).getStartIndex());
    assertEquals(2, sl.getBarlineGroup(1).getEndIndex());
    assertEquals(BarlineGroupStyle.Common, sl.getBarlineGroup(1).getStyle());
    //bracket groups (implicitly added)
    assertEquals(1, sl.getBracketGroupsCount());
    assertEquals(1, sl.getBracketGroup(0).getStartIndex());
    assertEquals(2, sl.getBracketGroup(0).getEndIndex());
    assertEquals(BracketGroupStyle.Brace, sl.getBracketGroup(0).getStyle());
  }
  
  
  /**
   * Reads and checks the parts and staves of the file "ActorPreludeSample.xml"
   * from the MusicXML 1.1 sample files.
   */
  @Test public void testStavesList2()
  {
    StavesList sl = createStavesList("../shared/data/test/musicxml11/ActorPreludeSample.xml");
    assertEquals(22, sl.getParts().size());
    assertEquals(23, sl.getStavesCount());
    assertEquals(41, sl.getMeasuresCount());
    //barline groups
    //(5, as can be seen on the first page of the PDF)
    assertEquals(5, sl.getBarlineGroupsCount());
    for (int i = 0; i < 23; i++)
    {
      assertNotNull("failed at " + i, sl.getBarlineGroup(i));
      assertEquals("failed at " + i, BarlineGroupStyle.Common, sl.getBarlineGroup(i).getStyle());
    }
    //bracket groups
    //(8, as can be seen on the first page of the PDF)
    assertEquals(8, sl.getBracketGroupsCount());
  }
  
  
  private StavesList createStavesList(String filePath)
  {
  	ScorePartwise doc = null;
    try
    {
      FileInputStream in = new FileInputStream(filePath);
      doc = ZongMarshalling.unmarshal(in);
    }
    catch (Exception ex)
    {
      fail(ex.toString());
    }
    MxlStavesList mxlSL = null;
    try
    {
    	mxlSL = new MxlStavesList(doc, new Score());
    }
    catch (Exception ex)
    {
      fail(ex.toString());
    }
    return mxlSL.getStavesList();
  }

}
