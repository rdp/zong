package com.xenoage.zong.data.info;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;


/**
 * Test cases for a ScoreInfo.
 *
 * @author Andreas Wenger
 */
public class ScoreInfoTest
{

  @Test public void testCreatorsAndRights()
  {
    ScoreInfo scoreInfo = new ScoreInfo(); 
    Creator c1 = new Creator("Bernhard", "Composer");
    Creator c2 = new Creator("Andi", "Arranger");
    Rights r1 = new Rights("(c) 2006", "Copyright");
    Rights r2 = new Rights("Gema-frei", "License");
    //set data
    scoreInfo.addCreator(c1);
    scoreInfo.addCreator(c2);
    scoreInfo.addRights(r1);
    scoreInfo.addRights(r2);
    //test data
    List<Creator> ci = scoreInfo.getCreators();
    assertTrue(ci.get(0).getName().equals("Bernhard"));
    assertTrue(ci.get(1).getType().equals("Arranger"));
    List<Rights> ri = scoreInfo.getRights();
    assertTrue(ri.get(0).getText().equals("(c) 2006"));
    assertTrue(ri.get(1).getType().equals("License"));
  }
  
}



