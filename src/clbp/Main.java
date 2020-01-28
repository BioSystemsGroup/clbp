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
  public static final String MAJOR_VERSION = "TUT-v0.8";
  public static final String MINOR_VERSION = "$Revision: 787 $";
  public static void main(String[] args) {
    // run the GUI?
    boolean useGUI = keyExists("-gui",args);
    clbp.ctrl.GUI gui = null;

    if (keyExists("-epf",args)) {
      System.out.println(new clbp.ctrl.Parameters().describe());
      System.exit(-1);
    }
    // run with custom parameters?
    java.io.InputStream pf = null;
    String fileName = "/parameters.json";
    if (keyExists("-pf",args)) {
      fileName = argumentForKey("-pf", args, 0);
      try {
        pf = new java.io.FileInputStream(new java.io.File(fileName));
      } catch (java.io.FileNotFoundException fnfe) { 
        throw new RuntimeException(fnfe);
      }
    } else {
      pf = Main.class.getResourceAsStream(fileName);
      if (pf == null) throw new RuntimeException("Could not find default parameters file in CLASSPATH.");
    }
    String expName = null;
    if (fileName != null && !fileName.equals("")) expName = fileName.substring(fileName.lastIndexOf('/'),fileName.indexOf(".json"));
    clbp.ctrl.Batch b = new clbp.ctrl.Batch(expName, pf);
    
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
