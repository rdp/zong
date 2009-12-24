package com.xenoage.zong.app.opengl;

import java.awt.Dimension;

import javax.media.opengl.*;
import javax.swing.BoxLayout;
import javax.swing.JFrame;


/**
 * We could not find a way to automate
 * JOGL tests yet.
 * 
 * This frame has a very small GLCanvas
 * and can be extended to do some tests
 * with its GL instance.
 *
 * @author Andreas Wenger
 */
public class GLTestFrame
  extends JFrame
  implements GLEventListener
{

  protected GLCanvas canvas;
  
  
  public GLTestFrame()
  {
    this.setTitle("OpenGL Test Frame");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);
    this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    canvas = new GLCanvas();
    canvas.addGLEventListener(this);
    this.add(canvas);
    this.setSize(new Dimension(4, 4));
  }


  public void display(GLAutoDrawable arg0)
  {
  }


  public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2)
  {
  }


  public void init(GLAutoDrawable arg0)
  {
  }


  public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4)
  {
  }
  
  
}
