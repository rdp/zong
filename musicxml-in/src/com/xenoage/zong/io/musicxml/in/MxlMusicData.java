package com.xenoage.zong.io.musicxml.in;

import static com.xenoage.util.iterators.It.it;
import static com.xenoage.util.lang.Tuple2.t;

import java.util.LinkedList;

import com.xenoage.util.iterators.It;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.exceptions.InvalidFormatException;

import proxymusic.Attributes;
import proxymusic.Backup;
import proxymusic.Barline;
import proxymusic.Bookmark;
import proxymusic.Direction;
import proxymusic.FiguredBass;
import proxymusic.Forward;
import proxymusic.Grouping;
import proxymusic.Harmony;
import proxymusic.Link;
import proxymusic.Note;
import proxymusic.Print;
import proxymusic.Sound;
import proxymusic.ScorePartwise.Part.Measure;


/**
 * This helper class reads the contents of a measure-element
 * and provides an iterator to run over them.
 * 
 * The enum <code>Type</code> allows to use a switch
 * statement instead of many <code>instanceof</code> calls.
 * 
 * All notes are packed together into <code>Chord</code> items,
 * so no lookahead to find notes belonging to a chord is
 * needed when using this class. Chords are stored as a
 * <code>List</code> of <code>Note</code> objects.
 * 
 * @author Andreas Wenger
 */
class MxlMusicData
{
	
	public enum Type
	{
		Chord, //group of note elements
		Backup,
		Forward,
		Direction,
		Attributes,
		Harmony,		
		FiguredBass,
		Print,
		Sound,
		Barline,
		Grouping,
		Link,
		Bookmark;
	}
	
	
	private final LinkedList<Tuple2<Type, ? extends Object>> items;
	
	
	public MxlMusicData(Measure mxlMeasure)
		throws InvalidFormatException
	{
		items = new LinkedList<Tuple2<Type, ? extends Object>>();
		LinkedList<Note> openChord = null;
    for(Object mxlElement : mxlMeasure.getNoteOrBackupOrForward())
    {
    	//chord note?
    	if (mxlElement instanceof Note && ((Note) mxlElement).getChord() != null)
    	{
    		//add this note to the current open chord
    		if (openChord == null)
    		{
    			throw new InvalidFormatException("chord element found with no open chord");
    		}
    		openChord.add((Note) mxlElement);
    	}
    	else
    	{
    		//other element than a chord-note. if chord was open, close it
    		if (openChord != null)
    		{
    			items.add(t(Type.Chord, openChord));
    			openChord = null;
    		}
    		//find out the type of the item
    		//ugly code, but hidden to the user of this class...
    		Type type = null;
    		if (mxlElement instanceof Note)
    		{
    			//new chord
    			openChord = new LinkedList<Note>();
    			openChord.add((Note) mxlElement);
    		}
    		else if (mxlElement instanceof Backup)
    		{
    			type = Type.Backup;
    		}
    		else if (mxlElement instanceof Forward)
    		{
    			type = Type.Forward;
    		}
    		else if (mxlElement instanceof Direction)
    		{
    			type = Type.Direction;
    		}
    		else if (mxlElement instanceof Attributes)
    		{
    			type = Type.Attributes;
    		}
    		else if (mxlElement instanceof Harmony)
    		{
    			type = Type.Harmony;
    		}
    		else if (mxlElement instanceof FiguredBass)
    		{
    			type = Type.FiguredBass;
    		}
    		else if (mxlElement instanceof Print)
    		{
    			type = Type.Print;
    		}
    		else if (mxlElement instanceof Sound)
    		{
    			type = Type.Sound;
    		}
    		else if (mxlElement instanceof Barline)
    		{
    			type = Type.Barline;
    		}
    		else if (mxlElement instanceof Grouping)
    		{
    			type = Type.Grouping;
    		}
    		else if (mxlElement instanceof Link)
    		{
    			type = Type.Link;
    		}
    		else if (mxlElement instanceof Bookmark)
    		{
    			type = Type.Bookmark;
    		}
    		else
    		{
    			throw new InvalidFormatException(
      			"unknown music-data element: " + mxlElement.toString());
    		}
    		if (type != null)
    		{
    			items.add(new Tuple2<Type, Object>(type, mxlElement));
    		}
    	}
    }
    //if chord is open, close it
		if (openChord != null)
		{
			items.add(t(Type.Chord, openChord));
			openChord = null;
		}
	}
	
	
	/**
	 * Gets an iterator over the list of items.
	 * Each item has the corresponding proxymusic type, except <code>Chord</code>
	 * which returns a {@link List} of {@link Note} objects.
	 */
	public It<Tuple2<Type, ? extends Object>> getItems()
	{
		return it(items);
	}
	

}
