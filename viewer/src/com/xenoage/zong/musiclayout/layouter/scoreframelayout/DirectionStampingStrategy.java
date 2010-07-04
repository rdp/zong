package com.xenoage.zong.musiclayout.layouter.scoreframelayout;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.core.music.format.SP.sp;

import java.util.EnumSet;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.font.FontInfo;
import com.xenoage.util.font.FontStyle;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.SymbolPool;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.core.music.Attachable;
import com.xenoage.zong.core.music.Globals;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.direction.Crescendo;
import com.xenoage.zong.core.music.direction.Diminuendo;
import com.xenoage.zong.core.music.direction.Direction;
import com.xenoage.zong.core.music.direction.Dynamics;
import com.xenoage.zong.core.music.direction.Pedal;
import com.xenoage.zong.core.music.direction.Tempo;
import com.xenoage.zong.core.music.direction.Wedge;
import com.xenoage.zong.core.music.direction.Words;
import com.xenoage.zong.core.music.format.Position;
import com.xenoage.zong.core.music.format.SP;
import com.xenoage.zong.core.text.Alignment;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.data.text.FormattedTextParagraph;
import com.xenoage.zong.data.text.FormattedTextStyle;
import com.xenoage.zong.data.text.FormattedTextSymbol;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.util.ChordStampings;
import com.xenoage.zong.musiclayout.stampings.PedalStamping;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.StaffSymbolStamping;
import com.xenoage.zong.musiclayout.stampings.StaffTextStamping;
import com.xenoage.zong.musiclayout.stampings.WedgeStamping;


/**
 * This strategy computes the text stampings of {@link Direction}s.
 * 
 * @author Andreas Wenger
 */
