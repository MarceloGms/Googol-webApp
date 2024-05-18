package com.googol.googolfe.objects;

/**
 * The Top10Obj class represents an object containing the name (search) and count (nr of times searched) of a top 10 item.
 */
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
