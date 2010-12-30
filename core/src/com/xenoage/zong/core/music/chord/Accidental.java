package com.xenoage.zong.core.music.chord;


/**
 * Class for explicitly notated accidentals.
 *
 * @author Andreas Wenger
 */
public final class Accidental
{

  //compare musicxml 2.0: note.mod, l. 216
	//sharp-sharp, flat-flat, natural-sharp, natural-flat, quarter-flat, quarter-sharp,
	//three-quarters-flat, and three-quarters-sharp are not implemented yet.
  public enum Type
  {
    DoubleSharp, Sharp, Natural, Flat, DoubleFlat; 
    
    public static Type getType(int alter)
    {
      switch (alter)
      {
        case -2: return DoubleFlat;
        case -1: return Flat;
        case +1: return Sharp;
        case +2: return DoubleSharp;
        default: return Natural;
      }
    }
    
  }
  private final Type type;
  
  
  public Accidental(Type type)
  {
    this.type = type;
  }
  
  
  public Type getType()
  {
    return type;
  }
  
  
}
