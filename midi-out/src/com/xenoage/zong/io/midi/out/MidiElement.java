package com.xenoage.zong.io.midi.out;

import javax.sound.midi.MidiMessage;

import com.xenoage.util.math.Fraction;


/**
 * This class is used as a storage to save the mididata and its position in the Score.
 * 
 * @author Uli Teschemacher
 *
 */
public final class MidiElement
{

	private final int measure;
	private final Fraction position;
	private final MidiMessage midiMessage;


	public MidiElement(int measure, Fraction position, MidiMessage midiMessage)
	{
		super();
		this.measure = measure;
		this.position = position;
		this.midiMessage = midiMessage;
	}


	/**
	 * Gets the number of the measure
	 * @return
	 */
	public int getMeasure()
	{
		return measure;
	}


	/**
	 * Gets the fraction in the measure, where the element is placed.
	 * @return
	 */
	public Fraction getPosition()
	{
		return position;
	}


	/**
	 * Gets the Midimessage of the element
	 * @return
	 */
	public MidiMessage getMidiMessage()
	{
		return midiMessage;
	}


}
