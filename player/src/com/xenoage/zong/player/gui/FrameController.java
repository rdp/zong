package com.xenoage.zong.player.gui;

import com.xenoage.util.exceptions.InvalidFormatException;
import com.xenoage.zong.io.midi.out.PlaybackListener;

import proxymusic.ScorePartwise;

import javax.sound.midi.Sequence;


/**
 * Controller for a frame with contains a MIDI player.
 *
 * @author Andreas Wenger
 * @author Hervé Bitteur
 */
public interface FrameController
	extends PlaybackListener
{

	
	/**
	 * Returns the view that belongs to this controller.
	 */
	FrameView getFrameView();


	/**
	 * Returns the currently active MIDI sequence.
	 */
	Sequence getSequence();

	
	/**
	 * Gets the current volume value.
	 * @return value between 0 (silent) and 1 (loud)
	 */
	float getVolume();
	

	/**
	 * Sets the playback volume
	 * @param volume  value between 0 (silent) and 1 (loud)
	 */
	void setVolume(float volume);


	/**
	 * Sets the current position of the playback cursor.
	 * @param position  a value between 0 (beginning) and 1 (ending)
	 */
	void setPlaybackPosition(float position);


	/**
	 * Register a listener to the playback
	 * @param listener the entity to receive callbacks
	 */
	void addPlaybackListener(PlaybackListener listener);


	/**
	 * Process the score defined by the provided ScorePartwise document by
	 * loading and playing.
	 * @param document the ready-to-use document
	 * @throws InvalidFormatException if the MusicXML is invalid
	 */
	void openDocument(ScorePartwise document) throws InvalidFormatException;


	/**
	 * Load the score defined by the provided ScorePartwise document
	 * @param document the ready-to-use document
	 * @throws InvalidFormatException if the MusicXML is invalid
	 */
	void loadDocument(ScorePartwise document) throws InvalidFormatException;


	/**
	 * Starts (or continue after a pause) the playback.
	 */
	void play();
	
	
	/**
	 * Pauses the playback.
	 */
	void pause();
	
	
	/**
	 * Stops the playback
	 */
	void stop();


	/**
	 * Unregisters the provided playback listener.
	 * @param listener the listener to unregister
	 */
	void removePlaybackListener(PlaybackListener listener);


	/**
	 * Displays the dialog about the audio settings.
	 */
	void showAudioSettingsDialog();


	/**
	 * Display sinformation about the current score.
	 */
	void showScoreInfo();

	
}
