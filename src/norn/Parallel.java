package norn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 * Expression that represents two expressions being evaluated
 * in parallel threads.
 * 
 * @author brettallen
 *
 */
class Parallel implements Expression {
    
    private final Expression left, right;
    
    
    // Abstraction function
    //   AF(left, right) = represents two expressions evaluated at the same time in two different threads
    //
    // Rep invariant
    //   - true, the expressions can be anything, the rules of cycles and such are not up to this
    //     class
    //
    // Safety from rep exposure
    //   - all fields are immutable, private and final
    //   - any returned sets are unmodifiable and unrelated to this instance
    //
    // Thread Safety Argument
    //   - we check if the threads rely on each other
    //     and if so, we throw an "Invalid Pipe!!!" error
    //   - now that we know threads are not reliant on each other
    //     we can now make the argument that there are no race conditions
    //     as the threads are declaring mailing lists and creating data separately
    //     and when they read data, they are unable to modify it
    //   - both threads must complete before they move on to the next
    //     expression operation, thus we do not have any race conditions
    
    /**
     * 
     * Represents an expression with two sub expressions that
     * should be evaluated in parallel threads
     * 
     * @param left one of the threads to be evaluated
     * @param right the other thread to be evaluated
     */
    public Parallel(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public SortedSet<String> evaluate (Map<String, Expression> map){
        Set<String> leftDefinedLists = this.left.subLists(map, new HashSet<String>(), true);
        Set<String> rightDefinedLists = this.right.subLists(map, new HashSet<String>(), true);
        Set<String> leftSubLists = this.left.subLists(map, new HashSet<String>(), false);
        Set<String> rightSubLists = this.right.subLists(map, new HashSet<String>(), false);

        leftSubLists.retainAll(rightDefinedLists);
        rightSubLists.retainAll(leftDefinedLists);
        
        
        if (leftSubLists.size()>0 || rightSubLists.size()>0) {
            throw new RuntimeException("Invalid Pipe!!!");
        }
        
        // if valid pipe
        
        Thread t1 = new Thread(() -> {
            this.left.evaluate(map);
        });
        
        Thread t2 = new Thread(() -> {
            this.right.evaluate(map);
        });
        
        t1.start();
        t2.start();
        
        try {
          t1.join();
          t2.join();
      } catch (InterruptedException e1) {
          e1.printStackTrace();
      }
        
        return new TreeSet<String>();
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
        return new Parallel(left.cycle(cycleName, map), right.cycle(cycleName, map));
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
        return "(" + this.left.toString() + " | " + this.right.toString();
    }
    
    @Override
    public boolean equals (Object that) {
        if (!(that instanceof Parallel)) {
            return false;
        }
        
        Parallel cast = (Parallel) that;
        return (this.left.equals(cast.left) && this.right.equals(cast.right));
    }

    @Override
    public int hashCode () {
        return this.left.hashCode() + this.right.hashCode();
    }

}
