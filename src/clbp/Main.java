/*
 * Copyright 2020 - Regents of the University of California, San
 * Francisco.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package clbp;

public class Main {
  public static final String MAJOR_VERSION = "CLBP-v0.0";
  public static final String MINOR_VERSION = "$Revision: 3 $";
  public static void main(String[] args) {
    // run the GUI?
    boolean useGUI = keyExists("-gui",args);
    clbp.ctrl.GUI gui = null;

    if (keyExists("-epf",args)) {
      System.out.println("Error!");
      System.exit(-1);
    }
    // run with custom parameters?
    java.io.File pd = null;
    String pd_name = null;
    String exp_name = null;
    String pf_name = null;
    java.io.File pf = null;
    if (keyExists("-pd",args)) {
      pd_name = argumentForKey("-pd", args, 0);
      pd = new java.io.File(pd_name);
      exp_name = pd.getName();
    } else {
      pd = new java.io.File("");
      pd_name = pd.getAbsolutePath()+java.io.File.separator+"cfg";
      exp_name = "cfg";
    }
    pf_name = pd_name+java.io.File.separator+"parameters.json";
    pf = new java.io.File(pf_name);
    if (!pf.exists()) throw new RuntimeException("Could not find default parameters file in "+pf_name);
    String expName = null;
    clbp.ctrl.Batch b = null;
    try {
      b = new clbp.ctrl.Batch(exp_name, pf);
    } catch (java.io.FileNotFoundException fnfe) {throw new RuntimeException("Couldn't launch Batch.",fnfe); }
    
    if (useGUI) {
      gui = new clbp.ctrl.GUI(b);
      gui.go();
    } else {
      b.load();
      b.go();
      b.finish();
    }
  }
  
  public static boolean keyExists(String key, String[] args) {
    for (String arg : args) if (arg.equalsIgnoreCase(key)) return true;
    return false;
  }

  public static String argumentForKey(String key, String[] args, int startingAt) {
    for (int x = 0; x < args.length - 1; x++) // key can't be the last string
      if (args[x].equalsIgnoreCase(key)) return args[x + 1];
    return null;
  }

}
