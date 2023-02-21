import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class backtracking {

    //File input variables
    public static File input;
    public static Scanner reader;
    public static String data;

    //Board state as well as the size variables for the length/width of the board
    public static Variable board[][];
    public static int rowNum;
    public static int colNum;

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

                    //TODO: Get consraints and insert them into objects

                    //TODO: Solve puzzle
                }
                reader.close();

            } catch(FileNotFoundException e){System.out.println("Could not find the file named " + args[0]);}
        }

        //If incorrect number of arguments given, prints error message
        else{System.out.println("ERROR: Not enough arguments given, expected 1 but received " + args.length + 1);}
    }

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
