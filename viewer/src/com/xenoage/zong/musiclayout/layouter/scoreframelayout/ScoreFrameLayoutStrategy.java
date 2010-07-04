package com.xenoage.zong.musiclayout.layouter.scoreframelayout;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.Range.range;
import static com.xenoage.zong.core.music.format.SP.sp;

import java.util.Iterator;
import java.util.LinkedList;

import com.xenoage.pdlib.PSet;
import com.xenoage.pdlib.PVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.NullTools;
import com.xenoage.util.enums.VSide;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.lang.Tuple3;
import com.xenoage.util.math.Point2f;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.format.MeasureNumbering;
import com.xenoage.zong.core.header.ColumnHeader;
import com.xenoage.zong.core.header.ScoreHeader;
import com.xenoage.zong.core.music.Globals;
import com.xenoage.zong.core.music.Measure;
import com.xenoage.zong.core.music.MeasureElement;
import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.core.music.Part;
import com.xenoage.zong.core.music.Staff;
import com.xenoage.zong.core.music.StavesList;
import com.xenoage.zong.core.music.Voice;
import com.xenoage.zong.core.music.barline.Barline;
import com.xenoage.zong.core.music.beam.Beam;
import com.xenoage.zong.core.music.beam.BeamWaypoint;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.curvedline.CurvedLine;
import com.xenoage.zong.core.music.curvedline.CurvedLineWaypoint;
import com.xenoage.zong.core.music.curvedline.CurvedLineWaypoint.Type;
import com.xenoage.zong.core.music.direction.Wedge;
import com.xenoage.zong.core.music.group.BarlineGroup;
import com.xenoage.zong.core.music.group.BracketGroup;
import com.xenoage.zong.core.music.rest.Rest;
import com.xenoage.zong.core.music.text.Lyric;
import com.xenoage.zong.core.music.text.Lyric.SyllableType;
import com.xenoage.zong.core.music.tuplet.Tuplet;
import com.xenoage.zong.core.music.util.BeatE;
import com.xenoage.zong.core.text.Alignment;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.data.text.FormattedTextElement;
import com.xenoage.zong.data.text.FormattedTextString;
import com.xenoage.zong.data.text.FormattedTextStyle;
import com.xenoage.zong.musiclayout.FrameArrangement;
import com.xenoage.zong.musiclayout.ScoreFrameLayout;
import com.xenoage.zong.musiclayout.SystemArrangement;
import com.xenoage.zong.musiclayout.continued.ContinuedCurvedLine;
import com.xenoage.zong.musiclayout.continued.ContinuedElement;
import com.xenoage.zong.musiclayout.continued.ContinuedVolta;
import com.xenoage.zong.musiclayout.continued.ContinuedWedge;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterContext;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.cache.NotationsCache;
import com.xenoage.zong.musiclayout.layouter.cache.OpenBeamsCache;
import com.xenoage.zong.musiclayout.layouter.cache.OpenCurvedLinesCache;
import com.xenoage.zong.musiclayout.layouter.cache.OpenLyricsCache;
import com.xenoage.zong.musiclayout.layouter.cache.OpenTupletsCache;
import com.xenoage.zong.musiclayout.layouter.cache.util.BeamedStemStampings;
import com.xenoage.zong.musiclayout.layouter.cache.util.CurvedLineCache;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.util.ChordStampings;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.util.LastLyrics;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.util.StaffStampings;
import com.xenoage.zong.musiclayout.notations.ChordNotation;
import com.xenoage.zong.musiclayout.notations.ClefNotation;
import com.xenoage.zong.musiclayout.notations.NormalTimeNotation;
import com.xenoage.zong.musiclayout.notations.Notation;
import com.xenoage.zong.musiclayout.notations.RestNotation;
import com.xenoage.zong.musiclayout.notations.TraditionalKeyNotation;
import com.xenoage.zong.musiclayout.spacing.ColumnSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.LeadingSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.MeasureSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.SpacingElement;
import com.xenoage.zong.musiclayout.spacing.horizontal.VoiceSpacing;
import com.xenoage.zong.musiclayout.stampings.BarlineStamping;
import com.xenoage.zong.musiclayout.stampings.BracketStamping;
import com.xenoage.zong.musiclayout.stampings.CurvedLineStamping;
import com.xenoage.zong.musiclayout.stampings.NoteheadStamping;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.StaffTextStamping;
import com.xenoage.zong.musiclayout.stampings.Stamping;
import com.xenoage.zong.musiclayout.stampings.TextStamping;
import com.xenoage.zong.musiclayout.stampings.VoltaStamping;
import com.xenoage.zong.musiclayout.stampings.WedgeStamping;


