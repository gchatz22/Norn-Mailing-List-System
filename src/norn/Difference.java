package norn;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 * Difference represents an expression that contains all emails in the left expression
 * other than those in the right expression
 *
 */
class Difference implements Expression {
    private final Expression left, right;
    
    // Abstraction function
    //   AF(left, right) = represents the items in the left expression without the ones in the right expression
    //
    // Rep invariant
    //   - holds emails that are in the left expression but not in the right
    //
    // Safety from rep exposure
    //   - all fields are immutable, private and final
    //
    // Thread Safety Argument
    //   - all fields and return types are immutable, therefore
    //     there is no concern for race conditions in regard to mutability

    
    /**
     * 
     * An expression that includes all items from the left expression
     * other than the ones found in the right
     * 
     * @param left list to draw from
     * @param right list to exclude
     */
    public Difference(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    
    private void checkRep(Map<String, Expression> map) {
        SortedSet<String> thisEmails = this.evaluate(map);
        SortedSet<String> emailsLeft = this.left.evaluate(map);
        SortedSet<String> emailsRight = this.right.evaluate(map);
        // for everything in the left, if it is not in the right, make sure we have it
        for(String email : emailsLeft) {
            if(!emailsRight.contains(email)) {
                assert thisEmails.contains(email);
            }
        }
    }
    
    @Override
    public SortedSet<String> evaluate (Map<String, Expression> map){
        SortedSet<String> emailsLeft = this.left.evaluate(map);
        SortedSet<String> emailsRight = this.right.evaluate(map);
        SortedSet<String> ret = new TreeSet<String>();

        for(String email : emailsLeft) {
            if (!emailsRight.contains(email)) {
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
        return new Difference(left.cycle(cycleName, map), right.cycle(cycleName, map));
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
     * 
     * @return right expression
     */
    public Expression getRight() {
        return this.right;
    }

    @Override
    public String toString() {
        return "(" + this.left.toString() + " ! " + this.right.toString() + ")";
    }
    
    @Override
    public boolean equals (Object that) {
        if (!(that instanceof Difference)) {
            return false;
        }
        
        Difference cast = (Difference) that;
        return this.left.equals(cast.left) && this.right.equals(cast.right);
    }

    @Override
    public int hashCode () {
        return this.left.hashCode() + this.right.hashCode();
    }
}
