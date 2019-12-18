package norn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * 
 * The empty email set represents a static, unchanging mailing list
 * that contains no emails.
 *
 */
public class EmptyEmailSet implements Expression {
    
    private static final Expression EMPTY = new EmptyEmailSet();
    
    // Abstraction function
    //   AF() = an empty email set
    //   - there is nothing in the AF argument because this is essentially a static, singular instance
    //
    // Rep invariant
    //   true
    //
    // Safety from rep exposure
    //   - returns brand new empty sets in evaluate and subLists
    //   - all variables are private and final
    //
    // Thread Safety Argument
    //   safety from rep exposure results in an unchanging static class,
    //   therefore we do not need to worry about its thread safety
    //

    
    /**
     * 
     * Expression that represents the empty set of emails
     * 
     * @return the empty Expression
     */
    public static Expression getEmpty() {
        return EMPTY;
    }

    
    // empty constructor
    private EmptyEmailSet() {
    }
    

    @Override
    public SortedSet<String> evaluate (Map<String, Expression> map){
        return new TreeSet<String>(Set.of());
    }
    
    @Override
    public Set<String> subLists(Map<String, Expression> map, Set<String> seen, boolean onlyListDefs) {
        return new HashSet<String>();
    }

    
    @Override
    public Expression cycle (String cycleName, Map<String, Expression> map) {
        return this;
    }
    
    @Override
    public String toString() {
        return "";
    }
    
}
