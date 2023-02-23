import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
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
    public static int rowNum;
    public static int colNum;

    //Node counter variable
    public static int nodeCount;

    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    //MAIN METHOD
    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    public static void main(String args[]){

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
                    System.out.println("\nSolving Puzzle:");
                    if(solve()){
                        System.out.print("\nTotal number of nodes: ");
                        System.out.println(nodeCount);
                        nodeCount = 0;
                        System.out.println("\nPuzzle Solved! Here's the solution:");
                        printBoard();
                    }
                    else{
                        System.out.println(nodeCount);
                        nodeCount = 0;
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
    public static boolean solve() {
        //TODO: Solve puzzle
        //Returns the board if it's a complete assignment - base case for recursion
        boolean consistent = false;
        if(nodeCount > 0) {
            consistent = true;
            for (int r = 0; r < rowNum; r++) {
                for (int c = 0; c < colNum; c++) {
                    if (!board[r][c].consistent()) {
                        consistent = false;
                        break;
                    }
                }
            }
        }
        if(consistent){ //Conditional return if consistency is true for each variable on the board
            return true;
        }
        else {
            for (int r = 0; r < rowNum; r++) {
                for (int c = 0; c < colNum; c++) {
                    if (board[r][c].getLabel() == '_'){
                        board[r][c].setLabel('b');
                        Constraint tempLConst;
                        if(board[r][c].partialConsistent()){
                            tempLConst = getLightConstraint(r, c);
                            nodeCount++;
                            consistent = solve();
                            if(!consistent){
                                board[r][c].removeConstraint(tempLConst);
                                board[r][c].setLabel('_');
                            }
                        }
                        else {
                            board[r][c].setLabel('_');
                        }
                    }
                }
            }
        }
        return consistent;
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
                board[r][c] = new Variable(vars[c]);
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
                    //getWallConstraint(row, col);
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
        //Stores the list of constrained varriables
        ArrayList<Variable> vars = new ArrayList<Variable>();
        wallConstraint constraint;

        //Checks if each cell around the wall is within the bounds of the array and if so adds it to the list
        if(row - 1 >= 0){vars.add(board[row-1][col]);}
        if(row + 1 < rowNum){vars.add(board[row+1][col]);}
        if(col - 1 >= 0) {vars.add(board[row][col-1]);}
        if(col + 1 < colNum){vars.add(board[row][col+1]);}

        //Creates new wall constraint containing the list of affected cells and the number of the wall amd adds it to every affected variable
        constraint = new wallConstraint(vars, board[row][col].getLabel());
        for(Variable var: vars){var.addConstraint(constraint);}

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
        for(Variable var: vars){var.addConstraint(constraint);}

        //Returns constraint
        return constraint;
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
