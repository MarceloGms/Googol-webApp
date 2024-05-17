package com.googol.googolfe;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * The interface for a Client of the search engine system.
 * This interface allows the Gateway to connect to the Client via RMI-callback.
 */
public interface IClient extends Remote {
  /**
   * Prints a message on the client's side.
   * @param s The message to print.
   * @throws RemoteException If a communication-related exception occurs.
   */
  public void printOnClient(String s) throws RemoteException;

  /**
   * Sends a list of the active barrels objects to the client.
   * @param brls The list of BrlObj objects to send.
   * @throws RemoteException If a communication-related exception occurs.
   */
  public void sendBrls(ArrayList<BrlObj> brls) throws RemoteException;
}