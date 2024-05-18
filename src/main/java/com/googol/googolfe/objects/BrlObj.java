package com.googol.googolfe.objects;

/**
 * The BrlObj class represents a Barrel object with an ID and time.
 */
public class BrlObj implements java.io.Serializable {
   int id;
   double time;

   /**
    * Constructs a BrlObj with the specified ID and time.
    * @param id the ID of the barrel
    * @param time the time associated with the barrel
    */
   public BrlObj(int id, double time) {
      this.id = id;
      this.time = time;
   }

   /**
    * Retrieves the ID of the barrel.
    * @return the ID
    */
   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   /**
    * Retrieves the time associated with the barrel.
    * @return the time
    */
   public double getTime() {
      return time;
   }

   public void setTime(double time) {
      this.time = time;
   }
}
