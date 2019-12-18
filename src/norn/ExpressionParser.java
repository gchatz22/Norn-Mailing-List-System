package norn;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.mit.eecs.parserlib.ParseTree;
import edu.mit.eecs.parserlib.Parser;
import edu.mit.eecs.parserlib.UnableToParseException;


public class ExpressionParser {
    
    /**
     * Main method. Parses and then reprints an example expression.
     * 
     * @param args command line arguments, not used
     * @throws UnableToParseException if example expression can't be parsed
     */
    public static void main(final String[] args) throws UnableToParseException {

    }
    
    // the nonterminals of the grammar
    private static enum ExpressionGrammar {
        EXPRESSION, PRIMITIVE, WHITESPACE, EMAIL, MAILINGLIST, DIFFERENCE,
        UNION, INTERSECTION, LISTDEFINITION, SEQUENCE, PARALLEL
        
    }
    
    private static Parser<ExpressionGrammar> parser = makeParser();
    
    /**
     * Compile the grammar into a parser.
     * 
     * @return parser for the grammar
     * @throws RuntimeException if grammar file can't be read or has syntax errors
     */
    private static Parser<ExpressionGrammar> makeParser() {
        try {
            // read the grammar as a file, relative to the project root.
            final File grammarFile = new File("src/norn/ListExpression.g");
            return Parser.compile(grammarFile, ExpressionGrammar.EXPRESSION);

        // Parser.compile() throws two checked exceptions.
        // Translate these checked exceptions into unchecked RuntimeExceptions,
        // because these failures indicate internal bugs rather than client errors
        } catch (IOException e) {
            throw new RuntimeException("can't read the grammar file", e);
        } catch (UnableToParseException e) {
            throw new RuntimeException("the grammar has a syntax error", e);
        }
    }
    
    /**
     * Parse a string into an expression.
     * 
     * @param string string to parse
     * @return Expression parsed from the string
     * @throws UnableToParseException if the string doesn't match the Expression grammar
     */
    public static Expression parse (final String string) throws UnableToParseException {

        // parse the example into a parse tree
        final ParseTree<ExpressionGrammar> parseTree = parser.parse(string);

        // display the parse tree in various ways, for debugging only
        // System.out.println("parse tree " + parseTree);
        // Visualizer.showInBrowser(parseTree);

        // make an AST from the parse tree
        final Expression expression = makeAbstractSyntaxTree(parseTree);
        
        return expression;
    }    
    
    /**
     * Convert a parse tree into an abstract syntax tree.
     * 
     * @param parseTree constructed according to the grammar in Exression.g
     * @return abstract syntax tree corresponding to parseTree
     * @throws UnableToParseException 
     */
    private static Expression makeAbstractSyntaxTree (final ParseTree<ExpressionGrammar> parseTree) throws UnableToParseException {
        switch (parseTree.name()) {
        case EXPRESSION:
            {
                return makeAbstractSyntaxTree(parseTree.children().get(0));
            }
        
        case EMAIL: // email ::= [a-zA-Z0-9_.-+]* '@' [a-zA-Z0-9_.-]*;
            {
                return new Email(parseTree.text());
            }
            
        case MAILINGLIST: // mailingList ::= [a-zA-Z0-9_.-]*;
            {
                return new MailingList(parseTree.text());
            }
            
        case UNION: // union ::= primitive (',' primitive)*;
            {
                final List<ParseTree<ExpressionGrammar>> children = parseTree.children();
                Expression expression = makeAbstractSyntaxTree(children.get(0));

                for (int i = 1; i < children.size(); ++i) {
                    expression = new Union(expression, makeAbstractSyntaxTree(children.get(i)));
                }
                return expression;
            }
            
        case DIFFERENCE: // difference ::= union ('!' union)*;
            {
                final List<ParseTree<ExpressionGrammar>> children = parseTree.children();
                Expression expression = makeAbstractSyntaxTree(children.get(0));

                for (int i = 1; i < children.size(); ++i) {
                    expression = new Difference(expression, makeAbstractSyntaxTree(children.get(i)));
                }
                return expression;
            }

        case INTERSECTION: // intersection ::= difference ('*' difference)*;
            {
                final List<ParseTree<ExpressionGrammar>> children = parseTree.children();
                Expression expression = makeAbstractSyntaxTree(children.get(0));

                for (int i = 1; i < children.size(); ++i) {
                    expression = new Intersection(expression, makeAbstractSyntaxTree(children.get(i)));
                }
                return expression;
            }
            
        case LISTDEFINITION: // listdefinition ::= (mailingList ('=' union)));j
        {
            final List<ParseTree<ExpressionGrammar>> children = parseTree.children();

            String name = children.get(0).text();
            Expression expression = EmptyEmailSet.getEmpty();

            if (children.size()==1) {
                return new ListDefinition(name, EmptyEmailSet.getEmpty());
            } else if (children.size()==2) {
                return new ListDefinition(name, makeAbstractSyntaxTree(children.get(1)));
            }
            
            expression = makeAbstractSyntaxTree(children.get(1));
            
            for (int i = 2; i < children.size(); ++i) {
                expression = new ListDefinition(children.get(i-1).text(), makeAbstractSyntaxTree(children.get(i)));
            }
            return expression;
        }
        
        case SEQUENCE: // sequence ::= (listdefinition | union) (';' (listdefinition | union))*;
        {
            final List<ParseTree<ExpressionGrammar>> children = parseTree.children();
            Expression expression = makeAbstractSyntaxTree(children.get(0));
            if (children.size()==1) {
                return expression;
            }
            
            for (int i = 1; i < children.size(); ++i) {
                expression = new Sequence(expression, makeAbstractSyntaxTree(children.get(i)));
            }
            
            return expression;
        }
        case PARALLEL: // parallel ::= sequence ('|' sequence)*;
        {
            final List<ParseTree<ExpressionGrammar>> children = parseTree.children();
            Expression expression = makeAbstractSyntaxTree(children.get(0));

            for (int i = 1; i < children.size(); ++i) {
                expression = new Parallel(expression, makeAbstractSyntaxTree(children.get(i)));
            }
            return expression;
        }   
        case PRIMITIVE: // primitive ::= email | mailingList | '(' expression ')';
            {
                final ParseTree<ExpressionGrammar> child = parseTree.children().get(0);
                return makeAbstractSyntaxTree(child);
            }
        
        default:
            throw new AssertionError("should never get here while parsing");
        }

    }

}