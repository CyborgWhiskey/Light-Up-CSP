import java.util.ArrayList;

//Light Constraint
//Constraint for how there can only be one bulb per row and colunm, unless there is a wall in between them
public class lightConstraint extends Constraint{

    //Constructor
    //Takes in a list of vars involved in constraint and stores them
    public lightConstraint(ArrayList<Variable> vars){
        super(vars);
    }

    //Checks if there is any other light bulb in the same row or column variable
    //Returns true if no b is found in any variable, false otherwise
    public boolean satisfied(){
        //Stores if a bulb is found or not
        boolean satisfied = true;

        //Checks if the vars list exists
        if(vars != null){
            //Searches through every cell in vars, if a bulb is found satisfied is set to false and the loop breaks
            for(Variable var: vars){
                if(var.getLabel() == 'b'){
                    satisfied = false;
                    break;
                }
            }
        }

        //Returns true if none of the list contains a b, false otherwise
        return satisfied;
    }
}
