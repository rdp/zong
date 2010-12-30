package com.xenoage.zong.musiclayout;

import static com.xenoage.util.Range.range;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.xenoage.pdlib.PVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.io.score.selections.Cursor;
import com.xenoage.zong.io.score.selections.Selection;
import com.xenoage.zong.musiclayout.stampings.StaffCursorStamping;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.Stamping;


/**
 * A {@link ScoreLayout} stores the layout of a score
 * and consists of at least one {@link ScoreFrameLayout}.
 *
 * @author Andreas Wenger
 */
public final class ScoreLayout
{
  
  private final Score score;
  private final Vector<ScoreFrameLayout> frames;
  
  
  /**
   * Creates a {@link ScoreLayout} with the given frame layouts.
   * @param score   the score which is shown
   * @param frames  a non-empty list of frame layouts (at least one is required)
   */
  public ScoreLayout(Score score, Vector<ScoreFrameLayout> frames)
  {
  	//one frame is required
  	if (frames.size() == 0)
  		throw new IllegalArgumentException("At least one (empty) frame is required");
    this.score = score;
    this.frames = frames;
  }


  /**
   * Gets the score this layout belongs to.
   */
  public Score getScore()
  {
    return score;
  }
  
  
  /**
   * Computes and returns the appropriate musical position
   * to the given metric position, or null, if unknown.
   */
  public MP computeMP(ScoreLayoutPosition coordinates)
  {
    if (coordinates == null)
      return null;
    //first test, if there is a staff element.
    ScoreFrameLayout frame = frames.get(coordinates.getFrameIndex());
    StaffStamping staff = frame.getStaffStampingAt(coordinates.getPosition());
    //if there is no staff, return null
    if (staff == null)
    {
      return null;
    }
    //otherwise, compute the beat at this position and return it
    else
    {
      float posX = coordinates.getX() - staff.getPosition().x;
      return staff.getMPAtX(posX);
    }
  }
  
  
  /**
   * Computes and returns the staff stamping and the x-coordinate in mm
   * relative to the position of the staff stamping
   * at the given musical position.
   */
  public StaffStampingPosition computeStaffStampingPosition(MP mp)
  {
    //go through all staff stampings and look for the given staff index
    //and measure index
    //TODO: find a much nicer way to do this, e.g. by storing an allocation
    //table for [staff index, measure index] -> [staff stamping]
    //within the layout
    for (int iFrame = 0; iFrame < frames.size(); iFrame++)
    {
      ScoreFrameLayout frame = frames.get(iFrame);
      for (StaffStamping s : frame.getStaffStampings())
      {
        if (s.getStaffIndex() == mp.getStaff())
        {
          //this staff stamping is part of the correct scorewide staff.
          //look if it contains the given musical score position
          Float posX = s.getXMmAt(mp);
          if (posX != null)
          {
            //we found it. return staff and position
            return new StaffStampingPosition(s, iFrame, posX);
          }
        }
      }
    }
    //we found nothing. return null
    return null;
  }
  
  
  /**
   * Computes the index of the frame and the system (relative to the frame,
   * thus beginning at 0 for each frame) containing the measure with the
   * given index. If not found, null is returned.
   */
  public Tuple2<Integer, Integer> getFrameAndSystemIndex(int measure)
  {
    //go through all frames
    for (int iFrame = 0; iFrame < frames.size(); iFrame++)
    {
      FrameArrangement frameArr = frames.get(iFrame).getFrameArrangement();
      if (frameArr.getStartMeasureIndex() <= measure && frameArr.getEndMeasureIndex() >= measure)
      {
      	//go through all systems of this frame
      	for (int iSystem : range(frameArr.getSystems()))
        {
          SystemArrangement system = frameArr.getSystems().get(iSystem);
          if (system.getStartMeasureIndex() <= measure && system.getEndMeasureIndex() >= measure)
          {
          	return new Tuple2<Integer, Integer>(iFrame, iSystem);
          }
        }
      }
    }
    //we found nothing. return null
    return null;
  }
  
  
  /**
   * Updates the selection stampings of this layout.
   */
  public void updateSelections()
  {
    //selections
    ArrayList<LinkedList<Stamping>> selections =
    	new ArrayList<LinkedList<Stamping>>(this.frames.size());
    for (int i = 0; i < this.frames.size(); i++)
    {
    	selections.add(new LinkedList<Stamping>());
    }
    Selection selection = null; //TODO input.getSelection();
    if (selection != null && selection instanceof Cursor)
    {
      Cursor cursor = (Cursor) selection;
      StaffStampingPosition ssp =
        computeStaffStampingPosition(cursor.getMP());
      if (ssp != null)
      {
        StaffCursorStamping scs = new StaffCursorStamping(ssp.getStaff(), ssp.getPositionX(), -0.5f);
        selections.get(ssp.getFrameIndex()).add(scs);
      }
    }
  	//stampings
    for (int i = 0; i < this.frames.size(); i++)
    {
    	//GOON
      //this.frames.get(i).setSelectionStampings(selections.get(i));
    }
  }
  
  
  /**
   * Gets the list of score frame layouts.
   */
  public List<ScoreFrameLayout> getScoreFrameLayouts()
  {
  	return frames;
  }
  
  
  /**
   * Creates an layout for showing an error.
   * This can be used if the layouter could not finish its work successfully.
   * Currently, the error layout consists of a single frame containing nothing.
   * TODO: show warning text in this frame!
   */
  public static ScoreLayout createErrorLayout(Score score)
  {
  	return new ScoreLayout(score, new PVector<ScoreFrameLayout>());
  }
  
  
  /* TEST: called when layout dies.
  @Override public void finalize()
  {
    JOptionPane.showMessageDialog(App.getInstance().getMainFrame(), "layout dead");
  } //*/
  
}
