// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).

import java.util.Random;
import java.util.concurrent.Callable;

class Worker implements Callable<Long> {
   private int numIterations;

   public Worker(int var1) {
      this.numIterations = var1;
   }

   public Long call() {
      long var1 = 0L;
      Random var3 = new Random();

      for(int var4 = 0; var4 < this.numIterations; ++var4) {
         double var5 = var3.nextDouble();
         double var7 = var3.nextDouble();
         if (var5 * var5 + var7 * var7 < 1.0) {
            ++var1;
         }
      }

      return var1;
   }
}
