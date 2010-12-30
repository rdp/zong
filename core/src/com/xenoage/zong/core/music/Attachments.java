package com.xenoage.zong.core.music;

import static com.xenoage.util.NullTools.notNull;

import com.xenoage.pdlib.PMap;
import com.xenoage.pdlib.PSet;
import com.xenoage.pdlib.PVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.text.Lyric;


/**
 * Set of {@link Attachable} elements attached to other music elements.
 * A single instance of this class should be used for the whole score.
 * 
 * Given a music element, its attachments can be queried in hash lookup time.
 * Given an attachment, its anchor element can also be queried in hash
 * lookup time.
 * 
 * @author Andreas Wenger
 */
public final class Attachments
{

	private final PMap<MusicElement, PSet<Attachable>> lookup;
	private final PMap<Attachable, MusicElement> reverseLookup;
	
	private static final Attachments empty =
		new Attachments(new PMap<MusicElement, PSet<Attachable>>(),
			new PMap<Attachable, MusicElement>());
	private static final PSet<Attachable> emptySet = new PSet<Attachable>();
	
	
	private Attachments(PMap<MusicElement, PSet<Attachable>> lookup,
		PMap<Attachable, MusicElement> reverseLookup)
	{
		this.lookup = lookup;
		this.reverseLookup = reverseLookup;
	}
	
	
	/**
	 * Gets an empty {@link Attachments}.
	 */
	public static Attachments empty()
	{
		return empty;
	}
	
	
	/**
	 * Gets all attachments of the given anchor element.
	 * If there are none, an empty set is returned.
	 */
	public PSet<Attachable> get(MusicElement anchor)
	{
		return notNull(lookup.get(anchor), emptySet);
	}
	
	
	/**
	 * Gets all anchor element of the given attachment.
	 * If there are none, null is returned.
	 */
	public MusicElement getAnchor(Attachable attachment)
	{
		return reverseLookup.get(attachment);
	}
	
	
	
	/**
	 * Adds a new attachment.
	 */
	public Attachments plus(MusicElement anchor, Attachable attachment)
	{
		PMap<MusicElement, PSet<Attachable>> lookup = this.lookup;
		PMap<Attachable, MusicElement> reverseLookup = this.reverseLookup;
		//forward link
		PSet<Attachable> lookupSet = lookup.get(anchor);
		if (lookupSet == null)
			lookupSet = new PSet<Attachable>();
		lookupSet = lookupSet.plus(attachment);
		lookup = lookup.plus(anchor, lookupSet);
		//backward link
		reverseLookup = reverseLookup.plus(attachment, anchor);
		return new Attachments(lookup, reverseLookup);
	}
	
	
	/**
	 * Removes an attachment.
	 */
	public Attachments minusAttachment(Attachable attachment)
	{
		PMap<MusicElement, PSet<Attachable>> lookup = this.lookup;
		PMap<Attachable, MusicElement> reverseLookup = this.reverseLookup;
		MusicElement anchor = reverseLookup.get(attachment);
		if (anchor == null)
		{
			//unused element
			return this;
		}
		//forward link
		PSet<Attachable> lookupSet = lookup.get(anchor);
		if (lookupSet.size() == 1)
		{
			//empty list of attachments. can be removed
			lookup = lookup.minus(anchor);
		}
		else
		{
			lookupSet = lookupSet.minus(attachment);
			lookup = lookup.plus(anchor, lookupSet);
		}
		//backward link
		reverseLookup = reverseLookup.minus(attachment);
		return new Attachments(lookup, reverseLookup);
	}
	
	
	/**
	 * Removes an anchor element.
	 */
	public Attachments minusAnchor(MusicElement anchor)
	{
		PMap<MusicElement, PSet<Attachable>> lookup = this.lookup;
		PMap<Attachable, MusicElement> reverseLookup = this.reverseLookup;
		//backward links
		PSet<Attachable> lookupSet = lookup.get(anchor);
		if (lookupSet == null)
		{
			//unused element
			return this;
		}
		for (Attachable attachment : lookupSet)
		{
			reverseLookup = reverseLookup.minus(attachment);
		}
		//forward link
		lookup = lookup.minus(anchor);
		return new Attachments(lookup, reverseLookup);
	}
	
	
	/**
	 * Adds the attachments (if any) of the given old chord to the
	 * given new chord.
	 */
	public Attachments replaceChord(Chord oldChord, Chord newChord)
	{
		PSet<Attachable> attachments = get(oldChord);
		if (attachments != null)
		{
			Attachments ret = minusAnchor(oldChord);
			for (Attachable attachment : attachments)
			{
				ret = ret.plus(newChord, attachment);
			}
			return ret;
		}
		else
		{
			return this;
		}
	}
	
	
	/**
	 * Gets the {@link Lyric}s of the given element as a vector:
	 * First verse at index 0, second verse at index 1, and so on.
	 * If there are no lyrics, an empty vector is returned.
	 */
	public Vector<Lyric> getLyrics(MusicElement anchor)
	{
		PVector<Lyric> ret = new PVector<Lyric>();
		for (Attachable attachment : get(anchor))
		{
			if (attachment instanceof Lyric)
			{
				Lyric lyric = (Lyric) attachment;
				int verse = lyric.getVerse();
				while (ret.size() < verse + 1)
				{
					ret = ret.plus(null);
				}
				ret = ret.with(verse, lyric);
			}
		}
		return ret;
	}
	
}
