import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;

//Class to implement the backtracking algorithim
//Takes input from the command line to run
public class backtracking {

    //File input variables
    public static File input;
    public static Scanner reader;
    public static String data;

    //Board state as well as the size variables for the length/width of the board
    public static Variable board[][];

    public static Stack<Variable> currAssign; //vvv these are the new arraylists and the assignment stack
    //current assignment stack
    public static ArrayList<Variable> wallVars;
    //all our '_' variables next to walls
    public static Stack<Variable> assignable;
    //the list of all the unassigned "assignable" '_' variables
    public static ArrayList<Constraint> walls; //^^^
    //list to track wall constraints
    public static int rowNum;
    public static int colNum;

    //Node counter variable
    public static int nodeCount;

    //Stores the current selected heuristic
    public static String heuristic;

    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    //MAIN METHOD
    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    public static void main(String args[]){

        //Timing variables
        //Used to time how long each puzzle takes to process
        long startTime;
        long endTime;

        //Instantiates Lists
        wallVars = new ArrayList<>();
        currAssign = new Stack<>();
        assignable = new Stack<>();
        walls = new ArrayList<>();

        //Checks for correct number of arguments
        if(args.length > 0){
            try{
                //Reads in file name given
                input = new File(args[0]);
                reader = new Scanner(input);

                System.out.println("Found file " + args[0]);
                System.out.println("Starting Processing");

                //If heuristic is passed in, the herustic variable will be set to it
                heuristic = "";
                if(args.length == 2){
                    heuristic = args[1];
                }

                //Main loop:
                //Loops through every board in input file and runs the solution algorithim on it
                while(reader.hasNextLine()){
                    //Gets board and prints it
                    getBoard();
                    System.out.println("Starting puzzle:");
                    printBoard();

                    //Gets the constraints for the current board and adds them to affected variables
                    getConstraints();
                    assignable = updateAssignableVars();
                    getWallVariables();

                    //Attempts to solve puzzle
                    System.out.println("\nSolving Puzzle:");
                    startTime = System.nanoTime();
                    if(solve(currAssign, assignable, wallVars)){     //Case 1: Solution found
                        endTime = System.nanoTime();
                        System.out.println("Puzzle Solved! Here's the solution:");
                        printBoard();
                        System.out.println();
                        System.out.println("Total number of nodes: " + nodeCount);
                        System.out.println("Processing Time: " + (endTime-startTime) + "ms");
                        System.out.println();
                    }
                    else{                                            //Case 2: No solution found
                        endTime = System.nanoTime();
                        System.out.println("Puzzle couldn't be solved.");
                        printBoard();
                        System.out.println();
                        System.out.println("Total number of nodes: " + nodeCount);
                        System.out.println("Processing Time: " + (endTime-startTime) + "ms");
                        System.out.println();
                    }
                    
                    //Resets node count to 0
                    nodeCount = 0;
                }
                reader.close();

            } catch(FileNotFoundException e){System.out.println("Could not find the file named " + args[0]);}
        }

        //If incorrect number of arguments given, prints error message
        else{System.out.println("ERROR: Not enough arguments given, expected at least 1 but received " + args.length);}
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    //SOLVING METHODS
    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    public static boolean solve(Stack<Variable> currAssign, Stack<Variable> availableVars, ArrayList<Variable> availableWallVars) {
        boolean solution = false;
        Constraint temp = null;
        Stack<Variable> updatedAvailable;

        //Returns the board if it's a complete assignment - base case for recursion
        if(checker()){
            return true;
        }
        else{ //Recursive case
            while(!availableVars.isEmpty()){
                Variable var = availableVars.pop();
                var.setLabel('b');
                temp = getLightConstraint(var.getRow(), var.getCol());

                if(var.partialConsistent()){
                    updatedAvailable = (Stack<Variable>) availableVars.clone();
                    currAssign.push(var);
                    nodeCount++;
                    solution = solve(currAssign,updatedAvailable,availableWallVars);
                    if(solution){
                        return true;
                    }
                    currAssign.pop();

                }
                var.setLabel('_');
                removeLightConstraint(temp);
            }
        }

        return solution;
    }

    public static boolean wallChecks(){
        boolean valid = true;
        for(Constraint constr: walls){
            if(!constr.satisfied()){
                valid = false;
            }
        }
        return valid;
    }

    public static boolean checker(){
        ArrayList<Variable> bulbs = new ArrayList<>();
        ArrayList<Variable> lit = new ArrayList<>();

        for(int r = 0; r < rowNum; r++){
            for(int c = 0; c < colNum; c++){
                if(board[r][c].getLabel() == 'b'){
                    bulbs.add(board[r][c]);
                }
                if(board[r][c].getLabel() == '_'){
                    lit.add(board[r][c]);
                }
            }
        }

        for(Variable var: bulbs){
            if(!var.consistent()){
                return false;
            }
        }
        for(Variable var: lit){
            if(!var.getLitStatus()){
                return false;
            }
        }
        if(!wallChecks()){
            return false;
        }
        return true;
    }

    //Checks for and adds the possible assignable variables to a stack and returns it
    //Also organizes the stack based on the possibly given heuristic
    public static Stack<Variable> updateAssignableVars(){
        //Local lists used to compute H3
        ArrayList<Variable> partition;
        Stack<Variable> out;
        Variable var;

        //Stack of possible variables
        Stack<Variable> temp = new Stack<>();

        //Default case: no heuristic is used
        //Triggers if no heruistic was selected or if the entered heuristic does not exist
        for(int r = 0; r < rowNum; r++){
            for(int c = 0; c < colNum; c++) {
                if(board[r][c].getLabel() == '_'){
                    temp.push(board[r][c]);
                }
            }
        }

        //Heuristic 1: Most Constrained
        //Orders stack from most constrained to least
        if(heuristic.equals("H1")){
            Collections.sort(temp, Collections.reverseOrder(new ConstraintComp()));
        }

        //Heuristic 2: Most Constraining
        //Orders stack by most constraining value (The one that would cast the most light) to least
        else if(heuristic.equals("H2")){
            Collections.sort(temp, Collections.reverseOrder(new ConstrainingComp()));
        }

        //Heuristic 3: Hybrid
        //Sorts by least constrained value and sorts each set of equal constrained values by their most constraining
        else if(heuristic.equals("H3")){
            //Instatniates temporary stack and list
            partition = new ArrayList<>();
            out = new Stack<>();

            //Sorts stack from least constrained variable to most
            Collections.sort(temp, new ConstraintComp());

            //Loops through stack of variables until empty
            while(!temp.empty()){
                //Pops first var and gets its # of constraints, and uses that to loop to get all variables with the same # of constraints
                //Stores resulkt in an arraylist
                var = temp.pop();
                partition.add(var);
                while(!temp.empty() && temp.peek().getNumConstraints() == var.getNumConstraints()){
                    partition.add(temp.pop());
                }

                //Sorts the arraylist by most constraining variable to least and pushes sorted order to the out stack
                Collections.sort(partition, Collections.reverseOrder(new ConstrainingComp()));
                for(Variable i : partition){out.push(i);}

                //Clears the partition
                partition.clear();
            }

            //Sets temp equal to out so the sorted stack is returned
            temp = out;
        }

        //FORWARD CHECKING
        //temp.removeIf(Variable::getZeroStatus);
        //temp.removeIf(Variable::getLitStatus);
        return temp;
    }

    public static void getWallVariables(){
        ArrayList<Variable> temp = new ArrayList<>(assignable);
        for(Variable var: temp){
            if(var.getWallStatus()){
                wallVars.add(var);
                //assignable.remove(var); <- re-enable for heuristics
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    //BOARD CREATION METHODS
    //------------------------------------------------------------------------------------------------------------------------------------------------------------

    //Reads in each line of a file to get the rows and columns of the current puzzle
    //Then reads in each line and creates a Variable object for each cell and stores it in the board array
    //Nothing is returned all changes are made to the array
    public static void getBoard(){
        StringTokenizer st;
        char[] vars;

        //Gets the first line and converts it to a char array
        data = reader.nextLine();
        vars = data.toCharArray();

        //Checks if the first char is a #, if so this line is a comment and is ignored
        while(vars[0] == '#'){
            data = reader.nextLine();
            vars = data.toCharArray();
        }

        //First line of puzzle is the dimensions, tokenizes data string to get the 2 values
        st = new StringTokenizer(data);

        //Gets the row and column number from input and instantiates board array
        rowNum = Integer.parseInt(st.nextToken());
        colNum = Integer.parseInt(st.nextToken());
        board = new Variable[rowNum][colNum];

        //Loops through each row of the text file to get the row of each board
        for(int r = 0; r < rowNum; r++){
            data = reader.nextLine();
            vars = data.toCharArray();

            //Loops through each column of current row to get values and adds them to the board
            for(int c = 0; c < colNum; c++){
                board[r][c] = new Variable(vars[c], false, false, false, r, c);
            }
        }

        reader.nextLine();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CONSTRAINT CREATION METHODS
    //------------------------------------------------------------------------------------------------------------------------------------------------------------

    //Finds and creates constraints for the board
    //Adds the constraints to the affected cells
    //Currently Uses:
    //  --> WallConstraints: Constraint for if a wall has the correct number of bulbs surrounding it
    //  --> LightConstraints: Constraint that there can only be one bulb per row/column, unless there is a wall between them
    public static void getConstraints(){
        //Loops through each cell of the board and finds constraints for that cell
        for(int row = 0; row < rowNum; row++){
            for(int col = 0; col < colNum; col++){
                //If the label is less than or equal to '4', then a WallConstraint is created
                if(board[row][col].getLabel() <= '4'){
                    walls.add(getWallConstraint(row, col));
                }

                //Else a possible light constraint is created
                else{
                    board[row][col].setPossibleConstraint(getlightConstrainedVars(row, col).size());
                }
            }
        }
    }

    //Creates a new wall constraint based on a passed row and column index
    //Does not add the given cell
    public static wallConstraint getWallConstraint(int row, int col){
        //Stores the list of constrained variables
        ArrayList<Variable> vars = new ArrayList<>();
        wallConstraint constraint;

        //Checks if each cell around the wall is within the bounds of the array and if so adds it to the list
        if(row - 1 >= 0){vars.add(board[row-1][col]);}
        if(row + 1 < rowNum){vars.add(board[row+1][col]);}
        if(col - 1 >= 0) {vars.add(board[row][col-1]);}
        if(col + 1 < colNum){vars.add(board[row][col+1]);}

        //Creates new wall constraint containing the list of affected cells and the number of the wall and adds it to every affected variable
        constraint = new wallConstraint(vars, board[row][col].getLabel());
        for(Variable var: vars){
            var.addConstraint(constraint);
            if(board[row][col].getLabel() == '0'){
                var.setZeroStatus(true);
            }
            else{
                var.setWallStatus(true);
            }
        }

        //Returns constraint
        return constraint;
    }

    //Creates a new light constraint based on a passed row and column index
    //Adds variable to list for every cell above, below, left or right unstil a wall is hit
    //Does not include the given cell
    public static lightConstraint getLightConstraint(int row, int col){
        //Stores the list of constrained varriables
        ArrayList<Variable> vars = getlightConstrainedVars(row, col);
        lightConstraint constraint;

        //Creates new light constraint containing the list of affected cells and assigns it to the affected cells
        constraint = new lightConstraint(vars);
        for(Variable var: vars){
            var.addConstraint(constraint);
            var.setLitStatus(true);
        }

        //Returns constraint
        return constraint;
    }

    public static ArrayList<Variable> getlightConstrainedVars(int row, int col){
        //Stores the list of constrained varriables
        ArrayList<Variable> vars = new ArrayList<Variable>();

        //Current row/column being inspected
        int r;
        int c;

        //Up direction
        r = row - 1;
        while(r >= 0 && board[r][col].getLabel() > '4'){
            vars.add(board[r][col]);
            r--;
        }

        //Down direction
        r = row + 1;
        while(r < rowNum && board[r][col].getLabel() > '4'){
            vars.add(board[r][col]);
            r++;
        }

        //Left direction
        c = col - 1;
        while(c >= 0 && board[row][c].getLabel() > '4'){
            vars.add(board[row][c]);
            c--;
        }

        //Right direction
        c = col + 1;
        while(c < colNum && board[row][c].getLabel() > '4'){
            vars.add(board[row][c]);
            c++;
        }

        //Returns list of affected variables
        return vars;
    }

    //This is going to be needed if we want to check using constraints, before light constraints just hung around after we unassigned things
    //Removes the reference of the constraint directly from all affected variables, and then sets the "lit" flag appropriately
    //based on whether at least one light constraint still applies to the cell
    public static void removeLightConstraint(Constraint oldLight){
        for(Variable var: oldLight.vars){
            var.removeConstraint(oldLight);
            var.setLitStatus(false);
            if(var.getNumConstraints() != 0){
                ArrayList<Constraint> remaining = var.getConstraints();
                for(Constraint c: remaining){
                    if(c instanceof lightConstraint){
                        var.setLitStatus(true);
                    }
                }
            }
        }
    }

    //Prints out current board state
    public static void printBoard(){
        for(int row = 0; row < rowNum; row++){
            for(int col = 0; col < colNum; col++){
                System.out.print(board[row][col].getLabel());
            }
            System.out.println();
        }
    }
}
