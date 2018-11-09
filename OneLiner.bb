Global mainXpos = (GadgetWidth(Desktop())/2)-192
Global mainYpos = (GadgetHeight(Desktop())/2)-135
Global mainWidth = 385
Global mainHeight = 270

Global listFile$ = CurrentDir$()+"LIST.txt"
Global lineFile$ = CurrentDir$()+"LINE.txt"
Global frequency = 15
Global runTimer = False

readConfig()

Global mainWindow = CreateWindow("OneLiner",mainXpos,mainYpos,mainWidth,mainHeight,Desktop(),13+2)
SetStatusText(mainWindow,"Starting up. . . This will not take long, I promise.")

menu = WindowMenu(mainWindow)
file = CreateMenu("File",0,menu)
		CreateMenu("Browse For Source List",10,file)
		CreateMenu("Select Destination file",11,file)
		CreateMenu("Save This Configuration",12,file)
autosave=	CreateMenu("Save On Exit",13,file)

UpdateWindowMenu(mainWindow)


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
SetGadgetLayout(lab1,1,1,1,0)
SetGadgetLayout(lab2,1,1,1,0)
SetGadgetLayout(lab3,1,1,1,0)
SetGadgetLayout(lab4,1,0,1,0)
SetGadgetLayout(lab5,1,0,1,0)
SetGadgetLayout(tex1,1,1,1,0)
SetGadgetLayout(tex2,1,1,1,0)
SetGadgetLayout(fast,1,0,1,0)
SetGadgetLayout(slow,1,0,1,0)
SetGadgetLayout(start,1,0,1,0)
SetGadgetLayout(stopb,1,0,1,0)
SetGadgetLayout(tex3,1,0,1,0)
SetGadgetLayout(tex4,1,0,1,0)
SetGadgetText(tex1,listFile$)
SetGadgetText(tex2,lineFile$)
SetGadgetText(tex3,frequency)
SetGadgetText(tex4,"Stopped")
DisableGadget(tex1):DisableGadget(tex2):DisableGadget(start)
file1 = False
file2 = True
timer = CreateTimer(1)
If FileSize(listFile$)>0 Then
	file1 = True
	strings = CountLines()
Else
	Notify "Unable to find file, Use the file menu"
	file1 = False
	SetGadgetText(tex1,"404, "+listFile$)
EndIf
;and here we go...
Repeat
If file1 And file2 = True Then EnableGadget(start) Else DisableGadget(start)
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
				DisableMenu(file)
				UpdateWindowMenu(mainWindow)
			Case stopb
				runTimer = False
				SetGadgetText(tex4,"Stopped")
				EnableGadget(start)
				EnableMenu(file)
				UpdateWindowMenu(mainWindow)
			Default
		End Select
	Case $803 ;The exit button.
		If MenuChecked(autosave) Then writeConfig()
		Exit
	Case $1001
		Select EventData() ;The Menus
			Case 10
				temp$ = RequestFile("Please select a list of stuff","txt",0,"LIST.txt")
				If temp$ <>0 Then listFile$ = temp$
			Case 11
				temp$ = RequestFile("Please select a file to write the Line to","txt",1,"LINE.txt")
				If temp$ <>0 Then lineFile$ = temp$
			Case 12
				writeConfig()
			Case 13
				If MenuChecked(autosave) Then
					UncheckMenu(autosave)
					writeConfig()
					SetStatusText(mainWindow,"AutoSaving Disabled")
				Else
					CheckMenu(autosave)
					SetStatusText(mainWindow,"AutoSaving Enabled")
				EndIf
		Default
		End Select
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
	file = ReadFile(listFile$)
		While Not Eof(file)
			temp = ReadLine(file)
			count = count + 1
		Wend
	CloseFile(file)
	Return count-1
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
				Case "runTimer" runTImer = Mid$(temp1$,temp2+3)
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
		WriteLine(config,"runTimer = "+runTimer)
	CloseFile(config)
End Function