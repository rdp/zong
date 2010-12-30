package com.xenoage.zong.app.language;

import com.xenoage.util.language.VocabularyID;


/**
 * An index of all vocabulary of this application.
 * 
 * @author Andreas Wenger
 */
public enum Voc
	implements VocabularyID
{

	ButtonPanel_DownloadDocument,
	ButtonPanel_OpenDocument,
	ButtonPanel_Playback,
	ButtonPanel_PrintDocument,
	ButtonPanel_SaveDocumentAs,
	Buttons_Close,
	Buttons_Details,
	Buttons_ReportError,
	CommandList_ChangeFrameOptions,
	Dialog_Details,
	Dialog_DocumentFormatError,
	Dialog_Error,
	Dialog_FatalError,
	Dialog_FrameOptions,
	Dialog_Instrumentation,
	Dialog_OpenDocument,
	Dialog_Warning,
	Error_CommandFailed,
	Error_CouldNotCreateTexture,
	Error_CouldNotInitApp,
	Error_CouldNotLoadSymbolPool,
	Error_CouldNotLoadTexture,
	Error_CouldNotSaveDocument,
	Error_Download,
	Error_InputError,
	Error_InvalidGUIFile,
	Error_MidiNotAvailable,
	Error_OpenFileInvalidFormat,
	Error_OpenFileIOError,
	Error_OpenFileNotFound,
	Error_PrintFailed,
	Error_SaveFileUnknownOrigin,
	Error_Security,
	Error_Unknown,
	Feedback_AttachFile,
	Feedback_AttachLog,
	Feedback_Cancel,
	Feedback_Description,
	Feedback_ExperiencedUsers,
	Feedback_HeadlineTextarea,
	Feedback_LabelMail,
	Feedback_Send,
	Feedback_SendFailed,
	Feedback_SendOK,
	Feedback_Title,
	FileFilter_AllPictures,
	General_Bottom,
	General_Cancel,
	General_Distance,
	General_Height,
	General_Impossible,
	General_Instruments,
	General_Left,
	General_MemoryUsage,
	General_OK,
	General_PageFormat,
	General_PageMargins,
	General_PageSettings,
	General_Position,
	General_Right,
	General_Rotation,
	General_Size,
	General_Staves,
	General_Top,
	General_Width,
	Label_CleanMemory,
	Label_CloseDocument,
	Label_CurrentMemoryUsage,
	Label_MaximumMemory,
	Label_RemoveInstrument,
	Label_SaveInstrumentation,
	Label_SelectColor,
	Label_SelectDocument,
	Label_used,
	Menu_Edit,
	Menu_Edit_Redo,
	Menu_Edit_Undo,
	Menu_Language,
	Message_Error,
	Message_FatalError,
	Message_MinStaffHeight,
	Message_NoDocumentsAvailable;


	/**
	 * Gets the ID of the vocabulary as a String.
	 */
	public String getID()
	{
		return this.toString();
	}

}
