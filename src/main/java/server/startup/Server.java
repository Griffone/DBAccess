/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.startup;

import common.FileServer;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Entry point of the program
 * 
 * @author Griffone
 */
public class Server {
    
    public static final String FILES_DIR = "D:\\Projects\\Java\\HW3\\server_files\\";
    
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.startRMIServant();
            System.out.println("File System Server started.");
        } catch (RemoteException | MalformedURLException ex) {
            System.err.println("Failed to start bank server.");
        }
    }
    
    private void startRMIServant() throws RemoteException, MalformedURLException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException registry_is_not_running) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
//        Controller contr = new Controller();
//        Naming.rebind(FileServer.NAME_IN_REGISTRY, contr);
    }
}
