package com.googol.googolfe;

public class Top10Obj implements java.io.Serializable {
   private String name;
   private int count;

   public Top10Obj(String name, int count) {
      this.name = name;
      this.count = count;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getCount() {
      return count;
   }

   public void setCount(int count) {
      this.count = count;
   }
}
