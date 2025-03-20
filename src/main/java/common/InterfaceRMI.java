package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface InterfaceRMI extends Remote {
    void checkExistingModel() throws RemoteException;
    void envoyerDonneesPatient(String clientID, Patient patient) throws RemoteException;
    String predireObesite(String clientID, Patient patient) throws RemoteException;
    String getServerStatus() throws RemoteException;

    // FIX: Add RemoteException here
    String addPatient(String clientID, Patient p) throws RemoteException;
    String deletePatient(String clientID, int patientNumber) throws RemoteException;
    String modifyPatient(String clientID, int patientNumber, Patient updatedPatient) throws RemoteException;
    List<String> listPatients(String clientID) throws RemoteException;
}
