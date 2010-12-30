package com.xenoage.zong.gui.view.panels;

import java.awt.Color;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;

import com.sun.opengl.util.j2d.TextureRenderer;
import com.xenoage.util.math.Point2i;
import com.xenoage.zong.gui.controller.panels.GLScorePanelController;
import com.xenoage.util.logging.Log;


/**
 * Panel that shows a score.
 * 
 * This panel is a GLCanvas (AWT) for rendering with OpenGL,
 * using all available hardware acceleration.
 *
 * @author Andreas Wenger
 */
public class GLScorePanel
  extends GLCanvas
  implements GLEventListener
{

  private GLScorePanelController controller = null;

  private boolean created;
  private boolean destroyed;
  
  TextureRenderer texren;
  JTextPane txtPane;


  /**
   * Creates a new GLScorePanel.
   * @param controller  the controller of the panel.
   */
  public GLScorePanel(GLScorePanelController controller, GLCapabilities caps)
  {
  	super(caps);
  	this.controller = controller;
  	this.setBackground(Color.white);
  	this.addGLEventListener(this);
  	this.addMouseListener(controller);
  	this.addMouseMotionListener(controller);
  	this.addMouseWheelListener(controller);
  }


  /**
   * Gets the controller of this panel.
   */
  public GLScorePanelController getController()
  {
    return controller;
  }


  /**
   * Shows the given popup menu at the given position.
   */
  public void showPopupMenu(JPopupMenu popupMenu, Point2i position)
  {
    popupMenu.show(this, position.x, position.y);
  }
  
  
  public void init(GLAutoDrawable drawable)
  {
  	GL gl = drawable.getGL();
  	
  	//test if antialiasing works
  	int buf[] = new int[1];
    int sbuf[] = new int[1];
    gl.glGetIntegerv(GL.GL_SAMPLE_BUFFERS, buf, 0);
    Log.log(Log.MESSAGE, "OpenGL: Number of sample buffers is " + buf[0]);
    gl.glGetIntegerv(GL.GL_SAMPLES, sbuf, 0);
    Log.log(Log.MESSAGE, "OpenGL: Number of samples is " + sbuf[0]);
    if (buf[0] > 0 && sbuf[0] > 0)
    {
    	//antialiasing is working
    	Log.log(Log.MESSAGE, "OpenGL: This means that antialiasing is working. Antialiasing ON.");
    	controller.setAntialiasing(true);
    }
    else
    {
    	Log.log(Log.MESSAGE, "OpenGL: This means that antialiasing is not working. Antialiasing OFF.");
    	controller.setAntialiasing(false);
    }
  	
    controller.init(gl);
  }


  public void display(GLAutoDrawable drawable)
  {
  	//JOptionPane.showMessageDialog(App.getInstance().getMainWindow(), "display"); //TEST
  	
  	GL gl = drawable.getGL();
  	
  	/*
  	//TEST
  	gl.glClearColor(0, 200, 0, 255);
  	gl.glClear(GL.GL_COLOR_BUFFER_BIT);
  	
  	//TEST
  	gl.glDisable(GL.GL_TEXTURE_2D);
  	gl.glColor3f(1, 1, 0);
    gl.glBegin(GL.GL_QUADS);
    gl.glVertex3f(0, getHeight(), 0);
    gl.glVertex3f(getWidth(), getHeight(), 0);
    gl.glVertex3f(getWidth(), 0, 0);
    gl.glVertex3f(0, 0, 0);
    gl.glEnd(); */
    
    //TEST
    //if (true) return;
    
  	
  	/* 
  	gl.glClearColor(0, 200, 0, 255);
  	gl.glClear(GL.GL_COLOR_BUFFER_BIT);
  	if (true) return; //*/
  	
  	/*//TEST: perspective view
  	gl.glViewport(0, 0, getWidth(), getHeight());
  	gl.glMatrixMode(GL.GL_PROJECTION); // Select The Projection Matrix
  	gl.glLoadIdentity();  // Reset The Projection Matrix
  	new GLU().gluPerspective(90, 1.3f, 0.1f, 5000f);
  	new GLU().gluLookAt(300, 300, -1000, 300, 0, 0, 0, 0, -1);
  	//*/
  	
  	//TEST
  	gl.glEnable(GL.GL_MULTISAMPLE);
  	
    controller.paint(gl);
    
    /* overlayTest = new Overlay(drawable);
    overlayTest.beginRendering();
    Graphics2D g = overlayTest.createGraphics();
    g.drawString("hallo", 50, 100);
    g.setColor(Color.RED);
    g.fillRect(5, 5, 100, 100);
    g.dispose();
    overlayTest.draw(0, 0, 1024, 768);
    overlayTest.endRendering(); */
    
    /*tR.beginRendering(1024, 768);
    for (int i = 0; i < 25; i++)
    {
      tR.setColor(new Color(255 - i * 10, i * 10, 0));
      tR.draw("Hallo", i * 20, i * 40);
      
    }
    tR.endRendering();*/
    
    /*
    GL gl = drawable.getGL();
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
    gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
    Texture tex = texren.getTexture();
    tex.bind();
    tex.enable();
    for (int i = 0; i < 1; i++)
    {
      gl.glBegin(GL.GL_QUADS);Panel()
      gl.glColor3f(1, 1, 1);
      gl.glTexCoord2f(0,0); gl.glVertex2f(i*50 + 0, 0);
      gl.glTexCoord2f(1,0); gl.glVertex2f(i*50 + 256, 0);
      gl.glTexCoord2f(1,1); gl.glVertex2f(i*50 + 256, 256);
      gl.glTexCoord2f(0,1); gl.glVertex2f(i*50 + 0, 256);
      gl.glEnd();
    } */
    
  }


  public void displayChanged(GLAutoDrawable drawable, boolean arg1, boolean arg2)
  {
  }

  
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) 
  {
    GL gl = drawable.getGL();
    gl.glMatrixMode(GL.GL_PROJECTION);
    gl.glLoadIdentity();
    //JOptionPane.showMessageDialog(App.getInstance().getMainWindow(), width + "x" + height); //TEST
    gl.glViewport(0, 0, width, height);
    gl.glOrtho(0, width, height, 0, 0, 128);
    //notify view
    controller.resize();
  }
  
  
  /**
   * addNotify, removeNotify, destroy:
   * Avoid reinitialization when the panel is added
   * to another tab's content pane. Thanks for the idea to
   * Kenneth Russell,
   * http://www.javagaming.org/forums/index.php?topic=16475.0
   */
  @Override public void addNotify()
  {
    if (!created)
    {
      created = true;
      super.addNotify();
    }
  }


  @Override public void removeNotify()
  {
    if (destroyed)
    {
      super.removeNotify();
    }
  }


  public void destroy()
  {
    destroyed = true;
    removeNotify();
  }


}
