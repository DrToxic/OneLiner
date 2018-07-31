;counts lines in a given text file. what more do you want?
;DrToxic, 2017

;function RandomString(infile$,strings)
number=Rand(0,strings)
infile = ReadFile(infile$)
For n=0 To number
	out$ = ReadLine(infile)
Next
CloseFile(infile)
Return out$
;end function