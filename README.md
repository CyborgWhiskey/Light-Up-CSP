# Light-Up-CSP
An implementation of a CSP solver to light up puzzles using a backtracking and forward checking algorithim.
Compiling:
* To compile backtracking.java type 'javac backtracking.java'
* To compile forwardchecking.java type 'javac forward_checking.java'

Execution:
* To run either program, the user must specify a txt file of a puzzle and a possible heuristic to use
* The program will not run without a text file specified
* The possible values for the heuristic are 'H1' for most constrained, 'H2' for most constraining, and 'H3' for a hybrid of h1 and h2. If no heuristic is specified  the program will run without any ordering.
* To run the each file type:
    * java backtracking "puzzle file here" "heruistic here"
    * java forward_checking "puzzle file here" "heruistic here"

Known Issues:
* The program will tend to run for a very long time on puzzles much larger than 10x10, especilly if the number of walls is sparse