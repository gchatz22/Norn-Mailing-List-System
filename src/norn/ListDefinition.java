package norn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 * 
 * An expression that represents defining a mailing list
 * to an expression
 * 
 * @author brettallen
 *
 */
import java.util.SortedSet;

/**
 * 
 * A list definition is an assignment of a mailing list name to an expression
 * 
 * @author brettallen
 *
 */
class ListDefinition implements Expression{
    
    // Abstraction function
    //   AF(name, expr) = represents a mailing list with the name name and assigned
    //                     to the expression expr
    //
    // Rep invariant
    //   - true, because the name can be anything and assigned to any expression
    //
    // Safety from rep exposure
    //   - all fields are immutable, private and final
    //   - any returned sets are new/copies and will not affect this expression
    //
    // Thread Safety Argument
    //   - all fields and return types are immutable, therefore
    //     there is no concern for race conditions in regard to mutability
    
    private final String name;
    private final Expression expr;
    
    /**
     * 
     * An expression that represents an assignment of a
     * mailing list name to a given expression
     * 
     * @param name the name of the mailing list to assign
     * @param expr what to assign to the mailing list
     */
    public ListDefinition (String name, Expression expr) {
        this.name = name;
        this.expr = expr;
    }
    
    
    /**
     * 
     * Gives the name of the mailing list that is 
     * being assigned
     * 
     * @return the name
     */
    public String getName () {
        return this.name;
    }
    
    /**
     * 
     * Gives the expression that is assigned to this mailing list
     * 
     * @return the expression
     */
    public Expression getExpr () {
        return this.expr;
    }
    
    /**
     * 
     * To deal with the edge case of nested List Definitions (i.e. a=b=c)
     * mailing lists that are assigned to list definitions are assigned
     * to the emails that come from that single list definition instance, and no future
     * edits.
     * 
     * Therefore, if c = brett@gmail, and a=b=c, a, b, and c are all brett@gmail
     * However, if we enter b = giannis@gmail, this should not change a or c, but only b
     * Thus, b is assigned to giannis@gmail, and a and c are both brett@gmail
     * 
     */
    @Override
    public SortedSet<String> evaluate (Map<String, Expression> map){
        Expression a = this.expr.cycle(name, map);

        SortedSet<String> s = this.expr.evaluate(map);
        map.put(name, a);
        return s;
    }
    
    @Override
    public Set<String> subLists(Map<String, Expression> map, Set<String> seen, boolean onlyListDefs) {
        if (onlyListDefs) {
            Set<String> a = new HashSet<String>();
            a.add(name);
            a.addAll(this.expr.subLists(map, seen, onlyListDefs));
            return a;
        }

        if (!seen.contains(name)) {
            Set<String> a = new HashSet<String>();
            a.add(name);
            a.addAll(this.expr.subLists(map, seen, onlyListDefs));
            return a;
        }
        return new HashSet<String>();
    }

    @Override
    public Expression cycle (String cycleName, Map<String, Expression> map) {
        return new ListDefinition(this.name, expr.cycle(cycleName, map));
    }

    @Override
    public String toString() {
        return "(" + this.name + " = " + this.expr.toString() + ")";
    }
    
    @Override
    public boolean equals (Object that) {
        if (!(that instanceof ListDefinition)) {
            return false;
        }
        
        ListDefinition cast = (ListDefinition) that;
        return (this.name.equals(cast.name) && this.expr.equals(cast.expr));
    }

    @Override
    public int hashCode () {
        return this.name.hashCode() + this.expr.hashCode();
    }

}
