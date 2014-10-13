/* Kevin Zhang        Programming Assignment 5    Assmt.java
 * PID: A10810276     Login: cs12spq
 */


import java.util.Map;

/**
 * A class representing the <assmt> nonterminal symbol in an abstract syntax
 * tree. This class has no public constructors; the public interface for
 * creating an instance of the class is the static parse(String s) factory
 * method. Assigns the value of a constant to a symbol
 * 
 * @author Kevin
 * 
 */
public class Assmt extends ASTNode {
	// Local variable iden stores the name of variable
	private String iden;

	/**
	 * Private constructor which takes in two nodes and stores it as children
	 * 
	 * @param nodeA
	 *            the node to be assigned
	 * @param nodeB
	 *            the node which takes the value to store
	 */
	private Assmt(ASTNode nodeA, ASTNode nodeB) {
		// Calls the super constructor of ASTNode
		super(nodeA, nodeB);
	}

	/**
	 * A factory method that parses a String according to the BNF definition for
	 * assigniment <assmt> := <ident> "=" <expr>
	 * 
	 * @param the
	 *            String to parse.
	 * @return an Assmt object that is the root of an abstract syntax (sub)tree
	 *         resulting from the parse, or null if the String cannot be parsed
	 *         as a <assmt>.
	 */
	public static Assmt parse(String s) {
		// Trims the string
		s = s.trim();

		// Initializes the node
		ASTNode nodeA, nodeB;

		// Checks if there is equals
		int index = s.length();

		// Loop to check if there is more than one equals
		boolean outer;
		outer: do {

			outer = false;
			//Decrements the index value after each loop
			int equalsIndex = s.lastIndexOf("=", --index);

			// If there is none, return null
			if (equalsIndex == -1) {
				return null;
			} else {

				// Splits the string to two parts and parses it
				String iden = s.substring(0, equalsIndex).trim();
				nodeA = Ident.parse(iden);
				String exp = s.substring(equalsIndex + 1, s.length()).trim();
				nodeB = Expr.parse(exp);
				// Check if there are any errors in ident or expr
				if (nodeA == null || nodeB == null) {
					// Continues the outer loop
					outer = true;
					continue outer;
				}
				// Returns a new object with nodes
				return new Assmt(nodeA, nodeB);
			}
			// Outer loop
		} while (outer);
		// Returns null if nothing works
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
		// Gets the value of the second child which is the constant value
		double val = getChild(1).eval(symtab);
		// Assigns the value by getting the string value of child and the
		// constant value
		symtab.put(getChild(0).toString(), val);
		// Return the value it assigns
		return val;
	}

}
