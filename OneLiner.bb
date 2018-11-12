Global mainWidth = 385
Global mainHeight = 270
Global mainXpos = (GadgetWidth(Desktop())/2)-(mainWidth/2)
Global mainYpos = (GadgetHeight(Desktop())/2)-(mainHeight/2)
Global appName$ = "OneLiner" : AppTitle appName$
Global listFile$ = CurrentDir$()+"LIST.txt"
Global lineFile$ = CurrentDir$()+"LINE.txt"
Global frequency = 15
Global autoStart = False
Global runTimer = False
Global autoSave = True
Global saveOnExit = True
readConfig()
Global mainWindow = CreateWindow(appName$,mainXpos,mainYpos,mainWidth,mainHeight,Desktop(),13+2)
SetStatusText(mainWindow,"Starting up. . . This will not take long, I promise.")
SetMinWindowSize mainWindow,385,270

;Draw the gadgets.
lab1 = CreateLabel("Input File",10,10,mainWidth-35,40,mainWindow,3)
tex1 = CreateTextField(12,25,mainWidth-39,20,mainWindow)

lab2 = CreateLabel("Output File",10,60,mainWidth-35,40,mainWindow,3)
tex2 = CreateTextField(12,75,mainWidth-39,20,mainWindow)

lab3 = CreateLabel("Settings",10,110,mainWidth-35,80,mainWindow,3)
lab4 = CreateLabel("Delay (seconds)",12,128,80,20,mainWindow)
fast = CreateButton("Down",90,125,50,20,mainWindow)
tex3 = CreateTextField(140,125,50,20,mainWindow)
slow = CreateButton("Up",190,125,50,20,mainWindow)

lab5 = CreateLabel("Condition",12,150,80,20,mainWindow)
Global start = CreateButton("Start",190,150,50,20,mainWindow)
tex4 = CreateTextField(140,150,50,20,mainWindow)
stopb = CreateButton("Stop",90,150,50,20,mainWindow)

;housekeeping
SetGadgetLayout(lab1,1,1,1,0) : SetGadgetLayout(lab2,1,1,1,0) : SetGadgetLayout(lab3,1,1,1,0) : SetGadgetLayout(lab4,1,0,1,0)
SetGadgetLayout(lab5,1,0,1,0) : SetGadgetLayout(tex1,1,1,1,0) : DisableGadget(tex1) : SetGadgetLayout(tex2,1,1,1,0)
DisableGadget(tex2) : SetGadgetLayout(fast,1,0,1,0) : SetGadgetLayout(slow,1,0,1,0) : SetGadgetLayout(start,1,0,1,0)
SetGadgetLayout(stopb,1,0,1,0) : SetGadgetLayout(tex3,1,0,1,0) : SetGadgetLayout(tex4,1,0,1,0) : SetGadgetText(tex1,listFile$)
SetGadgetText(tex2,lineFile$) : SetGadgetText(tex3,frequency) : timer = CreateTimer(1)

menu = WindowMenu(mainWindow)
file = CreateMenu("File",0,menu)
		CreateMenu("Browse For Source List",10,file)
		CreateMenu("Move source list",11,file)
		CreateMenu("Edit source list",12,file)
		CreateMenu("",0,file)
		CreateMenu("Select Destination file",13,file)
		CreateMenu("",0,file)
save = CreateMenu("Save Configuration",0,file)
		CreateMenu("Right Now!",20,save)
eSave =	CreateMenu("automagically on exit",21,save)
		CreateMenu("",0,file)
		CreateMenu("Exit",19,file)
conf = CreateMenu("Configure",0,menu)
aStart = CreateMenu("Automatically start when loaded?",40,conf)
help = CreateMenu("Help",0,menu)
		CreateMenu("Help me!",30,help)
		CreateMenu("",0,help)
		CreateMenu("About OneLiner.",31,help)
If autoStart = 1 Then
	runTimer = True : autoStart = True
	CheckMenu(aStart)
	SetGadgetText(tex4,"Running")
	DisableGadget(start)
Else
	runTimer = False : autoStart = False
	SetGadgetText(tex4,"Stopped")
	EnableGadget(start)
EndIf
If saveOnExit = 1 Then
	saveOnExit = True
	CheckMenu(eSave)
Else
	saveOnExit = False
EndIf
UpdateWindowMenu(mainWindow)

