package com.xenoage.zong.data.music.clef;

import com.xenoage.zong.data.music.NoVoiceElement;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Class for a clef.
 * 
 * @author Andreas Wenger
 */
public final class Clef
	extends NoVoiceElement
{

	private final ClefType type;


	/**
	 * Creates a new clef.
	 * @param typeID the type of the clef
	 */
	public Clef(ClefType type)
	{
		this.type = type;
	}


	/**
	 * Returns this instance (since it is immutable).
	 */
	@Override public Clef deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}


	public ClefType getType()
	{
		return type;
	}


	/**
	 * Computes and returns the line position of the given pitch, that is the
	 * vertical offset of the note in half-spaces from the bottom line at a
	 * 5-lines-staff. Some examples:
	 * <ul>
	 * <li>0: note is on the bottom line</li>
	 * <li>-2: note is on the first lower leger line</li>
	 * <li>5: note is between the 3rd and 4th line from below</li>
	 * </ul>
	 */
	public int computeLinePosition(Pitch pitch)
	{
		int ret = pitch.getStep() + 7 * pitch.getOctave();
		ret -= type.getPitch().getStep() + 7 * type.getPitch().getOctave();
		ret += type.getLine();
		ret -= type.getOctaveChange() * 7;
		return ret;
	}


	/**
	 * Gets the lowest line that may be used for a key signature.
	 */
	public int getKeySignatureLowestLine(int fifth)
	{
		if (fifth < 0)
		{
			return type.getMinFlatLine();
		}
		else
		{
			return type.getMinSharpLine();
		}
	}


}
