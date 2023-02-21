import java.util.ArrayList;

//Variable Class
//Stores information about a cell in the Light Up Puzzle
//Stores: 
//  --label: the char label fo the cell (either _, b, or a number)
//  --constraints: the list of constraint objects that contain this variable
public class Variable{

    private char label;
    private ArrayList<Constraint> constraints;

    //Constructer takes a char label as input
    public Variable(char label){
        this.label = label;
        constraints = new ArrayList<Constraint>(); 
    }

    //Returns the label of this variable
    public char getLabel(){return label;}

    //Adds a constraint that affects this variable to its constraint list
    //Is given constraint type object and returns nothing
    public void addConstraint(Constraint cons){ constraints.add(cons);}

    //Checks list of constraints and returns true if all variables constraints are met
    //Should be called after every change to the CSP
    public boolean consistent(){
        //TODO: Implement this, just calls all satisfied methods on each element of the list
        return true;
    }
}