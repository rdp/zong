/**
 * 
 */
package com.xenoage.zong.io.midi.out;

import java.util.ArrayList;
import java.util.List;

import com.xenoage.util.MathTools;
import com.xenoage.util.RAList;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.settings.Settings;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.Direction;
import com.xenoage.zong.data.music.DurationElement;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.MusicElement;
import com.xenoage.zong.data.music.Staff;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.directions.Dynamics;
import com.xenoage.zong.data.music.directions.DynamicsType;


/**
 * This class is responsable for the converting of the Dynamics in a MidiScore
 * 
 * @author Uli Teschemacher
 * 
 */
public final class MidiVelocityConverter
{

	private static final int defaultvalue = 90;


	public static int getNumberOfVoicesInStaff(Staff staff)
	{
		int voiceCount = 0;
		ArrayList<Measure> measures = staff.getMeasures();
		for (Measure measure : measures)
		{
			int size = measure.getVoices().size();
			voiceCount = MathTools.clampMin(voiceCount, size);
		}
		return voiceCount;
	}


	public static int getVelocity(Chord chord, int currentVelocity)
	{
		List<Direction> directions = chord.getDirections();
		if (directions != null)
		{
			for (Direction direction : directions)
			{
				if (direction instanceof Dynamics)
				{
					Dynamics dynamic = (Dynamics) direction;
					int setting = Settings.getInstance().getSetting(
						dynamic.getType().name(), "playback-dynamics", currentVelocity);
					return convertToMidiVelocity(setting);
				}
			}
		}
		return currentVelocity;
	}


