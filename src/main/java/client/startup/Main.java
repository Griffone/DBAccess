/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.startup;

import client.view.Client;
import client.view.Command;
import java.net.MalformedURLException;
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
            new Client(args[0]).start();
        } catch (RemoteException | NotBoundException | MalformedURLException ex) {
            System.out.println("Could not start client!");
        }
    }
}
