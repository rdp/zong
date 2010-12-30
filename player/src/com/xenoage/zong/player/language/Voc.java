package com.xenoage.zong.player.language;

import com.xenoage.util.language.VocabularyID;


/**
 * An index of all vocabulary of this application.
 * 
 * @author Andreas Wenger
 */
public enum Voc
	implements VocabularyID
{
	
	Error_Files,
	Error_Midi,
	Error_NoValidMusicXMLFile,
	Error_SavingFile,
	Error_Skin,
	Label_AudioSettings,
	Label_Cancel,
	Label_CancelAtFirstError,
	Label_Channels,
	Label_Close,
	Label_Copyright,
	Label_CopyrightContent,
	Label_Creators,
	Label_Default,
	Label_Description,
	Label_DescriptionContent,
	Label_DeviceDefault,
	Label_DeviceName,
	Label_IncludeSubdirectories,
	Label_Info,
	Label_InterpolationCubic,
	Label_InterpolationLinear,
	Label_InterpolationMode,
	Label_InterpolationPoint,
	Label_InterpolationSinc,
	Label_Bits,
	Label_LatencyMs,
	Label_License,
	Label_MaxPolyphony,
	Label_MoreInfoContent,
	Label_MovementNumber,
	Label_MovementTitle,
	Label_NoFileLoaded,
	Label_OK,
	Label_Parts,
	Label_ProgramInfo,
	Label_Rights,
	Label_SampleRateHz,
	Label_ScoreInfo,
	Label_Select_,
	Label_SelectDocument,
	Label_Soundbank,
	Label_UnnamedScore,
	Label_UseDefault,
	Label_Version,
	Label_WorkNumber,
	Label_WorkTitle,
	Menu_About,
	Menu_Audio,
	Menu_Convert,
	Menu_DirToMidi,
	Menu_Exit,
	Menu_File,
	Menu_FileToMidi,
	Menu_Help,
	Menu_Info,
	Menu_Open,
	Menu_ReadMe,
	Menu_SaveAs,
	Menu_Settings,
	Message_DirectoryConversionResult,
	Message_NoDocumentsAvailable,
	Message_NoFileLoaded;

	
	/**
	 * Gets the ID of the vocabulary as a String.
	 */
	public String getID()
	{
		return this.toString();
	}

}
