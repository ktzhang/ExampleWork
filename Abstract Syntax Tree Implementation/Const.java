/* Kevin Zhang        Programming Assignment 5    Const.java
 * PID: A10810276     Login: cs12spq
 */

import java.util.Map;

/**
 * A class representing the <const> nonterminal symbol in an abstract syntax
 * tree. This class has no public constructors; the public interface for
 * creating an instance of the class is the static parse(String s) factory
 * method.
 * 
 * @author Kevin
 * 
 */
public class Const extends ASTNode {
	// Stores the double value in local variable
	private double store;

	/**
	 * Private constructor that stores the double into local variable
	 * 
	 * @param val
	 *            the value of the constant in double
	 */
	private Const(double val) {
		// Stores to local variable
		store = val;
	}

	/**
	 * A factory method that parses a String accoring to the definition for
	 * <const>. Any String that can be parsed by java.lang.Double.parseDouble(s)
	 * without throwing a NumberFormatException.
	 * 
	 * @param s
	 *            the String to parse.
	 * @return Const object that is the root of an abstract syntax subtree
	 *         resulting from the parse, or null if the String cannot be parsed
	 *         as a <const>.
	 */
	public static Const parse(String s) {
		// Trims the string
		s = s.trim();

		// Attempts to parse to double
		try {
			return new Const(Double.parseDouble(s));
		} catch (NumberFormatException e) {
			// If cannot be parsed, returns null
			return null;
		}
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
		// Returns the local variable
		return store;
	}

}
