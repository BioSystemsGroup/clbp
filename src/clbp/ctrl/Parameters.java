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

import java.util.Map;
import java.util.HashMap;

public class Parameters {
  String version = "";
  public Map<String,Number> batch = new HashMap<>(2);
  public Map<String,Number> model = new HashMap<>(2);
  
  public Parameters() {
    batch.put("seed",-Long.MAX_VALUE);
    
    model.put("timeLimit",-Long.MAX_VALUE);
    model.put("cyclePerTime",Double.MIN_NORMAL);
  }
  
  public static Parameters readOneOfYou(String json) {
    com.owlike.genson.Genson g = new com.owlike.genson.Genson();
    Parameters p = g.deserialize(json, Parameters.class);
    if (p == null || !p.test()) clbp.ctrl.Batch.logln("Parameter file has missing values.");
    return p;
  }
  private boolean test(String name, Map<String,Number> ref, Map<String,Number> map) {
    java.util.Optional<Map.Entry<String, Number>> broken = ref.entrySet().stream().filter(me -> map.get(me.getKey()) == null).findAny();
    if (broken.isPresent())
      clbp.ctrl.Batch.logln(name+"."+broken.get().getKey()+" not found in parameter file.");
    return broken.isPresent();
  }
  private boolean test() {
    boolean retVal = true;
    Parameters testP = new Parameters();
    if (test("batch", testP.batch, batch)) retVal = false;
    return retVal;
  }

  public static void main(String[] args) throws java.io.FileNotFoundException {
    java.io.File f = new java.io.File(args[0]);
    String json = new java.util.Scanner(f).useDelimiter("\\A").next();
    Parameters p = readOneOfYou(json);
    System.out.println(Batch.describe(p));
  }
  
}
