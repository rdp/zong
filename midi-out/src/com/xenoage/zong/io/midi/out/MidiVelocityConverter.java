package com.xenoage.zong.io.midi.out;

import static com.xenoage.util.iterators.ReverseIterator.reverseIt;
import static com.xenoage.util.lang.Tuple2.t;

import com.xenoage.pdlib.Vector;
import com.xenoage.util.MathTools;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.settings.Settings;
import com.xenoage.zong.core.music.Attachable;
import com.xenoage.zong.core.music.Globals;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.Measure;
import com.xenoage.zong.core.music.Staff;
import com.xenoage.zong.core.music.Voice;
import com.xenoage.zong.core.music.VoiceElement;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.direction.Dynamics;
import com.xenoage.zong.core.music.direction.DynamicsType;


/**
 * This class converts dynamics.
 * 
 * @author Uli Teschemacher
 */
public final class MidiVelocityConverter
{

	private static final int defaultvalue = 90;


	public static int getNumberOfVoicesInStaff(Staff staff)
	{
		int voiceCount = 0;
		for (Measure measure : staff.getMeasures())
		{
			int size = measure.getVoices().size();
			voiceCount = MathTools.clampMin(voiceCount, size);
		}
		return voiceCount;
	}


	public static int getVelocity(Chord chord, int currentVelocity,
		Globals globals)
	{
		for (Attachable attachment : globals.getAttachments().get(chord))
		{
			if (attachment instanceof Dynamics)
			{
				Dynamics dynamic = (Dynamics) attachment;
				int setting = Settings.getInstance().getSetting(
					dynamic.getType().name(), "playback-dynamics", currentVelocity);
				return convertToMidiVelocity(setting);
			}
		}
		return currentVelocity;
	}


	private static boolean voiceHasDynamics(Staff staff, int voiceIndex,
		Globals globals)
	{
		for (Measure measure : staff.getMeasures())
		{
			if (measure.getVoices().size() > voiceIndex)
			{
				Vector<VoiceElement> elements = measure.getVoice(voiceIndex).getElements();
				for (VoiceElement element : elements)
				{
					for (Attachable attachment : globals.getAttachments().get(element))
					{
						if (attachment instanceof Dynamics)
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}


	private static boolean[] voicesInStaffHaveDynamics(Staff staff, Globals globals)
	{
		int numberOfVoices = getNumberOfVoicesInStaff(staff);
		boolean[] dyn = new boolean[numberOfVoices];
		for (int i = 0; i < numberOfVoices; i++)
		{
			dyn[i] = voiceHasDynamics(staff, i, globals);
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


	public static int[] getVoiceforDynamicsInStaff(Staff staff, Globals globals)
	{
		boolean[] voicesInStaffHaveDynamics = voicesInStaffHaveDynamics(staff, globals);
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
		MP mp, int currentVelocity, Globals globals)
	{
		Tuple2<DynamicsType, MP> latestDynamicsType = getLatestDynamicsType(
			staff, voice, mp, globals);
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

			if (latestDynamicsType.get2().getBeat().compareTo(mp.getBeat()) != 0)
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
	
	
	private static Tuple2<DynamicsType, MP> getLatestDynamicsType(Staff staff,
		int voiceNumber, MP mp, Globals globals)
	{
		Tuple2<DynamicsType, MP> latestDynamicsType = getLatestDynamicsBeforePosition(
			staff, voiceNumber, mp, globals);
		int iMeasure = mp.getMeasure() - 1;
		while (iMeasure >= 0 && latestDynamicsType == null)
		{
			latestDynamicsType = getLatestDynamicsInMeasure(staff, voiceNumber, iMeasure, globals);
			iMeasure--;
		}
		return latestDynamicsType;
	}


	private static Tuple2<DynamicsType, MP> getLatestDynamicsBeforePosition(
		Staff staff, int voiceNumber, MP position, Globals globals)
	{
		Measure measure = staff.getMeasures().get(position.getMeasure());
		Voice voice = measure.getVoices().get(voiceNumber);
		Tuple2<DynamicsType, MP> type = null;
		for (VoiceElement element : voice.getElements())
		{
			MP elementMP = globals.getMP(element);
			if (elementMP.getBeat().compareTo(position.getBeat()) < 1)
			{
				for (Attachable attachment : globals.getAttachments().get(element))
				{
					if (attachment instanceof Dynamics)
					{
						type = t(((Dynamics) attachment).getType(), elementMP);
					}
				}
			}
		}
		return type;
	}


	private static Tuple2<DynamicsType, MP> getLatestDynamicsInMeasure(
		Staff staff, int voiceNumber, int measureNumber, Globals globals)
	{
		Measure measure = staff.getMeasures().get(measureNumber);
		if (voiceNumber < measure.getVoices().size())
		{
			Voice voice = measure.getVoices().get(voiceNumber);
			for (VoiceElement element : reverseIt(voice.getElements()))
			{
				for (Attachable attachment : globals.getAttachments().get(element))
				{
					if (attachment instanceof Dynamics)
					{
						return t(((Dynamics) attachment).getType(), globals.getMP(element));
					}
				}
			}
		}
		return null;
	}
}
