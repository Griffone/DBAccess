/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import common.FileDTO;
import common.FileServer;
import common.NotificationClient;
import common.Session;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.integration.DataAccessObject;

/**
 * The controlling code for the server.
 * 
 * @author Griffone
 */
public class Controller extends UnicastRemoteObject implements FileServer {

    private final DataAccessObject dao;
    private final ServerSocket listenSocket;
    
    public Controller(int port) throws RemoteException, IOException {
        super();    // Required for the JRMI to function correctly
        dao = new DataAccessObject();
        listenSocket = new ServerSocket(port);
    }
    
    @Override
    public SocketAddress getServerAddress() throws RemoteException, UnknownHostException {
        return new InetSocketAddress(InetAddress.getLocalHost(), listenSocket.getLocalPort());
    }

    @Override
    public boolean createAccount(String name, String password) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void deleteAccount(Session session) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Session login(NotificationClient client, String name, String password) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void logout(Session session) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean createFile(Session session, String name, boolean isPublic, boolean isReadOnly) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updateFile(Session session, String name) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updateFileDetails(Session session, String name, boolean isPublic, boolean isReadOnly) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean renameFile(Session session, String originalName, String newName) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteFile(Session session, String name) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<FileDTO> getFiles(Session session) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
