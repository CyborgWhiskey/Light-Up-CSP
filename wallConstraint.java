import java.util.ArrayList;

//Wall Constraint
//Constraint for each wall that there must be number of bulbs equal to the walls number adjacent too it
public class wallConstraint extends Constraint{

    //The number of bulbs to check for
    private int num;

    //Constructor
    //Takes in a list of vars involved in constraint and stores them
    //Also takes in the number of bulbs to check for and stores it
    public wallConstraint(ArrayList<Variable> vars, char num){
        super(vars);
        this.num = Character.getNumericValue(num);
    }

    //Checks if a wall gas the correct number of bulbs adjcent to it
    public boolean satisfied(){
        int count = 0;

        //Checks if the vars list exists
        if(vars != null){
            //Sums the number of b occurences
            for(Variable var: vars){
                if(var.getLabel() == 'b'){count++;}
            }   
        }

        //Returns if the number of b's is equal to the number of the wall
        return count == num;
    }
    
}
