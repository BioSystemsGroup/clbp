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

import clbp.model.Comp;

import java.util.Map;
import java.util.ArrayList;

public abstract class Obs implements sim.engine.Steppable {
  public static int VIEW_ORDER = clbp.model.Model.MODEL_ORDER+10;
  clbp.model.Model subject = null;
  String expName = null;
  java.io.File out_dir = null;
  clbp.ctrl.Parameters params = null;

  public Obs(String en, clbp.ctrl.Parameters p) {
    params = p;
    if (en != null && !en.equals("")) expName = en;
    else throw new RuntimeException("Experiment name cannot be null or empty.");
  }
  
  public void init(java.io.File dir, clbp.model.Model m) {
    out_dir = dir;
    if (m != null) subject = m;
    else throw new RuntimeException("Subject to Observe cannot be null.");
  }

  public abstract void writeHeaders();
  public abstract Map<Comp, ArrayList<Double>> measure();
  
  @Override
  public abstract void step(sim.engine.SimState state);
}
