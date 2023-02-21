import java.util.ArrayList;

//Variable Class
//Stores information about a cell in the Light Up Puzzle
//Stores: 
//  --label: the char label fo the cell (either _, b, or a number)
//  --constraints: the list of constraint objects that contain this variable
public class Variable{

    private char label;
    private ArrayList<Constraint> constraints;

    //Constructor takes a char label as input
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
}