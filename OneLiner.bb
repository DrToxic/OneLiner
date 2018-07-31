wind = CreateWindow("OneLiner",(GadgetWidth(Desktop())/2)-192.5,(GadgetHeight(Desktop())/2)-135,385,270,Desktop(),13)

menu = WindowMenu(wind)
file = CreateMenu("File",0,menu)
	CreateMenu("1. Browse for Source List",1,file)
	CreateMenu("2. Select Destination file",2,file)
	CreateMenu("Help",3,menu)
UpdateWindowMenu(wind)
;File Input
CreateLabel("Input File",10,10,360,40,wind,3)
tex1 = CreateTextField(12,25,356,20,wind)
Global infile$ = CurrentDir$()+"Text\LIST.txt"

;File Output
CreateLabel("Output File",10,60,360,40,wind,3)
tex2 = CreateTextField(12,75,356,20,wind)
Global outfile$ = CurrentDir$()+"Text\OBS.txt"

CreateLabel("Settings",10,110,360,80,wind,3)
CreateLabel("Delay (seconds)",12,128,80,20,wind)
fast = CreateButton("Down",90,125,50,20,wind)
tex3 = CreateTextField(140,125,50,20,wind)
slow = CreateButton("Up",190,125,50,20,wind)

CreateLabel("Condition",12,150,80,20,wind)
Global start = CreateButton("Start",190,150,50,20,wind)
tex4 = CreateTextField(140,150,50,20,wind)
stopb = CreateButton("Stop",90,150,50,20,wind)

freq = 15 ;number of seconds since the last update

;housekeeping
SetGadgetText(tex1,infile$)
SetGadgetText(tex2,outfile$)
SetGadgetText(tex3,freq)
SetGadgetText(tex4,"Stopped")
DisableGadget(tex1):DisableGadget(tex2):DisableGadget(start)
RUN = False
file1 = False
file2 = True
timer = CreateTimer(1)
If FileSize(infile$)>0 Then
	file1 = True
	strings = CountLines(infile$)
Else
	Notify "Unable to find file, Use the file menu"
	file1 = False
	SetGadgetText(tex1,"404, "+infile$)
EndIf
;and here we go...
Repeat
If file1 And file2 = True Then EnableGadget(start) Else DisableGadget(start)
Select WaitEvent()
	Case $401 ;BUTTONS!
		Select EventSource()
			Case fast
				If freq <6 Then
					Notify "You really don't want fewer than 5 seconds between writes."
				Else
					freq=freq-1
					SetGadgetText(tex3,freq)
				EndIf
			Case slow
				freq=freq+1
				SetGadgetText(tex3,freq)
			Case start
				RUN = True
				SetGadgetText(tex4,"Running")
				DisableGadget(start)
				DisableMenu(file)
				UpdateWindowMenu(wind)
			Case stopb
				RUN = False
				SetGadgetText(tex4,"Stopped")
				EnableGadget(start)
				EnableMenu(file)
				UpdateWindowMenu(wind)
			Default
		End Select
	Case $803 ;The exit button.
		Exit
	Case $1001
		Select EventData()
			Case 1
				infile$ = RequestFile("Choose a file","txt",0,"Strings.txt")
				If infile$="" Then
					SetGadgetText(tex1,"No file selected")
					file1 = False
				Else
					SetGadgetText(tex1,infile$)
					strings = CountLines(infile$)
					file1 = True
				EndIf
			Case 2
				outfile$ = RequestFile("Where do we put the output?","txt",1,"OneLiner.txt")
				If outfile$="" Then
					SetGadgetText(Tex2,"No file selected")
					file2 = False
				Else
					SetGadgetText(tex2,outfile$)
					file2 = True
				EndIf
			Case 3 Notify "Load an input file, select an output file, configure the delay, click start. a single random line will be written to the output file for OBS to read. It's really not that hard."+Chr$(13)+Chr$(13)+"Oh yeah, if the app is in RUN state, the FILE menu is disabled. lazy way of avoiding user issues."+Chr$(13)+Chr$(13)+"Created by DrToxic. Compiled 2018-07-27 at 01:03AM for Lave Radio."
		Default
		End Select
	Case $4001 ;This shit happens once every second!
		If sec>0 Then sec=sec-1
		If RUN = True Then ;ok, only if the RUN switch is turned on..
			If sec=0 Then
				sec = freq
				strings = countlines(infile$)
				out$ = RandomString$(infile$,strings)
				fileout = WriteFile(outfile$)
					WriteLine (fileout,out$)
				CloseFile(fileout)
			EndIf
		EndIf
		SetStatusText(wind,sec+" seconds until next write")
	Default

End Select

Forever


Function CountLines(infile$)
	Include "includes\CountLines.bb"
End Function

Function RandomString$(infile$,strings)
	Include "includes\RandomString.bb"
End Function