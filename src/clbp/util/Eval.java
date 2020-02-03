/*
 * Copyright 2020 - Regents of the University of California, San
 * Francisco.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package clbp.util;

import org.graalvm.polyglot.Context;

public class Eval {
   public static Context graal = null;
   public static void initGraal() {
//   try {
     graal = Context.newBuilder().allowAllAccess(true).build();
     java.util.Set<String> languages = graal.getEngine().getLanguages().keySet();
     System.out.println("Current Languages available in GraalVM: " + languages);
//   }
   }
   
  /**
   * Stolen from: https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form/26227947#26227947
   */
  public static double oldeval(final String str) {
    return new Object() {
      int pos = -1, ch;

      void nextChar() {
        ch = (++pos < str.length()) ? str.charAt(pos) : -1;
      }

      boolean eat(int charToEat) {
        while (ch == ' ') {
          nextChar();
        }
        if (ch == charToEat) {
          nextChar();
          return true;
        }
        return false;
      }

      double parse() {
        nextChar();
        double x = parseExpression();
        if (pos < str.length()) {
          throw new RuntimeException("Unexpected: " + (char) ch);
        }
        return x;
      }

      // Grammar:
      // expression = term | expression `+` term | expression `-` term
      // term = factor | term `*` factor | term `/` factor
      // factor = `+` factor | `-` factor | `(` expression `)`
      //        | number | functionName factor | factor `^` factor
      double parseExpression() {
        double x = parseTerm();
        for (;;) {
          if (eat('+')) {
            x += parseTerm(); // addition
          } else if (eat('-')) {
            x -= parseTerm(); // subtraction
          } else {
            return x;
          }
        }
      }

      double parseTerm() {
        double x = parseFactor();
        for (;;) {
          if (eat('*')) {
            x *= parseFactor(); // multiplication
          } else if (eat('/')) {
            x /= parseFactor(); // division
          } else {
            return x;
          }
        }
      }

      double parseFactor() {
        if (eat('+')) {
          return parseFactor(); // unary plus
        }
        if (eat('-')) {
          return -parseFactor(); // unary minus
        }
        double x;
        int startPos = this.pos;
        if (eat('(')) { // parentheses
          x = parseExpression();
          eat(')');
        } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
          while ((ch >= '0' && ch <= '9') || ch == '.') {
            nextChar();
          }
          x = Double.parseDouble(str.substring(startPos, this.pos));
        } else if (ch >= 'a' && ch <= 'z') { // functions
          while (ch >= 'a' && ch <= 'z') {
            nextChar();
          }
          String func = str.substring(startPos, this.pos);
          x = parseFactor();
          if (func.equals("sqrt")) {
            x = Math.sqrt(x);
          } else if (func.equals("sin")) {
            x = Math.sin(Math.toRadians(x));
          } else if (func.equals("cos")) {
            x = Math.cos(Math.toRadians(x));
          } else if (func.equals("tan")) {
            x = Math.tan(Math.toRadians(x));
          } else {
            throw new RuntimeException("Unknown function: " + func);
          }
        } else {
          throw new RuntimeException("Unexpected: " + (char) ch);
        }

        if (eat('^')) {
          x = Math.pow(x, parseFactor()); // exponentiation
        }
        return x;
      }
    }.parse();
  }
  public static void main(String[] args) {
    double[] values = {4,3,1};
    String expr = "(("+values[0]+" - 2^"+values[1]+" + "+values[2]+") * -sqrt(3*3+4*4)) / 2";
    System.out.println(oldeval(expr));
  }
}
