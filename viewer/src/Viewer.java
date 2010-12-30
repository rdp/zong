import java.awt.Frame;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

import com.xenoage.util.AppletTools;
import com.xenoage.zong.app.ViewerApplet;
import com.xenoage.zong.gui.applet.ZongApplet;


/**
 * The {@link Viewer} component used for Xenoage Score.
 * 
 * This is the class used in the "code" parameter of the applet HTML tag.
 * It creates an instance of the {@link ViewerApplet} class which
 * manages the whole applet.
 *
 * It is within the default package, because the qualified name of the
 * applet class is shown to the user in the security warning dialog.
 * There, <code>Viewer</code> looks much better than
 * <code>com.xenoage.zong.gui.applet.ViewerApplet</code> or so.
 *
 * @author Andreas Wenger
 */
public class Viewer
  extends JApplet
  implements ZongApplet
{
  
  //true, if the applet was already initialized in the browser
  private boolean initialized = false;
  
  //the application class - TODO: needed?
  private ViewerApplet app;
  
  
  /**
   * This method is called when the applet is
   * initialized in the browser. It is called only once.
   */
  @Override public void init()
  {
    //there may be browsers that call this method more
    //than once (bug). don't allow this.
    if (initialized)
      return;
    initialized = true;
    //register as an applet
    try
    {
    	//TEST
    	//JOptionPane.showMessageDialog(getParentWindow(), "Welcome! PRE-ALPHA version"); //TEST
    	app = new ViewerApplet(this, this);
    	//add(new TestPanel().canvas);
    	//add(new GLScorePanel(null, new GLCapabilities()).getGLCanvas());
    	//TEST
    	//JOptionPane.showMessageDialog(getParentWindow(), "Loading done"); //TEST
    }
    catch (Throwable t)
    {
    	//TEST
    	Writer result = new StringWriter();
      PrintWriter printWriter = new PrintWriter(result);
      t.printStackTrace(printWriter);
      JOptionPane.showMessageDialog(getParentFrame(), "Error: " + t.toString() + "\n" + result.toString());
    }
  }
  
  
  @Override public void start()
  {
  }
  
  
  @Override public void stop()
  {
  }
  
  
  @Override public void destroy()
  {
  }
  
  
  /**
   * Gets the parent {@link Frame} of this applet, or null if none
   * can be found.
   */
  public Frame getParentFrame()
  { 
    return AppletTools.getParentFrame(this);
  } 
  

}
