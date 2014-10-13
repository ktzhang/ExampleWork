/* Kevin Zhang        Programming Assignment 5    Oprn.java
 * PID: A10810276     Login: cs12spq
 */

import java.util.Map;

/**
 * A class representing the <term> nonterminal symbol in an abstract syntax
 * tree. This class has no public constructors; the public interface for
 * creating an instance of the class is the static parse(String s) factory
 * method.
 * 
 * @author Kevin
 * 
 */
public class Term extends ASTNode {
	private char oper;

	/**
	 * Basic constuctor when there is no operator
	 * 
	 * @param n
	 *            the child to be stored
	 */
	private Term(ASTNode n) {
		// Stores a child
		super(n);
	}

	/**
	 * Overloaded constuctor when there is an operator. Stores nodes and
	 * operation
	 * 
	 * @param n
	 *            the first part of the string as a node
	 * @param p
	 *            the second part after the operation as a node
	 * @param oper
	 *            the operation to be stored
	 */
	private Term(ASTNode n, ASTNode p, char oper) {
		// Stores the two nodes as child
		super(n, p);
		// Stores operation
		this.oper = oper;
	}

	/**
	 * A factory method that parses a String accoring to the BNF definition for
	 * <term>. The BNF definition is: <term> := <factor> | <term> "*" <factor> |
	 * <term> "/" <factor>
	 * 
	 * @param s
	 *            - the String to parse.
	 * 
	 * @return a Term object that is the root of an abstract syntax (sub)tree
	 *         resulting from the parse, or null if the String cannot be parsed
	 *         as a <term>.
	 */
	public static Term parse(String s) {
		// Trims the string
		s = s.trim();

		ASTNode nodeA, nodeB;

		// Checks if can parse as a Factor
		nodeA = Factor.parse(s);
		if (nodeA != null) {
			return new Term(nodeA);
		}

		// Initializes the pointer index for multiple operators
		int index = s.length();

		// Loop to check multiple operators
		boolean outer;
		outer: do {
			// Initialize boolean
			outer = false;
			// Decrements each index value after each loop
			int times = s.lastIndexOf("*", --index);
			int divide = s.lastIndexOf("/", index);

			// Check which operator comes first
			if (times > divide) {
				// If there is a times method
				if (times != -1) {
					// Parse the splitting of the string
					nodeA = Term.parse(s.substring(0, times));
					nodeB = Factor.parse(s.substring(times + 1, s.length()));
					if (nodeA == null || nodeB == null) {
						// Will skip this operator
						outer = true;
						// Continues the loop
						continue outer;
					}
					// Returns with the operator
					return new Term(nodeA, nodeB, '*');
				}
			} else {
				if (divide != -1) {
					// Parsing the splitting of the string before and after the
					// operator
					nodeA = Term.parse(s.substring(0, divide));
					nodeB = Factor.parse(s.substring(divide + 1, s.length()));
					if (nodeA == null || nodeB == null) {
						// Will skip this operator
						outer = true;
						// Continues the loop
						continue outer;
					}
					// Returns with operator
					return new Term(nodeA, nodeB, '/');
				}
			}
		} while (outer);
		// If doesn't work, return null
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
		// Gets the child value as a double
		double a = getChild(0).eval(symtab);
		double b;
		// Checks what the operation is
		if (oper == '*') {
			b = getChild(1).eval(symtab);
			// Returns the multiplication of it
			return a * b;
		} else if (oper == '/') {
			b = getChild(1).eval(symtab);
			// Returns the division of it
			return a / b;
		} else {
			// If no operation, return the double value of child
			return a;
		}

	}

}
