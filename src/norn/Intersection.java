package norn;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * 
 * Intersection represents an expression that contains emails found in both
 * expressions passed in.
 *
 */
class Intersection implements Expression {
    
    private final Expression left, right;

    // Abstraction function
    //   AF(left, right) = represents the items found in both left and right expressions
    //
    // Rep invariant
    //   - holds emails that are found in both expressions passed in
    //
    // Safety from rep exposure
    //   - all fields are immutable, private and final
    //
    // Thread Safety Argument
    //   - all fields and return types are immutable, therefore
    //     there is no concern for race conditions in regard to mutability
    
    
    /**
     * 
     * A list that includes items that are found both in left and right list expressions
     * 
     * @param left email list expression
     * @param right email list expression
     */
    public Intersection(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    
    private void checkRep(Map<String, Expression> map) {
        SortedSet<String> thisEmails = this.evaluate(map);
        Set<String> emailsLeft = this.left.evaluate(map);
        Set<String> emailsRight = this.right.evaluate(map);
        // for everything in the left, if it is in the right, make sure it is in these emails
        for(String email : emailsLeft) {
            if(emailsRight.contains(email)) {
                assert thisEmails.contains(email);
            }
        }
    }
    
    @Override
    public SortedSet<String> evaluate (Map<String, Expression> map){
        Set<String> emailsLeft = new HashSet<String>(this.left.evaluate(map));
        Set<String> emailsRight = new HashSet<String>(this.right.evaluate(map));
        SortedSet<String> ret = new TreeSet<String>();

        for(String email : emailsLeft) {
            if (emailsRight.contains(email)) {
                ret.add(email);
            }
        }
        return Collections.unmodifiableSortedSet(ret);
    }
    
    @Override
    public Set<String> subLists(Map<String, Expression> map, Set<String> seen, boolean onlyListDefs) {
        this.checkRep(map);
        Set<String> s = this.left.subLists(map, seen, onlyListDefs);
        s.addAll(this.right.subLists(map, seen, onlyListDefs));
        seen.addAll(s);
        this.checkRep(map);
        return s;
    }
    
    @Override
    public Expression cycle (String cycleName, Map<String, Expression> map) {
        this.checkRep(map);
        return new Intersection(left.cycle(cycleName, map), right.cycle(cycleName, map));
    }
    
    /**
     * 
     * @return left expression
     */
    public Expression getLeft() {
        return this.left;
    }
    
    /**
     * 
     * @return right expression
     */
    public Expression getRight() {
        return this.right;
    }

    @Override
    public String toString() {
        return "(" + this.left.toString() + " * " + this.right.toString() + ")";
    }
    
    @Override
    public boolean equals (Object that) {
        if (!(that instanceof Intersection)) {
            return false;
        }
        
        Intersection cast = (Intersection) that;
        return this.left.equals(cast.left) && this.right.equals(cast.right);
    }

    @Override
    public int hashCode () {
        return this.left.hashCode() + this.right.hashCode();
    }

}
