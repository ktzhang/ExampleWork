/* Kevin Zhang        Programming Assignment 5    Expr.java
 * PID: A10810276     Login: cs12spq
 */

import java.util.Map;

/**
 * A class representing the <expr> nonterminal symbol in an abstract syntax
 * tree. This class has no public constructors; the public interface for
 * creating an instance of the class is the static parse(String s) factory
 * method.
 * 
 * @author Kevin Zhang
 * 
 */
public class Expr extends ASTNode {

	/**
	 * Private constructor that adds the node to its children
	 * 
	 * @param n
	 *            the node that needs to be its child
	 */
	private Expr(ASTNode n) {
		// Calls ASTNodes constructor, which adds a child
		super(n);
	}

	/**
	 * A factory method that parses a String accoring to the BNF definition for
	 * <expr>. The BNF definition is: <expr> := <assmt> | <oprn>
	 * 
	 * @param s
	 *            the String to parse.
	 * @return n Expr object that is the root of an abstract syntax (sub)tree
	 *         resulting from the parse, or null if the String cannot be parsed
	 *         as an <expr>.
	 */
	public static Expr parse(String s) {
		// Trims the string
		s = s.trim();

		// Initializes the result
		ASTNode result;

		// Attempts to parse as Assmnt
		result = Assmt.parse(s);

		// Checks if result isnt null
		if (result != null)
			// If parse works, returns it
			return new Expr(result);
		else {
			// Tries parsing it as a operation
			result = Oprn.parse(s);
			if (result != null)
				// If parse work returns it
				return new Expr(result);
		}
		// REturns null if nothing works
		return null;
	}

	/**
	 * Evaluate the abstract syntax (sub)tree that is rooted at this ASTNode in
	 * the context of the given symbol table, and return the result.
	 * 
	 * @param symtab
	 *            - A map from variable identifiers to values, to use as a
	 *            symbol table in the evaluation.
	 * 
	 * @return the double value that is the result of evaluating the abstract
	 *         syntax (sub)tree rooted at this ASTNode.
	 */
	public double eval(Map<String, Double> symtab) {
		//Returns the double value of its child
		return getChild(0).eval(symtab);
	}

}
