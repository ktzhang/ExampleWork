/**
 * Do some simple P5 tests
 */
public class SimpleP5Test {

  public static void main(String args[]) {

    try {
    	System.out.println("fdsafs");
      Expr e =  Expr.parse("2.0");
      Ident i = Ident.parse("xyz");
      Assmt a = Assmt.parse("a = 2.0");
      Oprn c = Oprn.parse("2.0");
      Const x =  Const.parse("3.0");
      Factor f =  Factor.parse("4.0");
      Term t =  Term.parse("5.0");
      double val = e.eval(new java.util.HashMap<String,Double>());
  	System.out.println("fdsafs");

      if(val == 2.0) {
	System.exit(0);
      }
    } catch (Throwable t) {}

    System.out.println("Problem parsing or evaluating...");
    System.exit(-1);

  }

}
