package norn;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 * Union represents an expression that contains all emails found in either (or both)
 * expressions passed in.
 * 
 * @author brettallen
 *
 */
class Union implements Expression {
    
    // Abstraction function
    //   AF(left, right) = represents the emails that are in either the left, right or both expressions.
    //
    // Rep invariant
    //   - all emails in this expression are in both left, right, or both
    //
    // Safety from rep exposure
    //   - all fields are immutable, private and final.
    //   - any returned set is unmodifiable and unrelated to this instance
    //
    // Thread Safety Argument
    //   - all fields and return types are immutable, therefore
    //     there is no concern for race conditions in regard to mutability
    
    private final Expression left, right;
    
    /**
     * 
     * A list expression that includes emails from either left or right or both
     * 
     * @param left list expression
     * @param right list expression
     */
    public Union(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    
    private void checkRep(Map<String, Expression> map) {
        SortedSet<String> thisEmails = this.evaluate(map);
        Set<String> emailsLeft = this.left.evaluate(map);
        Set<String> emailsRight = this.right.evaluate(map);
        // for everything in the left, if it is in the right, make sure it is in these emails
        for(String email : emailsLeft) {
            assert thisEmails.contains(email);
        }
        for(String email : emailsRight) {
            assert thisEmails.contains(email);
        }
    }
    
    @Override
    public SortedSet<String> evaluate (Map<String, Expression> map){
        Set<String> emailsLeft = new HashSet<String>(this.left.evaluate(map));
        Set<String> emailsRight = new HashSet<String>(this.right.evaluate(map));

        emailsLeft.addAll(emailsRight);
        return Collections.unmodifiableSortedSet(new TreeSet<String>(emailsLeft));
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
        return new Union(left.cycle(cycleName, map), right.cycle(cycleName, map));
    }
    
    /**
     * 
     * @return the left expression
     */
    public Expression getLeft() {
        return this.left;
    }
    
    /**
     * 
     * @return the right expression
     */
    public Expression getRight() {
        return this.right;
    }

    @Override
    public String toString() {
        return "(" + this.left.toString() + ", " + this.right.toString() + ")";
    }
    
    @Override
    public boolean equals (Object that) {
        if (!(that instanceof Union)) {
            return false;
        }
        
        Union cast = (Union) that;
        return (this.left.equals(cast.left) && this.right.equals(cast.right)) || 
               (this.left.equals(cast.right) && this.right.equals(cast.left));
    }

    @Override
    public int hashCode () {
        return this.left.hashCode() + this.right.hashCode();
    }
    
}
