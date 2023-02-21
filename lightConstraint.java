import java.util.ArrayList;

//Light Constraint
//Constraint for how there can only be one bulb per row and colunm, unless there is a wall in between them
public class lightConstraint extends Constraint{

    //Constructer
    //Takes in a list of vars involved in constraint and stores them
    public lightConstraint(ArrayList<Variable> vars){
        super(vars);
    }

    //Checks if there is any other light bulb in the same row or column variable
    public boolean satisfied(){
        //TODO: replace with actual implementation
        return true;
    }
}
