package com.xenoage.util;

import static com.xenoage.util.StringTools.*;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xenoage.util.StringTools;


/**
 * Test cases for a {@link StringTools} class.
 *
 * @author Andreas Wenger
 */
public class StringToolsTest
{

  
  @Test public void trimRightTest()
  {
  	assertEquals("abc", trimRight("abc"));
    assertEquals("abc", trimRight("abc "));
    assertEquals("abc", trimRight("abc    "));
    assertEquals("a b c", trimRight("a b c"));
    assertEquals("a  b c", trimRight("a  b c    "));
  }
  
}