	private static boolean voiceHasDynamics(Staff staff, int voiceNumber)
	{
		ArrayList<Measure> measures = staff.getMeasures();
		for (Measure measure : measures)
		{
			if (measure.getVoices().size() > voiceNumber)
			{
				RAList<MusicElement> elements = measure.getVoices().get(voiceNumber)
					.getElements();
				for (MusicElement musicElement : elements)
				{
					if (musicElement instanceof DurationElement)
					{
						DurationElement durationElement = (DurationElement) musicElement;
						List<Direction> directions = durationElement.getDirections();
						if (directions != null)
						{
							for (Direction direction : directions)
							{
								if (direction instanceof Dynamics)
								{
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}


	private static boolean[] voicesInStaffHaveDynamics(Staff staff)
	{
		int numberOfVoices = getNumberOfVoicesInStaff(staff);
		boolean[] dyn = new boolean[numberOfVoices];
		for (int i = 0; i < numberOfVoices; i++)
		{
			dyn[i] = voiceHasDynamics(staff, i);
		}
		return dyn;
	}


	private static boolean staffHasDynamics(boolean[] dynamics)
	{
		for (boolean b : dynamics)
		{
			if (b)
			{
				return true;
			}
		}
		return false;
	}


	private static int getFirstVoiceWithDynamics(boolean[] dynamics)
	{
		for (int i = 0; i < dynamics.length; i++)
		{
			if (dynamics[i])
			{
				return i;
			}
		}
		return 0;
	}


	public static int[] getVoiceforDynamicsInStaff(Staff staff)
	{
		boolean[] voicesInStaffHaveDynamics = voicesInStaffHaveDynamics(staff);
		int[] dynamicVoices = new int[voicesInStaffHaveDynamics.length];
		// If there are no dynamics in any voice, every voice can use its own
		// "dynamics"
		if (!staffHasDynamics(voicesInStaffHaveDynamics))
		{
			for (int i = 0; i < voicesInStaffHaveDynamics.length; i++)
			{
				dynamicVoices[i] = i;
			}
		}
		else
		{
			for (int i = 0; i < dynamicVoices.length; i++)
			{
				if (voicesInStaffHaveDynamics[i])
				{
					dynamicVoices[i] = i;
				}
				else
				{
					dynamicVoices[i] = getFirstVoiceWithDynamics(voicesInStaffHaveDynamics);
				}
			}
		}
		return dynamicVoices;
	}


	/**
	 * 
	 * @param staff
	 * @param voice
	 * @param position
	 * @param currentVelocity
	 * @return The first value is the current velocity, the second one is the
	 * velocity of the subsequent chords.
	 */
	public static int[] getVelocityAtPosition(Staff staff, int voice,
		ScorePosition position, int currentVelocity)
	{
		Tuple2<DynamicsType, ScorePosition> latestDynamicsType = getLatestDynamicsType(
			staff, voice, position);
		if (latestDynamicsType != null)
		{
			int vel[] = new int[2];
			int velocityFactorAtBeat = Settings.getInstance().getSetting(
				latestDynamicsType.get1().name(), "playback-dynamics", -1);
			if (velocityFactorAtBeat == -1)
			{
				vel[0] = currentVelocity;
				
			}
			else
			{
				vel[0] = convertToMidiVelocity(velocityFactorAtBeat);				
			}
			int subsequentVelocityFactor = Settings.getInstance().getSetting(
				latestDynamicsType.get1().name() + "_subsequent", "playback-dynamics",
				velocityFactorAtBeat);

			if (latestDynamicsType.get2().getBeat().compareTo(position.getBeat()) != 0)
			{
				if (subsequentVelocityFactor != -1)
				{
					vel[0] = convertToMidiVelocity(subsequentVelocityFactor);
				}
				else
				{
					vel[0] = currentVelocity;
				}
			}
			if (subsequentVelocityFactor == -1)
			{
				vel[1] = currentVelocity;
			}
			else
			{
				vel[1] = convertToMidiVelocity(subsequentVelocityFactor);
			}
			return vel;
		}
		else
		{
			int vel[] = { currentVelocity, currentVelocity };
			return vel;
		}
	}


	private static int convertToMidiVelocity(int vel)
	{
		int velocity = defaultvalue * vel / 100;
		return MathTools.clamp(velocity, 0, 127);
	}
	
	private static Tuple2<DynamicsType, ScorePosition> getLatestDynamicsType(Staff staff,
		int voiceNumber, ScorePosition position)
	{
		Tuple2<DynamicsType, ScorePosition> latestDynamicsType = getLatestDynamicsBeforePosition(
			staff, voiceNumber, position);
		int iMeasure = position.getMeasure() - 1;
		while (iMeasure >= 0 && latestDynamicsType == null)
		{
			latestDynamicsType = getLatestDynamicsInMeasure(staff, voiceNumber, iMeasure);
			iMeasure--;
		}
		return latestDynamicsType;
	}


	private static Tuple2<DynamicsType, ScorePosition> getLatestDynamicsBeforePosition(
		Staff staff, int voiceNumber, ScorePosition position)
	{
		Measure measure = staff.getMeasures().get(position.getMeasure());
		Voice voice = measure.getVoices().get(voiceNumber);
		RAList<MusicElement> elements = voice.getElements();
		Tuple2<DynamicsType, ScorePosition> type = null;
		for (MusicElement element : elements)
		{
			if (element instanceof DurationElement)
			{
				if (element.getScorePosition().getBeat().compareTo(position.getBeat()) < 1)
				{
					DurationElement durationElement = (DurationElement) element;
					List<Direction> directions = durationElement.getDirections();
					if (directions != null)
					{
						for (Direction direction : directions)
						{
							if (direction instanceof Dynamics)
							{
								type = new Tuple2<DynamicsType, ScorePosition>(
									((Dynamics) direction).getType(), element
										.getScorePosition());
							}
						}
					}
				}
			}
		}
		return type;
	}


	private static Tuple2<DynamicsType, ScorePosition> getLatestDynamicsInMeasure(
		Staff staff, int voiceNumber, int measureNumber)
	{
		Measure measure = staff.getMeasures().get(measureNumber);
		if (voiceNumber < measure.getVoices().size())
		{
			Voice voice = measure.getVoices().get(voiceNumber);
			RAList<MusicElement> elements = voice.getElements();
			int size = elements.size();
			for (int i = size - 1; i >= 0; i--)
			{
				MusicElement musicElement = elements.get(i);
				if (musicElement instanceof DurationElement)
				{
					DurationElement dur = (DurationElement) musicElement;
					List<Direction> directions = dur.getDirections();
					if (directions != null)
					{
						for (Direction direction : directions)
						{
							if (direction instanceof Dynamics)
							{
								return new Tuple2<DynamicsType, ScorePosition>(
									((Dynamics) direction).getType(), musicElement
										.getScorePosition());
							}
						}
					}
				}
			}
		}
		return null;
	}
}
