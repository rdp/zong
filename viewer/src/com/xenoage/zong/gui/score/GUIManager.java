package com.xenoage.zong.gui.score;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.gui.score.buttons.LogoButton;
import com.xenoage.zong.gui.score.buttons.OpenButton;
import com.xenoage.zong.gui.score.buttons.PlayTuttiButton;
import com.xenoage.zong.gui.score.buttons.PrintButton;
import com.xenoage.zong.gui.score.buttons.SaveButton;
import com.xenoage.zong.gui.score.effects.EffectType;
import com.xenoage.zong.gui.score.effects.FadeIn;
import com.xenoage.zong.gui.score.effects.Hide;
import com.xenoage.zong.gui.score.effects.MoveIn;
import com.xenoage.zong.gui.score.effects.SlideEffect;
import com.xenoage.zong.gui.score.layout.CornerLayoutManager;
import com.xenoage.zong.gui.score.layout.CornerLayoutManager.Corner;
import com.xenoage.zong.gui.score.panels.GUIRightButtonPanel;
import com.xenoage.zong.renderer.GLGraphicsContext;
import com.xenoage.zong.view.ScorePageView;


/**
 * This class contains all the GUI elements within a score panel, paints them
 * and handles events.
 * 
 * GUI elements can be added or removed.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class GUIManager
{
	
	public enum Layer
	{
		Front, Back;
	}

	//parent view
	private final ScorePageView view;
	
	//two panels: background panel (behind scores) and foreground panel
	private GUIPanel backPanel, frontPanel;
	
	//list of tooltips (waiting or open)
	private TooltipManager tooltipManager;


	/**
	 * Creates a new {@link GUIManager} for the given view.
	 */
	public GUIManager(ScorePageView view)
	{
		this.view = view;
		this.backPanel = new GUIPanel(this, new Point2i(0, 0), view.getSize(),
			new CornerLayoutManager()); //TEST
		this.frontPanel = new GUIPanel(this, new Point2i(0, 0), view.getSize(),
			new CornerLayoutManager()); //TEST
		
		//TEST: panel with some buttons
		GUIPanel appletPanel = new GUIRightButtonPanel(this);
		appletPanel.addInEffect(new Hide(400, this));
		appletPanel.addInEffect(new MoveIn(200, new Point2i(80, 0), this));
		int panelTime = 200;
		int sx = 50;
		GUIButton b = new OpenButton(this, new Point2i(0,0), new Size2i(64, 64));
		b.addInEffect(new Hide(panelTime, this));
		b.addInEffect(new SlideEffect(EffectType.In, 400, new Point2i(0 + sx, 16), this));
		appletPanel.addElement(b);
		b = new SaveButton(this, new Point2i(0,0), new Size2i(64, 64));
		b.addInEffect(new Hide(panelTime + 200, this));
		b.addInEffect(new SlideEffect(EffectType.In, 400, new Point2i(0 + sx, 16), this));
		appletPanel.addElement(b);
		b = new PrintButton(this, new Point2i(0,0), new Size2i(64, 64));
		b.addInEffect(new Hide(panelTime + 400, this));
		b.addInEffect(new SlideEffect(EffectType.In, 400, new Point2i(0 + sx, 80), this));
		appletPanel.addElement(b);
		b = new PlayTuttiButton(this, new Point2i(0,0), new Size2i(64, 64));
		b.addInEffect(new Hide(panelTime + 600, this));
		b.addInEffect(new SlideEffect(EffectType.In, 400, new Point2i(0 + sx, 144), this));
		appletPanel.addElement(b);
		this.frontPanel.addElement(appletPanel, Corner.NE);
		//logo
		b = new LogoButton(this, new Point2i(0,0), new Size2i(128, 82));
		b.addInEffect(new Hide(2000, this));
		b.addInEffect(new FadeIn(500, this));
		this.backPanel.addElement(b, Corner.SE);
		
		this.tooltipManager = new TooltipManager(this);
	}


	/**
	 * Gets the parent view.
	 */
	public ScorePageView getView()
	{
		return view;
	}


	/**
	 * Paints the given panel with OpenGL.
	 */
	public void paint(GLGraphicsContext glContext, Layer layer)
	{
		if (layer == Layer.Back)
		{
			backPanel.paint(glContext);
		}
		else
		{
			frontPanel.paint(glContext);
			tooltipManager.paint(glContext);
		}
	}
	
	
	/**
	 * This method is called when the mouse has been clicked on the view. If a
	 * {@link GUIElement} is below the cursor, true is returned, otherwise
	 * false.
	 */
	public boolean mouseClicked(MouseEvent e, Layer layer)
	{
		GUIElement target = getElementAt(new Point2i(e.getX(), e.getY()), layer);
		if (target != null)
		{
			Point2i p = target.getPosition();
			e.translatePoint(-p.x, -p.y);
			target.mouseClicked(e);
			e.translatePoint(p.x, p.y);
			return true;
		}
		else
		{
			return false;
		}
	}


	/**
	 * This method is called when the mouse has been pressed on the view. If a
	 * {@link GUIElement} is below the cursor, true is returned, otherwise
	 * false.
	 */
	public boolean mousePressed(MouseEvent e, Layer layer)
	{
		GUIElement target = getElementAt(new Point2i(e.getX(), e.getY()), layer);
		if (target != null)
		{
			Point2i p = target.getPosition();
			e.translatePoint(-p.x, -p.y);
			target.mousePressed(e);
			e.translatePoint(p.x, p.y);
			return true;
		}
		else
		{
			return false;
		}
	}


	/**
	 * This method is called when the mouse has been released on the view. If a
	 * {@link GUIElement} is below the cursor, true is returned, otherwise
	 * false.
	 */
	public boolean mouseReleased(MouseEvent e, Layer layer)
	{
		GUIElement target = getElementAt(new Point2i(e.getX(), e.getY()), layer);
		if (target != null)
		{
			Point2i p = target.getPosition();
			e.translatePoint(-p.x, -p.y);
			target.mouseReleased(e);
			e.translatePoint(p.x, p.y);
			return true;
		}
		else
		{
			return false;
		}
	}


	/**
	 * This method is called when the mouse has been moved over the view. If a
	 * {@link GUIElement} is below the cursor, true is returned, otherwise
	 * false.
	 */
	public boolean mouseMoved(MouseEvent e, Layer layer)
	{
		GUIElement target = getElementAt(new Point2i(e.getX(), e.getY()), layer);
		updateHover(target, e);
		if (target != null)
		{
			Point2i p = target.getPosition();
			e.translatePoint(-p.x, -p.y);
			target.mouseMoved(e);
			e.translatePoint(p.x, p.y);
			return true;
		}
		else
		{
			return false;
		}
	}


	/**
	 * This method is called when the mouse has been dragged over the view. If a
	 * {@link GUIElement} is below the cursor, true is returned, otherwise
	 * false.
	 */
	public boolean mouseDragged(MouseEvent e, Layer layer)
	{
		GUIElement target = getElementAt(new Point2i(e.getX(), e.getY()), layer);
		updateHover(target, e);
		if (target != null)
		{
			Point2i p = target.getPosition();
			e.translatePoint(-p.x, -p.y);
			target.mouseDragged(e);
			e.translatePoint(p.x, p.y);
			return true;
		}
		else
		{
			return false;
		}
	}


	/**
	 * Gets the last registered active {@link GUIElement} below the given position
	 * in score panel space on the given panel , or null if there is none.
	 */
	private GUIElement getElementAt(Point2i p, Layer layer)
	{
		if (layer == Layer.Back)
			return backPanel.getElementAt(p);
		else
			return frontPanel.getElementAt(p);
	}


	/**
	 * This method is called by the view when the score panel was resized.
	 */
	public void resizeView()
	{
		backPanel.setSize(view.getSize());
		frontPanel.setSize(view.getSize());
	}


	/**
	 * Updates the hover states of the elements, based on the given
	 * {@link MouseEvent}. The mouse is currently over the given element (or
	 * null).
	 */
	private void updateHover(GUIElement hoveredElement, MouseEvent e)
	{
		backPanel.updateHover(hoveredElement, e);
		frontPanel.updateHover(hoveredElement, e);
	}


	/**
	 * Repaints the view.
	 */
	public void repaint()
	{
		view.repaint();
	}
	
	
	/**
	 * Adds a {@link GUIElement}. This element exists, until it is removed by
	 * calling {@code removeGUIElement}. The {@code repaint} method must be
	 * called after this method if repainting the view is required.
	 */
	public void addElement(GUIElement element, Layer layer)
	{
		if (layer == Layer.Back)
			backPanel.addElement(element);
		else
			frontPanel.addElement(element);
	}


	/**
	 * Adds a List of {@link GUIElement}. These elements exist, until they are removed by
	 * calling {@code removeGUIElement}. The {@code repaint} method must be
	 * called after this method if repainting the view is required.
	 */
	public void addElements(ArrayList<GUIElement> elements, Layer layer)
	{
		for (GUIElement element : elements)
		{
			addElement(element, layer);
		}
	}


	/**
	 * Removes a {@link GUIElement} previously added by calling {@code
	 * addGUIElement}. The {@code repaint} method must be called after this
	 * method if repainting the view is required.
	 */
	public void removeElement(GUIElement element)
	{
		//don't remove it now, but remove it when it is dead
		//(checked in the paint-method)
		element.die();
	}


	/**
	 * Removes an ArrayList of {@link GUIElement}s previously added by calling
	 * {@code addGUIElement}. The {@code repaint} method must be called after
	 * this method if repainting the view is required.
	 */
	public void removeElements(ArrayList<GUIElement> elements)
	{
		for (GUIElement element : elements)
		{
			removeElement(element);
		}
	}
	
	
	/**
	 * Requests continuous repaints for the given time in ms.  
	 */
	public void requestRepaintsForTime(int ms)
	{
		view.requestRepaintsForTime(ms);
	}
	
	
	/**
	 * Gets the tooltip manager.
	 */
	public TooltipManager getTooltipManager()
	{
		return tooltipManager;
	}

}