public class DirectionStampingStrategy
	implements ScoreLayouterStrategy
{
	
	
	private static final float FONT_SIZE = 12; //TODO
	
	
	/**
	 * Creates the {@link StaffTextStamping}s for the {@link Direction}s of
	 * the given {@link Chord} and its {@link ChordStampings}.
	 */
	public PVector<StaffTextStamping> createForChord(Chord chord, ChordStampings chordStampings,
		Globals globals)
	{
		PVector<StaffTextStamping> ret = pvec();
		for (Attachable attachable : globals.getAttachments().get(chord))
		{
			if (attachable instanceof Dynamics)
			{
				ret = ret.plus(createDynamics((Dynamics) attachable, chord, chordStampings));
			}
		}
		return ret;
	}
	
	
	/**
	 * Creates a {@link StaffTextStamping} for the given {@link Dynamics}s
	 * below the given {@link Chord} and its {@link ChordStampings}.
	 */
	public StaffTextStamping createDynamics(Dynamics dynamics,
		Chord chord, ChordStampings chordStampings)
	{
		//TODO: support dynamics.getPosition()
		//3 IS below the base line, or 2 IS below the lowest note
		StaffStamping staff = chordStampings.staffStamping;
		float lp = -3f * 2;
		if (chordStampings.noteheads.size() > 0)
		{
			lp = Math.min(lp, chordStampings.noteheads.getFirst().getPosition().yLp - 2 * 2);
		}
		//create text
		SymbolPool symbolPool = App.getInstance().getSymbolPool();
		FormattedTextParagraph paragraph = new FormattedTextParagraph(Alignment.Center);
		for (CommonSymbol s : CommonSymbol.getDynamics(dynamics.getType()))
		{
			Symbol symbol = symbolPool.getSymbol(s);
			paragraph.addElement(new FormattedTextSymbol(symbol, FONT_SIZE));
		}
		FormattedText text = new FormattedText(paragraph);
		//create stamping
		return new StaffTextStamping(staff, chord, text, sp(chordStampings.positionX, lp));
	}
	
	
	/**
	 * Creates a {@link StaffTextStamping} for the given {@link Dynamics}s
	 * at the given {@link MP} within the given {@link StaffStamping}.
	 */
	public StaffTextStamping createDynamics(Dynamics dynamics,
		MP mp, StaffStamping staffStamping)
	{
		//TODO: support dynamics.getPosition()
		//horizontal position
		float x = notNull(staffStamping.getXMmAt(mp), 0f) + staffStamping.getPosition().x;
		//vertical position: 4 IS below the bottom staff line
		float lp = -4 * 2;
		//create text
		SymbolPool symbolPool = App.getInstance().getSymbolPool();
		FormattedTextParagraph paragraph = new FormattedTextParagraph(Alignment.Center);
		for (CommonSymbol s : CommonSymbol.getDynamics(dynamics.getType()))
		{
			Symbol symbol = symbolPool.getSymbol(s);
			paragraph.addElement(new FormattedTextSymbol(symbol, FONT_SIZE));
		}
		FormattedText text = new FormattedText(paragraph);
		//create stamping
		return new StaffTextStamping(staffStamping, null, text, sp(x, lp));
	}
	
	
	/**
	 * Creates a {@link StaffTextStamping} for the given {@link Tempo}
	 * at the given {@link MP} within the given {@link StaffStamping}.
	 */
	public StaffTextStamping createTempo(Tempo tempo, MP mp,
		StaffStamping staffStamping)
	{
		SP p = computePosition(tempo, mp, staffStamping);
		
		//create text
		FormattedTextParagraph paragraph = new FormattedTextParagraph(Alignment.Left);
		FormattedTextStyle style = new FormattedTextStyle(
			new FontInfo("Times New Roman", FONT_SIZE, EnumSet.of(FontStyle.Bold))); //TODO
		if (tempo.getText() != null)
		{
			//use custom text
			paragraph.addElement(tempo.getText(), style);
		}
		else
		{
			//show meaning, e.g. "♩ = 120"
			SymbolPool symbolPool = App.getInstance().getSymbolPool();
			Fraction beat = tempo.getBaseBeat();
			if (beat.equals(fr(1, 4)))
			{
				paragraph.addElement(new FormattedTextSymbol(symbolPool.getSymbol(CommonSymbol.TextNoteQuarter), FONT_SIZE));
			}
			else if (beat.equals(fr(1, 2)))
			{
				paragraph.addElement(new FormattedTextSymbol(symbolPool.getSymbol(CommonSymbol.TextNoteHalf), FONT_SIZE));
			}
			else
			{
				paragraph.addElement(beat.toString(), style);
			}
			paragraph.addElement(" = " + tempo.getBeatsPerMinute(), style);
		}
		FormattedText text = new FormattedText(paragraph);
		
		//create stamping
		return new StaffTextStamping(staffStamping, null, text, p);
	}
	
	
	/**
	 * Creates a {@link StaffTextStamping} for the given {@link Words}
	 * at the given {@link MP} within the given {@link StaffStamping}.
	 */
	public StaffTextStamping createWords(Words words, MP mp,
		StaffStamping staffStamping)
	{
		SP p = computePosition(words, mp, staffStamping);
		
		//create text
		FormattedTextParagraph paragraph = new FormattedTextParagraph(Alignment.Left);
		FormattedTextStyle style = new FormattedTextStyle(words.getFontInfo());
		paragraph.addElement(words.getText(), style);
		FormattedText text = new FormattedText(paragraph);
		
		//create stamping
		return new StaffTextStamping(staffStamping, null, text, p);
	}
	
	
	/**
	 * Creates a {@link StaffSymbolStamping} for the given {@link Pedal}
	 * at the given {@link MP} within the given {@link StaffStamping}.
	 */
	public StaffSymbolStamping createPedal(Pedal pedal, MP mp,
		StaffStamping staffStamping)
	{
		SP p = computePosition(pedal, mp, staffStamping);
		
		//create stamping
		return new PedalStamping(pedal, staffStamping, p, 1);
	}
	
	
	/**
	 * Creates a {@link WedgeStamping} for the given {@link Wedge} on the given staff.
	 * The start and end measure of the wedge may be outside the staff, then the
	 * wedge is clipped to the staff.
	 */
	public WedgeStamping createWedgeStamping(Wedge wedge, StaffStamping staffStamping,
		Globals globals)
	{
		//musical positions of wedge
		MusicElement anchorStart = globals.getAttachments().getAnchor(wedge);
		MP p1 = globals.getMP(anchorStart);
		MusicElement anchorStop = globals.getAttachments().getAnchor(wedge.getWedgeEnd());
		MP p2 = globals.getMP(anchorStop);
		//clip start to staff
		float x1Mm;
		if (p1.getMeasure() < staffStamping.getStartMeasureIndex())
		{
			//begins before staff
			x1Mm = staffStamping.getMeasureLeadingMm(staffStamping.getStartMeasureIndex());
		}
		else
		{
			//begins within staff
			x1Mm = staffStamping.getXMmAt(p1);
		}
		//clip end to staff
		float x2Mm;
		if (p2.getMeasure() > staffStamping.getEndMeasureIndex())
		{
			//ends after staff
			x2Mm = staffStamping.getMeasureEndMm(staffStamping.getEndMeasureIndex());
		}
		else
		{
			//ends within staff
			x2Mm = staffStamping.getXMmAt(p2);
		}
		//spread
		float d1Is = 0;
		float d2Is = 0;
		float defaultSpreadIS = 1.5f;
		if (wedge instanceof Crescendo)
		{
			d2Is = (wedge.getSpread() != null ? wedge.getSpread() : defaultSpreadIS);
		}
		else if (wedge instanceof Diminuendo)
		{
			d1Is = (wedge.getSpread() != null ? wedge.getSpread() : defaultSpreadIS);
		}
		
		//custom horizontal position
		Position customPos = wedge.getPosition();
		float length = x2Mm - x1Mm;
		if (customPos != null && customPos.x != null)
			x1Mm = customPos.x;
		x1Mm += Position.getRelativeX(customPos);
		x2Mm = x1Mm + length;
		
		//vertical position
		float lp;
		if (customPos != null && customPos.y != null)
			lp = customPos.y;
		else
			lp = -6;
		lp += Position.getRelativeY(customPos);
		
		return new WedgeStamping(wedge, lp, x1Mm, x2Mm, d1Is, d2Is, staffStamping);
	}
	
	
	/**
	 * Computes the position for the given {@link Direction}
	 * at the given {@link MP} within the given {@link StaffStamping}.
	 */
	private SP computePosition(Direction direction, MP mp,
		StaffStamping staffStamping)
	{
		Position customPos = direction.getPosition();
		
		//horizontal position
		float x;
		if (customPos != null && customPos.x != null)
			x = customPos.x;
		else
			x = notNull(staffStamping.getXMmAt(mp), 0f) + staffStamping.getPosition().x;
		x += Position.getRelativeX(customPos);
		
		//vertical position: 2 IS above the top staff line
		float lp;
		if (customPos != null && customPos.y != null)
			lp = customPos.y;
		else
			lp = (staffStamping.getLinesCount() - 1) * 2 + 2 * 2;
		lp += Position.getRelativeY(customPos);
		
		return sp(x, lp);
	}
	

}
