package com.xenoage.zong.renderer.screen;

import static org.junit.Assert.*;

import java.awt.TexturePaint;

import org.junit.Test;


/**
 * Test cases for a Texture.
 *
 * @author Andreas Wenger
 */
public class TextureTest
{

  @Test public void createDesktopPaint()
  {
    Texture p = new Texture("data/img/desktop/blue.png");
    assertNotNull(p.getPaint());
    assertTrue(p.getPaint() instanceof TexturePaint);
  }
  
}
