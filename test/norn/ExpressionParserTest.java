package norn;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import edu.mit.eecs.parserlib.UnableToParseException;

/**
 * 
 * Tests for Expression ADT
 * 
 * @author the team
 *
 */ 
public class ExpressionParserTest {
    /**
     * 
     * Partitions
     * 
     *
     * equals, toString, hashCode, getEmails:
     *   appear 0, 1, >1
     *       - union
     *       - difference
     *       - intersection
     *       - combinations
     *      
     *   same expression different order
     * 
     *   emails:
     *       - 0
     *       - 1   
     *       - >1
     *       - invalid
     *       - duplicate
     *   
     *   backtracking
     *   
     *   loop exceptions
     *      
     */
//           
//    Precedence:
//        "*" before "," and vice versa with parentheses to set precedence
//        "*" before "!" and vice versa with parentheses to set precedence
//        "," before "!" and vice versa with parentheses to set precedence
//        ";" before "|" and vice versa with parentheses to set precedence
//        "*" before "=" and vice versa with parentheses to set precedence
//        "=" before "|" and vice versa with parentheses to set precedence
//        "*" before "|" and vice versa with parentheses to set precedence
//        "*" before "=" and vice versa with parentheses to set precedence
//        "*" before ";" and vice versa with parentheses to set precedence
//        "," before "|" and vice versa with parentheses to set precedence
//        "," before "=" and vice versa with parentheses to set precedence
//        "," before ";" and vice versa with parentheses to set precedence
//        "!" before "|" and vice versa with parentheses to set precedence
//        "!" before "=" and vice versa with parentheses to set precedence
//        "!" before ";" and vice versa with parentheses to set precedence

    
    
    
    // equals() tests
    
