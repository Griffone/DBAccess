/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import common.AccountDTO;
import common.FileDTO;
import common.NotificationClient;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Griffone
 */
public class NotificationPusher extends UnicastRemoteObject implements NotificationClient {

    private final ThreadSafeOut out;
    
    public NotificationPusher(ThreadSafeOut out) throws RemoteException {
        super();
        this.out = out;
    }
    
    @Override
    public void pushNotification(FileDTO file, String action, AccountDTO performer) throws RemoteException {
        out.println("File " + file.name + " was " + action + " by " + performer.username);
    }
    
}
