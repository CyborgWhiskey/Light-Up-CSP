import java.util.ArrayList;

//Wall Constraint
//Constraint for each wall that there must be number of bulbs equal to the walls number adjacent too it
public class wallConstraint extends Constraint{

    //Constructer
    //Takes in a list of vars involved in constraint and stores them
    public wallConstraint(ArrayList<Variable> vars){super(vars);}

    //Checks if a wall gas the correct number of bulbs adjcent to it
    public boolean satisfied(){
        //TODO: replace with actual implementation
        return true;
    }
    
}
