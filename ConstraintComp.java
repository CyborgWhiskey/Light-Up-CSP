import java.util.*;

public class ConstraintComp implements Comparator<Variable>{
    public int compare(Variable s1, Variable s2)
    {
        if (s1.getNumConstraints() == s2.getNumConstraints())
            return 0;
        else if (s1.getNumConstraints() > s2.getNumConstraints())
            return 1;
        else
            return -1;
    }
}
