/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.startup;

import client.view.Client;
import client.view.Command;
import common.FileServer;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 *
 * @author Griffone
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            Command.initializeCommands();
            new Client(args[0], FileServer.FILE_TRANSFER_PORT).start();
        } catch (RemoteException | NotBoundException | MalformedURLException | UnknownHostException ex) {
            System.out.println("Could not start client!");
        }
    }
}
