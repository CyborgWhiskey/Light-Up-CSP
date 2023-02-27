import java.util.*;

public class ConstrainingComp implements Comparator<Variable>{
    public int compare(Variable s1, Variable s2)
    {
        if (s1.getPossibleConstraint() == s2.getPossibleConstraint())
            return 0;
        else if (s1.getPossibleConstraint() > s2.getPossibleConstraint())
            return 1;
        else
            return -1;
    }
}
