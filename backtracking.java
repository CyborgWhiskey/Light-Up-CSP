import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
    public static ArrayList<Variable> assignable;
    //the list of all the unassigned "assignable" '_' variables
    public static ArrayList<Constraint> walls; //^^^
    //list to track wall constraints
    public static int rowNum;
    public static int colNum;

    //Node counter variable
    public static int nodeCount;

    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    //MAIN METHOD
    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    public static void main(String args[]){

        wallVars = new ArrayList<>();

        currAssign = new Stack<>();

        assignable = new ArrayList<>();

        walls = new ArrayList<>();
        //Checks for correct number of arguments
        if(args.length == 1){
            try{
                //Reads in file name given
                input = new File(args[0]);
                reader = new Scanner(input);

                System.out.println("Found file " + args[0]);
                System.out.println("Starting Processing");

                //Main loop:
                //Loops through every board in input file and runs the solution algorithim on it
                while(reader.hasNextLine()){
                    //Gets board and prints it
                    getBoard();
                    System.out.println("Starting puzzle:");
                    printBoard();

                    //Gets the constraints for the current board and adds them to affected variables
                    getConstraints();
                    updateAssignableVars();
                    getWallVariables();
                    System.out.println("\nSolving Puzzle:");
                    if(solve(currAssign, assignable, wallVars)){
                        System.out.print("\nTotal number of nodes: ");
                        System.out.println(nodeCount);
                        System.out.println("\nPuzzle Solved! Here's the solution:");
                        printBoard();
                    }
                    else{
                        System.out.println(nodeCount);
                        System.out.println("\nPuzzle couldn't be solved.");
                        printBoard();
                    }
                }
                reader.close();

            } catch(FileNotFoundException e){System.out.println("Could not find the file named " + args[0]);}
        }

        //If incorrect number of arguments given, prints error message
        else{System.out.println("ERROR: Not enough arguments given, expected 1 but received " + args.length + 1);}
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    //SOLVING METHODS
    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    public static boolean solve(Stack<Variable> currAssign, ArrayList<Variable> availableVars, ArrayList<Variable> availableWallVars) {
        //TODO: Solve puzzle
        nodeCount = 0;
        boolean solution = false;
        ArrayList<Variable> temp;
        //Returns the board if it's a complete assignment - base case for recursion
        if(checker()){
            return true;
        }
        else{ //Recursive case
            Constraint light = null;
            temp = new ArrayList<>(availableWallVars); //messy, but otherwise I'd get runtime errors like concurrent modifications
            for(Variable wallVar: temp){
                if(wallChecks()){ //if we aren't through the unassigned wall adjacent variables but the walls' constraints are all met, we break
                    break;
                }
                wallVar.setLabel('b');
                for(int r = 0; r < rowNum; r++){
                    for(int c = 0; c < colNum; c++){
                        if(board[r][c] == wallVar){
                            light = getLightConstraint(r,c);
                            break;
                        }
                    }
                }
                if(wallVar.partialConsistent()){ //Checks for any other wall violations and light constraint violations
                    currAssign.push(wallVar);
                    availableWallVars.remove(wallVar);
                    solution = solve(currAssign, availableVars, availableWallVars);
                    if(!solution){ //if solution is invalid, we pop the most recent additon to currassign and re-add it to the list of wall variables
                        currAssign.pop();
                        availableWallVars.add(wallVar);
                        wallVar.setLabel('_');
                        wallVar.removeConstraint(light); //removes the light constraints from the bulb placed
                    }
                }
            }
            temp = new ArrayList<>(availableVars);
            for(Variable var: temp){ //this is where we assign the rest of the cells
                Variable assign = var;
                var.setLabel('b');
                for(int r = 0; r < rowNum; r++){
                    for(int c = 0; c < colNum; c++){
                        if(board[r][c] == var){
                            light = getLightConstraint(r,c);
                            break;
                        }
                    }
                }
                if(var.consistent()){
                    currAssign.push(var);
                    availableVars.remove(var);
                    solution = solve(currAssign,availableVars,availableWallVars);
                    if(!solution){
                        currAssign.pop();
                        availableVars.add(assign);
                        assign.setLabel('_');
                        assign.removeConstraint(light);
                    }
                }
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
        ArrayList<Variable> walls = new ArrayList<>();

        for(int r = 0; r < rowNum; r++){
            for(int c = 0; c < colNum; c++){
                if(board[r][c].getLabel() == 'b'){
                    bulbs.add(board[r][c]);
                }
                if(board[r][c].getLabel() == '_'){
                    lit.add(board[r][c]);
                }
                if(board[r][c].getLabel() <= '4'){
                    walls.add(board[r][c]);
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
        for(Variable var: walls){
            if(!var.consistent()){
                return false;
            }
        }
        return true;
    }

    public static void updateAssignableVars(){
        assignable.clear();
        for(int r = 0; r < rowNum; r++){
            for(int c = 0; c < colNum; c++) {
                if(board[r][c].getLabel() == '_'){
                    assignable.add(board[r][c]);
                }
            }
        }
        assignable.removeIf(Variable::getZeroStatus);
        assignable.removeIf(Variable::getLitStatus);
    }

    public static void getWallVariables(){
        ArrayList<Variable> temp = new ArrayList<>(assignable);
        for(Variable var: temp){
            if(var.getWallStatus()){
                wallVars.add(var);
                assignable.remove(var);
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

        //Skips a line to get to number of rows/columns
        data = reader.nextLine();
        data = reader.nextLine();
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
                board[r][c] = new Variable(vars[c], false, false, false);
            }
        }

        //Moves reader one line ahead to be at #End
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
                //Else a light constraint is created
                //else{
                    //getLightConstraint(row, col);
                //}
            }
        }
    }

    //Creates a new wall constraint based on a passed row and column index
    //Does not add the given cell
    public static wallConstraint getWallConstraint(int row, int col){
        //Stores the list of constrained variables
        ArrayList<Variable> vars = new ArrayList<Variable>();
        wallConstraint constraint;

        //Checks if each cell around the wall is within the bounds of the array and if so adds it to the list
        if(row - 1 >= 0){vars.add(board[row-1][col]);}
        if(row + 1 < rowNum){vars.add(board[row+1][col]);}
        if(col - 1 >= 0) {vars.add(board[row][col-1]);}
        if(col + 1 < colNum){vars.add(board[row][col+1]);}

        //Creates new wall constraint containing the list of affected cells and the number of the wall amd adds it to every affected variable
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
        ArrayList<Variable> vars = new ArrayList<Variable>();
        lightConstraint constraint;

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

        //Creates new light constraint containing the list of affected cells and assigns it to the affected cells
        constraint = new lightConstraint(vars);
        for(Variable var: vars){
            var.addConstraint(constraint);
            var.setLitStatus(true);
        }

        //Returns constraint
        return constraint;
    }

    //This is going to be needed if we want to check using constraints, before light constraints just hung around after we unassigned things
    //Removes the reference of the constraint directly from all affected variables, and then sets the "lit" flag appropriately
    //based on whether at least one light constraint still applies to the cell
    public static void removeLightConstraint(Constraint oldLight){
        for(Variable var: oldLight.vars){
            var.removeConstraint(oldLight);
            if(var.getNumConstraints() != 0){
                ArrayList<Constraint> remaining = var.getConstraints();
                var.setLitStatus(false);
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
