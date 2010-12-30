;use modern UI
!include MUI2.nsh
!include "FileFunc.nsh"
!include LogicLib.nsh

;installation variables for Java
!define JRE_VERSION "1.6"
!define JRE_URL "http://javadl.sun.com/webapps/download/AutoDL?BundleId=33787"
!include "JREDyna.nsh"


!define SOURCEPATH "..\dist\os\windows-i586" 
!define MUI_ICON ${SOURCEPATH}\viewer\data\img\icons\zong.ico
!define MUI_WELCOMEFINISHPAGE_BITMAP Papagei_hoch.bmp
;!define MUI_HEADERIMAGE
;!define MUI_HEADERIMAGE_BITMAP Papagei_header.bmp
!define MUI_UNWELCOMEFINISHPAGE_BITMAP Papagei_hoch.bmp

!define INSTALLATIONNAME "Zong Viewer"
!ifndef OUTFILE
!define OUTFILE "..\dist\packages\zongviewer_setup.exe"
!endif
!ifndef VERSION
!define VERSION ""
!endif

RequestExecutionLevel admin


  BrandingText " "

;--------------------------------
;compress installer
;SetCompress force
;SetCompressor /SOLID lzma

;--------------------------------
;General
Name "Zong! Viewer"

OutFile ${OUTFILE}
InstallDir $PROGRAMFILES\Zong

;--------------------------------
;Interface Settings


;--------------------------------
;Pages

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "${SOURCEPATH}\viewer\gpl.txt"
!insertmacro MUI_PAGE_COMPONENTS
!define MUI_PAGE_CUSTOMFUNCTION_LEAVE "CheckBadDirectory"
!insertmacro MUI_PAGE_DIRECTORY

;Start Menu Folder Page Configuration
!define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
!define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\${INSTALLATIONNAME}" 
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
!define MUI_STARTMENUPAGE_DEFAULTFOLDER "Zong!" 
Var StartMenuFolder
!insertmacro MUI_PAGE_STARTMENU Application $StartMenuFolder
!insertmacro CUSTOM_PAGE_JREINFO
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH


;--------------------------------
;Languages
 
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "German"


Var run

Function .onInit

  !insertmacro MUI_LANGDLL_DISPLAY

  StrCpy $run 0
  
FunctionEnd

!macro Both
  ${If} $run == 0
	  ;install java, if necessary
	  call DownloadAndInstallJREIfNecessary

  SetOutPath $INSTDIR
	  
	  ;save icon
	  File ${SOURCEPATH}\viewer\data\img\icons\zong.ico
	  
	  ;Store installation folder
	  WriteRegStr HKCU "Software\${INSTALLATIONNAME}" "" $INSTDIR
	  
	  ;Create uninstaller
	  WriteUninstaller "$INSTDIR\Uninstall.exe"
	  
		WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${INSTALLATIONNAME}" "DisplayName"\
		"Zong Viewer"

		WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${INSTALLATIONNAME}" "UninstallString" \
		"$INSTDIR\Uninstall.exe"

		WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${INSTALLATIONNAME}" \
		"QuietUninstallString" "$\"$INSTDIR\Uninstall.exe$\" /S"
		
		WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${INSTALLATIONNAME}" "Publisher" \
		"Xenoage Software"
		
		WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${INSTALLATIONNAME}" "DisplayIcon" \
		"$INSTDIR\zong.ico"
		
		WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${INSTALLATIONNAME}" "URLUpdateInfo" \
		"http://www.zong-music.com"
		
		WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${INSTALLATIONNAME}" "URLInfoAbout" \
		"http://www.zong-music.com"
		
		WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${INSTALLATIONNAME}" "NoModify" 1
		
		WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${INSTALLATIONNAME}" "NoRepair" 1
		
		${GetSize} "$INSTDIR" "/S=0K" $0 $1 $2
		IntFmt $0 "0x%08X" $0
		WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${INSTALLATIONNAME}" "EstimatedSize" "$0"
		
		${If} VERSION <> ""
			WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${INSTALLATIONNAME}" "DisplayVersion" "1.1";"${VERSION}"
		${EndIf}
		StrCpy $run 1
	${EndIf}
!macroend



Section "Viewer" viewer
  !insertmacro Both


  File /r ${SOURCEPATH}\viewer
  
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    ;Create shortcuts
    CreateDirectory "$SMPROGRAMS\$StartMenuFolder"
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\Zong! Viewer.lnk" "$INSTDIR\viewer\zongviewer.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_END
  
SectionEnd

Section "Player" player
 	!insertmacro Both


  File /r ${SOURCEPATH}\player
  
    !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    ;Create shortcuts
    CreateDirectory "$SMPROGRAMS\$StartMenuFolder"
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\Zong! Player.lnk" "$INSTDIR\player\zongplayer.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_END

 
SectionEnd


LangString DESC_viewer ${LANG_ENGLISH} "Viewer to display and play MusicXML files."
LangString DESC_player ${LANG_ENGLISH} "Player to play MusicXML files."
LangString DESC_viewer ${LANG_GERMAN} "Programm zum betrachten und abspielen von MusicXML-Dateien."
LangString DESC_player ${LANG_GERMAN} "Programm zum abspielen von MusicXML-Dateien."

LangString ErrorSymbol ${LANG_ENGLISH} 'Error: The Installaton directory contains a "!".$\nPlease remove it to make Zong! work properly.'
LangString ErrorSymbol ${LANG_GERMAN} 'Fehler: Der Installationspfad enthält ein "!".$\nBitte entfernen Sie dieses, damit Zong! einwandfrei funktioniert.'

!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${viewer} $(DESC_viewer)
  !insertmacro MUI_DESCRIPTION_TEXT ${player} $(DESC_player)
!insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;REMOVE FILES 

  RMDir /r "$INSTDIR"

  !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder
    
  ;Delete "$SMPROGRAMS\$StartMenuFolder\ZongViewer.lnk"
  ;Delete "$SMPROGRAMS\$StartMenuFolder\ZongPlayer.lnk"

  RMDir /r "$SMPROGRAMS\$StartMenuFolder"

  DeleteRegKey /ifempty HKCU "Software\${INSTALLATIONNAME}"
  
  ;remove registry keys for add/remove in WINDOWS
  DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${INSTALLATIONNAME}"
  
SectionEnd



; On the basis of http://nsis.sourceforge.net/Check_for_spaces_in_a_directory_path
Function CheckBadDirectory
 
  # Call the CheckForSpaces function.
  Push $INSTDIR # Input string (install path).
   Call CheckForBadSymbols
  Pop $R0 # The function returns the number of spaces found in the input string.
 
  # Check if any spaces exist in $INSTDIR.
  StrCmp $R0 0 NoBadSymbols
  
    # Show message box then take the user back to the Directory page.
    MessageBox MB_OK|MB_ICONEXCLAMATION $(ErrorSymbol)
    Abort
 
  NoBadSymbols:
 
FunctionEnd

Function CheckForBadSymbols
 Exch $R0
 Push $R1
 Push $R2
 Push $R3
 StrCpy $R1 -1
 StrCpy $R3 $R0
 StrCpy $R0 0
 loop:
   StrCpy $R2 $R3 1 $R1
   IntOp $R1 $R1 - 1
   StrCmp $R2 "" done
   StrCmp $R2 "!" 0 loop
   IntOp $R0 $R0 + 1
 Goto loop
 done:
 Pop $R3
 Pop $R2
 Pop $R1
 Exch $R0
FunctionEnd