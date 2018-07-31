;counts lines. what more do you want?
;DrToxic, 2017

;Function countlines(infile$)
	file = ReadFile(infile$)
		While Not Eof(file)
			temp = ReadLine(file)
			count = count + 1
		Wend
	CloseFile(file)
	Return count-1
;End Function