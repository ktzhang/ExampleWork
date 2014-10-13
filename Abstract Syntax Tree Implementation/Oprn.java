/* Kevin Zhang        Programming Assignment 5    Oprn.java
 * PID: A10810276     Login: cs12spq
 */

import java.util.Map;

/**
 * A class representing the <oprn> nonterminal symbol in an abstract syntax
 * tree. This class has no public constructors; the public interface for
 * creating an instance of the class is the static parse(String s) factory
 * method.
 * 
 * @author Kevin Zhang
 * 
 */
public class Oprn extends ASTNode {
	// Stores the operation character
	private char oper;

	/**
	 * Basic constuctor when there is no operator
	 * 
	 * @param n
	 *            the child to be stored
	 */
	private Oprn(ASTNode n) {
		// Sets child to that node
		super(n);
	}

	/**
	 * Overloaded constructor when there is an operator
	 * 
	 * @param n
	 *            the first part of the string as a node
	 * @param p
	 *            the second part after the operation as a node
	 * @param oper
	 *            the operation to be stored
	 */
	private Oprn(ASTNode n, ASTNode p, char oper) {
		// Stores the two nodes as child
		super(n, p);
		// Changes the class variable
		this.oper = oper;
	}

	/**
	 * A factory method that parses a String accoring to the BNF definition for
	 * <oprn>. The BNF definition is: <oprn> := <term> | <oprn> "+" <term> |
	 * <oprn> "-" <term>
	 * 
	 * @param s
	 *            - the String to parse.
	 * 
	 * @return an Oprn object that is the root of an abstract syntax (sub)tree
	 *         resulting from the parse, or null if the String cannot be parsed
	 *         as an <oprn>.
	 */
	public static Oprn parse(String s) {
		// Trims the string
		s = s.trim();

		ASTNode nodeA, nodeB;

		// Tries the parse as a term
		nodeA = Term.parse(s);
		if (nodeA != null) {
			// If works, returns it as node
			return new Oprn(nodeA);
		}

		// Checking plus or minus comes first
		// Index of minus or plus sign starts at the end of the string
		int index = s.length();
		boolean outer;
		outer: do {
			outer = false;
			// Decrements the index value once
			int plus = s.lastIndexOf("+", --index);
			int minus = s.lastIndexOf("-", index);
			// if plus comes first
			if (minus < plus) {
				// Checking adding
				if (plus != -1) {

					// Splitting the string and calling parse
					nodeA = Oprn.parse(s.substring(0, plus));
					nodeB = Term.parse(s.substring(plus + 1, s.length()));
					// Returns null
					if (nodeA == null || nodeB == null) {
						// Will skip this operator
						outer = true;
						// Continues the loop
						continue outer;
					}
					// Returns as new obj
					return new Oprn(nodeA, nodeB, '+');
				}

				// If minus comes first
			} else {

				// Checking subtracting
				if (minus != -1) {

					// Check if there are two negative signs in a row
					int rand = s.lastIndexOf("-", minus - 1);
					String neg = s.substring(rand + 1, minus).trim();

					// If there are two negatives,
					// one will be minus and one will be negative
					try {
						// Check if there is gap between the negative sign
						if (neg.length() == 0) {
							// If there is, splitting includes one negative sign
							nodeA = Oprn.parse(s.substring(0, rand));
							nodeB = Term.parse(s.substring(minus, s.length()));

						} else {
							// Otherwise exclude the negative sign
							nodeA = Oprn.parse(s.substring(0, minus));
							nodeB = Term.parse(s.substring(minus + 1,
									s.length()));
						}
						// Catch exception if - is first letter
					} catch (Exception e) {
						return null;
					}

					// If any node returns null, it will also return null
					if (nodeA == null || nodeB == null) {
						// Will skip this operator
						outer = true;
						// Continues the loop
						continue outer;
					}
					// otherwise return nodeA, B
					return new Oprn(nodeA, nodeB, '-');

				}
			}
		} while (outer);
		// Return null if everything fails
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
		// Gets the child value
		double a = getChild(0).eval(symtab);
		double b;
		// Checks what the operation is
		if (oper == '+') {
			b = getChild(1).eval(symtab);
			// Returns the addition of the two doubles
			return a + b;
		} else if (oper == '-') {
			b = getChild(1).eval(symtab);
			// Returns the subtraction of the two doubles
			return a - b;
		} else {
			// if there is no operation, return the child 0
			return a;
		}
	}

}
