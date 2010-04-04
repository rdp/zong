package com.xenoage.zong.renderer.screen.page;

import javax.media.opengl.GL;

import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Rectangle2i;
import com.xenoage.util.math.Size2f;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.core.format.PageFormat;
import com.xenoage.zong.layout.LayoutPosition;
import com.xenoage.zong.layout.Page;
import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.renderer.GLGraphicsContext;
import com.xenoage.zong.renderer.GraphicsContext;
import com.xenoage.util.Units;
import com.xenoage.zong.view.ScorePageView;


/**
 * This class renders a page view of a score document
 * on the screen with OpenGL.
 *
 * @author Andreas Wenger
 */
public class GLPageRenderer
  implements PageLayoutScreenRenderer
{
  
  private static GLPageRenderer instance = null;
  
  //TEST
  private long startTime;
  private int frameCount;
  private float fps;
  
  
  /**
   * Gets the renderer for page views of score documents.
   */
  public static GLPageRenderer getInstance()
  {
    if (instance == null)
      instance = new GLPageRenderer();
    return instance;
  }
  
  
  private GLPageRenderer()
  {
  }
  
  
  /**
   * Paints the background with the current renderer settings.
   */
  public void paintDesktop(GraphicsContext g)
  {
    GLGraphicsContext glContext = ((GLGraphicsContext) g);
    
    //draw the desktop
    paintDesktop(glContext);
  }


  /**
   * Paints the given page view with the current renderer settings.
   */
  public void paintPages(ScorePageView pageView, GraphicsContext g)
  {
    GLGraphicsContext glContext = ((GLGraphicsContext) g);
    
    GL gl = glContext.getGL();
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    
    //draw pages
    GLGraphicsContext pageContext = glContext;
    //draw paper
    //TODO: compute interesting page range and coordinates one time,
    //and then begin the rendering of pages, frames, and handles. this
    //saves lots of time
    for (int i = 0; i < pageView.getLayout().getPages().size(); i++)
    {
      Page page = pageView.getLayout().getPages().get(i);
      Rectangle2i pageRect = computePageRect(i, page.getFormat(), pageView);
      if (isRectVisible(glContext.getWindowRect().size, pageRect))
      {
        paintPaper(glContext, pageRect, page.getFormat().getSize(), pageView.getCurrentScaling());
      }
    }
    //draw frames
    for (int i = 0; i < pageView.getLayout().getPages().size(); i++)
    {
      Page page = pageView.getLayout().getPages().get(i);
      Rectangle2i pageRect = computePageRect(i, page.getFormat(), pageView);
      if (isRectVisible(glContext.getWindowRect().size, pageRect))
      {
        //activate page clipping
        int x1 = pageRect.position.x;
        int width = pageRect.size.width - 1;
        int y2 = pageContext.getWindowRect().size.height - pageRect.position.y;
        int y1 = y2 - pageRect.size.height;
        int height = y2 - y1 - 1;
        //clamp to values inside the window (otherwise everything is
        //clipped out with some drivers, e.g. free ATI drivers on Andi's notebook)
        if (x1 < 0)
        {
        	width += x1;
        	x1 = 0;
        }
        if (y2 > pageContext.getWindowRect().size.height)
        {
        	height -= y2 - pageContext.getWindowRect().size.height;
        }
        //clip
        gl.glScissor(x1, y1, width, height);
        gl.glEnable(GL.GL_SCISSOR_TEST);
        
        //draw frames
        pageContext = pageContext.changeOriginOffset(pageRect.position);
        for (Frame frame : page.getFrames())
        {
          frame.paint(pageContext);
        }
        
        //deactivate page clipping
        gl.glDisable(GL.GL_SCISSOR_TEST);
        
      }
    }
    //draw adjustment handles of the frames
    for (int i = 0; i < pageView.getLayout().getPages().size(); i++)
    {
      Page page = pageView.getLayout().getPages().get(i);
      Rectangle2i pageRect = computePageRect(i, page.getFormat(), pageView);
      if (isRectVisible(glContext.getWindowRect().size, pageRect))
      {
        pageContext = pageContext.changeOriginOffset(pageRect.position);
        for (Frame frame : page.getFrames())
        {
          frame.paintHandles(pageContext);
        }
      }
    }
    
    //TEST: FPS
    //(quick repaints are needed for a correct result.
    //scroll as fast as you can for some seconds).
    if (startTime == 0) {
      startTime = System.currentTimeMillis();
    }
    if (++frameCount == 30) {
      long endTime = System.currentTimeMillis();
      fps = 30.0f / (float) (endTime - startTime) * 1000;
      frameCount = 0;
      startTime = System.currentTimeMillis();
    }
    if (fps > 0) {
      //TEST
    	//System.out.println("FPS: " + ((int) fps));
    }
    
  }
  
  
  /**
   * Paints the desktop over the whole window.
   */
  private void paintDesktop(GLGraphicsContext g)
  {
    Rectangle2i windowRect = g.getWindowRect();
    float width = windowRect.size.width;
    float height = windowRect.size.height;
    GL gl = g.getGL();
    gl.glEnable(GL.GL_TEXTURE_2D);
    g.getTextureManager().activateAppTexture(TextureManager.IDBASE_DESKTOPS);
    gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
      GL.GL_LINEAR);
    gl.glColor3f(1, 1, 1);
    gl.glBegin(GL.GL_QUADS);
    gl.glTexCoord2f(0,1); gl.glVertex3f(0, height, 0);
    gl.glTexCoord2f(1,1); gl.glVertex3f(width, height, 0);
    gl.glTexCoord2f(1,0); gl.glVertex3f(width, 0, 0);
    gl.glTexCoord2f(0,0); gl.glVertex3f(0, 0, 0);
    gl.glEnd();
    gl.glDisable(GL.GL_TEXTURE_2D);
  }
  
  
  /**
   * Returns the screen coordinates of the given page.
   */
  private Rectangle2i computePageRect(int pageIndex, PageFormat pageFormat, ScorePageView pageView)
  {
    //get page coordinates in px
    Point2i pagePositionUpperLeftPx = pageView.computeScreenPosition(
      new LayoutPosition(pageIndex, 0, 0));
    Size2f size = pageFormat.getSize();
    Point2i pagePositionLowerRightPx = pageView.computeScreenPosition(
      new LayoutPosition(pageIndex, size.width, size.height));
    //create rectangle
    Rectangle2i ret = new Rectangle2i(pagePositionUpperLeftPx,
    	new Size2i(pagePositionLowerRightPx.x - pagePositionUpperLeftPx.x, pagePositionLowerRightPx.y - pagePositionUpperLeftPx.y));
    return ret;
  }
  
  
  /**
   * Returns true, if the given rectangle is partially
   * or fully visible on the screen, otherwise false.
   */
  boolean isRectVisible(Size2i windowSize, Rectangle2i r)
  {
    return (r.position.x <= windowSize.width && r.position.y <= windowSize.height &&
      r.position.x + r.size.width >= 0 && r.position.y + r.size.height >= 0);
  }
  
  
  /**
   * Paints paper and shadow at the given rectangle.
   */
  private void paintPaper(GLGraphicsContext g, Rectangle2i r, Size2f pageSize, float scaling)
  {
    
    GL gl = g.getGL();
    gl.glEnable(GL.GL_TEXTURE_2D);
    
    float textureWidthMm = 20; //paper texture covers 20x20 mm
    float pWidthTiles = pageSize.width / textureWidthMm;
    float pHeightTiles = pageSize.height / textureWidthMm;
    
    int x1 = r.position.x;
    int y1 = r.position.y;
    int x2 = r.position.x + r.size.width;
    int y2 = r.position.y + r.size.height;
    gl.glColor3f(1, 1, 1);
    
    //draw shadow
    float sd = Units.mmToPxFloat(3f, scaling); //shadow distance: 3mm
    g.getTextureManager().activateAppTexture(TextureManager.IDBASE_SHADOWS);
    gl.glBegin(GL.GL_QUADS);
    //top-right shadow
    gl.glTexCoord2f(0.5f,0.0f); gl.glVertex3f(x2, y1 + sd, 0);
    gl.glTexCoord2f(1,0.0f); gl.glVertex3f(x2 + sd, y1 + sd, 0);
    gl.glTexCoord2f(1,0.4f); gl.glVertex3f(x2 + sd, y1 + 2 * sd, 0);
    gl.glTexCoord2f(0.5f,0.4f); gl.glVertex3f(x2, y1 + 2 * sd, 0);
    //right shadow
    gl.glTexCoord2f(0.5f,0.4f); gl.glVertex3f(x2, y1 + 2 * sd, 0);
    gl.glTexCoord2f(1,0.4f); gl.glVertex3f(x2 + sd, y1 + 2 * sd, 0);
    gl.glTexCoord2f(1,0.6f); gl.glVertex3f(x2 + sd, y2, 0);
    gl.glTexCoord2f(0.5f,0.6f); gl.glVertex3f(x2, y2, 0);
    //bottom-right shadow
    gl.glTexCoord2f(0.5f,0.5f); gl.glVertex3f(x2, y2, 0);
    gl.glTexCoord2f(1,0.5f); gl.glVertex3f(x2 + sd, y2, 0);
    gl.glTexCoord2f(1,1f); gl.glVertex3f(x2 + sd, y2 + sd, 0);
    gl.glTexCoord2f(0.5f,1f); gl.glVertex3f(x2, y2 + sd, 0);
    //bottom shadow
    gl.glTexCoord2f(0.4f,0.5f); gl.glVertex3f(x1 + 2 * sd, y2, 0);
    gl.glTexCoord2f(0.6f,0.5f); gl.glVertex3f(x2, y2, 0);
    gl.glTexCoord2f(0.6f,1f); gl.glVertex3f(x2, y2 + sd, 0);
    gl.glTexCoord2f(0.4f,1f); gl.glVertex3f(x1 + 2 * sd, y2 + sd, 0);
    //bottom-left shadow
    gl.glTexCoord2f(0.0f,0.5f); gl.glVertex3f(x1 + sd, y2, 0);
    gl.glTexCoord2f(0.4f,0.5f); gl.glVertex3f(x1 + 2 * sd, y2, 0);
    gl.glTexCoord2f(0.4f,1f); gl.glVertex3f(x1 + 2 * sd, y2 + sd, 0);
    gl.glTexCoord2f(0.0f,1f); gl.glVertex3f(x1 + sd, y2 + sd, 0);
    gl.glEnd();
    
    //draw paper
    g.getTextureManager().activateAppTexture(TextureManager.IDBASE_PAPERS);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
    gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
      GL.GL_LINEAR_MIPMAP_NEAREST);
    
    //TEST: draw paper in random color
    /*OpenGLTools.setColor(gl, new Color(
    	(int) (Math.random() * 155) + 100,
    	(int) (Math.random() * 155) + 100,
    	(int) (Math.random() * 155) + 100)); //*/
    
    gl.glBegin(GL.GL_QUADS);
    gl.glTexCoord2f(0,0); gl.glVertex3f(x1, y2, 0);
    gl.glTexCoord2f(pWidthTiles,0); gl.glVertex3f(x2, y2, 0);
    gl.glTexCoord2f(pWidthTiles,pHeightTiles); gl.glVertex3f(x2, y1, 0);
    gl.glTexCoord2f(0,pHeightTiles); gl.glVertex3f(x1, y1, 0);
    gl.glEnd();
    
    //draw border
    gl.glDisable(GL.GL_TEXTURE_2D);
    gl.glBegin(GL.GL_LINE_LOOP);
    gl.glColor3f(0, 0, 0);
    gl.glVertex3f(x1, y2, 0);
    gl.glVertex3f(x2, y2, 0);
    gl.glVertex3f(x2, y1, 0);
    gl.glVertex3f(x1, y1, 0);
    gl.glEnd();
  }
  
  
}
