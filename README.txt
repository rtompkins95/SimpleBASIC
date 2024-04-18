
SimpleBASIC is a simplified version of the original BASIC dialect. While there were standards published, there were dozens of implementations, many of which varied greatly. It is not object oriented; it is procedural. This is a simple model of computation – everything is a number or a string. Example:

Data Types
Simple variables do not need to be pre-defined! In Java, you have to declare a variable before using it. Not in BASIC. This has advantages and disadvantages…
They are typed by their ending character:
$ = string		% = float		any other ending = integer
Example: myString$, percentOfPeople%, count


Structure:
Classic BASIC has a number for each line of code. An example:
10 PRINT “Hello!”
20 GOTO 10

A label is a name followed by a colon. It must be the first item on a line. We can then reference that line from anywhere else in our program. Translating the above example:

beginning: PRINT “Hello!”
GOTO beginning

As you see, this does not use line numbers

In the version of BASIC, there are no user-defined functions. There are built-in functions, but you cannot make your own. You can have subroutines. A subroutine is like a function, but it doesn’t have parameters or return values. It uses global variables for communication. Every variable is global is BASIC

Example:
FtoC: C = 5*(F-32)/9
RETURN

F=72
GOSUB FtoC
PRINT C

Flow Control
IF expression THEN label
	If expression is true, GOTO label
	Example: IF x<5 THEN xIsSmall

FOR variable = initialValue TO limit STEP increment
NEXT variable
	Sets variable to initialValue, loops by adding increment to variable on each loop until limit is hit/surpassed. Note that the “STEP” and increment is optional – the step is assumed to be 1 if it is left off. NEXT variable marks the end of the “block”
	Example:
	FOR A = 0 TO 10 STEP 2
	PRINT A
	NEXT A

WHILE expression label
Loops from the current line to label until the expression is not true.
WHILE x < 5 endWhileLabel
x = x + 1
endWhileLabel: 

END ends the program. We need this because we might want to have subroutines/functions

Dealing with Data
DATA – defines a list of constant data that can be accessed with READ
READ – reads the next item from DATA
	Example:
	DATA 10,”mphipps”
	READ a, a$
If the data types don’t match correctly (like the $ was on the wrong variable), that is a runtime error.
INPUT – expects a string, then any number of variables. Prints the string, then waits for the user to enter the inputs, comma separated. 
	Example:
	INPUT “What is your name and age?”, name$, age
	PRINT “Hi “, name$, “ you are “, age, “ years old!” 

PRINT – prints any number of values, separated by a comma
	Example:
	PRINT “hello. 5 + 3 = “, 5+3, “ how do you like that “, name$, “?”

Built-in Functions:
RANDOM() – returns a random integer
LEFT$(string, int) – returns the leftmost N characters from the string 
RIGHT$(string, int) – returns the rightmost N characters from the string 
MID$(string,int, int) – returns the characters of the string, starting from the 2nd argument and taking the 3rd argument as the count; MID$(“Albany”,2,3) = “ban”
NUM$(int or float) – converts a number to a string
VAL(string) – converts a string to an integer
VAL%(string) – converts a string to a float


INSTALLATION:

Java 1.8
Maven 3.8.x (at the time of publishing, use the latest version otherwise)
Ensure you have at least Maven 3.8.x installed on your system or environment where the project is being built

### Installation

Clone the repo and run:

> mvn clean install -U
> mvn package

An easy way to do this in IntelliJ:
- Click "Open",  navigate to the project root directory, select the "pom.xml" file, then click "Open as Project"
- Click "View" -> "Tool Windows" -> "Maven"
- Open Maven Settings for your project and ensure you are using "Bundled (Maven 3)" under "Maven home path"
  - This will run the correct Maven version if you are running a different version of Maven at the system level

## Usage

java -jar /target/basic-app-1.0.jar "basic_file_name.bas"

Interactive Mode: 
java -jar /target/basic-app-1.0.jar [-interactive|-i]
