/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.startup;

import common.FileServer;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import server.controller.Controller;

/**
 * Entry point of the program
 * 
 * @author Griffone
 */
public class Server {
    
    public static final String FILES_DIR = "D:\\Projects\\Java\\HW3\\server_files\\";
    
    public static void main(String[] args) throws IOException {
        try {
            Server server = new Server();
            server.startRMIServant();
            System.out.println("File System Server started.");
        } catch (RemoteException | MalformedURLException ex) {
            System.err.println("Failed to start the server.");
        }
    }
    
    private void startRMIServant() throws RemoteException, MalformedURLException, IOException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException registry_is_not_running) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        Controller contr = new Controller(4269);
        Thread t = new Thread(contr.listener);
        t.setDaemon(true);
        t.start();
        Naming.rebind(FileServer.NAME_IN_REGISTRY, contr);
    }
}
