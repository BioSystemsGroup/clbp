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
import java.util.Map;

public class Comp implements sim.engine.Steppable {
  private Model callback = null; // callback
  public int id = -Integer.MAX_VALUE;
  String name = null;
  public String getName() { return name; }
  public java.util.LinkedHashMap<String, Double> variables = new java.util.LinkedHashMap<>(1);
  public boolean finished = false;
  public double executionTime = -1.0;
  public double updateInterval = 1.0;
  public ArrayList<String> assignments = new ArrayList<>();
  private ArrayList<String> rhses = new ArrayList<>();
  private ArrayList<String> lhses = new ArrayList<>();

  public Comp() {}
  public Comp(Model m, int ident, double start) {
    this();
    callback = m;
    id = ident;
    name = "dummyName-"+id;
    variables.put("dummy1", start);
    variables.put("dummy2", start);
    assignments.add("personality = age * bmi * sex");
    assignments.add("pain_beliefs = personality + smoking * bmi");
  }
  public void setCallback(Model m) { callback = m; }
  
  public void parse() {
    for (String a : assignments) {
      int lhsindex = a.indexOf('=');
      String lhs = a.substring(0, lhsindex).trim();
      String rhs = a.substring(lhsindex+1).trim();
      lhses.add(lhs);
      rhses.add(rhs);
    }
    clbp.ctrl.Batch.logln(name+".LHS = "+lhses.toString());
    clbp.ctrl.Batch.logln(name+".RHS = "+rhses.toString());
  }
  private void readAllMyVariables() {
    variables.keySet().stream().forEach((v) -> {variables.replace(v,callback.env.variables.get(v));});
  }
  private synchronized void writeLHS() {
//    clbp.ctrl.Batch.logln(name+" - Writing");
    java.util.HashMap<String,Double> writing = new java.util.HashMap<>(lhses.size());
    lhses.forEach((var) -> {
//      writing.put(var,variables.get(var));
      callback.env.variables.replace(var,variables.get(var));
    });
//    clbp.ctrl.Batch.logln(writing.toString());
  }
  
  @Override
  public void step(sim.engine.SimState state) {
//    clbp.ctrl.Batch.logln(name+".step()");
    if (this != callback.env) readAllMyVariables();
    
    for (int andx=0 ; andx<assignments.size() ; andx++) {
      String script_bound = rhses.get(andx);
      String lhskey = lhses.get(andx);
      for (Map.Entry<String,Double> me : variables.entrySet()) {
        script_bound = script_bound.replaceAll(me.getKey(), me.getValue().toString());
      }
//      clbp.ctrl.Batch.logln("Eval: "+script_bound);
      double result = clbp.util.Eval.graal.eval("R", script_bound).asDouble();
      variables.replace(lhskey, result);
    }
    
    if (this != callback.env) writeLHS();
    
  }
 
  public static void main(String[] args) throws java.io.FileNotFoundException {
    Comp c = new Comp(null,0,0.0);
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
