package norn;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;


/**
 * 
 * A sequence is an expression followed by another expression, separated by a semi colon
 * that represents two expressions calculated in sequence, with the first first
 * and the second second. This will return the data from the second expression
 * 
 * @author brettallen
 *
 */
class Sequence implements Expression {
    
    private final Expression left, right;
    
    // Abstraction function
    //   AF(left, right) = represents an expression with two expressions, left and right, that
    //                     are evaluated in sequence (left and then right) and the right is returned
    //
    // Rep invariant
    //   - true, rules about what can be on either side are established outside of this class
    //
    // Safety from rep exposure
    //   - all fields are immutable, private and final
    //   - any returned set is unmodifiable and unrelated to this instance
    //
    // Thread Safety Argument
    //   - all fields and return types are immutable, therefore
    //     there is no concern for race conditions in regard to mutability

    /**
     * 
     * Sequence represents two expression that should be calculated in sequence
     * i.e. the first one first and the second one second. It returns the data
     * from the second expression
     * 
     * @param left the first expression to be evaluated
     * @param right the second expression to be evaluated and returned
     */
    public Sequence(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public SortedSet<String> evaluate (Map<String, Expression> map){
        this.left.evaluate(map);
        return this.right.evaluate(map);
    }
    
    @Override
    public Set<String> subLists(Map<String, Expression> map, Set<String> seen, boolean onlyListDefs) {
        Set<String> s = this.left.subLists(map, seen, onlyListDefs);
        s.addAll(this.right.subLists(map, seen, onlyListDefs));
        seen.addAll(s);
        return s;
    }
    
    @Override
    public Expression cycle (String cycleName, Map<String, Expression> map) {
        return new Sequence(left.cycle(cycleName, map), right.cycle(cycleName, map));
    }
    
    /**
     * 
     * Get the first expression to be evaluated
     * 
     * @return the first expression
     */
    public Expression getLeft() {
        return this.left;
    }
    
    /**
     * 
     * Get the second expression to be evaluated
     * 
     * @return the second expression
     */
    public Expression getRight() {
        return this.right;
    }
    
    @Override
    public String toString() {
        return "(" + this.left.toString() + "; " + this.right.toString() + ")";
    }
    
    @Override
    public boolean equals (Object that) {
        if (!(that instanceof Sequence)) {
            return false;
        }
        
        Sequence cast = (Sequence) that;
        return (this.left.equals(cast.left) && this.right.equals(cast.right));
    }

    @Override
    public int hashCode () {
        return this.left.hashCode() + this.right.hashCode();
    }

}