Repeat
Select WaitEvent()
	Case $401 ;BUTTONS!
		Select EventSource()
			Case fast
				If frequency <6 Then
					Notify "You really don't want fewer than 5 seconds between writes."
				Else
					frequency=frequency-1
					SetGadgetText(tex3,frequency)
				EndIf
			Case slow
				frequency=frequency+1
				SetGadgetText(tex3,frequency)
			Case start
				runTimer = True
				SetGadgetText(tex4,"Running")
				DisableGadget(start)
				UpdateWindowMenu(mainWindow)
			Case stopb
				runTimer = False
				SetGadgetText(tex4,"Stopped")
				EnableGadget(start)
				UpdateWindowMenu(mainWindow)
			Default
		End Select
	Case $803 ;The exit button.
		If saveOnExit = True Then writeConfig()
		Exit
	Case $1001
		Select EventData() ;The Menus
			Case 10
				temp$ = RequestFile("Please select a list of stuff","txt",0,"LIST.txt")
				If temp$ <>"" Then listFile$ = temp$
				SetGadgetText(tex1,listFile$)
			Case 11
				temp$ = RequestFile("Where do you want me to deposit the file?","txt",1,"LIST.txt")
				If temp$ <> "" Then
					CopyFile listFile$,temp$
					DeleteFile listFile$
					listFile$ = temp$
					SetGadgetText(tex1,listFile$)
				EndIf
			Case 12
				ExecFile "Notepad.exe "+listFile$
			Case 13
				temp$ = RequestFile("Please select a file to write the Line to","txt",1,"LINE.txt")
				If temp$ <>"" Then lineFile$ = temp$
				SetGadgetText(tex2,lineFile$)
			Case 19
				If saveOnExit = True Then
					writeConfig()
					Exit
				EndIf
			Case 20
				writeConfig()
			Case 21
				If saveOnExit = True Then
					UncheckMenu(eSave) : saveOnExit = False
					SetStatusText(mainWindow,"I Will NOT save when I quit.") : nsec = 5
				Else
					CheckMenu(eSave) : saveOnExit = True
					SetStatusText(mainWindow,"I Will save when I quit.") : nsec = 5
				EndIf
			Case 30 ExecFile(appName$+".chm")
			Case 31 Notify "OneLiner v1.3 or something like that."+Chr$(13)+"Written by Dr. Toxic for Lave Radio."+Chr$(13)+"Sauce: https://github.com/DrToxic/OneLiner"+Chr$(13)+"Compiled by BlitzPlus IDE V1.47."
			Case 40
				If autoStart = True Then
					autoStart = False
					UncheckMenu(aStart)
					SetStatusText(mainWindow,"I will not automatically start when loaded next time (Don't forget to save config!)") : nsec = 5
				Else
					autoStart = True
					CheckMenu(aStart)
					SetStatusText(mainWindow,"I will automatically start when loaded next time. (Don't forget to save config!)") : nsec = 5
				EndIf
		Default
		End Select
		UpdateWindowMenu(mainWindow)
	Case $4001 ;This shit happens once every second!
		If sec>0 Then sec=sec-1
		If runTimer = True Then ;ok, only if the RUN switch is turned on..
			If sec=0 Then
				SetStatusText(mainWindow,"WRITING.")
				If FileSize(listFile$) = 0 Then
					runTimer = False
					SetGadgetText(tex4,"Error")
					SetStatusText(mainWindow,"Error: input file blank or does not exist.") : nsec = 5
					EnableGadget(start)
				Else
					sec = frequency
					inFile = ReadFile(listFile$)
					strings = -1
					While Not Eof(inFile)
						myString$ = ReadLine(inFile)
						strings = strings + 1
					Wend
					SeekFile(inFile,0)
					For i = 0 To Rand(0,strings)
						myString$ = ReadLine(inFile)
					Next
					CloseFile(inFile)
					outFile = WriteFile(lineFile$)
						WriteLine(outFile,myString$)
					CloseFile(outFile)
				EndIf
			EndIf
		EndIf
		If nsec > 0 Then
			nsec = nsec - 1
		Else
			SetStatusText(mainWindow,sec+" Seconds until next write. Previous line: "+myString$)
		EndIf
	Default

End Select

Forever

Function readConfig()
config = ReadFile(appName$+".cfg")
	If config <> 0 Then
		While Not Eof(config)
			temp1$ = ReadLine(config)
			temp2 = Instr (temp1$," = ")
			Select Mid$(temp1$,0,temp2)
				Case "mainXpos" mainXpos = Mid$(temp1$,temp2+3)
				Case "mainYpos" mainYpos = Mid$(temp1$,temp2+3)
				Case "mainWidth" mainWidth = Mid$(temp1$,temp2+3)
				Case "mainHeight" mainHeight = Mid$(temp1$,temp2+3)
				Case "listFile" listFile$ = Mid$(temp1$,temp2+3)
				Case "lineFile" lineFile$ = Mid$(temp1$,temp2+3)
				Case "frequency" frequency = Mid$(temp1$,temp2+3)
				Case "autoStart" autoStart = Mid$(temp1$,temp2+3)
				Case "saveOnExit" saveOnExit = Mid$(temp1$,temp2+3)
			End Select
		Wend
	EndIf
End Function

Function writeConfig()
	config = WriteFile(appName$+".cfg")
		WriteLine(config,"[Application Configuration]")
		WriteLine(config,"mainXpos = "+GadgetX(mainWindow))
		WriteLine(config,"mainYpos = "+GadgetY(mainWindow))
		WriteLine(config,"mainWidth = "+GadgetWidth(mainWindow))
		WriteLine(config,"mainHeight = "+GadgetHeight(mainWindow))
		WriteLine(config,"listFile = "+listFile$)
		WriteLine(config,"lineFile = "+lineFile$)
		WriteLine(config,"frequency = "+frequency)
		WriteLine(config,"autoStart = "+autoStart)
		WriteLine(config,"saveOnExit = "+saveOnExit)
	CloseFile(config)
End Function