package com.xenoage.zong.documents;

import static org.junit.Assert.*;

import com.xenoage.zong.data.Score;

import java.io.File;

import org.junit.Test;


/**
 * Test cases for a ScoreDocument.
 *
 * @author Andreas Wenger
 */
public class ScoreDocumentTest
{

  @Test public void createScoreDocument()
  {
    Score score = new Score();
    //create score document without filename
    ScoreDocument doc = new ScoreDocument(score, null, null);
    assertNotNull(doc);
    assertTrue(doc.getScore(0) == score);
    //create score document with filename
    String filename = "test.xml";
    doc = new ScoreDocument(score, filename, null);
    assertTrue(filename.equals(doc.getFilePath()));
  }
  
  
  @Test(expected = IllegalArgumentException.class)
  public void createScoreDocumentError()
  {
    //parameter score may not be null
    new ScoreDocument(null, null, null);
  }

  
}
