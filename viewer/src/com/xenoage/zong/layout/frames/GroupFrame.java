package com.xenoage.zong.layout.frames;

import java.util.ArrayList;
import java.util.List;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.renderer.GLGraphicsContext;
import com.xenoage.zong.renderer.SwingGraphicsContext;
import com.xenoage.zong.renderer.frames.GLGroupFrameRenderer;
import com.xenoage.zong.renderer.frames.SwingGroupFrameRenderer;


/**
 * A group frame is a frame that contains
 * any number of child frames.
 * 
 * @author Andreas Wenger
 */
public class GroupFrame
	extends Frame
{

	private ArrayList<Frame> children = new ArrayList<Frame>();


	/**
	 * Creates a new group frame.
	 */
	public GroupFrame(Point2f position, Size2f size)
	{
		super(position, size);
	}


	/**
	 * Adds a child frame to this group frame.
	 */
	public void addChildFrame(Frame child)
	{
		child.setParentFrame(this);
		children.add(child);
	}


	/**
	 * Removes a child frame from the group frame
	 */
	public void removeChildFrame(Frame child)
	{
		if (children.contains(child))
		{
			children.remove(child);
			child.setParentFrame(null);
		}
	}


	/**
	 * Paints this frame and all child frames with the given OpenGL context.
	 */
	@Override public void paint(GLGraphicsContext context)
	{
		//paint this frame
		GLGroupFrameRenderer.getInstance().paint(this, context);
		//paint child frames
		for (Frame child : children)
		{
			child.paint(context);
		}
	}


	/**
	 * Paints the adjustment handles of this frame and of
	 * all child frames with the given OpenGL context,
	 * if this frame is selected.
	 */
	@Override public void paintHandles(GLGraphicsContext context)
	{
		super.paintHandles(context);
		//paint handles of child frames
		for (Frame child : children)
		{
			child.paintHandles(context);
		}
	}


	/**
	 * Paints this frame and all child frames with the given Swing context.
	 */
	@Override public void paint(SwingGraphicsContext context)
	{
		//paint this frame
		SwingGroupFrameRenderer.getInstance().paint(this, context);
		//paint child frames
		for (Frame child : children)
		{
			child.paint(context);
		}
	}


	/**
	 * Gets the list of child frames.
	 */
	public List<Frame> getChildren()
	{
		return children;
	}


	/**
	 * Transforms the given coordinates in page space to
	 * a frame position.
	 * If the given coordinates are not within this
	 * frame, null is returned.
	 * If the given coordinates are within a child frame,
	 * the child frame and the coordinates relative to
	 * the child frame are returned.
	 */
	@Override public FramePosition computeFramePosition(Point2f p)
	{
		//check child frames in reverse direction (begin with
		//top frame).
		for (int i = children.size() - 1; i >= 0; i--)
		{
			FramePosition fp = children.get(i).computeFramePosition(p);
			if (fp != null)
			{
				return fp;
			}
		}
		//check this frame
		FramePosition fp = super.computeFramePosition(p);
		if (fp != null)
		{
			return fp;
		}
		else
		{
			return null;
		}
	}


	/**
	 * Computes the adjustment handle at the given position
	 * and returns it. If there is none, null is returned.
	 * @param position    the position where to look for a handle
	 * @param scaling     the current scaling factor
	 */
	@Override public FrameHandle computeFrameHandleAt(Point2f layoutPosition, float scaling)
	{
		//look in children
		for (int i = children.size() - 1; i >= 0; i--)
		{
			FrameHandle fh = children.get(i).computeFrameHandleAt(layoutPosition, scaling);
			if (fh != null)
			{
				return fh;
			}
		}
		//look in this frame
		return super.computeFrameHandleAt(layoutPosition, scaling);
	}


	/**
	 * Sets the selection state of this frame
	 * and all child frames.
	 */
	@Override public void setSelectedRecursive(boolean selected)
	{
		setSelected(selected);
		for (Frame frame : children)
		{
			frame.setSelectedRecursive(selected);
		}
	}


}
