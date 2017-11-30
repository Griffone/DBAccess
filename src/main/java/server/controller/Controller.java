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
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import server.controller.Transaction.TransactionType;
import server.integration.DataAccessObject;
import server.model.AccountModel;
import server.model.FileModel;

/**
 * The controlling code for the server.
 * 
 * @author Griffone
 */
public class Controller extends UnicastRemoteObject implements FileServer {

    public final ListenThread listener;
    private final DataAccessObject dao;
    private final Map<Long, Client> clients;
    private static long lastClientSessionID = 0;
    private static long lastTransactionID = 0;
    
    public Controller(int port) throws RemoteException, IOException {
        super();    // Required for the JRMI to function correctly
        listener = new ListenThread(port);
        dao = new DataAccessObject();
        clients = Collections.synchronizedMap(new HashMap());
    }

    @Override
    public void createAccount(String name, String password) throws RemoteException, AccountException {
        if (dao.findAccount(name, true) != null)
            throw new AccountException();
        else
            dao.createAccount(new AccountModel(name, password));
    }
    
    @Override
    public void deleteAccount(Session session) throws RemoteException {
        if (session == null || !clients.containsKey(session.id))
            return;
        
        AccountModel account = clients.get(session.id).account;
        if (account != null)
            dao.deleteAccount(account.getName());
    }

    @Override
    public Session login(NotificationClient client, String name, String password) throws RemoteException, LoginException, SessionException {
        try {
            AccountModel account = dao.findAccount(name, false);
            if (account == null)
                throw new LoginException();
            if (!account.isPassword(password))
                throw new LoginException();

            // Extremely unlikely the server is stable enough to handle a total of 9,223,372,036,854,775,807 clients, but this isn't very expensive check
            if (++lastClientSessionID == 0)
                lastClientSessionID++;
            Session session = new Session(lastClientSessionID);
            if (clients.containsKey(session.id))
                throw new SessionException();

            account.updateSessionID(session.id);
            clients.put(session.id, new Client(account, client));
            return session;
        } finally {
            dao.updateEntity();
        }
    }

    @Override
    public void logout(Session session) throws RemoteException, SessionException {
        if (session == null || !clients.containsKey(session.id))
            throw new SessionException();
        
        clients.remove(session.id);
    }

    @Override
    public long createFile(Session session, String name, boolean isPublic, boolean isReadOnly) throws RemoteException, SessionException, FileException {
        if (session == null || !clients.containsKey(session.id))
            throw new SessionException();
        
        if (name == null)
            throw new NullPointerException();
        
        FileModel file = dao.findFile(name, true);
        if (file != null)
            throw new FileException();
        
        Client client = clients.get(session.id);
        file = new FileModel(client.account, name, 0, isPublic, isReadOnly);
        if (++lastTransactionID == 0)
            lastTransactionID++;
        long transactionID = lastTransactionID;
        listener.transactions.put(transactionID, new Transaction(TransactionType.TT_CLIENT_TO_SERVER, clients.get(session.id), file, dao, false));
        return transactionID;
    }
    
    @Override
    public long downloadFile(Session session, String name) throws RemoteException, SessionException, FileException, PermissionException {
        if (session == null || !clients.containsKey(session.id))
            throw new SessionException();
        
        if (name == null)
            throw new NullPointerException();
        
        FileModel file = dao.findFile(name, true);
        if (file == null)
            throw new FileException();
        
        Client client = clients.get(session.id);
        if (!file.isPublic() && client.account.getName().equals(file.getOwnerName()))
            throw new PermissionException();
        
        if (++lastTransactionID == 0)
            lastTransactionID++;
        long transactionID = lastTransactionID;
        listener.transactions.put(transactionID, new Transaction(TransactionType.TT_SERVER_TO_CLIENT, client, file, dao, true));
        notifyOwner(file, client, "downloaded");
        return transactionID;
    }

