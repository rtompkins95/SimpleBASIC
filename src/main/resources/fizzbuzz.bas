FizzBuzzStart:
FOR I = 1 TO 100
	IF I / 3 = INT(I / 3) THEN
		IF I / 5 = INT(I / 5) THEN
			PRINT "Fizz Buzz"
			GOTO nextNumber
		END IF
		PRINT "Fizz"
		GOTO nextNumber
	END IF
	IF I / 5 = INT(I / 5) THEN
		PRINT "buzz"
		GOTO nextNumber
	END IF
	PRINT I
nextNumber:
NEXT I