/**
 * This strategy creates the {@link ScoreFrameLayout}
 * for a given {@link FrameArrangement}.
 * 
 * TODO: TIDY or redesign. This is probably the ugliest
 * class in the whole project.
 * 
 * @author Andreas Wenger
 */
public class ScoreFrameLayoutStrategy
	implements ScoreLayouterStrategy
{
	
	//used strategies
	private final StaffStampingsStrategy staffStampingsStrategy;
	private final MusicElementStampingStrategy musicElementStampingStrategy;
	private final BeamStampingStrategy beamStampingStrategy;
	private final CurvedLineStampingStrategy curvedLineStampingStrategy;
	private final LyricStampingStrategy lyricStampingStrategy;
	private final VoltaStampingStrategy voltaStampingStrategy;
	private final DirectionStampingStrategy directionStampingStrategy;
	private final TupletStampingStrategy tupletStampingStrategy;
	
	
	public ScoreFrameLayoutStrategy(StaffStampingsStrategy staffStampingsStrategy,
		MusicElementStampingStrategy voiceElementStampingStrategy,
		BeamStampingStrategy beamStampingStrategy, CurvedLineStampingStrategy curvedLineStampingStrategy,
		LyricStampingStrategy lyricStampingStrategy, VoltaStampingStrategy voltaStampingStrategy,
		DirectionStampingStrategy directionStampingStrategy, TupletStampingStrategy tupletStampingStrategy)
	{
		this.staffStampingsStrategy = staffStampingsStrategy;
		this.musicElementStampingStrategy = voiceElementStampingStrategy;
		this.beamStampingStrategy = beamStampingStrategy;
		this.curvedLineStampingStrategy = curvedLineStampingStrategy;
		this.lyricStampingStrategy = lyricStampingStrategy;
		this.voltaStampingStrategy = voltaStampingStrategy;
		this.directionStampingStrategy = directionStampingStrategy;
		this.tupletStampingStrategy = tupletStampingStrategy;
	}
	
	
	/**
   * Creates a {@link ScoreFrameLayout} from the given {@link FrameArrangement}.
   * 
   * @param continuedElements  unclosed elements from the last frame, like slurs
   *                           spanning over more than one frame
   */
  public ScoreFrameLayout computeScoreFrameLayout(FrameArrangement frameArr, 
  	NotationsCache notations, Vector<ContinuedElement> unclosedElements, ScoreLayouterContext lc)
  {
  	Score score = lc.getScore(); 
  	Globals globals = score.getGlobals();
  	ScoreHeader header = score.getScoreHeader();
    int stavesCount = score.getStavesCount();
    StavesList stavesList = score.getStavesList();
    LinkedList<StaffStamping> staffStampsPool = new LinkedList<StaffStamping>();
    LinkedList<Stamping> otherStampsPool = new LinkedList<Stamping>();
    MeasureNumbering measureNumbering = score.getScoreFormat().getMeasureNumbering();
    
    //default lyric style
    FormattedTextStyle defaultLyricStyle = new FormattedTextStyle(
    	score.getScoreFormat().getLyricFont());
    
    //caches
    OpenBeamsCache openBeamsCache = new OpenBeamsCache();
    OpenCurvedLinesCache openCurvedLinesCache = new OpenCurvedLinesCache();
    LinkedList<ContinuedVolta> openVoltasCache = new LinkedList<ContinuedVolta>();
    LinkedList<ContinuedWedge> openWedgesCache = new LinkedList<ContinuedWedge>();
    OpenLyricsCache openLyricsCache = new OpenLyricsCache();
    LastLyrics lastLyrics = new LastLyrics();
    OpenTupletsCache openTupletsCache = new OpenTupletsCache();
    
    //add continued elements
    for (ContinuedElement ce : unclosedElements)
	  {
    	if (ce instanceof ContinuedCurvedLine)
    	{
    		openCurvedLinesCache.add(CurvedLineCache.createContinued((ContinuedCurvedLine) ce));
    	}
    	else if (ce instanceof ContinuedVolta)
    	{
    		openVoltasCache.add((ContinuedVolta) ce);
    	}
    	else if (ce instanceof ContinuedWedge)
    	{
    		openWedgesCache.add((ContinuedWedge) ce);
    	}
	  }
    
    //create staff stampings
    StaffStampings staffStampings = staffStampingsStrategy.createStaffStampings(score, frameArr);
    staffStampings.addAllTo(staffStampsPool);
    
    //go through the systems
    for (int iSystem : range(frameArr.getSystems()))
    { 
      SystemArrangement system = frameArr.getSystems().get(iSystem);
      Vector<StaffStamping> systemStaves = staffStampings.getAllOfSystem(iSystem);
      
      //add the part names (first system) or part abbreviations (other systems)
    	int iStaff = 0;
    	for (Part part : stavesList.getParts())
    	{
    		StaffStamping firstStaff = systemStaves.get(iStaff);
    		StaffStamping lastStaff = systemStaves.get(iStaff + part.getStavesCount() - 1);
    		iStaff += part.getStavesCount();
    		String name = (iSystem == 0 ? part.getName() : part.getAbbreviation());
    		if (name != null && name.length() > 0)
    		{
      		//in the middle of the staves
      		float top = firstStaff.getPosition().y;
      		float bottom = lastStaff.getPosition().y + (lastStaff.getLinesCount() - 1) *
      			lastStaff.getInterlineSpace();
      		FormattedText text = new FormattedText(name,
      			new FormattedTextStyle(), Alignment.Right);
      		float middle = (top + bottom) / 2 + 
      			text.getParagraphs().next().getAscent() / 3; //correction of baseline. /3 looks good.
      		otherStampsPool.add(new TextStamping(text,
      			new Point2f(firstStaff.getPosition().x - 5, middle))); //TODO
    		}
    	}
    	
      //create the brackets at the beginning of the system
      for (int i = 0; i < stavesList.getBracketGroups().size(); i++)
      {
      	BracketGroup bracketGroup = stavesList.getBracketGroups().get(i);
        otherStampsPool.add(new BracketStamping(systemStaves.get(bracketGroup.getStavesRange().getStartIndex()),
        	systemStaves.get(bracketGroup.getStavesRange().getEndIndex()), system.getMarginLeft() - 1.4f, bracketGroup.getStyle()));
      }
      
      //add the barlines
      float xOffset = staffStampings.get(iSystem, 0).getPosition().x;
      //common barline at the beginning, when system has at least one measure
      if (system.getColumnSpacings().size() > 0)
      {
      	otherStampsPool.add(new BarlineStamping(Barline.createRegularBarline(),
      		systemStaves, xOffset, BarlineGroup.Style.Common));
      }
      //barlines within the system and measure numbers
      int iMeasureMax = system.getColumnSpacings().size() - 1;
      StaffStamping firstStaff = staffStampings.get(iSystem, 0);
      for (int iMeasure = 0; iMeasure <= iMeasureMax; iMeasure++)
      {
      	float xLeft = xOffset;
      	//measure numbering
      	int globalMeasureIndex = system.getStartMeasureIndex() + iMeasure;
      	boolean showMeasureNumber = false;
      	if (measureNumbering == MeasureNumbering.System)
      	{
      		//measure number at the beginning of each system (except the first one)
      		if (iMeasure == 0 && globalMeasureIndex > 0)
      		{
      			showMeasureNumber = true;
      		}
      	}
      	else if (measureNumbering == MeasureNumbering.Measure)
      	{
      		//measure number at each measure (except the first one)
      		if (globalMeasureIndex > 0)
      		{
      			showMeasureNumber = true;
      		}
      	}
      	if (showMeasureNumber)
      	{
      		otherStampsPool.add(new StaffTextStamping(firstStaff,
    				null, new FormattedText("" + (globalMeasureIndex + 1),
    					new FormattedTextStyle(8), Alignment.Left),
    					sp(xLeft, firstStaff.getLinesCount() * 2)));
      	}
      	//for the first measure in the system: begin after leading spacing
      	if (iMeasure == 0)
      		xLeft += system.getColumnSpacings().get(iMeasure).getLeadingWidth();
      	xOffset += system.getColumnSpacings().get(iMeasure).getWidth();
      	float xRight = xOffset;
        //regard the groups of the score
        for (iStaff = 0; iStaff < stavesCount; iStaff++)
        {
        	ColumnHeader columnHeader = score.getScoreHeader().getColumnHeader(
          	iMeasure + system.getStartMeasureIndex());
          BarlineGroup.Style barlineGroupStyle = BarlineGroup.Style.Single;
          BarlineGroup group = stavesList.getBarlineGroupByStaff(iStaff);
          if (group != null)
            barlineGroupStyle = group.getStyle();
          Vector<StaffStamping> groupStaves = getBarlineGroupStaves(systemStaves, group);
          //start barline
          Barline startBarline = columnHeader.getStartBarline();
          if (startBarline != null)
          	otherStampsPool.add(new BarlineStamping(startBarline, groupStaves, xLeft, barlineGroupStyle));
          //end barline. if none is set, use a regular one.
          Barline endBarline = columnHeader.getEndBarline();
          if (endBarline == null)
          	endBarline = Barline.createRegularBarline();
          otherStampsPool.add(new BarlineStamping(endBarline, groupStaves, xRight, barlineGroupStyle));
          //middle barlines
          if (columnHeader.getMiddleBarlinesCount() > 0)
          {
          	for (BeatE<Barline> middleBarline : columnHeader.getMiddleBarlines())
          	{
          		otherStampsPool.add(new BarlineStamping(
          			middleBarline.getElement(), groupStaves,
          			xLeft + system.getColumnSpacings().get(iMeasure).getBarlineOffset(middleBarline.getBeat()),
          			barlineGroupStyle));
          	}
          }
          //go to next group
          if (group != null)
            iStaff = group.getStavesRange().getEndIndex();
        }
      }
      
      //fill the staves
      for (iStaff = 0; iStaff < stavesCount; iStaff++)
      {
      	StaffStamping staff = systemStaves.get(iStaff);
      	xOffset = staff.getPosition().x;
        float interlineSpace = staff.getInterlineSpace();
        
        for (int iMeasure = 0; iMeasure < system.getColumnSpacings().size(); iMeasure++)
        {
          ColumnSpacing measureColumnSpacing = system.getColumnSpacings().get(iMeasure);
          MeasureSpacing measureStaffSpacing = measureColumnSpacing.getMeasureSpacings().get(iStaff);
          
          //add leading spacing elements, if available
          LeadingSpacing leadingSpacing = measureStaffSpacing.getLeadingSpacing();
          if (leadingSpacing != null)
          {
            for (SpacingElement spacingElement : leadingSpacing.getSpacingElements())
            {
              MusicElement element = spacingElement.getElement();
              if (element != null)
              {
              	float x = xOffset + spacingElement.getOffset() * interlineSpace;
              	Notation notation = notations.get(element);
              	if (notation == null)
              		throw new RuntimeException("No notation for element " + element + " at " +
              			score.getMP(element));
              	otherStampsPool.add(createNoVoiceElementStamping(notation, x, staff));
              }
            }
          }
          
          //now begin with the voices
          float voicesOffset = xOffset + measureColumnSpacing.getLeadingWidth();
          
          //add voice elements within this measure
          for (VoiceSpacing voiceSpacing : measureStaffSpacing.getVoiceSpacings())
          {
            Vector<SpacingElement> voice = voiceSpacing.getSpacingElements();
            
            //TODO
            //don't stamp leading rests in voice 2 - TODO: config?
            //boolean stampRests = (iVoice == 0);
            
            //create the elements
            for (SpacingElement spacingElement : voice)
            {
              MusicElement element = spacingElement.getElement();
              if (element != null /* TODO && (stampRests || !(element instanceof Rest)) */)
              {
              	Notation notation = notations.get(element);
                float x = voicesOffset + spacingElement.getOffset() * interlineSpace;
                if (element instanceof Chord)
                {
                	//chord
                	otherStampsPool.addAll(createChordStampings((ChordNotation) notation, x, staff,
                		iStaff, iSystem, defaultLyricStyle, openBeamsCache, openCurvedLinesCache,
                		openLyricsCache, lastLyrics, openTupletsCache, globals));
                }
                else if (element instanceof Rest)
                {
                	//rest
                	otherStampsPool.add(musicElementStampingStrategy.createRestStamping((RestNotation) notation, x, staff));
                }
                else if (element instanceof MeasureElement)
                {
                	//clef, key, time, ...
                	otherStampsPool.add(createNoVoiceElementStamping(notations.get(element), x, staff));
                }
                else
                {
                	throw new IllegalArgumentException("Notation not supported: " + notation);
                }
              }
            }
            
            
            //create the elements, for which there are no spacing elements available
            /* GOON
            for (MusicElement element : score.getStaff(iStaff).getMeasures().get(
            	iMeasure + system.getStartMeasureIndex()).getVoices().get(iVoice).getElements())
            {
            	if (element instanceof Tempo)
            	{
            		otherStampsPool.add(directionStampingStrategy.createTempo((Tempo) element,
            			mp(iStaff, iMeasure + system.getStartMeasureIndex(),
            				iVoice, element.getBeat()), staff));
            	}
            	else if (element instanceof Dynamics)
            	{
            		otherStampsPool.add(directionStampingStrategy.createDynamics((Dynamics) element,
            			mp(iStaff, iMeasure + system.getStartMeasureIndex(),
            				iVoice, element.getBeat()), staff));
            	}
            	else if (element instanceof Pedal)
            	{
            		otherStampsPool.add(directionStampingStrategy.createPedal((Pedal) element,
            			mp(iStaff, iMeasure + system.getStartMeasureIndex(),
            				iVoice, element.getBeat()), staff));
            	}
            	else if (element instanceof Words)
            	{
            		otherStampsPool.add(directionStampingStrategy.createWords((Words) element,
            			mp(iStaff, iMeasure + system.getStartMeasureIndex(),
            				iVoice, element.getBeat()), staff));
            	}
            }
            */
            
          }
          
          xOffset += measureColumnSpacing.getWidth();
          
        }
        
      }
      
      //create all voltas in this system
      otherStampsPool.addAll(createVoltas(
      	iSystem, system, header, staffStampings, openVoltasCache, defaultLyricStyle));
      
      //create all wedges in this system
      otherStampsPool.addAll(createWedges(
      	iSystem, system, score, staffStampings, openWedgesCache));
      
    }
    
    //create the collected beams
    otherStampsPool.addAll(createBeams(openBeamsCache, globals));
		
		//create the collected ties and slurs
    otherStampsPool.addAll(createTiesAndSlurs(
    	openCurvedLinesCache, staffStampings, frameArr.getSystems().size()));
		
		//create the open lyric underscore lines
		for (Tuple3<StaffTextStamping, NoteheadStamping, Integer> openUnderscore : openLyricsCache.getUnderscores())
		{
			//TODO: fetch style efficiently
			FormattedTextStyle style = defaultLyricStyle;
			FormattedTextElement firstElement = openUnderscore.get1().getText().getParagraphs().next().getElements().next();
			if (firstElement instanceof FormattedTextString)
			{
				style = ((FormattedTextString) firstElement).getStyle();
			}
			otherStampsPool.addAll(
				lyricStampingStrategy.createUnderscoreStampings(openUnderscore.get1(), openUnderscore.get2(), style,
					staffStampings.getAllOfStaff(openUnderscore.get3())));
		}
		
		//create tuplet brackets/numbers
		for (Tuplet tuplet : openTupletsCache)
		{
			otherStampsPool.add(tupletStampingStrategy.createTupletStamping(tuplet, openTupletsCache));
		}
		
		//collect elements that have to be continued on the next frame
		PVector<ContinuedElement> continuedElements = pvec();
		for (CurvedLineCache clc : openCurvedLinesCache)
	  {
			continuedElements = continuedElements.plus(clc.getContinuedCurvedLine());
	  }
		continuedElements = continuedElements.plusAll(openVoltasCache);
		continuedElements = continuedElements.plusAll(openWedgesCache);
		
		return new ScoreFrameLayout(frameArr,
			pvec(staffStampsPool), pvec(otherStampsPool), continuedElements);
  }
  
  
  /**
   * Returns the stamping for the given notation of a {@link MusicElement},
   * using an appropriate strategy.
   */
  private Stamping createNoVoiceElementStamping(Notation notation,
  	float positionX, StaffStamping staff)
  {
  	if (notation instanceof ClefNotation)
  	{
  		return musicElementStampingStrategy.createClefStamping(
  			(ClefNotation) notation, positionX, staff);
  	}
  	else if (notation instanceof TraditionalKeyNotation)
  	{
  		return musicElementStampingStrategy.createKeyStamping(
  			(TraditionalKeyNotation) notation, positionX, staff);
  	}
  	else if (notation instanceof NormalTimeNotation)
  	{
  		return musicElementStampingStrategy.createTimeStamping(
  			(NormalTimeNotation) notation, positionX, staff);
  	}
  	else
  	{
  		throw new IllegalArgumentException("Notation not supported: " + notation);
  	}
  }
	
	
  /**
   * Returns the stampings for the given {@link Chord}.
   * The given {@link OpenBeamsCache}, {@link OpenCurvedLinesCache},
   * {@link OpenLyricsCache}, {@link LastLyrics} and {@link OpenTupletsCache} may be modified.
   */
  private LinkedList<Stamping> createChordStampings(ChordNotation chord, float positionX, StaffStamping staff,
  	int staffIndex, int systemIndex, FormattedTextStyle defaultLyricStyle, OpenBeamsCache openBeamsCache,
  	OpenCurvedLinesCache openCurvedLinesCache, OpenLyricsCache openLyricsCache, LastLyrics lastLyrics,
  	OpenTupletsCache openTupletsCache, Globals globals)
  {
  	LinkedList<Stamping> ret = new LinkedList<Stamping>();
  	
  	//noteheads, leger lines, dots, accidentals, stem, flags, articulations
    ChordStampings chordSt = musicElementStampingStrategy.createChordStampings(
    	chord, positionX, staff, globals);
    chordSt.addAllTo(ret);
    
    //beam  
    if (chordSt.stem != null || chordSt.openStem != null)
    {
      //if the chord belongs to a beam, add the stem to
      //the corresponding list of beamed stems, so that the
      //beam can be created later. the middle stems were not stamped
    	//yet, also remember them.
    	Beam beam = globals.getBeams().get(chord.getMusicElement());
      if (beam != null)
      {
      	BeamWaypoint.Type wpt = beam.getWaypointType(chord.getMusicElement());
      	BeamedStemStampings bss = openBeamsCache.get(beam);
        if (wpt == BeamWaypoint.Type.Start)
        	bss = bss.withFirstStem(chordSt.stem);
        else if (wpt == BeamWaypoint.Type.Stop)
        	bss = bss.withLastStem(chordSt.stem);
        else
        	bss = bss.plusMiddleStem(chordSt.openStem);
        openBeamsCache.set(beam, bss);
      }
    }
    
    //ties and slurs
    Chord chordElement = (Chord) chord.getMusicElement();
    PSet<CurvedLine> curvedLines = globals.getCurvedLines().get(chordElement);
    if (curvedLines != null)
    {
	    for (CurvedLine cl : curvedLines)
	    {
	    	Type type = cl.getWaypointType(chordElement);
	    	CurvedLineWaypoint clwp = cl.getWaypoint(chordElement);
	    	int noteIndex = NullTools.notNull(clwp.getNoteIndex(), 0); //TODO: choose top/bottom
	    	NoteheadStamping notehead = chordSt.noteheads.get(noteIndex);
	    	//define the placement: above or below (TODO: better strategy)
	    	VSide side = VSide.Top;
	    	if (type == Type.Start)
	    	{
	    		if (cl.getSide() != null)
	    		{
	    			//use custom side
	    			side = cl.getSide();
	    		}
	    		else
	    		{
	    			//use default side:
		    		//for all notes over line position 3, use above, else below
		    		side = (notehead.getPosition().yLp > 3 ? VSide.Top : VSide.Bottom);
	    		}
	    	}
	    	//compute position
	  		if (type == Type.Start)
	  		{
	  			//create start information
	  			//regard articulations
	  			float distance = 0;
	  			if (chord.getArticulationsAlignment() != null)
	  			{
	  				distance = chord.getArticulationsAlignment().getTotalHeightIS();
	  			}
	  			CurvedLineCache tiedChords = CurvedLineCache.createNew(
	  				cl, side, staffIndex, notehead, distance, systemIndex);
	  			openCurvedLinesCache.add(tiedChords);
	  		}
	  		else
	  		{
	  			//create stop information
	  			CurvedLineCache tiedChords = openCurvedLinesCache.get(cl);
	  			if (tiedChords == null)
	  			{
	  				//start notehead of this tie is unknown (must be from some preceding frame), which was forgotten
	  				//ignore it. TODO: warning
	  			}
	  			else
	  			{
	  				//regard articulations
	    			float distance = 0;
	    			if (chord.getArticulationsAlignment() != null)
	    			{
	    				distance = chord.getArticulationsAlignment().getTotalHeightIS();
	    			}
	  				tiedChords.setStop(notehead, distance, systemIndex);
	  			}
	  		}
	    }
    }
    
    //lyric
    Vector<Lyric> lyrics = globals.getAttachments().getLyrics(chordElement);
    if (lyrics.size() > 0)
    {
    	float baseLine = -8;
    	
    	for (Lyric lyric : lyrics)
    	{
    		SyllableType lyricType = lyric.getSyllableType();
    		StaffTextStamping lastLyric = lastLyrics.get(staffIndex, lyric.getVerse());
    		
    		if (lyricType == SyllableType.Extend)
    		{
    			//extend
      		if (lastLyric != null) //TODO: frame breaks...
      		{
	    			//remember it
	  				openLyricsCache.setUnderscore((Lyric) lastLyric.getMusicElement(),
	  					lastLyric, chordSt.noteheads.getFirst()/* TODO*/, staffIndex);
      		}
    		}
    		else
    		{
    			//normal lyric
    			
    			//create text stamping
    			StaffTextStamping sts = lyricStampingStrategy.createSyllableStamping(lyric,
    				defaultLyricStyle, staff, chordSt.noteheads.getFirst()/* TODO*/.getPosition().xMm, baseLine);
    			ret.add(sts);
    			
    			//when middle or end syllable, add a hypen between the preceding syllable and this syllable
    			if (lastLyric != null) //TODO: frame breaks...
      		{
	    			if (lyricType == SyllableType.Middle || lyricType == SyllableType.End)
	    			{
	    				StaffTextStamping hyphenStamping = lyricStampingStrategy.createHyphenStamping(
	    					lastLyric, sts, defaultLyricStyle);
	      			ret.add(hyphenStamping);
	    			}
      		}
    				
    			//remember this lyric as the currently last one in the current staff and verse
      		lastLyrics.set(staffIndex, lyric.getVerse(), sts);
    			
    		}
    		
    		baseLine += -5;
    	}
    }
    
    //directions
    ret.addAll(directionStampingStrategy.createForChord(
    	chord.getMusicElement(), chordSt, globals));
    
    //tuplet
    Tuplet tuplet = globals.getTuplets().get(chordElement);
    if (tuplet != null)
    {
    	openTupletsCache.addChord(chordElement, tuplet, chordSt);
    }
    
    return ret;
  }
  
  
  /**
   * Creates the beams collected in the given {@link OpenBeamsCache}.
   */
  private LinkedList<Stamping> createBeams(OpenBeamsCache openBeamsCache, Globals globals)
  {
  	LinkedList<Stamping> ret = new LinkedList<Stamping>();
    for (BeamedStemStampings beam : openBeamsCache)
    {
    	ret.addAll(beamStampingStrategy.createBeamStampings(beam, globals));
    }
    return ret;
  }
  
  
  /**
   * Gets the staves of the given group, using the given
   * list of all staves. If the given group is null,
   * all staves are returned.
   */
  private Vector<StaffStamping> getBarlineGroupStaves(
  	Vector<StaffStamping> systemStaves, BarlineGroup barlineGroup)
  {
    if (barlineGroup == null)
      return systemStaves;
    else
    {
    	//use efficient sublist
    	return systemStaves.subList(barlineGroup.getStavesRange().getStartIndex(),
    		barlineGroup.getStavesRange().getEndIndex() + 1);
    }
  }


	/**
	 * Creates the ties and slurs collected in the given {@link OpenCurvedLinesCache}.
	 * All closed slurs are removed from the cache. The unclosed slurs (which have to
	 * be continued on the next frame) remain in the cache.
	 */
	private LinkedList<CurvedLineStamping> createTiesAndSlurs(OpenCurvedLinesCache openCurvedLinesCache,
		StaffStampings staffStampings, int systemsCount)
	{
		LinkedList<CurvedLineStamping> ret = new LinkedList<CurvedLineStamping>();
	  for (Iterator<CurvedLineCache> itCL = openCurvedLinesCache.iterator(); itCL.hasNext();)
	  {
	  	boolean simpleLine = false; //current curved line simple (within one staff)?
	  	CurvedLineCache cl = itCL.next();
	  	int middleSlurStartIndex = 0;
	  	//if the start is known, begin (and if possible end) the slur in its staff
	  	if (cl.isStartKnown())
	  	{
	  		Tuple2<CurvedLineStamping, Boolean> startCLS =
	  			curvedLineStampingStrategy.createCurvedLineStampingStart(cl);
	  		ret.add(startCLS.get1());
	  		if (startCLS.get2() == false)
	  		{
	  			//curved line is finished, remove cache
	  			simpleLine = true;
	  			itCL.remove();
	  		}
	  		else
	  		{
	  			//curved lines is continued in next system
	  			middleSlurStartIndex = cl.getStartSystem() + 1;
	  		}
	  	}
	  	//if not a simple line, compute and stamp middle slurs and end slur
	  	if (!simpleLine)
	  	{
	  		//if the stop notehead is known, compute the system up zo where middle staff slurs (if any)
		  	//have to be stamped and stamp them
		  	int middleSlurStopIndex = systemsCount - 1;
		  	if (cl.isStopKnown())
		  	{
		  		middleSlurStopIndex = cl.getStopSystem() - 1;
		  	}
		  	for (int iSystem = middleSlurStartIndex; iSystem <= middleSlurStopIndex; iSystem++)
		  	{
		  		ret.add(curvedLineStampingStrategy.createCurvedLineStampingMiddle(
		  			cl.getContinuedCurvedLine(), staffStampings.get(iSystem, cl.getStaffIndex())));
		  	}
		  	//create stop slur (if any)
		  	if (!simpleLine && cl.isStopKnown())
		  	{
		  		ret.add(curvedLineStampingStrategy.createCurvedLineStampingStop(cl));
		  		//curved line is finished, remove cache
		  		itCL.remove();
		  	}
	  	}
	  }
	  return ret;
	}
	
	
	/**
	 * Creates all volta stampings in the given system.
	 * All closed voltas are removed from the cache. The unclosed voltas (which have to
	 * be continued on the next system or frame) remain in the cache (or are added, if they are new).
	 */
	private LinkedList<VoltaStamping> createVoltas(int systemIndex, SystemArrangement system,
		ScoreHeader header, StaffStampings staffStampings,
		LinkedList<ContinuedVolta> openVoltasCache, FormattedTextStyle textStyle)
	{
		LinkedList<VoltaStamping> ret = new LinkedList<VoltaStamping>();
		//find new voltas beginning in this system
		for (int iMeasure = 0; iMeasure < system.getColumnSpacings().size(); iMeasure++)
    {
      int scoreMeasure = system.getStartMeasureIndex() + iMeasure;
      ColumnHeader columnHeader = header.getColumnHeader(scoreMeasure);
      if (columnHeader.getVolta() != null)
      {
      	openVoltasCache.add(new ContinuedVolta(columnHeader.getVolta(),
      		scoreMeasure, 0)); //staff 0: TODO
      }
    }
		//draw voltas in the cache, and remove them if closed in this system
		int endMeasureIndex = system.getEndMeasureIndex();
		for (Iterator<ContinuedVolta> itV = openVoltasCache.iterator(); itV.hasNext();)
	  {
			ContinuedVolta volta = itV.next();
			ret.add(voltaStampingStrategy.createVoltaStamping(volta.getMusicElement(), volta.getStartMeasureIndex(),
				staffStampings.get(systemIndex, volta.getStaffIndex()), textStyle));
			if (volta.getStartMeasureIndex() + volta.getMusicElement().getLength() - 1 <= endMeasureIndex)
			{
				//volta is closed
				itV.remove();
			}
	  }
		return ret;
	}
	
	
	/**
	 * Creates all wedge stampings in the given system.
	 * All closed wedges are removed from the cache. The unclosed wedges (which have to
	 * be continued on the next system or frame) remain in the cache (or are added, if they are new).
	 */
	private LinkedList<WedgeStamping> createWedges(int systemIndex,
		SystemArrangement system, Score score, StaffStampings staffStampings,
		LinkedList<ContinuedWedge> openWedgesCache)
	{
		LinkedList<WedgeStamping> ret = new LinkedList<WedgeStamping>();
		Globals globals = score.getGlobals();
		//find new wedges beginning in this staff
		for (int iStaff = 0; iStaff < score.getStavesCount(); iStaff++)
		{
			Staff staff = score.getStaff(iStaff);
			for (int iMeasure = system.getStartMeasureIndex(); iMeasure <= system.getEndMeasureIndex(); iMeasure++)
	    {
				Measure measure = staff.getMeasures().get(iMeasure);
				for (Voice voice : measure.getVoices())
				{
					for (MusicElement element : voice.getElements())
					{
						if (element instanceof Wedge)
						{
							openWedgesCache.add(new ContinuedWedge((Wedge) element, iStaff));
						}
					}
				}
	    }
		}
		//draw wedges in the cache, and remove them if closed in this system
		for (Iterator<ContinuedWedge> itW = openWedgesCache.iterator(); itW.hasNext();)
	  {
			ContinuedWedge wedge = itW.next();
			ret.add(directionStampingStrategy.createWedgeStamping(wedge.getMusicElement(),
				staffStampings.get(systemIndex, wedge.getStaffIndex()), globals));
			if (globals.getMPWithAttachments(wedge.getMusicElement().getWedgeEnd()).getMeasure() <=
				system.getEndMeasureIndex())
			{
				//wedge is closed
				itW.remove();
			}
	  }
		return ret;
	}
	

}
