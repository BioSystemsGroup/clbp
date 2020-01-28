/*
 * Copyright 2020 - Regents of the University of California, San
 * Francisco.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package clbp.ctrl;

import clbp.view.ObsVar;

public class Batch {
  long seed = -Integer.MAX_VALUE;
  Parameters params = null;
  public sim.engine.SimState state = null;
  public static String expName = "notset";
  private static String out_dir_name = null;
  static java.io.File dir = null;
  clbp.model.Model model = null;
  String paramString;
  
  public Batch(String en, java.io.InputStream pf) {
    if (en != null && !en.equals("")) expName = en;
    else throw new RuntimeException("Experiment name cannot be null or empty.");
    paramString = new java.util.Scanner(pf).useDelimiter("\\A").next();
    state = new sim.engine.SimState(System.currentTimeMillis());
  }
  public final void load() {
    out_dir_name = setupOutput(expName);
    clbp.ctrl.Parameters p = clbp.ctrl.Parameters.readOneOfYou(paramString);
    params = p;
    Number seed_n = params.batch.get("seed");
    if (seed_n != null) {
      seed = seed_n.longValue();
      state.random = new ec.util.MersenneTwisterFast(seed);
    } else {
      seed = state.seed();
      params.batch.put("seed",seed);
    }

    writeParameters(out_dir_name, params);

    model = new clbp.model.Model(params);
    model.init(state, params.model.get("timeLimit").doubleValue(), params.model.get("cyclePerTime").doubleValue());
    model.instantiate();
    // schedule the components
    model.comps.forEach((c) -> { state.schedule.scheduleOnce(c,clbp.model.Model.MODEL_ORDER); });
    // schedule the model
    state.schedule.scheduleOnce(model,clbp.model.Model.MODEL_ORDER);
        
    // schedule an observer
    ObsVar ov = new ObsVar(expName, p);
    ov.init(dir, model);
    state.schedule.scheduleOnce(ov,clbp.view.Obs.VIEW_ORDER);
  }
  
  public void go() {
    while (!model.finished || !model.finished)
      state.schedule.step(state);
    log("Batch.go() - Submodels are finished!");
  }
  
  public void finish() {
    log.close();
  }

  private static java.io.PrintWriter log = null;
  public static void log(String entry) { log.println(entry); log.flush(); }
  
  static String setupOutput(String en) {
    final String DATE_FORMAT = "yyyy-MM-dd-HHmmss";
    final java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
    StringBuffer date_s = new StringBuffer("");
    sdf.format(new java.util.Date(System.currentTimeMillis()), date_s,
            new java.text.FieldPosition(0));
    String dirName = date_s.toString();

    // create a directory using the current date and time
    dir = new java.io.File(dirName);
    if (!dir.exists()) dir.mkdir();
    
    // initialize the output for run-time messages
    try {
      log = new java.io.PrintWriter(new java.io.File(dirName
              + java.io.File.separator + en + "-output.txt"));
    } catch (java.io.FileNotFoundException fnfe) {
      throw new RuntimeException("Couldn't open " + dirName
              + java.io.File.separator + en + ".txt", fnfe);
    }
    return dirName;
  }
  
  private static void writeParameters(String dirName, Parameters p) {
    try {
      java.io.FileWriter fw = new java.io.FileWriter(new java.io.File(dirName 
              + java.io.File.separator 
              + "parameters-" + clbp.Main.MAJOR_VERSION + "-" + System.currentTimeMillis()+".json"));
      p.version = clbp.Main.MAJOR_VERSION+" Subversion"+clbp.Main.MINOR_VERSION;
      fw.write(p.describe());
      fw.close();
    } catch (java.io.IOException ioe) {
      System.exit(-1);
    }
  }
  
  public double getMaxCycle() {
    return Math.max(model.timeLimit*model.cyclePerTime, model.timeLimit*model.cyclePerTime);
  }
}
