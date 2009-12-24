package com.xenoage.zong.io.musicxml.in;

import static com.xenoage.util.NullTools.notNull;

import com.xenoage.util.iterators.ClassFilterIt;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.StavesList;
import com.xenoage.zong.data.instrument.Instrument;
import com.xenoage.zong.data.music.BracketGroupStyle;
import com.xenoage.zong.data.music.barline.BarlineGroupStyle;
import com.xenoage.util.exceptions.InvalidFormatException;

import java.math.BigInteger;
import java.util.*;

import proxymusic.Attributes;
import proxymusic.PartGroup;
import proxymusic.PartList;
import proxymusic.PartName;
import proxymusic.ScorePart;
import proxymusic.ScorePartwise;
import proxymusic.StartStop;
import proxymusic.ScorePartwise.Part;
import proxymusic.ScorePartwise.Part.Measure;


/**
 * This class manages the list of staves of a partwise
 * MusicXML 2.0 document.
 * 
 * An empty {@link StavesList} can be created,
 * which can then be filled with the musical data of
 * the document by another class.
 * 
 * This class contains a method which
 * maps MusicXML part-IDs and staff indices
 * to the correct application's staff indices.
 * 
 * The class is initialized with a document containing a part-list
 * and staves-elements within the musical content (optional),
 * if a part needs more than one staff.
 *
 * @author Andreas Wenger
 */
class MxlStavesList
{
  
  //the final StavesList
  private final StavesList stavesList;
  
  
  /**
   * Creates a {@link MxlStavesListTest} from the given
   * partwise MusicXML 2.0 document.
   */
  public MxlStavesList(ScorePartwise doc, Score parentScore)
    throws InvalidFormatException
  {
  	//list of parts
  	ArrayList<MxlPartInfo> parts = new ArrayList<MxlPartInfo>();
  	//list of groups
  	ArrayList<MxlGroupInfo> groups = new ArrayList<MxlGroupInfo>();
    //open groups: number (ID), content
    Hashtable<String, MxlGroupInfo> openMxlGroups = new Hashtable<String, MxlGroupInfo>();
    //read score-part and part-group elements
    //each score-part is a part in our application
    PartList mxlPartList = doc.getPartList();
    int currentPartIndex = 0;
    List<Object> mxlPartObjects = mxlPartList.getPartGroupOrScorePart();
    for (Object mxlPartObject : mxlPartObjects)
    {
      //score-part
      if (mxlPartObject instanceof ScorePart)
      {
        MxlPartInfo part = new MxlPartInfo((ScorePart) mxlPartObject);
        parts.add(part);
        currentPartIndex++;
      }
      //part-group
      else if (mxlPartObject instanceof PartGroup)
      {
        MxlGroupInfo group = readPartGroup(currentPartIndex, (PartGroup) mxlPartObject, openMxlGroups);
        if (group != null)
        {
        	//a group was closed, add it
        	groups.add(group);
        }
      }
    }
    //if there are unclosed score-groups, throw an exception
    if (openMxlGroups.size() > 0)
    {
      throw new InvalidFormatException("There are unclosed score-groups");
    }
    //count the number of staves and measures used by each part
    countStavesAndMeasuresPerPart(doc, parts);
    //creates the final StavesList for this document
    stavesList = createStavesList(parentScore, parts, groups);
  }


