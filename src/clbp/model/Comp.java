/*
 * Copyright 2020 - Regents of the University of California, San
 * Francisco.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package clbp.model;

import java.util.ArrayList;

public class Comp implements sim.engine.Steppable {
  public int id = -Integer.MAX_VALUE;
  String name = null;
  public String getName() { return name; }
  public java.util.LinkedHashMap<String, Double> variables = new java.util.LinkedHashMap<>(1);
  public boolean finished = false;
  ArrayList<String> assignments = new ArrayList<>();

  public Comp() {}
  public Comp(int ident, double start) {
    this();
    id = ident;
    name = "dummyName-"+id;
    variables.put("dummy1", start);
    variables.put("dummy2", start);
    assignments.add("x20 = x0 + x1^x2");
    assignments.add("x21 = x20 * x3");
//    clbp.ctrl.Batch.log("Comp("+id+", "+start+")");
  }
  
  @Override
  public void step(sim.engine.SimState state) {
    ArrayList<String> lhs = new ArrayList<>();
    ArrayList<String> rhs = new ArrayList<>();
    for (String a : assignments) {
      String[] splitted = a.split("=");
      lhs.add(splitted[0]);
      rhs.add(splitted[1]);
    }
    for (int andx=0 ; andx<assignments.size() ; andx++) {
      String script_bound = rhs.get(andx);
      String lhskey = lhs.get(andx).trim();
      for (java.util.Map.Entry<String,Double> me : variables.entrySet()) {
        script_bound = script_bound.replaceAll(me.getKey(), me.getValue().toString());
      }
      double result = clbp.util.Eval.eval(script_bound);
      variables.replace(lhskey, result);
    }
    if (!finished)
      state.schedule.scheduleOnce(this,clbp.model.Model.SUB_ORDER);
  }
 
  public static void main(String[] args) throws java.io.FileNotFoundException {
    Comp c = new Comp(0,0.0);
        try {
      java.io.FileWriter fw = new java.io.FileWriter(new java.io.File("." 
              + java.io.File.separator 
              + "tmpComp.json"));
      fw.write(clbp.ctrl.Batch.describe(c));
      fw.close();
    } catch (java.io.IOException ioe) {
      System.exit(-1);
    }
  }
}
