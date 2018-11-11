Global mainXpos = (GadgetWidth(Desktop())/2)-192
Global mainYpos = (GadgetHeight(Desktop())/2)-135
Global mainWidth = 385
Global mainHeight = 270

Global listFile$ = CurrentDir$()+"LIST.txt"
Global lineFile$ = CurrentDir$()+"LINE.txt"
Global frequency = 15
Global autoStart = False
Global runTimer = False
Global autoSave = True
Global saveOnExit = True
readConfig()
Global mainWindow = CreateWindow("OneLiner",mainXpos,mainYpos,mainWidth,mainHeight,Desktop(),13+2)
SetStatusText(mainWindow,"Starting up. . . This will not take long, I promise.")
SetMinWindowSize mainWindow,385,270

;File Input
lab1 = CreateLabel("Input File",10,10,mainWidth-35,40,mainWindow,3)
tex1 = CreateTextField(12,25,mainWidth-39,20,mainWindow)

;File Output
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
		CreateMenu("Select Destination file",11,file)
		CreateMenu("",0,file)
save = CreateMenu("Save Configuration",0,file)
		CreateMenu("Right Now!",20,save)
eSave =	CreateMenu("automagically on exit",21,save)
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
				temp$ = RequestFile("Please select a file to write the Line to","txt",1,"LINE.txt")
				If temp$ <>"" Then lineFile$ = temp$
				SetGadgetText(tex2,lineFile$)
			Case 20
				writeConfig()
			Case 21
				If saveOnExit = True Then
					UncheckMenu(eSave) : saveOnExit = False
					SetStatusText(mainWindow,"I Will NOT save when I quit.")
				Else
					CheckMenu(eSave) : saveOnExit = True
					SetStatusText(mainWindow,"I Will save when I quit.")
				EndIf
			Case 30 ExecFile("OneLiner.chm")
			Case 31 Notify "OneLiner v1.3 or something like that."+Chr$(13)+"Written by Dr. Toxic for Lave Radio."+Chr$(13)+"Sauce: https://github.com/DrToxic/OneLiner"+Chr$(13)+"Compiled by BlitzPlus IDE V1.47."
			Case 40
				If autoStart = True Then
					autoStart = False
					UncheckMenu(aStart)
					SetStatusText(mainWindow,"I will not automatically start when loaded next time (Don't forget to save config!)")
				Else
					autoStart = True
					CheckMenu(aStart)
					SetStatusText(mainWindow,"I will automatically start when loaded next time. (Don't forget to save config!)")
				EndIf
		Default
		End Select
		UpdateWindowMenu(mainWindow)
	Case $4001 ;This shit happens once every second!
		If sec>0 Then sec=sec-1
		If runTimer = True Then ;ok, only if the RUN switch is turned on..
			If sec=0 Then
				sec = frequency
				inFile = ReadFile(listFile$)
				strings = 0
				While Not Eof(inFile)
					myString$ = ReadLine(inFile)
					strings = strings + 1
				Wend
				strings = strings - 1
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
		SetStatusText(mainWindow,sec+" seconds until next write. Previous line: "+myString$)
	Default

End Select

Forever


Function CountLines()
If FileSize(listFile$) >0 Then
	file = ReadFile(listFile$)
		While Not Eof(file)
			temp = ReadLine(file)
			count = count + 1
		Wend
	CloseFile(file)
	Return count-1
Else
	Notify "There doesn't seem to be a list file. Please select a file using the file menu and try again."
	runTimer = False
EndIf
End Function

Function RandomString$(strings)
	number=Rand(0,strings)
	listFile = ReadFile(listFile$)
	For n=0 To number
		out$ = ReadLine(listFile)
	Next
	CloseFile(infile)
	Return out$
End Function

Function readConfig()
config = ReadFile("OneLiner.cfg")
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
	config = WriteFile("OneLiner.cfg")
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