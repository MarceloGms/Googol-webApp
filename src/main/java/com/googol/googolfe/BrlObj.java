package com.googol.googolfe;

public class BrlObj implements java.io.Serializable {
   int id;
   double time;

   public BrlObj(int id, double time) {
      this.id = id;
      this.time = time;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public double getTime() {
      return time;
   }

   public void setTime(double time) {
      this.time = time;
   }
}
