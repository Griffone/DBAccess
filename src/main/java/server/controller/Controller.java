/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import common.FileDTO;
import common.FileServer;
import common.exceptions.*;
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
import java.util.HashMap;
import java.util.List;
import server.integration.DataAccessObject;
import server.model.Account;

/**
 * The controlling code for the server.
 * 
 * @author Griffone
 */
public class Controller extends UnicastRemoteObject implements FileServer {

    private final DataAccessObject dao;
    private final ServerSocket listenSocket;
    private final HashMap<Long, Client> clients;
    private static long lastClientSessionID = 0;
    
    public Controller(int port) throws RemoteException, IOException {
        super();    // Required for the JRMI to function correctly
        dao = new DataAccessObject();
        listenSocket = new ServerSocket(port);
        clients = new HashMap();
    }
    
    @Override
    public SocketAddress getServerAddress() throws RemoteException, UnknownHostException {
        return new InetSocketAddress(InetAddress.getLocalHost(), listenSocket.getLocalPort());
    }

    @Override
    public void createAccount(String name, String password) throws RemoteException, AccountException {
        if (dao.findAccount(name, true) != null)
            throw new AccountException();
        else
            dao.createAccount(new Account(name, password));
    }
    
    @Override
    public void deleteAccount(Session session) throws RemoteException {
        if (session == null || !clients.containsKey(session.id))
            return;
        
        Account account = clients.get(session.id).account;
        if (account != null)
            dao.deleteAccount(account.username);
    }

    @Override
    public Session login(NotificationClient client, String name, String password) throws RemoteException, LoginException, SessionException {
        Account account = dao.findAccount(name, true);
        if (account == null)
            throw new LoginException();
        if (account.password.compareTo(password) != 0)
            throw new LoginException();
        
        // Extremely unlikely the server is stable enough to handle a total of 9,223,372,036,854,775,807 clients, but this isn't very expensive check
        if (++lastClientSessionID == 0)
            lastClientSessionID++;
        Session session = new Session(lastClientSessionID);
        if (clients.containsKey(session.id))
            throw new SessionException();
        
        clients.put(session.id, new Client(account, client));
        return session;
    }

    @Override
    public void logout(Session session) throws RemoteException, SessionException {
        if (session == null || !clients.containsKey(session.id))
            throw new SessionException();
        
        clients.remove(session.id);
    }

    @Override
    public void createFile(Session session, String name, boolean isPublic, boolean isReadOnly) throws RemoteException, SessionException, FileException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateFile(Session session, String name) throws RemoteException, SessionException, FileException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateFileDetails(Session session, String name, boolean isPublic, boolean isReadOnly) throws RemoteException, SessionException, FileException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void renameFile(Session session, String originalName, String newName) throws RemoteException, SessionException, FileException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteFile(Session session, String name) throws RemoteException, SessionException, FileException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<FileDTO> getFiles(Session session) throws RemoteException, SessionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
