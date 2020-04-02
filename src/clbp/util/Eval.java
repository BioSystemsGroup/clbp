/*
 * Copyright 2020 - Regents of the University of California, San
 * Francisco.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package clbp.util;

import org.graalvm.polyglot.Context;

public class Eval {
   public static Context graal = null;
   public static void initGraal() {
     graal = Context.newBuilder().allowAllAccess(true).build();
     java.util.Set<String> languages = graal.getEngine().getLanguages().keySet();
     //System.out.println("Current Languages available in GraalVM: " + languages);
   }
   public static void setSeed(long s) {
     graal.eval("R", "set.seed("+s+")");
   }
}
