import java.util.ArrayList;


//Abstract type for constraint
//Used to implement different constraints depending of the search space
public abstract class Constraint {
    
    //List of variables affected by this constraint, provided on initilization
    protected ArrayList<Variable> vars;

    public Constraint(ArrayList<Variable> vars){
        this.vars = vars;
    }

    //Returns true if constraint is met. Must implement for each constraint
    public abstract boolean satisfied();
}
