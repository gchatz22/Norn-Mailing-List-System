package norn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * 
 * Email represents a single email address
 * 
 *
 */
class Email implements Expression {

    private final String address;
    
    // Abstraction function
    //   AF(address) = an email address
    //
    // Rep invariant
    //   true
    //   - the argument for this is that the email is really just a fancy string
    //
    // Safety from rep exposure
    //   - all fields are immutable, private, and final
    //
    // Thread Safety Argument
    //   - single immutable field
    //   - never return anything mutable
    //
    
 
    /**
     * 
     * A singular email address
     * 
     * @param address the name of the email
     */
    public Email(String address) {
        this.address = address;
    }
    
    @Override
    public SortedSet<String> evaluate (Map<String, Expression> map){
        return new TreeSet<String>(Set.of(this.toString()));
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
        return this.address;
    }
    
    @Override
    public boolean equals (Object that) {
        if (!(that instanceof Email)) {
            return false;
        }
        
        Email cast = (Email) that;
        return this.address.equals(cast.address);
    }

    @Override
    public int hashCode () {
        return this.address.hashCode();
    }
    
}
