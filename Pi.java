// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
public class Pi {
   public Pi() {
   }

   public static void main(String[] var0) throws Exception {
      long var1 = 0L;
      var1 = (new Master()).doRun(50000, 10);
      System.out.println("total from Master = " + var1);
   }
}
