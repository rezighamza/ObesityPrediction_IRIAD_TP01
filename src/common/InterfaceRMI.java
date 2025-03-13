package common;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceRMI extends Remote {
    void envoyerDonneesPatient(Patient patient) throws RemoteException;
    String predireObesite(Patient patient) throws RemoteException;
}