  /**
   * Reads a part-group element.
   * If a group was closed, it is returned. If a group was opened,
   * null is returned.
   */
  private MxlGroupInfo readPartGroup(int currentPartIndex, PartGroup mxlPartGroup,
  	Hashtable<String, MxlGroupInfo> openMxlGroups)
    throws InvalidFormatException
  {
  	String number = notNull(mxlPartGroup.getNumber(), "1"); 
    StartStop type = mxlPartGroup.getType();
    if (type == StartStop.START)
    {
      //group begins here
      //read group-symbol and group-barline
      MxlGroupInfo group = new MxlGroupInfo(mxlPartGroup);
      group.setStartPartIndex(currentPartIndex);
      if (openMxlGroups.get(number) != null)
      {
        throw new InvalidFormatException("score-group \"" + number +
          "\" was already opened");
      }
      openMxlGroups.put(number, group);
      return null;
    }
    else if (type == StartStop.STOP)
    {
      //group ends here
      MxlGroupInfo group = openMxlGroups.get(number);
      if (group == null)
      {
        throw new InvalidFormatException(
          "score-group \"" + number + "\" was closed before it was opened");
      }
      else
      {
        openMxlGroups.remove(number);
        group.setEndPartIndex(currentPartIndex - 1);
        return group;
      }
    }
    return null;
  }
  
  
  /**
   * Counts the number of staves and measures used in each part
   * and sets the maxStaves and measuresCount value of the given parts
   * accordingly.
   */
  private void countStavesAndMeasuresPerPart(ScorePartwise doc, List<MxlPartInfo> parts)
    throws InvalidFormatException
  {
    //check all parts
    for (Part mxlPart : doc.getPart())
    {
      String id = ((ScorePart) mxlPart.getId()).getId();
      MxlPartInfo part = getPart(id, parts);
      if (part == null) throw new InvalidFormatException("undefined part \"" + id + "\"");
      //count the measures, and check all measures for attributes
      //with staves-element and store the greatest value
      int maxStaves = 1;
      int measuresCount = 0;
      for (Measure mxlMeasure : mxlPart.getMeasure())
      {
      	ClassFilterIt<Attributes> mxlAttributesList =
      		new ClassFilterIt<Attributes>(mxlMeasure.getNoteOrBackupOrForward(), Attributes.class);
        for (Attributes mxlAttributes : mxlAttributesList)
        {
          BigInteger xmlStaves = mxlAttributes.getStaves();
          if (xmlStaves != null)
          {
            maxStaves = Math.max(maxStaves, xmlStaves.intValue());
          }
        }
	      measuresCount++;
      }
      //set the number of staves of the part
      part.setMaxStaves(maxStaves);
      //set the number of measures
      part.setMeasuresCount(measuresCount);
    }
  }
  
  
  /**
   * Creates the final StavesList for this document.
   */
  private StavesList createStavesList(Score parentScore, List<MxlPartInfo> parts, List<MxlGroupInfo> groups)
  {
  	StavesList ret = new StavesList(parentScore);
    //add parts
    for (MxlPartInfo part : parts)
    {
    	PartName mxlPartAbbr = part.getMxlScorePart().getPartAbbreviation();
    	Instrument instrument = null;
    	if (part.getInstruments().size() > 0)
    	{
    		//currently use only one instrument per part
    		instrument = part.getInstruments().get(0); 
    	}
      com.xenoage.zong.data.Part p = new com.xenoage.zong.data.Part(
      	part.getMxlScorePart().getPartName().getValue(),
      	(mxlPartAbbr != null ? mxlPartAbbr.getValue() : null),
      	part.getMaxStaves(), instrument);
      ret.addPart(p, part.getMeasuresCount());
    }
    //add groups
    for (MxlGroupInfo group : groups)
    {
      int startIndex = getFirstStaffIndex(group.getStartPartIndex(), parts);
      int endIndex = getLastStaffIndex(group.getEndPartIndex(), parts);
      PartGroup mxlPartGroup = group.getMxlPartGroup();
      if (mxlPartGroup.getGroupBarline() != null)
      {
        //add barline group
        BarlineGroupStyle style = BarlineGroupStyle.Single;
      	switch (mxlPartGroup.getGroupBarline().getValue())
      	{
      		case YES:
      			style = BarlineGroupStyle.Common; break;
      		case NO:
      			style = BarlineGroupStyle.Single; break;
      		case MENSURSTRICH:
      			style = BarlineGroupStyle.Mensurstrich; break;
        }
      	ret.addBarlineGroup(startIndex, endIndex, style);
      }
      if (mxlPartGroup.getGroupSymbol() != null)
      {
        //add bracket group
        BracketGroupStyle style = null;
      	switch (mxlPartGroup.getGroupSymbol().getValue())
      	{
      		case NONE:
      			style = null; break;
      		case BRACE:
      			style = BracketGroupStyle.Brace; break;
      		case BRACKET:
      			style = BracketGroupStyle.Bracket; break;
      		case LINE:
      			style = BracketGroupStyle.Line; break;
      	}
        if (style != null)
        {
        	ret.addBracketGroup(startIndex, endIndex, style);
        }
      }
    }
    //add implicit brace- and barline-groups for ungrouped
    //parts with more than one staff
    for (int i = 0; i < parts.size(); i++)
    {
      if (parts.get(i).getMaxStaves() > 1 && !isPartInGroup(i, groups))
      {
        int startIndex = getFirstStaffIndex(i, parts);
        int endIndex = getLastStaffIndex(i, parts);
        ret.addBarlineGroup(startIndex, endIndex, BarlineGroupStyle.Common);
        ret.addBracketGroup(startIndex, endIndex, BracketGroupStyle.Brace);
      }
    }
    return ret;
  }
  
  
  /**
   * Gets the {@link MxlPartInfo} with the given ID
   * from the given list of parts, or null if not found.
   */
  private MxlPartInfo getPart(String id, List<MxlPartInfo> parts)
  {
    for (MxlPartInfo part : parts)
    {
      if (part.getMxlScorePart().getId().equals(id))
        return part;
    }
    return null;
  }
  
  
  /**
   * Gets the index of the first staff of the given part.
   */
  private int getFirstStaffIndex(int partIndex, List<MxlPartInfo> parts)
  {
    int startIndex = 0;
    int i = 0;
    for (MxlPartInfo p : parts)
    {
      if (i == partIndex)
        break;
      startIndex += p.getMaxStaves();
      i++;
    }
    return startIndex;
  }
  
  
  /**
   * Gets the index of the lasz staff of the given part.
   */
  private int getLastStaffIndex(int partIndex, List<MxlPartInfo> parts)
  {
    int endIndex = 0;
    int i = 0;
    for (MxlPartInfo p : parts)
    {
      endIndex += p.getMaxStaves();
      if (i == partIndex)
        break;
      i++;
    }
    return endIndex - 1;
  }
  
  
  /**
   * Returns true, if the part with the given index
   * is in at least one barline- or bracket-group.
   */
  private boolean isPartInGroup(int partIndex, List<MxlGroupInfo> groups)
  {
    for (MxlGroupInfo group : groups)
    {
      if (group.getStartPartIndex() >= partIndex &&
        group.getEndPartIndex() <= partIndex)
      {
        return true;
      }
    }
    return false;
  }

  
  public StavesList getStavesList()
  {
    return stavesList;
  }


}
