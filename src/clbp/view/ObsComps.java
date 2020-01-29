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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import clbp.model.Comp;

public class ObsComps extends Obs {
  private final boolean fraction = false;
  Map<Comp,PrintWriter> outFiles = new HashMap<>();

  public ObsComps(String en, clbp.ctrl.Parameters p) {
    super(en,p);
  }
  public void init(java.io.File dir, clbp.model.Model m) {
    super.init(dir,m);
    for (Comp c : subject.comps) {
      String fileName = fileNamePrefix + "-Comp:"+c.id+".csv";
      try {
         PrintWriter outFile = new PrintWriter(new java.io.File(fileName));
         outFiles.put(c,outFile);
      } catch (java.io.IOException ioe) { throw new RuntimeException("Couldn't open output file for Comp:"+c.id, ioe); }
    }
    writeHeaders();
  }
  
  @Override
  public void writeHeaders() {
    // write the output file headers
    StringBuilder sb = new StringBuilder("Time");
    if (subject == null) System.err.println("subject == null");
    if (subject.comps == null) System.err.println("subject.comps == null");
    subject.comps.stream().forEach((c) -> {
      for (Map.Entry<String,Double> me : c.variables.entrySet()) {
        sb.append(", ").append(me.getKey());
      }
      System.out.println(outFiles.get(c));
      outFiles.get(c).println(sb.toString());
    });
  }
  
  @Override
  public Map<Comp, ArrayList<Double>> measure() {
    Map<Comp, ArrayList<Double>> retVal = new HashMap<>();
    subject.comps.stream().forEach((c) -> {
      ArrayList<Double> perCompResults = new ArrayList<>();
      for (Map.Entry<String,Double> me : c.variables.entrySet()) {
        double result = me.getValue();
        perCompResults.add(result);
      }
      retVal.put(c, perCompResults);
    });
    return retVal;
  }
  
  public void step(sim.engine.SimState state) {
    Map<Comp, ArrayList<Double>> data = measure();
    double t = state.schedule.getTime()/subject.cyclePerTime;
    for (Map.Entry<Comp, ArrayList<Double>> measpercomp : data.entrySet()) {
      StringBuilder sb = new StringBuilder(Double.toString(t));
      Comp c = measpercomp.getKey();
      for (Map.Entry<String,Double> compvar : c.variables.entrySet()) {
        sb.append(", ").append(compvar.getValue());
      }
      PrintWriter outFile = outFiles.get(c);
      outFile.println(sb.toString());
      outFile.flush();
    }
    if (!subject.finished) {
      state.schedule.scheduleOnce(this, VIEW_ORDER);
    }
  }
  
}
