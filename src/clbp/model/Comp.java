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

public class Comp implements sim.engine.Steppable {
  public int id = -Integer.MAX_VALUE;
  public java.util.LinkedHashMap<String, sim.util.MutableDouble> variables = new java.util.LinkedHashMap<>(1);
  public boolean finished = false;
  
  public Comp(int ident, double start) {
    id = ident;
    variables.put("Variable1", new sim.util.MutableDouble(start));
    clbp.ctrl.Batch.log("Comp("+id+", "+start+")");
  }
  
  @Override
  public void step(sim.engine.SimState state) {
    clbp.ctrl.Batch.log("incrementing"+variables.get("Variable1"));
    variables.get("Variable1").val++;
    if (!finished)
      state.schedule.scheduleOnce(this,clbp.model.Model.SUB_ORDER);
  }
}
