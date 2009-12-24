package com.xenoage.zong.data.music;

import com.xenoage.util.math.Fraction;


/**
 * Musical data of a {@link Chord} element.
 * {@link ChordData} instances are not bound to a certain
 * voice and beam but can exist unattachedly.
 * 
 * TODO: TIDY notes, pitches
 *
 * @author Andreas Wenger
 */
public final class ChordData
{
	
	//the duration
  private final Fraction duration;
  
  //the notes within this chord, sorted ascending (begin with lowest pitch)
  private final Note[] notes;
  
  //the list of dots
  //private final Dot[] dots; //TODO: ?
  
  //the accidentals within this chord, sorted by ascending distance to the chord
  private final Articulation[] articulations;
  
  
  /**
   * Creates a {@link ChordData} with a single note.
   */
  public ChordData(Note note, Fraction duration)
  {
  	this(new Note[]{note}, duration);
  }
  
  
  /**
   * Creates a chord with the given notes and articulations (or null).
   * The pitches must be sorted ascending (begin with the lowest pitch,
   * end with the highest pitch), otherwise an {@link IllegalArgumentException}
   * is thrown.
   */
  public ChordData(Note[] notes, Fraction duration, Articulation[] articulations)
    throws IllegalArgumentException
  {
  	this.duration = duration;
  	this.notes = notes;
  	this.articulations = articulations;
  	Pitch currentPitch = null;
  	Pitch lastPitch = notes[0].getPitch();
    for (int i = 1; i < notes.length; i++)
    {
    	currentPitch = notes[i].getPitch();
      //pitches must be sorted ascending
      if (currentPitch.compareTo(lastPitch) < 0)
        throw new IllegalArgumentException("Pitches must be sorted ascending!");
      lastPitch = currentPitch;
    }
  }
  
  
  public ChordData(Note[] notes, Fraction duration)
    throws IllegalArgumentException
  {
  	this(notes, duration, null);
  }
  
  
  public Fraction getDuration()
  {
    return duration;
  }
  
  
  public Note[] getNotes()
  {
    return notes;
  }
  
  
  public Articulation[] getArticulations()
  {
    return articulations;
  }
  
  
  /**
   * Collects and returns all pitches of this chord.
   * Pitches that are used more times are also used more
   * times in the list.
   */
  public Pitch[] getPitches()
  {
    Pitch[] ret = new Pitch[notes.length];
    int i = 0;
    for (Note note : notes)
    {
      ret[i] = note.getPitch();
      i++;
    }
    return ret;
  }
  
  
  /**
   * Adds the given note to this chord and returns the result.
   */
  public ChordData addNote(Note note)
  {
  	Note[] newNotes = new Note[notes.length + 1];
  	int i = 0;
  	while (i < notes.length && note.getPitch().compareTo(notes[i].getPitch()) > 0)
		{
  		newNotes[i] = notes[i];
  		i++;
		}
  	newNotes[i] = note;
  	i++;
  	while (i < newNotes.length)
  	{
  		newNotes[i] = notes[i-1];
  		i++;
  	}
    return new ChordData(newNotes, duration);
  }
  
  
  /**
   * Gets the index of the given note. If unknown, an
   * {@link IllegalArgumentException} is thrown.
   */
  public int getNoteIndex(Note note)
  {
  	for (int i = 0; i < notes.length; i++)
  	{
  		if (notes[i] == note)
  			return i;
  	}
  	throw new IllegalArgumentException("unknown note");
  }
  

}
