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
import java.util.Objects;

public class Parameters {
  String version = "";
  public Map<String,Number> batch = new HashMap<>(2);
  public Map<String,Number> model = new HashMap<>(2);
  
  public Parameters() {
    batch.put("seed",-Long.MAX_VALUE);
    
    model.put("timeLimit",-Long.MAX_VALUE);
    model.put("cyclePerTime",Double.MIN_NORMAL);
  }
  
  public static class Edge {
    String from = "s";
    String to = "t";
    public Edge() {}
    public Edge(String s, String t) {
      from = s; to = t;
    }

    @Override
    public int hashCode() {
      return from.hashCode()+to.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Edge other = (Edge) obj;
      if (!Objects.equals(this.from, other.from)) {
        return false;
      }
      if (!Objects.equals(this.to, other.to)) {
        return false;
      }
      return true;
    }
    
  }
  
  private Map<Edge,Double> specNet() {
    Map<Edge,Double> m = new HashMap<>(4);
    m.put(new Edge("source","central"), Double.MIN_NORMAL);
    m.put(new Edge("central","periph"), Double.MIN_NORMAL);
    m.put(new Edge("periph","central") , Double.MIN_NORMAL);
    m.put(new Edge("central","sink"), Double.MIN_NORMAL);
    return m;
  }
  
  public String describe() {
    com.owlike.genson.Genson g = new com.owlike.genson.Genson();
    String json = g.serialize(this);
    return json;
  }
  public static Parameters readOneOfYou(String json) {
    com.owlike.genson.Genson g = new com.owlike.genson.Genson();
    Parameters p = g.deserialize(json, Parameters.class);
    if (p == null || !p.test()) System.out.println("Parameter file has missing values.");
    return p;
  }
  
  private boolean test(String name, Map<String,Number> ref, Map<String,Number> map) {
    java.util.Optional<Map.Entry<String, Number>> broken = ref.entrySet().stream().filter(me -> map.get(me.getKey()) == null).findAny();
    if (broken.isPresent())
      clbp.ctrl.Batch.log(name+"."+broken.get().getKey()+" not found in parameter file.");
    return broken.isPresent();
  }
  private boolean testNet(String key, Map<String,Map<Edge,Double>> map) {
    boolean retVal = false;
    if (map.isEmpty() || !map.containsKey(key)) {
      retVal = true;
      clbp.ctrl.Batch.log(key+" map of Edges -> Doubles is absent.");
    }
    return retVal;
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
    System.out.println(p.describe());
  }
  
}
