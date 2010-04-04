package com.xenoage.zong.musiclayout.layouter.notation;

import static com.xenoage.zong.core.music.Pitch.pi;
import static com.xenoage.zong.core.music.util.BeatInterval.BeforeOrAt;
import static com.xenoage.zong.io.score.ScoreController.getInterlineSpace;
import static com.xenoage.zong.io.score.ScoreController.getMusicContext;

import java.awt.Font;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xenoage.pdlib.Vector;
import com.xenoage.util.Range;
import com.xenoage.util.math.Fraction;
import com.xenoage.util.xml.XMLReader;
import com.xenoage.zong.app.symbols.SymbolPool;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.Globals;
import com.xenoage.zong.core.music.InstrumentChange;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.Measure;
import com.xenoage.zong.core.music.MusicContext;
import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.core.music.Voice;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.chord.StemDirection;
import com.xenoage.zong.core.music.clef.Clef;
import com.xenoage.zong.core.music.direction.Direction;
import com.xenoage.zong.core.music.key.TraditionalKey;
import com.xenoage.zong.core.music.rest.Rest;
import com.xenoage.zong.core.music.text.Lyric;
import com.xenoage.zong.core.music.text.Lyric.SyllableType;
import com.xenoage.zong.core.music.time.NormalTime;
import com.xenoage.zong.core.music.util.BeatInterval;
import com.xenoage.zong.core.music.util.Column;
import com.xenoage.zong.io.score.ScoreController;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterContext;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.cache.NotationsCache;
import com.xenoage.zong.musiclayout.notations.ChordNotation;
import com.xenoage.zong.musiclayout.notations.ClefNotation;
import com.xenoage.zong.musiclayout.notations.NormalTimeNotation;
import com.xenoage.zong.musiclayout.notations.Notation;
import com.xenoage.zong.musiclayout.notations.RestNotation;
import com.xenoage.zong.musiclayout.notations.TraditionalKeyNotation;
import com.xenoage.zong.musiclayout.notations.chord.AccidentalsAlignment;
import com.xenoage.zong.musiclayout.notations.chord.ArticulationsAlignment;
import com.xenoage.zong.musiclayout.notations.chord.NotesAlignment;
import com.xenoage.zong.musiclayout.notations.chord.StemAlignment;
import com.xenoage.zong.musiclayout.spacing.horizontal.ElementWidth;
import com.xenoage.zong.util.exceptions.IllegalMPException;
import com.xenoage.util.io.IO;
import com.xenoage.util.logging.Log;
import com.xenoage.util.text.TextMeasurer;


/**
 * This strategy computes information about the layout
 * of {@link MusicElement}s, e.g.
 * the needed horizontal space in interline spaces,
 * for musical elements like chords, rests,
 * clefs and so on.
 * 
 * Some of the default values of data/layout/widths.xml
 * are adepted from "Ross: The Art of Music Engraving",
 * page 77.
 *
 * @author Andreas Wenger
 */
