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

public abstract class Obs implements sim.engine.Steppable {
  public static int VIEW_ORDER = clbp.model.Model.MODEL_ORDER+10;
  clbp.model.Model subject = null;
  String expName = null;
  java.io.PrintWriter outFile = null;
  clbp.ctrl.Parameters params = null;

  public Obs(String en, clbp.ctrl.Parameters p) {
    params = p;
    if (en != null && !en.equals("")) expName = en;
    else throw new RuntimeException("Experiment name cannot be null or empty.");
  }
  
  public void init(java.io.File dir, clbp.model.Model m) {
    if (m != null) subject = m;
    else throw new RuntimeException("Subject to Observe cannot be null.");
    
    // setup the output file
    if (dir != null && dir.exists()) {
      try {
        String fileName = dir.getCanonicalPath() + java.io.File.separator
                + clbp.ctrl.Batch.expName + "-"
                + m.getClass().getSimpleName() + "-"
                + this.getClass().getSimpleName()
                + ".csv";
        outFile = new java.io.PrintWriter(new java.io.File(fileName));
      } catch (java.io.IOException ioe) { throw new RuntimeException(ioe); }
    }
    
    writeHeader();
  }

  public void writeHeader() {
    StringBuilder sb = new StringBuilder("Time");
    subject.comps.stream().forEach((c) -> {
      sb.append(", Comp").append(c.id);
    });
    outFile.println(sb.toString());
  }

  public abstract java.util.ArrayList<Double> measure();
  
  @Override
  public void step(sim.engine.SimState state) {
    java.util.ArrayList<Double> data = measure();
    double t = state.schedule.getTime()/subject.cyclePerTime;
    StringBuilder sb = new StringBuilder(Double.toString(t));
    data.stream().forEach((v) -> { sb.append(", ").append(v); });
    outFile.println(sb.toString()); outFile.flush();

    if (!subject.finished) {
      state.schedule.scheduleOnce(this, VIEW_ORDER);
    }
  }
}
