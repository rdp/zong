package com.xenoage.zong.data;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * Test cases for a StavesList.
 *
 * @author Andreas Wenger
 */
public class StavesListTest
{
  
  @Test public void testStavesList()
  {
    Score score = new Score();
    StavesList stavesList = score.stavesList;
    //new staves list must be empty
    assertEquals(0, stavesList.getStavesCount());
    //add a new part with 2 staves
    score.addPart(0, 2);
    assertEquals(2, stavesList.getStavesCount());
    //add another part with 3 staves after the first one
    score.addPart(1, 3);
    assertEquals(5, stavesList.getStavesCount());
    //add another part with 1 staff before the other ones
    score.addPart(0, 1);
    assertEquals(6, stavesList.getStavesCount());
    //the first part must have 1 staff, the second one 2,
    //the third one 3
    assertEquals(0, stavesList.getPartStartIndex(stavesList.parts.get(0)));
    assertEquals(0, stavesList.getPartEndIndex(stavesList.parts.get(0)));
    assertEquals(1, stavesList.getPartStartIndex(stavesList.parts.get(1)));
    assertEquals(2, stavesList.getPartEndIndex(stavesList.parts.get(1)));
    assertEquals(3, stavesList.getPartStartIndex(stavesList.parts.get(2)));
    assertEquals(5, stavesList.getPartEndIndex(stavesList.parts.get(2)));
  }

}
