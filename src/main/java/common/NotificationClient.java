/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Griffone
 */
public interface NotificationClient extends Remote {
    
    /**
     * Push notification to this client, when a file was accessed
     * 
     * @param file - the file that was modified
     * @param action - the action that was taken
     * @param performer - the account of the user who performed the action
     * @throws RemoteException 
     */
    public void pushNotification(FileDTO file, String action, AccountDTO performer) throws RemoteException;
}
