package com.xenoage.zong.core.music.chord;

import static com.xenoage.pdlib.IVector.ivec;
import static com.xenoage.util.Range.range;

import java.util.ArrayList;

import com.xenoage.pdlib.Vector;
import com.xenoage.util.NullTools;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.Globals;
import com.xenoage.zong.core.music.Pitch;
import com.xenoage.zong.core.music.VoiceElement;


/**
 * Class for a chord.
 * 
 * To make things easy, every note is in a chord.
 * Thus also single notes are chord elements by definition.
 * 
 * A chord can have a stem and articulations.
 * It can also be part of a tuplet. Currently, tuplets can not be nested.
 * 
 * A chord can be part of a tuplet and can be attached to a beam and one or
 * more lyrics, directions and slurs, but this is stored in the
 * {@link Globals} class.
 *
 * @author Andreas Wenger
 */
public final class Chord
  implements VoiceElement
{
	
	//the notes within this chord, sorted ascending (begin with lowest pitch)
  private final Vector<Note> notes;
  
	//the duration
  private final Fraction duration;
  
  //stem, or null for default stem
	private final Stem stem;
  
  //the accidentals within this chord, sorted by ascending distance to the chord
  private final Vector<Articulation> articulations;
  
  
	/**
   * Creates a chord with the given notes, duration, stem (or null) and articulations (or null).
   * The pitches must be sorted ascending (begin with the lowest pitch,
   * end with the highest pitch), otherwise an {@link IllegalArgumentException} is thrown.
   */
  public Chord(Vector<Note> notes, Fraction duration, Stem stem, Vector<Articulation> articulations)
  {
  	NullTools.throwNullArg(notes, duration);
  	checkNotesOrder(notes);
    this.notes = notes;
    this.duration = duration;
    this.stem = stem;
    this.articulations = articulations;
  }
  
  
  /**
   * Creates a chord with the given note and duration.
   */
  public static Chord createMinimal(Note note, Fraction duration)
  {
  	return new Chord(ivec(note), duration, null, null);
  }


  /**
   * Gets the notes in ascending order. Never null.
   */
  public Vector<Note> getNotes()
  {
    return notes;
  }
  
  
  /**
   * Gets the duration of this chord. Never null.
   */
  @Override public Fraction getDuration()
  {
    return duration;
  }
  
  
  /**
   * Gets the stem of this chord, or null if a default stem is used.
   */
  public Stem getStem()
  {
  	return stem;
  }
  
  
  /**
   * Sets the stem of this chord, or null if a default stem is used.
   */
  public Chord withStem(Stem stem)
  {
  	return new Chord(notes, duration, stem, articulations);
  }
  
  
  /**
   * Gets the articulations of this chord, which may also be null
   * if there are none.
   */
  public Vector<Articulation> getArticulations()
  {
    return articulations;
  }
  
  
  /**
   * Collects and returns all pitches of this chord.
   * Pitches that are used more times are also used more
   * times in the list.
   */
  public Vector<Pitch> getPitches()
  {
    Pitch[] ret = new Pitch[notes.size()];
    for (int i : range(notes))
    {
      ret[i] = notes.get(i).getPitch();
    }
    return ivec(ret);
  }
  
  
  /**
   * Adds a pitch this chord.
   */
  public Chord plusPitch(Pitch pitch)
  {
  	return plusNote(new Note(pitch));
  }
  
  
  /**
   * Adds a note this chord.
   */
  public Chord plusNote(Note note)
  {
  	ArrayList<Note> notes = new ArrayList<Note>(this.notes);
  	//insert at right position
  	int i = 0;
  	for (; i < notes.size(); i++)
  	{
  		if (notes.get(i).getPitch().compareTo(note.getPitch()) < 0)
  			break;
  	}
  	notes.add(i, note);
  	return new Chord(ivec(notes), duration, stem, articulations);
  }

	
	private void checkNotesOrder(Vector<Note> notes)
	{
  	Pitch currentPitch = null;
  	Pitch lastPitch = notes.get(0).getPitch();
    for (int i : range(1, notes.size() - 1))
    {
    	currentPitch = notes.get(i).getPitch();
      //pitches must be sorted ascending
      if (currentPitch.compareTo(lastPitch) < 0)
        throw new IllegalArgumentException("Pitches must be sorted ascending!");
      lastPitch = currentPitch;
    }
	}
	
	
	/**
   * Returns this element but with the given duration.
   */
  public Chord withDuration(Fraction duration)
  {
  	return new Chord(notes, duration, stem, articulations);
  }
  
  
  @Override public String toString()
  {
  	return "Chord (first note: " + notes.get(0).toString() + ")";
  }
  

}