public class NotationStrategy
	implements ScoreLayouterStrategy
{

	//used strategies
	private final StemDirectionStrategy stemDirectionStrategy;
	private final NotesAlignmentStrategy notesAlignmentStrategy;
	private final AccidentalsAlignmentStrategy accidentalsAlignmentStrategy;
	private final StemAlignmentStrategy stemAlignmentStrategy;
	private final ArticulationsAlignmentStrategy articulationsAlignmentStrategy;
	
	
	//duration-to-width mapping
	class DurationWidth
	{

		public int durationNum, durationDen;
		public float width;


		public DurationWidth(int durationNum, int durationDen, float width)
		{
			this.durationNum = durationNum;
			this.durationDen = durationDen;
			this.width = width;
		}
	}

	static DurationWidth[] widths;

	//TODO: xml file. cleaner struct.
	public static float distanceSharps = 1.2f;
	public static float distanceFlats = 1f;
	
	public static float clefWidthIS = 4f;
	public static float smallClefScaling = 0.75f;
	

	/**
	 * Creates a new instance of a {@link NotationStrategy}.
	 */
	public NotationStrategy(StemDirectionStrategy stemDirectionStrategy,
		NotesAlignmentStrategy notesAlignmentStrategy, AccidentalsAlignmentStrategy accidentalsAlignmentStrategy,
		StemAlignmentStrategy stemAlignmentStrategy, ArticulationsAlignmentStrategy articulationsAlignmentStrategy)
	{
		this.stemDirectionStrategy = stemDirectionStrategy;
		this.notesAlignmentStrategy = notesAlignmentStrategy;
		this.accidentalsAlignmentStrategy = accidentalsAlignmentStrategy;
		this.stemAlignmentStrategy = stemAlignmentStrategy;
		this.articulationsAlignmentStrategy = articulationsAlignmentStrategy;
		//load the duration-to-width mapping from the file "data/layout/widths.xml".
		try
		{
			Document doc = XMLReader.readFile(IO.openInputStream("data/layout/widths.xml"));
			List<Element> eWidths = XMLReader.elements(XMLReader.root(doc), "width");
			widths = new DurationWidth[eWidths.size()];
			int i = 0;
			for (Element e : eWidths)
			{
				//duration format: x/y, e.g. 1/4
				String[] duration = XMLReader.attributeNotNull(e, "duration").split("/");
				//width format: x+y/z, eg. 3+1/2
				String[] width1 = XMLReader.attributeNotNull(e, "width").split("\\+");
				String[] width2 = width1[1].split("/");
				widths[i] = new DurationWidth(Integer.parseInt(duration[0]), Integer
					.parseInt(duration[1]), Float.parseFloat(width1[0])
					+ Float.parseFloat(width2[0]) / Float.parseFloat(width2[1]));
				i++;
			}
		}
		catch (Exception ex)
		{
			//error: could not read the file!
			Log.log(Log.ERROR, "Could not read the file \"data/layout/widths.xml\":");
			Log.log(ex);
			//use some hardcoded values
			widths = new DurationWidth[] { new DurationWidth(1, 64, 1 + 1f / 4),
				new DurationWidth(1, 32, 1 + 1f / 2),
				new DurationWidth(1, 16, 1 + 3f / 4),
				new DurationWidth(1, 8, 2 + 1f / 2), new DurationWidth(1, 4, 3 + 1f / 2),
				new DurationWidth(1, 2, 4 + 3f / 4), new DurationWidth(1, 1, 7 + 1f / 4) };
		}
	}
	
	
	/**
	 * Computes the {@link Notation}s of all {@link MusicElement}s
	 * in the given context.
	 */
	public NotationsCache computeNotations(ScoreLayouterContext lc)
	{
		NotationsCache ret = new NotationsCache();
		Score score = lc.getScore();
		for (int iMeasure : new Range(0, score.getMeasuresCount() - 1))
		{
			Column measureColumn = Column.column(score, iMeasure);
			for (Measure measure : measureColumn)
			{
				for (Voice voice : measure.getVoices())
				{
					for (MusicElement element : voice.getElements())
					{
						Notation notation = computeNotation(element, lc);
						if (notation != null)
							ret.set(notation, element);
					}
				}
			}
		}
		return ret;
	}


	/**
	 * Computes the {@link Notation} of the given {@link MusicElement},
	 * using the given musical context and layouter context.
	 * //LAYOUT-PERFORMANCE (needed 2 of 60 seconds)
	 */
	public Notation computeNotation(MusicElement element, ScoreLayouterContext lc)
	{
		//not nice, but we want to stay the logic within this class.
		//multiple dispatch would be needed.
		Notation notation;
		if (element instanceof Chord)
			notation = computeChord((Chord) element, null, lc);
		else if (element instanceof Clef)
			notation = computeClef((Clef) element);
		else if (element instanceof NormalTime)
			notation = computeNormalTime((NormalTime) element, lc.getSymbolPool());
		else if (element instanceof Rest)
			notation = computeRest((Rest) element);
		else if (element instanceof TraditionalKey)
		{
			try
			{
				Score score = lc.getScore();
				MP mp = score.getGlobals().getMP(element);
				notation = computeTraditionalKey((TraditionalKey) element,
					ScoreController.getClef(lc.getScore(), mp, BeatInterval.At));
			}
			catch (IllegalMPException ex)
			{
				Log.log(Log.ERROR, this, ex);
				notation = computeUnsupported(element);
			}
		}
		else if (element instanceof Direction)
		{
			//directions need no notations at the moment
			notation = null;
		}
		else if (element instanceof InstrumentChange)
		{
			//ignore instrument change
			notation = null;
		}
		else
		{
			notation = computeUnsupported(element);
		}
		
		return notation;
	}
	
	
	/**
	 * Computes the {@link Notation} of the given {@link Chord},
	 * using the given {@link StemDirection}, musical context and layouter context.
	 * //LAYOUT-PERFORMANCE (needed 3 of 60 seconds)
	 */
	public ChordNotation computeChord(Chord chord, StemDirection stemDirection,
		ScoreLayouterContext lc)
	{
		Score score = lc.getScore();
		Globals globals = score.getGlobals();
		//get the music context and the parent voice
		MP mp = globals.getMP(chord);
		MusicContext mc = getMusicContext(score, mp, BeforeOrAt);
		//compute the notation
		return computeChord(chord, mc, getInterlineSpace(score, mp),
			score.getScoreFormat().getLyricFont(), stemDirection, globals);
	}


	/**
	 * Computes the layout of a {@link Chord}.
	 * //LAYOUT-PERFORMANCE (needed 6 of 60 seconds)
	 */
	public ChordNotation computeChord(Chord chord, MusicContext mc, float interlineSpace,
		Font lyricsFont, StemDirection stemDirection, Globals globals)
	{
		//stem direction
		if (stemDirection == null)
		{
			stemDirection = stemDirectionStrategy.computeStemDirection(chord, mc);
		}
		//notes alignment
		NotesAlignment notesAlignment = notesAlignmentStrategy.computeNotesAlignment(chord, stemDirection, mc);
		//accidentals alignment
		AccidentalsAlignment accidentalsAlignment = accidentalsAlignmentStrategy.computeAccidentalsAlignment(
			chord, notesAlignment, mc);
		float accidentalsWidth = (accidentalsAlignment != null ? accidentalsAlignment.getWidth() : 0);
		
		//TODO: gap between accidentals and left-suspended note?
		float leftSuspendedWidth = 0; //UNNEEDED, since left-suspended notes are on posx=0 notesAlignment.getLeftSuspendedWidth();
		float frontGap = accidentalsWidth + leftSuspendedWidth;

		//symbol's width: width of the noteheads (without left-suspended) and dots
		float symbolWidth = notesAlignment.getWidth() - leftSuspendedWidth;

		//rear gap: empty duration-dependent space behind the chord
		//minus the symbol's width
		float rearGap = computeWidth(chord.getDuration()) - symbolWidth;
		
		//lyric width
		float lyricWidth = 0;
		Font lyricFont = lyricsFont;
		Vector<Lyric> lyrics = globals.getAttachments().getLyrics(chord);
		for (Lyric lyric : lyrics)
		{
			if (lyric != null && lyric.getText() != null)
			{
				//width of lyric in interline spaces
				float l = new TextMeasurer(lyricFont, lyric.getText()).getWidth() / interlineSpace;
				//for start and end syllable, request "-" more space, for middle syllables "--"
				//TODO: unsymmetric - start needs space on the right, end on the left, ...
				SyllableType lyricType = lyric.getSyllableType();
				if (lyricType == SyllableType.Begin || lyricType == SyllableType.End)
				{
					l += new TextMeasurer(lyricFont, "-").getWidth() / interlineSpace;
				}
				else if (lyricType == SyllableType.Middle)
				{
					l += new TextMeasurer(lyricFont, "--").getWidth() / interlineSpace;
				}
				//save with of the widest lyric
				lyricWidth = Math.max(lyricWidth, l);
			}
		}

		//compute length of the stem (if any)
		StemAlignment stemAlignment = stemAlignmentStrategy.computeStemAlignment(chord.getStem(), notesAlignment,
			stemDirection, 5); //TODO mc.getLines());
		
		//compute articulations
		ArticulationsAlignment articulationsAlignment =
			articulationsAlignmentStrategy.computeArticulationsAlignment(chord, stemDirection,
				notesAlignment, 5); //TODO mc.getLines());
		
		return new ChordNotation(chord, new ElementWidth(frontGap, symbolWidth, rearGap, lyricWidth), notesAlignment,
			stemDirection, stemAlignment, accidentalsAlignment, articulationsAlignment);
	}


	/**
	* Computes the layout of a {@link Clef}, which is not in a leading spacing.
	* These clefs are always drawn smaller.
	*/
	public ClefNotation computeClef(Clef clef)
	{
		//TODO: width from XML
		return new ClefNotation(clef, new ElementWidth(0, clefWidthIS * smallClefScaling, 0),
			clef.getType().getLine(), smallClefScaling);
	}


	/**
	* Computes the layout of a {@link NormalTime}.
	*/
	public NormalTimeNotation computeNormalTime(NormalTime time, SymbolPool symbolPool)
	{
		//front and rear gap: 1 space
		float gap = 1f;
		//gap between digits: 0.1 space
		float digitGap = 0.1f;
		//the numbers of a normal time signature are centered.
		//the width of the time signature is the width of the wider number
		float numeratorWidth = 2;
		float denominatorWidth = 2;
		if (symbolPool != null)
		{
			numeratorWidth = symbolPool.computeNumberWidth(time.getNumerator(), digitGap);
			denominatorWidth = symbolPool.computeNumberWidth(time.getDenominator(), digitGap);
		}
		float width = Math.max(numeratorWidth, denominatorWidth);
		//create element layout
		float numeratorOffset = (width - numeratorWidth) / 2;
		float denominatorOffset = (width - denominatorWidth) / 2;
		return new NormalTimeNotation(time, new ElementWidth(gap, width, gap),
			numeratorOffset, denominatorOffset, digitGap);
	}


	/**
	* Computes the layout of a {@link Rest}.
	*/
	public RestNotation computeRest(Rest rest)
	{
		float width = computeWidth(rest.getDuration());
		return new RestNotation(rest, new ElementWidth(width));
	}


	/**
	* Computes the layout of a {@link TraditionalKey}.
	*/
	public TraditionalKeyNotation computeTraditionalKey(TraditionalKey key, Clef contextClef)
	{
		float width = 0;
		int fifth = key.getFifth();
		if (fifth > 0)
		{
			width = fifth * distanceSharps;
		}
		else
		{
			width = -fifth * distanceFlats;
		}
		return new TraditionalKeyNotation(key, new ElementWidth(0, width, 1),
			contextClef.getType().computeLinePosition(pi(0, 0, 4)),
			contextClef.getType().getKeySignatureLowestLine(fifth));
	}


	/**
	* Computes the layout of an unknown {@link MusicElement}.
	*/
	public Notation computeUnsupported(MusicElement element)
	{
		throw new IllegalArgumentException("Unsupported MusicElement of type "
			+ element.getClass());
	}


	/**
	* Computes and returns the layout that fits to the given
	* duration.
	*/
	public float computeWidth(Fraction duration)
	{
		if (duration == null)
		{
			throw new IllegalArgumentException("duration may not be null");
		}
		//look, if the duration is defined
		for (int i = 0; i < widths.length; i++)
		{
			if (widths[i].durationNum == duration.getNumerator()
				&& widths[i].durationDen == duration.getDenominator())
				//found! return it.
				return widths[i].width;
		}
		//not found. find the greatest lesser duration and the lowest
		//greater duration and interpolate linearly.
		float thisDur = ((float) duration.getNumerator()) / duration.getDenominator();
		float lowerDur = 0;
		float lowerWidth = 0;
		float higherDur = Float.MAX_VALUE;
		float higherWidth = 0;
		for (int i = 0; i < widths.length; i++)
		{
			float dur = ((float) widths[i].durationNum) / widths[i].durationDen;
			if (dur <= thisDur && dur > lowerDur)
			{
				lowerDur = dur;
				lowerWidth = widths[i].width;
			}
			if (dur >= thisDur && dur < higherDur)
			{
				higherDur = dur;
				higherWidth = widths[i].width;
			}
		}
		if (lowerDur == 0)
		{
			return higherWidth;
		}
		else
		{
			return (lowerWidth + higherWidth) * thisDur / (lowerDur + higherDur);
		}
	}


}
