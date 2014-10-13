/* Kevin Zhang        Programming Assignment 5    Ident.java
 * PID: A10810276     Login: cs12spq
 */

import java.util.Map;

/**
 * A class representing the <Identity> nonterminal symbol in an abstract syntax
 * tree. This class has no public constructors; the public interface for
 * creating an instance of the class is the static parse(String s) factory
 * method.
 * 
 * @author Kevin Zhang
 * 
 */
public class Ident extends ASTNode {
	// Private instance variable storing the identity of the variable
	private String iden;

	/**
	 * Stores the string into into local variable
	 * 
	 * @param iden
	 *            the string used a identifier or variable name
	 */
	private Ident(String iden) {
		// Storing the string into instance
		this.iden = iden;
	}

	/**
	 * A factory method that parses a String accoring to the definition for
	 * <ident>. The definition is: Any String that starts with a
	 * JavaIdentifierStart character, followed by 0 or more JavaIdentifierPart
	 * characters.
	 * 
	 * @param s
	 *            - the String to parse.
	 * 
	 * @return a Ident object that is the root of an abstract syntax subtree
	 *         resulting from the parse, or null if the String cannot be parsed
	 *         as a <ident>.
	 */
	public static Ident parse(String s) {
		// Trimming the string
		s = s.trim();

		// Attempting to check if contains valid characters
		try {
			// Converts the string to char array
			char[] charArray = s.toCharArray();
			// Check if start is a valid identifier
			if (Character.isJavaIdentifierStart(charArray[0])) {
				// For the rest of the string, check if valid identifier parts
				for (int i = 1; i < s.length(); i++) {
					if (!Character.isJavaIdentifierPart(charArray[i])) {
						return null;
					}
				}
				// If this all passes, creates new Ident obj with the string
				return new Ident(s);
			} else {
				// If not start with valid iden, return null
				return null;
			}
			// When there is out of bounds, returns null
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Evaluate the abstract syntax (sub)tree that is rooted at this ASTNode in
	 * the context of the given symbol table, and return the result. This result
	 * is the value of the symbol inputted, otherwise it would throw an
	 * exception
	 * 
	 * @throws RuntimeException
	 *             when variable does not exist
	 * @param symtab
	 *            is the identity mapping with constants
	 * @return the double value of corresponding constant
	 */
	public double eval(Map<String, Double> symtab) {
		// Checks if ident is null, meaning it doesnt exist yet
		if (symtab.get(iden) == null) {
			// Throw exception
			throw new RuntimeException("UNINITIALIZED VARIABLE: " + iden);
		} else {
			// returns the double value
			return symtab.get(iden);
		}
	}

	/**
	 * Return the String representation of this ident.
	 */
	public String toString() {
		return iden;
	}
}
