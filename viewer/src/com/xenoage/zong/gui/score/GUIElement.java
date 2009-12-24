package com.xenoage.zong.gui.score;

import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Size2f;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.data.text.Alignment;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.data.text.FormattedTextStyle;
import com.xenoage.zong.gui.score.effects.EffectType;
import com.xenoage.zong.gui.score.effects.GUIEffect;
import com.xenoage.zong.gui.score.effects.MoveIn;
import com.xenoage.zong.gui.score.effects.SlideEffect;
import com.xenoage.zong.gui.score.layout.LayoutInfo;
import com.xenoage.zong.renderer.GLGraphicsContext;
import com.xenoage.util.Units;
import com.xenoage.zong.view.ScorePageView;


/**
 * Abstract base class for all GUI elements that
 * are used within the score panel.
 * 
 * GUI elements may be positioned in pixel on the screen
 * or in layout coordinates, just as needed.
 * 
 * A GUI element can have a {@link GUITooltip}, which is shown
 * after a certain time when the cursor is within the element.
 * 
 * @author Andreas Wenger
 */
public abstract class GUIElement
	implements MouseListener, MouseMotionListener
{
	
	//GUI Manager containing this element
	protected final GUIManager guiManager;
	
	//parent element
	protected GUIContainer parent = null;
	
	//effects that have to be applied when the element is added to a container
	protected ArrayList<GUIEffect> inEffects = new ArrayList<GUIEffect>();
	protected int currentInEffectIndex = -1;
	
	//effects that have to be applied when the element is removed from a container
	protected ArrayList<GUIEffect> outEffects = new ArrayList<GUIEffect>();
	protected int currentOutEffectIndex = -1;
	
	//state
	public enum State { New, InEffect, Active, OutEffect, Dead };
	protected State state = State.New;
	
	//layout positioning
	protected LayoutInfo layoutInfo = null;	
	
	//tooltip
	protected GUITooltip tooltip = null;


	/**
	 * Creates a new {@link GUIElement} on the given view.
	 */
	public GUIElement(GUIManager guiManager)
	{
		this.guiManager = guiManager;
	}
	
	
	/**
   * Paints this element with the given OpenGL context.
   * If an effect is active, it is used.
   */
  public void paint(GLGraphicsContext context)
  {
  	if (state == State.New)
  	{
  		//new state
  		if (inEffects.size() > 0)
  		{
  			//begin with in-effects
  			state = State.InEffect;
  			currentInEffectIndex = 0;
  			inEffects.get(currentInEffectIndex).start();
  		}
  		else
  		{
  			//no effects. element is active
  			state = State.Active;
  		}
  	}
  	if (state == State.InEffect)
  	{
  		//in-effect
  		GUIEffect effect = inEffects.get(currentInEffectIndex);
  		if (!effect.isFinished())
  		{
  			//effect still running
  			paintEffect(context, effect);
  		}
  		else
  		{
  			//effect finished
  			if (currentInEffectIndex + 1 < inEffects.size())
  			{
  				//start next in-effect
  				currentInEffectIndex++;
    			inEffects.get(currentInEffectIndex).start();
  			}
  			else
  			{
  				//all in-effects are finished, element is now active
  				state = State.Active;
  			}
  		}
  	}
  	if (state == State.Active)
  	{
  		//active
  		paintNormal(context);
  	}
  	//state OutEffect or Dead is set by the die() method
  	if (state == State.OutEffect)
  	{
  		//out-effect
  		GUIEffect effect = outEffects.get(currentOutEffectIndex);
  		if (!effect.isFinished())
  		{
  			//effect still running
  			paintEffect(context, effect);
  		}
  		else
  		{
  			//effect finished
  			if (currentOutEffectIndex + 1 < outEffects.size())
  			{
  				//start next out-effect
  				currentOutEffectIndex++;
  				outEffects.get(currentOutEffectIndex).start();
  				paintEffect(context, outEffects.get(currentOutEffectIndex));
  			}
  			else
  			{
  				//all out-effects are finished, element is now dead
  				state = State.Dead;
  			}
  		}
  	}
  }
  
  
  /**
   * Sets the parent container of this element.
   * Call this method by the container when it adds this element.
   */
  void setParent(GUIContainer parent)
  {
  	this.parent = parent;
  }
  
  
  /**
   * Paints this element with the given OpenGL context
   * without an effect.
   */
  public abstract void paintNormal(GLGraphicsContext context);
  
  
  /**
   * Paints this element with the given OpenGL context, using the
   * given effect.
   */
  public void paintEffect(GLGraphicsContext context, GUIEffect effect)
  {
  	effect.paintUnsupported(this, context);
  }
  
  
  /**
   * Gets the {@link ScorePageView} where this element is shown on.
   */
  protected ScorePageView getView()
  {
  	return guiManager.getView();
  }
  
  
  /**
   * Gets the bounding shape of this element (or null, if there is none,
   * e.g. because this elements is not interested in mouse events), relative
   * to the top left corner of this element.
   */
  public Shape getBoundingShape()
  {
  	return null;
  }
  
  
  /**
   * This method is called when the mouse has been clicked within the bounding
   * shape of this element, but only when its state is active.
   */
  public void mouseClicked(MouseEvent e)
	{
	}
  
  
  /**
   * This method is called when the mouse has been pressed within the bounding
   * shape of this element, but only when its state is active.
   */
  public void mousePressed(MouseEvent e)
	{
	}
  
  
  /**
   * This method is called when the mouse has been released within the bounding
   * shape of this element, but only when its state is active.
   */
  public void mouseReleased(MouseEvent e)
	{
	}
	
	
	/**
   * This method is called when the mouse entered the bounding
   * shape of this element, but only when its state is active.
   */
	public void mouseEntered(MouseEvent e)
	{
	}
	
	
	/**
   * This method is called when the mouse entered the bounding
   * shape of this element, but only when its state is active.
   */
	public void mouseExited(MouseEvent e)
	{
	}
	
	
	/**
   * This method is called when the mouse has been moved within the bounding
   * shape of this element, but only when its state is active.
   */
	public void mouseMoved(MouseEvent e)
	{
	}
	
	
	/**
   * This method is called when the mouse has been dragged within the bounding
   * shape of this element, but only when its state is active.
   */
	public void mouseDragged(MouseEvent e)
	{
	}
	
	
	/**
   * This method is called when the mouse entered the bounding
   * shape of this element, but only when its state is active.
   * It is used to register the tooltip at the tooltip manager
   * (this is not done in <code>mouseEntered</code> because it is
   * often overridden).
   */
	void mouseEnteredTooltip(MouseEvent e)
	{
		if (tooltip != null)
		{
			guiManager.getTooltipManager().registerTooltip(tooltip);
		}
	}
	
	
	/**
   * This method is called when the mouse entered the bounding
   * shape of this element, but only when its state is active.
   * It is used to unregister the tooltip at the tooltip manager
   * (this is not done in <code>mouseExited</code> because it is
   * often overridden).
   */
	void mouseExitedTooltip(MouseEvent e)
	{
		if (tooltip != null)
		{
			guiManager.getTooltipManager().unregisterTooltip(tooltip);
		}
	}
  
  
  /**
   * Gets the position (upper left corner) in px.
   */
  public abstract Point2i getPosition();
  
  
  /**
   * Gets the size in px.
   */
  public abstract Size2i getSize();
  
  
  /**
   * Sets the position (upper left corner) in px.
   * This method is optional for classes like {@link GUIArrow}, but
   * should be implemented in buttons, panels, and so on.
   */
  public void setPosition(Point2i position)
  {
  }
  
  
  /**
   * Sets the size in px.
   * This method is optional for classes like {@link GUIArrow}, but
   * should be implemented in buttons, panels, and so on.
   */
  public void setSize(Size2i size)
  {
  }
  
  
  /**
   * Adds an effect that will be applied when the element is added to a container.
   * The state of the element is reset to <code>New</code>.
   */
  public void addInEffect(GUIEffect effect)
  {
  	inEffects.add(effect);
  	this.state = State.New;
  }
  
  
  /**
   * Adds an effect that will be applied when the element is removed from a container.
   */
  public void addOutEffect(GUIEffect effect)
  {
  	outEffects.add(effect);
  }
  
  
  /**
   * Gets the {@link State} of this element.
   */
  public State getState()
  {
  	return state;
  }
  
  
  /**
   * Sets the state of the element to <code>New</code> again.
   */
  public void revive()
  {
  	state = State.New;
  	currentInEffectIndex = -1;
  	currentOutEffectIndex = -1;
  }
  
  
  /**
   * Let the element die. If there are out-effects, they are shown
   * before the element has the <code>Dead</code> state, otherwise
   * it is set to <code>Dead</code> immediately.
   */
  public void die()
  {
  	if (outEffects.size() > 0)
		{
			//begin with out-effects
			state = State.OutEffect;
			currentOutEffectIndex = 0;
			outEffects.get(currentOutEffectIndex).start();
		}
		else
		{
			//no effects. element is dead
			state = State.Dead;
		}
  }
  
  
  /**
   * Resets the state of this element, which makes it reusable.
   * Its state is changed to <code>New</code>.
   */
  public void resetState()
  {
  	state = State.New;
  	currentInEffectIndex = -1;
  	currentOutEffectIndex = -1;
  }
  
  
  /**
   * Returns true, if the element is dead.
   */
  public boolean isDead()
  {
  	return state == State.Dead;
  }
  
  
  /**
   * Gets information how to layout the element, or null
   * for default layout.
   */
  public LayoutInfo getLayoutInfo()
	{
		return layoutInfo;
	}


  /**
   * Sets information how to layout the element, or null
   * for default layout.
   */
	public void setLayoutInfo(LayoutInfo layoutInfo)
	{
		this.layoutInfo = layoutInfo;
	}
	
	
	/**
	 * Sets the tooltip text or null to use no tooltip.
	 */
	public void setTooltipText(String text)
	{
		if (tooltip != null)
		{
			guiManager.getTooltipManager().unregisterTooltip(tooltip);
		}
		if (text != null)
		{
			FormattedText ft = new FormattedText(text, new FormattedTextStyle(18), Alignment.Left);
			Size2i size = new Size2i(40 + Units.mmToPx(ft.getWidth(), 1), 40 + Units.mmToPx(ft.getHeight(), 1));
			Point2i position = new Point2i(-size.width - 20, 0);
			tooltip = new GUITooltip(guiManager, position, size, ft, this);
			tooltip.addInEffect(new SlideEffect(EffectType.In, 200, new Point2i(getSize().width / 2, getSize().height / 2), guiManager));
			tooltip.addOutEffect(new SlideEffect(EffectType.Out, 200, new Point2i(getSize().width / 2, getSize().height / 2), guiManager));
		}
	}
	
	
	/**
	 * Gets the position of this element in score panel coordinates.
	 */
	public Point2i getAbsolutePosition()
	{
		Point2i parentAbsPos = (parent != null ? parent.getAbsolutePosition() : new Point2i(0, 0));
		return parentAbsPos.add(getPosition());
	}
	

}
