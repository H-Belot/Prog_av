// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


class Master {
   Master() {
   }

   public long doRun(int var1, int var2) throws InterruptedException, ExecutionException {
      long var3 = System.currentTimeMillis();
      ArrayList var5 = new ArrayList();

      for(int var6 = 0; var6 < var2; ++var6) {
         var5.add(new Worker(var1));
      }

      ExecutorService var15 = Executors.newFixedThreadPool(var2);
      List var7 = var15.invokeAll(var5);
      long var8 = 0L;

      Future var11;
      for(Iterator var10 = var7.iterator(); var10.hasNext(); var8 += (Long)var11.get()) {
         var11 = (Future)var10.next();
      }

      double var14 = 4.0 * (double)var8 / (double)var1 / (double)var2;
      long var12 = System.currentTimeMillis();
      System.out.println("\nPi : " + var14);
      PrintStream var10000 = System.out;
      double var10001 = Math.abs(var14 - Math.PI);
      var10000.println("Error: " + var10001 / Math.PI + "\n");
      System.out.println("Ntot: " + var1 * var2);
      System.out.println("Available processors: " + var2);
      System.out.println("Time Duration (ms): " + (var12 - var3) + "\n");
      System.out.println(Math.abs(var14 - Math.PI) / Math.PI + " " + var1 * var2 + " " + var2 + " " + (var12 - var3));
      var15.shutdown();
      return var8;
   }
}
