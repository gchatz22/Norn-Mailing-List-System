package norn;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
public class ExpressionADTTest {
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
    
//    Set operators:
//        union 
//            doesn't occur, occurs once, occurs many
//            of 1x1, >1x1, 1x>1
//
//        difference 
//            doesn't occur, occurs once, occurs many
//            of 1x1, >1x1, 1x>1
//            result size is 0, 1, >1
//
//        intersection 
//            doesn't occur, occurs once, occurs many
//            of 1x1, >1x1, 1x>1
//            results size is 0, 1, >1
//
//
//    List definitions:
//        size is 1, >1
//        test for a=(b=c) examples
//
//
//    Sequence:
//        no semicolons, 1 semicolon, >1 semicolons
//        result is length 0, 1, >1
//        invalid sequence, unknown list name. e.g. (a=x@mit.edu)*b
//
//
//    Parallel:
//        0 pipes, 1 pipe, >1 pipes
//        correct evaluation, incorrect evaluation (unknown list name)
//
//
//    Whitespace:
//        whitespace occurrence 0, 1, >1 before and after ","
//        whitespace occurrence 0, 1, >1 before and after "!"
//        whitespace occurrence 0, 1, >1 before and after "*"
//        whitespace occurrence 0, 1, >1 before and after "="
//        whitespace occurrence 0, 1, >1 before and after ";"
//        whitespace occurrence 0, 1, >1 before and after "|"
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


    
    // tests union occurs once, 1x1
    @Test
    public void testUnion1x1() {
        try {
            Expression one1 = ExpressionParser.parse("a@mit,b@mit");
            Set<String> expected = Set.of("a@mit", "b@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests union occurs twice, >1x1
    @Test
    public void testUnionG1x1() {
        try {
            Expression one1 = ExpressionParser.parse("(a@mit,c@mit),b@mit");
            Set<String> expected = Set.of("a@mit", "b@mit", "c@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests union occurs twice, 1x>1
    @Test
    public void testUnion1xG1() {
        try {
            Expression one1 = ExpressionParser.parse("a@mit,(c@mit,b@mit)");
            Set<String> expected = Set.of("a@mit", "b@mit", "c@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests union occurs thrice
    @Test
    public void testUnionThrice() {
        try {
            Expression one1 = ExpressionParser.parse("(a@mit,c@mit),b@mit, e@mit");
            Set<String> expected = Set.of("a@mit", "b@mit", "c@mit", "e@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    

    
    // tests difference occurs once 1x1
    @Test
    public void testDiffOnce1x1() {
        try {
            Expression one1 = ExpressionParser.parse("a@mit!b@mit");
            Set<String> expected = Set.of("a@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
  
    // tests difference occurs once >1x1
    @Test
    public void testDiffOnceG1x1() {
        try {
            Expression one1 = ExpressionParser.parse("(a@mit,b@mit)!b@mit");
            Set<String> expected = Set.of("a@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests difference occurs once 1x>1
    @Test
    public void testDiffOnce1xG1() {
        try {
            Expression one1 = ExpressionParser.parse("a@mit!(b@mit,a@mit)");
            Set<String> expected = Set.of();
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests difference occurs twice
    @Test
    public void testDiffTwice() {
        try {
            Expression one1 = ExpressionParser.parse("((a@mit,b@mit,c@mit)!a@mit!b@mit)");
            Set<String> expected = Set.of("c@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests difference occurs twice
    @Test
    public void testDiffTwice2() {
        try {
            Expression one1 = ExpressionParser.parse("((a@mit,b@mit,c@mit)!(a@mit!b@mit))");
            Set<String> expected = Set.of("c@mit", "b@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    
    // tests difference occurs thrice
    @Test
    public void testDiffThrice() {
        try {
            Expression one1 = ExpressionParser.parse("((a@mit,b@mit,c@mit)!a@mit!b@mit!c@mit)");
            Set<String> expected = Set.of();
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests difference occurs thrice
    @Test
    public void testDiffThrice2() {
        try {
            Expression one1 = ExpressionParser.parse("((a@mit,b@mit,c@mit)!(a@mit!b@mit!c@mit))");
            Set<String> expected = Set.of("b@mit", "c@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests intersection once 1x1
    @Test
    public void testIntersectionOnce1x1() {
        try {
            Expression one1 = ExpressionParser.parse("a@mit*b@mit");
            Set<String> expected = Set.of();
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests intersection once >1x1
    @Test
    public void testIntersectionOnceG1x1() {
        try {
            Expression one1 = ExpressionParser.parse("(a@mit,b@mit)*b@mit");
            Set<String> expected = Set.of("b@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests intersection once 1x>1
    @Test
    public void testIntersectionOnce1xG1() {
        try {
            Expression one1 = ExpressionParser.parse("a@mit*(b@mit,a@mit)");
            Set<String> expected = Set.of("a@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests intersection once 1x1
    @Test
    public void testIntersectionTwice() {
        try {
            Expression one1 = ExpressionParser.parse("a@mit*(b@mit,a@mit)*a@mit");
            Set<String> expected = Set.of("a@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests intersection twice 1x1
    @Test
    public void testIntersectionTwice2() {
        try {
            Expression one1 = ExpressionParser.parse("a@mit*(b@mit,a@mit*a@mit)");
            Set<String> expected = Set.of("a@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests intersection thrice 1x1
    @Test
    public void testIntersectionThrice() {
        try {
            Expression one1 = ExpressionParser.parse("a@mit*(b@mit,a@mit)*a@mit*b@mit");
            Set<String> expected = Set.of();
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests list definitions 1
    @Test
    public void testListDefinitionSize1() {
        try {
            Expression one1 = ExpressionParser.parse("list1 = a@mit,b@mit; list1");
            Set<String> expected = Set.of("a@mit", "b@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests list definitions >1
    @Test
    public void testListDefinitionSizeG1() {
        try {
            Expression one1 = ExpressionParser.parse("list1 = a@mit,b@mit; c=(a=list1)");
            Set<String> expected = Set.of("a@mit", "b@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }

    
    // tests list sequences 1
    @Test
    public void testSequencesOne() {
        try {
            Expression one1 = ExpressionParser.parse("a = a@mit; a");
            Set<String> expected = Set.of("a@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests list sequences >1
    @Test
    public void testSequencesMany() {
        try {
            Expression one1 = ExpressionParser.parse("a = a@mit; b=b@mit;a,b");
            Set<String> expected = Set.of("a@mit", "b@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests list sequences >1
    @Test
    public void testSequencesManyUnknown() {
        try {
            Expression one1 = ExpressionParser.parse("a = a@mit; b=b@mit;d,x");
            Set<String> expected = Set.of();
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }

    // tests list sequences 1, unknown list name
    @Test
    public void testParallel1False() {
        try {
            Expression one1 = ExpressionParser.parse("a=b=c | b=d@mit");
            Main.parseFactory(one1);
//            System.out.println(one1.subLists(new HashMap<String, Expression>(), new HashSet<String>(),  false));
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }
    
    // tests list sequences 1, known list name
    @Test
    public void testParallel1True() {
        try {
            Expression one1 = ExpressionParser.parse("a=b | b=d@mit");
            Main.parseFactory(one1);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }
    
    // tests list sequences >1
    @Test
    public void testParallel1True2() {
        try {
            Expression one1 = ExpressionParser.parse("a=b=c | (b=d@mit|c=x@mit)");
            Main.parseFactory(one1);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }
    
//  Whitespace:
//  whitespace occurrence 0, 1, >1 before and after ","
//  whitespace occurrence 0, 1, >1 before and after "!"
//  whitespace occurrence 0, 1, >1 before and after "*"
//  whitespace occurrence 0, 1, >1 before and after "="
//  whitespace occurrence 0, 1, >1 before and after ";"
//  whitespace occurrence 0, 1, >1 before and after "|"
    
    
    // tests list whitespace comma
    @Test
    public void testWhitespaceComma1() {
        try {
            Expression one1 = ExpressionParser.parse("x@mit, y@mit");
            Set<String> expected = Set.of("x@mit", "y@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests list whitespace comma
    @Test
    public void testWhitespaceComma2() {
        try {
            Expression one1 = ExpressionParser.parse("x@mit,y@mit");
            Set<String> expected = Set.of("x@mit", "y@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests list whitespace comma
    @Test
    public void testWhitespaceComma3() {
        try {
            Expression one1 = ExpressionParser.parse("x@mit  ,  y@mit");
            Set<String> expected = Set.of("x@mit", "y@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests list whitespace asterix
    @Test
    public void testWhitespaceAsterix1() {
        try {
            Expression one1 = ExpressionParser.parse("x@mit*y@mit");
            Set<String> expected = Set.of();
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests list whitespace asterix
    @Test
    public void testWhitespaceAsterix2() {
        try {
            Expression one1 = ExpressionParser.parse("x@mit * y@mit");
            Set<String> expected = Set.of();
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests list whitespace asterix
    @Test
    public void testWhitespaceAsterix3() {
        try { 
            Expression one1 = ExpressionParser.parse("x@mit    *     y@mit");
            Set<String> expected = Set.of();
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    
    // tests list whitespace exlamation
    @Test
    public void testWhitespaceExlamation1() {
        try { 
            Expression one1 = ExpressionParser.parse("x@mit!y@mit");
            Set<String> expected = Set.of("x@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests list whitespace exlamation
    @Test
    public void testWhitespaceExlamation2() {
        try { 
            Expression one1 = ExpressionParser.parse("x@mit ! y@mit");
            Set<String> expected = Set.of("x@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests list whitespace exlamation
    @Test
    public void testWhitespaceExlamation3() {
        try { 
            Expression one1 = ExpressionParser.parse("x@mit    !      y@mit");
            Set<String> expected = Set.of("x@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    
    // tests list whitespace equal
    @Test
    public void testWhitespaceEqual1() {
        try { 
            Expression one1 = ExpressionParser.parse("a=y@mit");
            Set<String> expected = Set.of("y@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    
    // tests list whitespace equal
    @Test
    public void testWhitespaceEqual2() {
        try { 
            Expression one1 = ExpressionParser.parse("a = y@mit");
            Set<String> expected = Set.of("y@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests list whitespace equal
    @Test
    public void testWhitespaceEqual3() {
        try { 
            Expression one1 = ExpressionParser.parse("a     =          y@mit");
            Set<String> expected = Set.of("y@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests list whitespace semicolon
    @Test
    public void testWhitespaceSemicolon1() {
        try { 
            Expression one1 = ExpressionParser.parse("a=y@mit;a,b@mit");
            Set<String> expected = Set.of("y@mit", "b@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests list whitespace semicolon
    @Test
    public void testWhitespaceSemicolon2() {
        try { 
            Expression one1 = ExpressionParser.parse("a=y@mit ; a,b@mit");
            Set<String> expected = Set.of("y@mit", "b@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // tests list whitespace semicolon
    @Test
    public void testWhitespaceSemicolon3() {
        try { 
            Expression one1 = ExpressionParser.parse("a=y@mit     ;         a,b@mit");
            Set<String> expected = Set.of("y@mit", "b@mit");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }    
    
    
    // tests list whitespace pipe
    @Test
    public void testWhitespacePipe1() {
        try { 
            Expression one1 = ExpressionParser.parse("a=y@mit|a,b@mit");
            Main.parseFactory(one1);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }
    
    // tests list whitespace pipe
    @Test
    public void testWhitespacePipe2() {
        try { 
            Expression one1 = ExpressionParser.parse("a=y@mit | a,b@mit");
            Main.parseFactory(one1);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }
    
    // tests list whitespace semicolon
    @Test
    public void testWhitespacePipe3() {
        try { 
            Expression one1 = ExpressionParser.parse("a=y@mit     |      a,b@mit");
            Main.parseFactory(one1);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }
    
    
    
    
    // equals() tests
    
    // one email
    @Test
    public void testOneEmailNormalEquals() {
        try {
            Expression one1 = ExpressionParser.parse("brett@gmail.com");
            Expression one2 = ExpressionParser.parse("brett@gmail.com");
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
            Expression one2 = ExpressionParser.parse("ryan+@gmail, brett@gmail.com");
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
            Set<String> expected = Set.of("ryan+@gmail");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // getEmails() tests
    
    
    @Test
    public void testOneEmailNormalEmails() {
        try {
            Expression one1 = ExpressionParser.parse("brett@gmail.com");
            Set<String> expected = Set.of("brett@gmail.com");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    
 // 2 emails, union out of order, plus sign, one space
    @Test
    public void testTwoEmailsUnionEmails() {
        try {
            Expression one1 = ExpressionParser.parse("brett@gmail.com,ryan+@gmail");
            Set<String> expected = Set.of("brett@gmail.com", "ryan+@gmail");
            assert testSameSetEmails(Set.of(one1), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
 // >2 emails, difference intersection union
    @Test
    public void testManyEmailsDIUEmails() {
        try {
            Expression one1 = ExpressionParser.parse("((brett@gmail.com,ryan+@gmail)!brett@gmail.com)*ryan+@gmail");
            Expression one2 = ExpressionParser.parse("((brett@gmail.com, ryan+@gmail)!brett@gmail.com)*ryan+@gmail");
            assert testEquals(one1, one2);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // toString()
    @Test
    public void testOneEmailNormalToString() {
        try {
            Expression one1 = ExpressionParser.parse("brett@gmail.com");
            Expression one2 = ExpressionParser.parse("brett@gmail.com");
            assert testToStringTwoExpressions(one1, one2);
            String expectedToString = "brett@gmail.com";
            assert testToStringOneString(one1, expectedToString);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // hashCode()
    @Test
    public void testOneEmailNormalHashCode() {
        try {
            Expression one1 = ExpressionParser.parse("brett@gmail.com");
            Expression one2 = ExpressionParser.parse("brett@gmail.com");
            assert testHashCode(one1, one2);
        } catch (UnableToParseException e) {
            assert false;
        }
        
    }
    
    // toString
    @Test
    public void testTwoEmailsUnionToString() {
        try {
            Expression one1 = ExpressionParser.parse("brett@gmail.com,ryan+@gmail");
            Expression one2 = ExpressionParser.parse("brett@gmail.com, ryan+@gmail");
            assert testToStringTwoExpressions(one1, one2);
            String expectedToString = "(brett@gmail.com, ryan+@gmail)";
            assert testToStringOneString(one1, expectedToString);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // hashCode
    @Test
    public void testTwoEmailsUnionHashCode() {
        try {
            Expression one1 = ExpressionParser.parse("brett@gmail.com,ryan+@gmail");
            Expression one2 = ExpressionParser.parse("ryan+@gmail, brett@gmail.com");
            assert testHashCode(one1, one2);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    
    
    // toString()
    @Test
    public void testManyEmailsDIUToString() {
        try {
            Expression one1 = ExpressionParser.parse("((brett@gmail.com,ryan+@gmail)!brett@gmail.com)*ryan+@gmail");
            Expression one2 = ExpressionParser.parse("((brett@gmail.com, ryan+@gmail)!brett@gmail.com)*ryan+@gmail");
            assert testToStringTwoExpressions(one1, one2);
            String expectedToString = "(((brett@gmail.com, ryan+@gmail) ! brett@gmail.com) * ryan+@gmail)";
            assert testToStringOneString(one1, expectedToString);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // hashCode()
    @Test
    public void testManyEmailsDIUHashCode() {
        try {
            Expression one1 = ExpressionParser.parse("((brett@gmail.com,ryan+@gmail)!brett@gmail.com)*ryan+@gmail");
            Expression one2 = ExpressionParser.parse("((brett@gmail.com, ryan+@gmail)!brett@gmail.com)*ryan+@gmail");
            assert testHashCode(one1, one2);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    
    // some tests for list definitions
    @Test
    public void testOneListDefinitionEmails() {
        try {
            Expression assign = ExpressionParser.parse("a=brett@gmail.com");
            Expression a = ExpressionParser.parse("a");
            Set<String> expected = Set.of("brett@gmail.com");
            assert testSameSetEmails(Set.of(assign, a), expected);
            ExpressionParser.parse("a = ");
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    @Test
    public void testManyDefinitionEmails() {
        try {
            Expression assign = ExpressionParser.parse("a=((brett@gmail.com, ryan+@gmail)!brett@gmail.com)*ryan+@gmail");
            Expression a = ExpressionParser.parse("a");
            Set<String> expected = Set.of("ryan+@gmail");
            assert testSameSetEmails(Set.of(assign, a), expected);
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
            Set<String> expected = Set.of("ryan+@gmail");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // test empty listname
    @Test
    public void testEmptyListnameEmails() {
        try {
            Expression empty = ExpressionParser.parse("abc");
            Set<String> expected = Set.of();
            assert testSameSetEmails(Set.of(empty), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    // test specification examples
    
    
    @Test
    public void testXAB() {
        try {
            Expression xab = ExpressionParser.parse("a = brett@gmail; b = ryan@gmail; x=a,b");
            Set<String> expected = Set.of("brett@gmail", "ryan@gmail");
            assert testSameSetEmails(Set.of(xab), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    @Test
    public void testRoom() {
        try {
            Expression xab = ExpressionParser.parse("(room=alice@mit.edu)*room");
            Set<String> expected = Set.of("alice@mit.edu");
            assert testSameSetEmails(Set.of(xab), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    @Test
    public void testLargeSameOutput() {
        try {
            Expression one = ExpressionParser.parse("suite=room1,room2; room1=alice@mit.edu; room2=bob@mit.edu; suite");
            Expression two = ExpressionParser.parse("room1=alice@mit.edu; room2=bob@mit.edu; suite=room1,room2; suite");
            assert testExpressionSameEmails(one, two);
        } catch (UnableToParseException e) {
            assert false;
        }
    }

    @Test
    public void testRecursiveAlice() {
        try {
            Expression expr = ExpressionParser.parse("room1=alice@mit.edu; room1=room1,eve@mit.edu; room1");
            Set<String> expected = Set.of("alice@mit.edu", "eve@mit.edu");
            assert testSameSetEmails(Set.of(expr), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    @Test
    public void testDeepRecursive() {
        try {
            Expression expr = ExpressionParser.parse("room1=alice@mit.edu; room1=eve@mit.edu, ryan@gmail, room1, teehee@gmail; room1");
            Set<String> expected = Set.of("alice@mit.edu", "eve@mit.edu", "teehee@gmail", "ryan@gmail");
            assert testSameSetEmails(Set.of(expr), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }

    @Test
    public void testABC() {
        try {
            Expression assign = ExpressionParser.parse("c=brett@gmail");
            Expression abc = ExpressionParser.parse("a=b=c");
            Set<String> expected = Set.of("brett@gmail");
            assert testSameSetEmails(Set.of(assign, abc), expected);
        } catch (UnableToParseException e) {
            e.printStackTrace();
        }
    }
    
    //tests "*" before "," to set precedence 
    @Test
    public void testPrecedenceAsterixComma1() {
        try {
            Expression a = ExpressionParser.parse("a@email * b@email,c@email");
            Set<String> expected = Set.of("c@email");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests "," before "*" to set precedence 
    @Test
    public void testPrecedenceAsterixComma2() {
        try {
            Expression a = ExpressionParser.parse("(a@email,c@email) * (b@email,c@email)");
            Set<String> expected = Set.of("c@email");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests "*" before "!" to set precedence 
    @Test
    public void testPrecedenceAsterixExclamation1() {
        try {
            Expression a = ExpressionParser.parse("(a@email,b@email) * b@email!c@email");
            Set<String> expected = Set.of("b@email");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests "!" before "*" to set precedence 
    @Test
    public void testPrecedenceAsterixExclamation2() {
        try {
            Expression a = ExpressionParser.parse("(a@email,b@email) * ((b@email,c@email)!c@email)");
            Set<String> expected = Set.of("b@email");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests "," before "!" to set precedence 
    @Test
    public void testPrecedenceCommaExclamation1() {
        try {
            Expression a = ExpressionParser.parse("a@email,b@email ! b@email");
            Set<String> expected = Set.of("a@email");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests "!" before "," to set precedence 
    @Test
    public void testPrecedenceCommaExclamation2() {
        try {
            Expression a = ExpressionParser.parse("(a@email,b@email) ! b@email");
            Set<String> expected = Set.of("a@email");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    
    //tests ";" before "|" to set precedence 
    // tests "=" before "|" with parentheses to set precedence
    // tests "," before "|" to set precedence
    public void testPrecedenceSemicolonPipe() {
        try {
            Expression a = ExpressionParser.parse("x = a@email,b@email ; x | b@email");
            Set<String> expected = Set.of();
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests "," before "=" to set precedence 
    @Test
    public void testPrecedenceCommaEquals() {
        try {
            Expression a = ExpressionParser.parse("(list = a@email,b@email),c@email");
            Set<String> expected = Set.of("a@email", "b@email", "c@email");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests "=" before "," to set precedence 
    @Test
    public void testPrecedenceCommaEquals2() {
        try {
            Expression a = ExpressionParser.parse("((list = a@email),b@email),c@email");
            Set<String> expected = Set.of("c@email", "b@email", "a@email");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    
    //tests "*" before "=" to set precedence 
    @Test
    public void testPrecedenceAsterixEquals() {
        try {
            Expression a = ExpressionParser.parse("(list = a@email,c@email)*c@email");
            Set<String> expected = Set.of("c@email");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests "=" before "*" to set precedence 
    @Test
    public void testPrecedenceAsterixEquals2() {
        try {
            Expression a = ExpressionParser.parse("((list = a@email),c@email)*c@email");
            Set<String> expected = Set.of("c@email");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests "*" before ";" to set precedence 
    //tests "," before ";" to set precedence
    @Test
    public void testPrecedenceAsterixSemicolon() {
        try {
            Expression a = ExpressionParser.parse("list = a@email,c@email; list*c@email");
            Set<String> expected = Set.of("c@email");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests ";" before "*" to set precedence
    //tests ";" before "," to set precedence

    @Test
    public void testPrecedenceAsterixSemicolon2() {
        try {
            ExpressionParser.parse("list = ");
            Expression a = ExpressionParser.parse("list = a@email,(c@email; list*c@email)");
            Set<String> expected = Set.of("a@email");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }

    //tests "!" before ";" to set precedence
    // tests "!" before "="
    @Test
    public void testPrecedenceExclamationSemicolon() {
        try {
            Expression a = ExpressionParser.parse("list = (a@email,c@email)!c@email; list!c@email");
            Set<String> expected = Set.of("a@email");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests ";" before "!" to set precedence
    // tests "=" before "!"
    @Test
    public void testPrecedenceExclamationSemicolon2() {
        try {
            Expression a = ExpressionParser.parse("list = (a@email,c@email)!(c@email; list!c@email); list");
            Set<String> expected = Set.of("a@email", "c@email");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests integration of many precedences
    @Test
    public void testPipe() {
        try {
            Expression a = ExpressionParser.parse("(x = a@mit.edu | y = b@mit.edu) , x");
            Set<String> expected = Set.of("a@mit.edu");
            assert testSameSetEmails(Set.of(a), expected);
        } catch (UnableToParseException e) {
            assert false;
        }
    }
    
    //tests integration of many precedences
    @Test
    public void testPrecedenceIntegration() {
        try {
            Expression a = ExpressionParser.parse("list = (a@email,c@email); (list2=f@email|list=(c@email; list!c@email)); list2,list");
            Set<String> expected = Set.of("a@email", "f@email");
            assert testSameSetEmails(Set.of(a), expected);
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

    //tests loop exceptions
    @Test
    public void testLoopException() throws UnableToParseException {
        try {
            Expression a = ExpressionParser.parse("a= ; b= ; a=b; b=a");
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
    
    private static boolean testToStringTwoExpressions(Expression e1, Expression e2) {
        return e1.toString().equals(e2.toString());
    }
    
    private static boolean testToStringOneString(Expression e, String s) {
        return e.toString().equals(s);
    }
    
    private static boolean testHashCode(Expression e1, Expression e2) {
        return e1.hashCode() == e2.hashCode();
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
    
    private static boolean testExpressionSameEmails(Expression e1, Expression e2) {
        Set<String> e2Emails = Main.parseFactory(e2);
        return testSameSetEmails(Set.of(e1), e2Emails);
    }
    
    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }
    
}
