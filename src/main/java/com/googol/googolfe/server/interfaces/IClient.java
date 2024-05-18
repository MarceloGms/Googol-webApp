package com.googol.googolfe.server.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import com.googol.googolfe.objects.BrlObj;
import com.googol.googolfe.objects.Top10Obj;

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

  /**
   * Sends the top 10 search results to the client.
   * @param top10 The list of Top10Obj objects to send.
   * @throws RemoteException If a communication-related exception occurs.
   */
  public void sendTop10(ArrayList<Top10Obj> top10) throws RemoteException;
}