package norn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * 
 * Mailing list represents an expression that has a defined variable name
 * that is assigned to a designated expression, or if it is not assigned,
 * it is represented by the empty expression.
 * 
 * @author brettallen
 */
class MailingList implements Expression {
    
    private final String name;
    
    // Abstraction function
    //   AF(name) = a mailing list named name
    //
    // Rep invariant
    //   true
    //
    // Safety from rep exposure
    //   all fields are immutable, private and final.
    //
    // Thread Safety Argument
    //   - all fields and return types are immutable, therefore
    //     there is no concern for race conditions in regard to mutability
    //

    /**
     * 
     * A mailing list with a designated name
     * 
     * @param name the name of the mailing list
     */
    public MailingList(String name) {
        this.name = name;
    }
    
    @Override
    public SortedSet<String> evaluate (Map<String, Expression> map){
        if (map.containsKey(name)) {
            return map.get(name).evaluate(map);
        } else {
            map.put(name, EmptyEmailSet.getEmpty());
            return new TreeSet<String>(Set.of());
        }
    }
    
    @Override
    public Set<String> subLists(Map<String, Expression> map, Set<String> seen, boolean onlyListDefs) {
        if (onlyListDefs) {
            return new HashSet<String>();
        }
        
        if (!seen.contains(name)){
            Set<String> s = map.getOrDefault(name, EmptyEmailSet.getEmpty()).subLists(map, seen, onlyListDefs);                
            s.add(name);
            return s;
        }
        Set<String> s = new HashSet<String>();
        s.add(name);
        return s;
    }
    
    @Override
    public Expression cycle (String cycleName, Map<String, Expression> map) {
        Set<String> dependentLists = map.getOrDefault(this.name, EmptyEmailSet.getEmpty()).subLists(map, new HashSet<String>(), false);
        
        if (dependentLists.contains(cycleName) && !this.name.equals(cycleName)){
            throw new RuntimeException("cycle detected");
        }
        
        if (this.name.equals(cycleName)) {
            return map.getOrDefault(this.name, EmptyEmailSet.getEmpty());
        }
        return this;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public boolean equals (Object that) {
        if (!(that instanceof MailingList)) {
            return false;
        }
        
        MailingList cast = (MailingList) that;
        return this.name.equals(cast.name);
    }
    
    /**
     * 
     * Get the name of this mailing list
     * 
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    @Override
    public int hashCode () {
        return this.name.hashCode();
    }
    
}
