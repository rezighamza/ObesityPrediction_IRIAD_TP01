package model;

import common.Patient;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ModeleIAInterface extends Remote {
    public void trainModele(List<Patient> patients) throws RemoteException;
    String prédire(Patient patient) throws RemoteException;
}