    // one email
    @Test
    public void testOneEmailNormalEquals() {
        try {
            Expression one1 = ExpressionParser.parse("brett@gmail.com");
            Expression one2 = new Email("brett@gmail.com");
            assert testEquals(one1, one2);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // 2 emails, union out of order, plus sign, one space
    @Test
    public void testTwoEmailsUnionEquals() {
        try {
            Expression one1 = ExpressionParser.parse("brett@gmail.com,ryan+@gmail");
            Expression one2 = new Union(new Email("brett@gmail.com"), new Email("ryan+@gmail"));
            assert testEquals(one1, one2);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // >2 emails, difference intersection union
    @Test
    public void testManyEmailsDIUEquals() {
        try {
            Expression one1 = ExpressionParser.parse("((brett@gmail.com,ryan+@gmail)!brett@gmail.com)*ryan+@gmail");
            Expression brett = new Email("brett@gmail.com");
            Expression ryan = new Email("ryan+@gmail");
            Expression one2 = new Intersection(new Difference(new Union(brett, ryan), brett), ryan);
            assert testEquals(one1, one2);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    
    
    
    // some tests for list definitions
    @Test
    public void testOneListDefinitionEmails() {
        try {
            Expression assign = ExpressionParser.parse("a=brett@gmail.com");
            Expression assign1 = new ListDefinition("a", new Email("brett@gmail.com"));
            assert testEquals(assign, assign1);
            ExpressionParser.parse("a = ");
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    @Test
    public void testManyDefinitionEmails() {
        try {
            Expression assign = ExpressionParser.parse("a=((brett@gmail.com, ryan+@gmail)!brett@gmail.com)*ryan+@gmail");
            Expression brett = new Email("brett@gmail.com");
            Expression ryan = new Email("ryan+@gmail");
            Expression assign1 = new ListDefinition("a", new Intersection(new Difference(new Union(brett, ryan), brett), ryan));
            assert testEquals(assign, assign1);
            ExpressionParser.parse("a = ");
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // some tests for sequence and list definitions
    
    // sequence, list definition, reassignment
    @Test
    public void testSequenceDefineEmails() {
        try {
            Expression a = ExpressionParser.parse("a=brett@gmail.com; a = ryan+@gmail; a");
            Expression a1 = new Sequence(new Sequence(new ListDefinition("a", new Email("brett@gmail.com")), new ListDefinition("a", new Email("ryan+@gmail"))), new MailingList("a"));
            assert testEquals(a, a1);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // test empty listname
    @Test
    public void testEmptyListnameEmails() {
        try {
            Expression empty = ExpressionParser.parse("abc");
            Expression empty1 = new MailingList("abc");
            assert testEquals(empty, empty1);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // test specification examples
    
    @Test
    public void testRoom() {
        try {
            Expression xab = ExpressionParser.parse("(room=alice@mit.edu)*room");
            Expression xab1 = new Intersection(new ListDefinition("room", new Email("alice@mit.edu")), new MailingList("room"));
            assert testEquals(xab, xab1);
        } catch (UnableToParseException e) {
            assert false;
        }
    }

    @Test
    public void testABC() {
        try {
            Expression abc = ExpressionParser.parse("a=b=c");
            Expression abc1 = new ListDefinition("a", new ListDefinition("b", new MailingList("c")));
            assert testEquals(abc, abc1);
        } catch (UnableToParseException e) {
            e.printStackTrace();
        }
    }
    
    //tests "*" before "," to set precedence 
    @Test
    public void testPrecedenceAsterixComma1() {
        try {
            Expression a = ExpressionParser.parse("a@email * b@email,c@email");
            Expression a1 = new Union(new Intersection(new Email("a@email"), new Email("b@email")), new Email("c@email"));
            assert testEquals(a, a1);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests "," before "*" to set precedence 
    @Test
    public void testPrecedenceAsterixComma2() {
        try {
            Expression a = ExpressionParser.parse("(a@email,c@email) * (b@email,c@email)");
            Expression a1 = new Intersection(new Union(new Email("a@email"), new Email("c@email")), new Union(new Email("b@email"), new Email("c@email")));
            assert testEquals(a, a1);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests "*" before "!" to set precedence 
    @Test
    public void testPrecedenceAsterixExclamation1() {
        try {
            Expression a = ExpressionParser.parse("(a@email,b@email) * b@email!c@email");
            Expression a1 = new Difference(new Intersection(new Union(new Email("a@email"), new Email("b@email")), new Email("b@email")), new Email("c@email"));
            assert testEquals(a, a1);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests "!" before "*" to set precedence 
    @Test
    public void testPrecedenceAsterixExclamation2() {
        try {
            Expression a = ExpressionParser.parse("(a@email,b@email) * ((b@email,c@email)!c@email)");
            Expression a1 = new Intersection(new Union(new Email("a@email"), new Email("b@email")), new Difference(new Union(new Email("b@email"), new Email("c@email")), new Email("c@email")));
            assert testEquals(a, a1);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests "," before "!" to set precedence 
    @Test
    public void testPrecedenceCommaExclamation1() {
        try {
            Expression a = ExpressionParser.parse("a@email,b@email ! b@email");
            Expression a1 = new Union(new Email("a@email"), new Difference(new Email("b@email"), new Email("b@email")));
            assert testEquals(a, a1);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    
    //tests ";" before "|" to set precedence 
    // tests "=" before "|" with parentheses to set precedence
    // tests "," before "|" to set precedence
    @Test
    public void testPrecedenceSemicolonPipe() {
        try {
            Expression a = ExpressionParser.parse("x = a@email,b@email ; x | b@email");
            Expression a1 = new Parallel(new Sequence(new ListDefinition("x", new Union(new Email("a@email"), new Email("b@email"))), new MailingList("x")), new Email("b@email"));
            assert testEquals(a, a1);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests integration of many precedences
    @Test
    public void testPipe() {
        try {
            Expression a = ExpressionParser.parse("(x = a@mit.edu | y = b@mit.edu) , x");
            Expression a1 = new Union(new Parallel(new ListDefinition("x", new Email("a@mit.edu")), new ListDefinition("y", new Email("b@mit.edu"))), new MailingList("x"));
            assert testEquals(a, a1);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // test pipe gives error
    @Test
    public void testPipeLoop() throws UnableToParseException {
        try {
            Expression a = ExpressionParser.parse("x = a@mit.edu | y = x, b@mit.edu");
            Main.parseFactory(a);
            assert false;
        } catch (RuntimeException e) {
            assert true;
        }
    }
    
    
    //tests loop exceptions many
    @Test
    public void testLoopExceptionMany() throws UnableToParseException {
        try {
            Expression a = ExpressionParser.parse("a= ; b= ; c= ; a=b; b=c; c=a");
            Main.parseFactory(a);
            assert false;
        } catch (RuntimeException e) {
            assert true;
        }
    } 

    //tests backtracking
    @Test 
    public void testBacktracking() {
        try {
            Expression a = ExpressionParser.parse("list = (a@email,c@email); list2= ; list");
            Set<String> expected = Set.of("a@email","c@email");
            assert testSameSetEmails(Set.of(a), expected);
            
            try {
                Expression a2 = ExpressionParser.parse("list = ");
                Set<String> expected2 = Set.of();
                assert testSameSetEmails(Set.of(a2), expected2);
                
                try {
                    Expression a3 = ExpressionParser.parse("list = list2,c@email");
                    Set<String> expected3 = Set.of("c@email");
                    assert testSameSetEmails(Set.of(a3), expected3);
                    ExpressionParser.parse("list = ");
                    ExpressionParser.parse("list2 = ");
                } catch (UnableToParseException e) {
                    assert false;
                }
                
            } catch (UnableToParseException e) {
                assert false;
            }
            
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    
    private static boolean testEquals(Expression e1, Expression e2) {
        return e1.equals(e2);
    }
    
    private static boolean testSameSetEmails(Set<Expression> e, Set<String> s) {
        
        Set<String> exprEmails = new HashSet<String>();
        for (Expression i : e) {
            exprEmails.addAll(Main.parseFactory(i));            
        }

        if(exprEmails.size() != s.size()) {
            return false;
        }
        for(String email : s) {
            if(!exprEmails.contains(email)) {
                return false;
            }
        }
        return true;
    }
    
    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }
    
}
