/* Kevin Zhang        Programming Assignment 5    Factor.java
 * PID: A10810276     Login: cs12spq
 */
import java.util.Map;

/**
 * A class representing the <factor> nonterminal symbol in an abstract syntax
 * tree. This class has no public constructors; the public interface for
 * creating an instance of the class is the static parse(String s) factory
 * method.
 * 
 * 
 * @author Kevin
 * 
 */
public class Factor extends ASTNode {
	/**
	 * Stores the node passed as a child by calling super method
	 * 
	 * @param n
	 */
	private Factor(ASTNode n) {
		// Calls super constructor of ASTNode
		super(n);
	}

	/**
	 * A factory method that parses a String accoring to the BNF definition for
	 * <factor>. The BNF definition is: <factor> := <const> | <ident> | "("
	 * <expr> ")"
	 * 
	 * @param s
	 *            - the String to parse.
	 * 
	 * @return a Factor object that is the root of an abstract syntax (sub)tree
	 *         resulting from the parse, or null if the String cannot be parsed
	 *         as a <factor>.
	 */
	public static Factor parse(String s) {
		// Trims the string
		s = s.trim();

		ASTNode node;

		// Tries to parse as constructor
		node = Const.parse(s);
		if (node != null) {
			return new Factor(node);
		}

		// Tries to parse as a ident
		node = Ident.parse(s);
		if (node != null) {
			return new Factor(node);
		}

		// Check if code is surrounded with brackets, parses the inside as a
		// expression
		if (s.startsWith("(") && s.endsWith(")")) {
			node = Expr.parse(s.substring(1, s.length() - 1));
			if (node != null) {
				// Returns the new factor object
				return new Factor(node);
			}
		}

		// Otherwise return null
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
		// Returns the double value of its child
		return getChild(0).eval(symtab);
	}

}
