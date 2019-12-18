package norn;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import edu.mit.eecs.parserlib.UnableToParseException;


/**
 * An immutable data type representing a email list expression, as defined
 * in the final project handout.
 */
public interface Expression {

    /**
     * Datatype definition
     * 
     * 
     *  Expression = Email(address: String) + MailingList(name: String) + Intersection(left: Expression, right: Expression)
     *  + Difference(left: Expression, right: Expression) + Union(left: Expression, right: Expression) + 
     *  ListDefinition(name: String, expr: Expression) + Parallel(left: Expression, right: Expression) + Sequence(left: Expression, right: Expression)) +
     *  EmptyEmailSet()
     * 
     */
    
    
     /**
     * Parse an expression.
     * 
     * @param input expression to parse, as defined in the project handout
     * @return expression AST for the input
     * @throws IllegalArgumentException if the expression is syntactically invalid.
     */
    public static Expression parse (String input) throws IllegalArgumentException {
        try {
            return ExpressionParser.parse(input);            
        } catch (UnableToParseException e) {
            throw new IllegalArgumentException();
        }
    }


    /**
     * Evaluates a parsed expression to the set of emails it corresponds to
     * 
     * @param map map that maps mailing lists names to their expression of the this mailing lists system instance
     * @return set of string email names an expression instance corresponds to
     */
    public SortedSet<String> evaluate (Map<String, Expression> map);
    
    /**
     * 
     * @param map map that maps mailing lists names to their expression of the this mailing lists system instance
     * @param seen set of mailing list names to keep track of the mailing lists I have seen up to this point to avoid infinite loops 
     * @param onlyListDefs boolean value that changes the content of the returned set of emails
     * @return when onlyListDefs true, returns set of the mailing list names that are defined in this expression
     *                     when onlyListDefs false returns all the mailing lists that appear in this expression (including nested mailing lists
     *                     other lists) 
     */
    public Set<String> subLists (Map<String, Expression> map, Set<String> seen, boolean onlyListDefs);
    
    /**
     * Checks for cycle of the same mailing list in the expression (e.g a = b@mit.edu, a = a, c@mit,edu) and if there is one,
     * in place of the cycle list in the expression replaces that list with its own simpler expression so that there is no cycle 
     * 
     * @param cycleName string name of the mailing list name that we are checking for cycle in this expression
     * @param map map that maps mailing lists names to their expression of the this mailing lists system instance
     * @return new expression which is the same the one as before, but in place of the cycle list there is its simpler expression 
     */
    public Expression cycle (String cycleName, Map<String, Expression> map);
    
    /**
     * @return a parsable representation of this expression, such that
     *         for all e:Expression, e.equals(Expression.parse(e.toString()))
     */
    @Override 
    public String toString();

    /**
     * @param that any object
     * @return true if and only if this and that are structurally-equal
     *         Expressions, as defined in the project handout
     */
    @Override
    public boolean equals(Object that);
    
    /**
     * @return hash code value consistent with the equals() definition of structural
     *         equality, such that for all e1,e2:Expression,
     *             e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();
    
    
    
    
}
