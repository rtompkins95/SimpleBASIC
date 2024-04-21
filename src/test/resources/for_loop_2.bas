count = 5
FOR I = 1 to count
	READ F%
	GOSUB FtoC
	PRINT C%
NEXT I
END

FtoC: C% = 5*(F%-32)/9
RETURN

DATA 32.0, 100.0, 212.0, 50.0, 0.0