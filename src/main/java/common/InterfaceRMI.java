package common;

import java.rmi.*;

public interface InterfaceRMI extends Remote {
    void envoyerDonneesPatient(Patient patient) throws RemoteException;
    String predireObesite(Patient patient) throws RemoteException;
    String getServerStatus() throws RemoteException;
}