package com.xenoage.zong.gui.controller.panels;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.Buffer;
import java.util.LinkedList;

import javax.media.opengl.GL;
import javax.media.opengl.GLCapabilities;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;
import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Rectangle2i;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.app.opengl.text.TextRenderer;
import com.xenoage.zong.app.symbols.SymbolPool;
import com.xenoage.zong.app.symbols.SymbolTexturePool;
import com.xenoage.zong.gui.cursor.Cursor;
import com.xenoage.zong.gui.cursor.CursorManager;
import com.xenoage.zong.gui.view.panels.GLScorePanel;
import com.xenoage.zong.renderer.GLGraphicsContext;
import com.xenoage.zong.renderer.screen.page.GLPageRenderer;
import com.xenoage.util.Units;
import com.xenoage.util.io.IO;
import com.xenoage.zong.view.ScoreView;


/**
 * Controller for a ScorePanel
 * rendered with OpenGL.
 *
 * @author Andreas Wenger
 */
public class GLScorePanelController
	implements ScorePanelController, MouseListener, MouseMotionListener, MouseWheelListener
{

	private GLScorePanel scorePanel;

	//the current view of the score panel
	private ScoreView view = null;

	private TextureManager textureManager;
	private TextRenderer textRenderer;
	
	private boolean antialiasing = false;
	
	//mouse cursors
	private Cursor[] cursors = new Cursor[CursorLevel.values().length];


	/**
	 * Creates a controller for a ScorePanel.
	 */
	public GLScorePanelController()
	{
		//always try to create 4 sample buffers for antialiasing
    //(if not possible, it is ignored by JOGL)
  	GLCapabilities caps = new GLCapabilities();
    caps.setSampleBuffers(true);
    caps.setNumSamples(4);
		
		this.scorePanel = new GLScorePanel(this, caps);
		this.textureManager = new TextureManager(10);
		this.textRenderer = new TextRenderer();
	}


	/**
	 * Gets the current view of this ScorePanel.
	 */
	public ScoreView getView()
	{
		return this.view;
	}


	/**
	 * Sets the current view of this ScorePanel.
	 */
	public void setView(ScoreView view)
	{
		//set new view
		this.view = view;
	}


	/**
	 * Gets the GLScorePanel.
	 */
	public GLScorePanel getPanel()
	{
		return scorePanel;
	}


	/**
	 * Gets the page layout screen renderer for this panel.
	 */
	public GLPageRenderer createPageScreenRenderer()
	{
		return GLPageRenderer.getInstance();
	}


	/**
	 * Gets the size of the panel in px.
	 */
	public Size2i getSize()
	{
		return new Size2i(scorePanel.getWidth(), scorePanel.getHeight());
	}


	/**
	 * Called by the ScorePanel when it needs to be repainted.
	 */
	public void paint(GL gl)
	{
		if (view != null)
		{
			view.paint(new GLGraphicsContext(gl, textureManager, textRenderer, new Point2i(0, 0),
				Units.mmToPxFloat(1, view.getTargetScaling()),
				Units.mmToPxFloat(1, view.getCurrentScaling()),
				new Rectangle2i(0, 0, scorePanel.getWidth(), scorePanel.getHeight()), antialiasing));
		}
	}


	/**
	 * Repaints the score panel.
	 */
	public void repaint()
	{
		scorePanel.repaint();
	}


	/**
	 * Gets the OpenGL pipeline object of this panel.
	 */
	public GL getGL()
	{
		return scorePanel.getGL();
	}


	/**
	 * Call this method within the init()-method of the panel.
	 */
	public void init(GL gl)
	{
		//disable depth buffer, because we need none. should be faster.
		gl.glDisable(GL.GL_DEPTH_TEST);

		//load symbols texture
		SymbolPool symbolPool = App.getInstance().getSymbolPool();
		try
		{
			//load all mipmaps levels
			LinkedList<TextureData> mipmapLevels = new LinkedList<TextureData>();
			for (int i = 0; true; i++)
			{
				TextureData mipmapLevel = TextureIO.newTextureData(
			    IO.openInputStream(SymbolTexturePool.getTexturePNGPath(symbolPool.getID(), i)), false, "png");
				mipmapLevels.add(mipmapLevel);
				if (mipmapLevel.getWidth() <= 1)
					break;
			}
			//collect buffers
			Buffer[] buffers = new Buffer[mipmapLevels.size()];
			int level = 0;
			for (TextureData mipmapLevel : mipmapLevels)
			{
				buffers[level] = mipmapLevel.getBuffer();
				level++;
			}
			//create the texture
			TextureData top = mipmapLevels.getFirst();
			TextureData textureData = new TextureData(top.getInternalFormat(), top.getWidth(), top.getHeight(),
				0, top.getPixelFormat(), top.getPixelType(), false, false, buffers, null);
			Texture texture = TextureIO.newTexture(textureData);
			int textureID = symbolPool.getTextureID();
			textureManager.addAppTexture(texture, textureID);
			//TEST
			/* JFrame frame = new JFrame();
			frame.setSize(texture.getWidth(), texture.getHeight());
			frame.add(new JLabel(new ImageIcon(ImageIO.read(IO.openDataFile(SymbolTexturePool.getTexturePNGPath(symbolPool.getID(), 0))))));
			frame.setVisible(true); */
		}
		catch (Exception ex)
		{
			//TODO: messagebox can't be read (just gray inside) - TODO: really? YES!
			App.err().report(ErrorLevel.Fatal, Voc.Error_CouldNotLoadTexture, ex);
		}
		//load app textures
		try
		{
			//load paper, shadow and background texture
			Texture texPaper = TextureIO.newTexture(IO
				.openInputStream("data/img/paper/paper1.png"), true, "png");
			textureManager.addAppTexture(texPaper, TextureManager.IDBASE_PAPERS);
			Texture texShadow = TextureIO.newTexture(IO
				.openInputStream("data/img/shadow/shadow1.png"), true, "png");
			textureManager.addAppTexture(texShadow, TextureManager.IDBASE_SHADOWS);
			Texture texDesktop = TextureIO.newTexture(IO
				.openInputStream("data/img/desktop/blue3.jpg"), false, "jpg");
			textureManager.addAppTexture(texDesktop, TextureManager.IDBASE_DESKTOPS);
			//load adjustment handles textures
			Texture tex = TextureIO.newTexture(IO
				.openInputStream("data/img/handles/handle-resize.png"), true, "png");
			textureManager.addAppTexture(tex, TextureManager.ID_HANDLE_RESIZE);
			tex = TextureIO.newTexture(IO
				.openInputStream("data/img/handles/handle-resize-hover.png"), true, "png");
			textureManager.addAppTexture(tex, TextureManager.ID_HANDLE_RESIZE_HOVER);
			tex = TextureIO.newTexture(IO
				.openInputStream("data/img/handles/handle-rotate.png"), true, "png");
			textureManager.addAppTexture(tex, TextureManager.ID_HANDLE_ROTATE);
			tex = TextureIO.newTexture(IO
				.openInputStream("data/img/handles/handle-rotate-hover.png"), true, "png");
			textureManager.addAppTexture(tex, TextureManager.ID_HANDLE_ROTATE_HOVER);
			//load warning texture
			tex = TextureIO.newTexture(IO
				.openInputStream("data/img/other/warning64.png"), true, "png");
			textureManager.addAppTexture(tex, TextureManager.ID_WARNING);
			//load app's specific textures
			App.getInstance().loadAppTextures(textureManager);
			
			//TEST
			tex = TextureIO.newTexture(IO
				.openInputStream("data/img/gui/button-open.png"), true, "png");
			textureManager.addAppTexture(tex, TextureManager.ID_GUI_BUTTON_OPEN);
			tex = TextureIO.newTexture(IO
				.openInputStream("data/img/gui/button-save.png"), true, "png");
			textureManager.addAppTexture(tex, TextureManager.ID_GUI_BUTTON_SAVE);
			tex = TextureIO.newTexture(IO
				.openInputStream("data/img/gui/button-print.png"), true, "png");
			textureManager.addAppTexture(tex, TextureManager.ID_GUI_BUTTON_PRINT);
			tex = TextureIO.newTexture(IO
				.openInputStream("data/img/gui/button-play.png"), true, "png");
			textureManager.addAppTexture(tex, TextureManager.ID_GUI_BUTTON_PLAY_TUTTI);
			tex = TextureIO.newTexture(IO
				.openInputStream("data/img/gui/gui-buttonpanel.png"), true, "png");
			textureManager.addAppTexture(tex, TextureManager.ID_GUI_BUTTONPANEL);
			tex = TextureIO.newTexture(IO
				.openInputStream("data/img/gui/logo-viewer.png"), true, "png");
			textureManager.addAppTexture(tex, TextureManager.ID_GUI_LOGO_VIEWER);
			tex = TextureIO.newTexture(IO
				.openInputStream("data/img/gui/gui-tooltip.png"), true, "png");
			textureManager.addAppTexture(tex, TextureManager.ID_GUI_TOOLTIP);
			tex = TextureIO.newTexture(IO
				.openInputStream("data/img/gui/gui-tooltip-small.png"), true, "png");
			textureManager.addAppTexture(tex, TextureManager.ID_GUI_TOOLTIP_SMALL);
		}
		catch (Exception ex)
		{
			App.err().report(ErrorLevel.Fatal, Voc.Error_CouldNotLoadTexture, ex);
		}

	}


	public TextureManager getTextureManager()
	{
		return textureManager;
	}


	/**
	 * Invoked when a mouse button has been pressed
	 * on this score panel.
	 */
	public void mouseClicked(MouseEvent e)
	{
		//forward to current view
		if (view != null)
		{
			view.mouseClicked(e);
		}
	}


	/**
	 * Invoked when a mouse button has been pressed
	 * on this score panel.
	 */
	public void mousePressed(MouseEvent e)
	{
		//forward to current view
		if (view != null)
		{
			view.mousePressed(e);
		}
	}


	/**
	 * Invoked when a mouse button has been released
	 * on this score panel.
	 */
	public void mouseReleased(MouseEvent e)
	{
		//forward to current view
		if (view != null)
		{
			view.mouseReleased(e);
		}
	}


	/**
	 * This method is called when the mouse was moved on the panel
	 * with a button pressed.
	 */
	public void mouseDragged(MouseEvent e)
	{
		//forward to current view

		if (view != null)
		{
			view.mouseDragged(e);
		}
	}


	/**
	 * This method is called when the mouse was moved on the panel
	 * without a button pressed.
	 */
	public void mouseMoved(MouseEvent e)
	{
		//forward to current view
		if (view != null)
		{
			view.mouseMoved(e);
		}
	}


	/**
	 * Invoked when the mouse enters this score panel.
	 */
	public void mouseEntered(MouseEvent e)
	{
	}


	/**
	 * Invoked when the mouse exits this score panel.
	 */
	public void mouseExited(MouseEvent e)
	{
	}


	/**
	 * Shows the given popup menu at the given position.
	 */
	public void showPopupMenu(JPopupMenu popupMenu, Point2i position)
	{
		popupMenu.addPopupMenuListener(new PopupMenuListener(){

			public void popupMenuCanceled(PopupMenuEvent e)
			{
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
			{
				App.getInstance().setKeyDispatchingEnabled(true);
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e)
			{
				App.getInstance().setKeyDispatchingEnabled(false);
			}
			
		});
		scorePanel.showPopupMenu(popupMenu, position);
	}


	public void mouseWheelMoved(MouseWheelEvent e)
	{
		if (e.getWheelRotation() > 0)
		{
			view.zoomOut();
		}
		else
		{
			view.zoomIn();
		}
	}

	
	public void setAntialiasing(boolean antialiasing)
	{
		this.antialiasing = antialiasing;
	}
	
	
	/**
   * This method is called by the score panel when it was resized.
   */
  public void resize()
  {
  	view.resize();
  }


  /**
   * Sets the mouse cursor at the given level, or null.
   */
	public void setCursor(Cursor cursor, CursorLevel level)
	{
		cursors[level.ordinal()] = cursor;
		
		//show cursor with highest priority, or open hand cursor if none is set
		Cursor newCursor = Cursor.OpenHand;
		for (CursorLevel l : CursorLevel.values())
		{
			if (cursors[l.ordinal()] != null)
			{
				newCursor = cursors[l.ordinal()];
				break;
			}
		}
		CursorManager.getInstance().setCursor(newCursor, scorePanel);
	}


}