    @Override
    public long updateFile(Session session, String name) throws RemoteException, SessionException, FileException, PermissionException {
        if (session == null || !clients.containsKey(session.id))
            throw new SessionException();
        
        if (name == null)
            throw new NullPointerException();
        
        FileModel file = dao.findFile(name, true);
        if (file == null)
            throw new FileException();
        
        Client client = clients.get(session.id);
        if ((!file.isPublic() || file.isReadOnly()) && !client.account.getName().equals(file.getOwnerName()))
            throw new PermissionException();
        
        if (++lastTransactionID == 0)
            lastTransactionID++;
        long transactionID = lastTransactionID;
        listener.transactions.put(transactionID, new Transaction(TransactionType.TT_CLIENT_TO_SERVER, client, file, dao, true));
        notifyOwner(file, client, "updated");
        return transactionID;
    }

    @Override
    public void updateFileDetails(Session session, String name, boolean isPublic, boolean isReadOnly) throws RemoteException, SessionException, FileException, PermissionException {
        if (session == null || !clients.containsKey(session.id))
            throw new SessionException();
        
        if (name == null)
            throw new NullPointerException();
        
        try {
            FileModel file = dao.findFile(name, false);
            if (file == null)
                throw new FileException();

            Client client = clients.get(session.id);
            if (!client.account.getName().equals(file.getOwnerName()))
                throw new PermissionException();

            file.setPublic(isPublic);
            file.setReadOnly(isReadOnly);
        } finally {
            dao.updateEntity();
        }
    }

    @Override
    public void renameFile(Session session, String originalName, String newName) throws RemoteException, SessionException, FileException, PermissionException {
        if (session == null || !clients.containsKey(session.id))
            throw new SessionException();
        
        if (originalName == null)
            throw new NullPointerException();
        
        try {
            FileModel file = dao.findFile(originalName, false);
            if (file == null)
                throw new FileException();

            Client client = clients.get(session.id);
            if ((!file.isPublic() || file.isReadOnly()) && !client.account.getName().equals(file.getOwnerName()))
                throw new PermissionException();

            notifyOwner(file, client, "renamed to " + newName);
            file.rename(newName);
        } finally {
            dao.updateEntity();
        }
    }

    @Override
    public void deleteFile(Session session, String name) throws RemoteException, SessionException, FileException, PermissionException {
        if (session == null || !clients.containsKey(session.id))
            throw new SessionException();
        
        if (name == null)
            throw new NullPointerException();
        
        FileModel file = dao.findFile(name, true);
        if (file == null)
            throw new FileException();

        Client client = clients.get(session.id);
        if ((!file.isPublic() || file.isReadOnly()) && !client.account.getName().equals(file.getOwnerName()))
            throw new PermissionException();
        
        notifyOwner(file, client, "deleted");
        dao.deleteFile(name);
    }

    @Override
    public List<FileDTO> getFiles(Session session) throws RemoteException, SessionException {
        if (session == null || !clients.containsKey(session.id))
            throw new SessionException();
        
        List<FileModel> files = dao.getFiles();
        List<FileDTO> sendFiles = new LinkedList();
        Client client = clients.get(session.id);
        for (FileModel file : files)
            if (file.isPublic() || client.account.getName().equals(file.getOwnerName()))
                sendFiles.add(file.toDTO());
        return sendFiles;
    }

    @Override
    public void enableNotification(Session session, String name) throws RemoteException, SessionException, FileException, PermissionException {
        if (session == null || !clients.containsKey(session.id))
            throw new SessionException();
        
        if (name == null)
            throw new NullPointerException();
        
        try {
            FileModel file = dao.findFile(name, false);
            if (file == null)
                throw new FileException();

            Client client = clients.get(session.id);
            if (!client.account.getName().equals(file.getOwnerName()))
                throw new PermissionException();

            file.setNotifications(true);
        } finally {
            dao.updateEntity();
        }
    }
    
    private void notifyOwner(FileModel file, Client client, String action) throws RemoteException {
        if (file.getOwnerName() == null || client == null || action == null)
            throw new NullPointerException();
        
        if (!file.isNotificationEnabled() || client.account.getName().equals(file.getOwnerName()))
            return;
      
        Client ownerClient = clients.get(file.getOwnerSessionID());
        if (ownerClient != null)
            ownerClient.client.pushNotification(file.toDTO(), action, client.account.toDTO());
    }
    
}
