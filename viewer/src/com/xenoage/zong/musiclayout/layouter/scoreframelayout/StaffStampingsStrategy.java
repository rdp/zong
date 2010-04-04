package com.xenoage.zong.musiclayout.layouter.scoreframelayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.xenoage.zong.util.ArrayTools;
import com.xenoage.util.SortedList;
import com.xenoage.util.math.Fraction;
import com.xenoage.util.math.Point2f;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.format.ScoreFormat;
import com.xenoage.zong.musiclayout.BeatMarker;
import com.xenoage.zong.musiclayout.FrameArrangement;
import com.xenoage.zong.musiclayout.MeasureMarks;
import com.xenoage.zong.musiclayout.StaffMarks;
import com.xenoage.zong.musiclayout.SystemArrangement;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.util.StaffStampings;
import com.xenoage.zong.musiclayout.spacing.MeasureColumnSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.MeasureSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.SpacingElement;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;


/**
 * This strategy creates the staves of all systems of a
 * given {@link FrameArrangement}.
 * 
 * @author Andreas Wenger
 */
public class StaffStampingsStrategy
{
	
	
	public StaffStampings createStaffStampings(Score score, FrameArrangement frameArr)
	{
		int systemsCount = frameArr.getSystemsCount();
		int stavesCount = score.getStavesCount();
		ScoreFormat scoreFormat = score.getScoreFormat();
		StaffStampings ret = new StaffStampings(systemsCount, stavesCount);
		
		//go through the systems
    for (int iSystem = 0; iSystem < systemsCount; iSystem++)
    { 
      //create staves of the system
      SystemArrangement system = frameArr.getSystem(iSystem);
      float systemXOffset = system.getMarginLeft();
      float yOffset = system.getOffsetY();
      for (int iStaff = 0; iStaff < stavesCount; iStaff++)
      {
      	yOffset += system.getStaffDistance(iStaff);
      	StaffStamping staff = new StaffStamping(iSystem, iStaff, 
      		system.getStartMeasureIndex(), system.getEndMeasureIndex(),
      		new Point2f(systemXOffset, yOffset), system.getWidth(), 5, scoreFormat.getInterlineSpace());
      	ret.set(iSystem, iStaff, staff);
        yOffset += system.getStaffHeight(iStaff);
      }
    }
    
    //create position marks
    //(TIDY: more or less the same positioning algorithm is in ScoreFrameLayoutStrategy
    // - perhaps that one can be based on the results created here?)
    for (int iSystem = 0; iSystem < frameArr.getSystemsCount(); iSystem++)
    { 
      SystemArrangement system = frameArr.getSystem(iSystem);
      List<StaffStamping> systemStaves = ret.getAllOfSystem(iSystem);
      int measuresCount = system.getMeasureColumnSpacings().length;
      
      //measure markers
      float[] measureMarkersLeft = new float[measuresCount];
      float[] measureMarkersLeading = new float[measuresCount];
      float[] measureMarkersRight = new float[measuresCount];
      float xOffset = 0; //start at the beginning of the staff
      for (int iMeasure = 0; iMeasure < measuresCount; iMeasure++)
      {
      	float xLeft = xOffset;
      	float xLeading = xLeft + system.getMeasureColumnSpacings()[iMeasure].getLeadingWidth();
      	xOffset += system.getMeasureColumnSpacings()[iMeasure].getWidth();
      	float xRight = xOffset;
      	//mark measure offset
      	measureMarkersLeft[iMeasure] = xLeft;
      	measureMarkersLeading[iMeasure] = xLeading;
      	measureMarkersRight[iMeasure] = xRight;
      }
      
      //beat markers: staff index, measure index, beats
      //create data structures
      ArrayList<ArrayList<SortedList<BeatMarker>>> beatMarkers = new ArrayList<ArrayList<SortedList<BeatMarker>>>(stavesCount);
      for (int iStaff = 0; iStaff < stavesCount; iStaff++)
      {
      	ArrayList<SortedList<BeatMarker>> staffBeats = new ArrayList<SortedList<BeatMarker>>(measuresCount);
      	for (int iMeasure = 0; iMeasure < measuresCount; iMeasure++)
        {
      		SortedList<BeatMarker> measureBeats = new SortedList<BeatMarker>(false);
      		staffBeats.add(measureBeats);
        }
      	beatMarkers.add(staffBeats);
      }
      //compute beat positions
      HashMap<Fraction, Float> rightmostPosForBeat = new HashMap<Fraction, Float>();
      for (int iStaff = 0; iStaff < stavesCount; iStaff++)
      {
      	StaffStamping staff = systemStaves.get(iStaff);
      	ArrayList<SortedList<BeatMarker>> staffBeats = beatMarkers.get(iStaff);
      	xOffset = 0; //start at the beginning of the staff
        float interlineSpace = staff.getInterlineSpace();
        for (int iMeasure = 0; iMeasure < measuresCount; iMeasure++)
        {
        	SortedList<BeatMarker> measureBeats = staffBeats.get(iMeasure);
          MeasureColumnSpacing measureColumnSpacing = system.getMeasureColumnSpacings()[iMeasure];
          MeasureSpacing measureStaffSpacing = measureColumnSpacing.getMeasureSpacings()[iStaff];
          float voicesOffset = xOffset + measureColumnSpacing.getLeadingWidth();
          for (int iVoice = 0; iVoice < measureStaffSpacing.getVoicesCount(); iVoice++)
          {
            SpacingElement[] voice = measureStaffSpacing.getVoice(iVoice).getSpacingElements();
            for (SpacingElement spacingElement : voice)
            {
              //OBSOLETE
            	//MusicElement element = spacingElement.getElement();
              //if (element != null /* TODO && (stampRests || !(element instanceof Rest)) */)
              //{
              float x = voicesOffset + spacingElement.getOffset() * interlineSpace;
              //mark the beat offset (use highest value = rightmost element on this beat)
              Float maxPos = rightmostPosForBeat.get(spacingElement.getBeat());
              if (maxPos == null || maxPos < x)
              {
              	measureBeats.addOrReplace(new BeatMarker(x, spacingElement.getBeat()));
              	rightmostPosForBeat.put(spacingElement.getBeat(), x);
              }
              //}
            }
          }
          xOffset += measureColumnSpacing.getWidth();
        }
      }
      
      //create StaffMarks for each staff
      for (int iStaff = 0; iStaff < stavesCount; iStaff++)
      {
      	ArrayList<SortedList<BeatMarker>> staffBeats = beatMarkers.get(iStaff);
      	MeasureMarks[] measureMarks = new MeasureMarks[staffBeats.size()];
      	for (int iMeasure = 0; iMeasure < measuresCount; iMeasure++)
      	{
      		BeatMarker[] beatMarkerArray = ArrayTools.toBeatMarkerArray(staffBeats.get(iMeasure));
      		if (beatMarkerArray.length == 0)
      		{
      			//TODO
      			//throw new IllegalStateException("No beat markers for measure " + iMeasure +
      			//	" in staff " + iStaff + " in system " + 
      			//	iSystem + " on frame beginning with measure " + frameArr.getStartMeasureIndex());
      		}
      		else
      		{
	      		measureMarks[iMeasure] = new MeasureMarks(measureMarkersLeft[iMeasure],
	      			measureMarkersLeading[iMeasure], measureMarkersRight[iMeasure], beatMarkerArray);
      		}
      	}
      	StaffMarks staffMarks = new StaffMarks(iSystem, iStaff, system.getStartMeasureIndex(), measureMarks);
      	ret.get(iSystem, iStaff).setStaffMarks(staffMarks);
      }
      
    }
		
    return ret;
	}
	

}
