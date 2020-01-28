/*
 * Copyright 2020 - Regents of the University of California, San
 * Francisco.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package clbp.view;

public class ObsVar extends Obs {
  private final boolean fraction = false;
  
  public ObsVar(String en, clbp.ctrl.Parameters p) {
    super(en,p);
  }
  
  @Override
  public void writeHeader() {
    // write the output file header
    StringBuilder sb = new StringBuilder("Time");
    if (subject == null) System.err.println("subject == null");
    if (subject.comps == null) System.err.println("subject.comps == null");
    subject.comps.stream().forEach((c) -> {
      sb.append(", Comp").append(c.id).append((fraction ? ".fract" : ".conc"));
    });
    outFile.println(sb.toString());
  }
  
  @Override
  public java.util.ArrayList<Double> measure() {
    java.util.ArrayList<Double> retVal = new java.util.ArrayList<>();
    subject.comps.stream().forEach((c) -> {
      double result = c.variables.get("Variable1").doubleValue();
      retVal.add(result);
    });
    return retVal;
  }
  
}
