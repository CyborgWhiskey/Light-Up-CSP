import java.util.ArrayList;

//Variable Class
//Stores information about a cell in the Light Up Puzzle
//Stores: 
//  --label: the char label fo the cell (either _, b, or a number)
//  --constraints: the list of constraint objects that contain this variable
public class Variable{

    private char label;
    private ArrayList<Constraint> constraints;
    private ArrayList<Constraint> myConstraints;
    private int numConstraints;

    private int row;

    private int col;

    private boolean nextToZero;

    private boolean nextToWall;

    private boolean lit;

    //Constructor takes a char label as input
    public Variable(char label, boolean zeroWall, boolean wall, boolean illuminated, int r, int c){
        this.label = label;
        this.nextToZero = zeroWall;
        this.nextToWall = wall;
        this.lit = illuminated;
        this.row = r;
        this.col = c;
        constraints = new ArrayList<Constraint>();
        numConstraints = 0;
    }

    //Returns the label of this variable
    public char getLabel(){return label;}

    public void setLabel(char l){label = l;}

    public boolean getZeroStatus(){return nextToZero;}

    public void setZeroStatus(boolean zero){nextToZero = zero;}

    public boolean getWallStatus(){return nextToWall;}

    public void setWallStatus(boolean wall){nextToWall = wall;}

    public boolean getLitStatus(){return lit;}

    public void setLitStatus(boolean illuminated){lit = illuminated;}

    public int getNumConstraints(){return numConstraints;}

    public int getRow(){return row;}

    public int getCol(){return col;}

    //Adds a constraint that affects this variable to its constraint list
    //Is given constraint type object and returns nothing
    public void addConstraint(Constraint cons){
        constraints.add(cons);
        numConstraints = constraints.size();
    }

    public ArrayList<Constraint> getConstraints() {
        return constraints;
    }

    public void removeConstraint(Constraint cons){
        constraints.remove(cons);
        numConstraints = constraints.size();
    }

    //Checks list of constraints and returns true if all variables constraints are met
    //Should be called after every change to the CSP
    public boolean consistent(){
        //Stores if every constraint is true
        boolean pass = true;

        //If the list exists, every constraint is checked for if it is satisfied
        //If the entire list is enumerated, then pass is left at true
        //If one constraint fails, then pass is set to false and the loop breaks
        if(constraints != null){
            for(Constraint con: constraints){
                if(!con.satisfied()){
                    pass = false;
                    break;
                }
            }
        }

        //Returns if the test is passed or not
        return pass;
    }

    public boolean partialConsistent(){
        //Stores if every constraint is true
        boolean pass = true;

        //If the list exists, every constraint is checked for if it is satisfied
        //If the entire list is enumerated, then pass is left at true
        //If one constraint fails, then pass is set to false and the loop breaks
        if(constraints != null){
            for(Constraint con: constraints){
                if(!con.partialSatisfied()){
                    pass = false;
                    break;
                }
            }
        }

        //Returns if the test is passed or not
        return pass;
    }
}