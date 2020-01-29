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

import java.util.Map;

public class Comp implements sim.engine.Steppable {
  public int id = -Integer.MAX_VALUE;
  public java.util.LinkedHashMap<String, Double> variables = new java.util.LinkedHashMap<>(1);
  public boolean finished = false;

  public Comp() { }  
  public Comp(int ident, double start) {
    id = ident;
    variables.put("dummy1", start);
    variables.put("dummy2", start);
    clbp.ctrl.Batch.log("Comp("+id+", "+start+")");
  }
  
  @Override
  public void step(sim.engine.SimState state) {
    for (Map.Entry<String,Double> me : variables.entrySet()) {
      variables.replace(me.getKey(), me.getValue()+1);
    }
    if (!finished)
      state.schedule.scheduleOnce(this,clbp.model.Model.SUB_ORDER);
  }
  
}
