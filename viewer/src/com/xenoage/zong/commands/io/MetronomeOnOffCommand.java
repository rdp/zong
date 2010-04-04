package com.xenoage.zong.commands.io;

import javax.sound.midi.MidiUnavailableException;

import com.xenoage.zong.commands.Command;
import com.xenoage.zong.commands.input.KeyboardScoreInputCommand;
import com.xenoage.zong.io.midi.out.MIDIOutput;
import com.xenoage.zong.util.exceptions.PropertyAlreadySetException;


/**
 * Switches the metronome on or off.
 * @author Uli Teschemacher
 *
 */
public class MetronomeOnOffCommand
	extends Command
	implements KeyboardScoreInputCommand
{
	
	
	@Override
	public void execute() throws PropertyAlreadySetException
	{
		try
		{
			MIDIOutput.getInstance().setMetronomeEnabled(
				!MIDIOutput.getInstance().getMetronomeEnabled());
		}
		catch (MidiUnavailableException ex)
		{
		}
	}

}
