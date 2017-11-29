/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import common.exceptions.*;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The interface for Server-side methods to invoke
 *
 * @author Griffone
 */
public interface FileServer extends Remote {
    
    public static final String NAME_IN_REGISTRY = "FileServer";
    
    /**
     * Get the server's socket address for file transfer
     * 
     * @return SocketAddress of the server's listening socket
     * @throws RemoteException - rmi thrown exception
     * @throws UnknownHostException - could not resolve local host address
     */
    public SocketAddress getServerAddress() throws RemoteException, UnknownHostException;
    
    /**
     * Create a new acccount with provided name and password.
     * Does not log in, the client must explicitly call login instead
     * 
     * @param name the username
     * @param password password
     * @throws RemoteException - rmi thrown exception
     * @throws AccountException - could not create account with the provided name
     */
    public void createAccount(String name, String password) throws RemoteException, AccountException;
    
    /**
     * Delete the current account.
     * Invalidates the porovided session
     * 
     * @param session current session
     * @throws RemoteException - rmi thrown exception
     * @throws SessionException - something wrong with session handle
     */
    public void deleteAccount(Session session) throws RemoteException, SessionException;
    
    /**
     * Attempt to login
     * 
     * @param client the client-side remote method definitions reference
     * @param name the username of the account to login into
     * @param password the password of the account to login into
     * @return the new session identifier or null if the name was wrong
     * @throws RemoteException - rmi thrown exception
     * @throws LoginException - wrong username-password combo
     * @throws SessionException - something went wrong trying to create a new session handle
     */
    public Session login(NotificationClient client, String name, String password) throws RemoteException, LoginException, SessionException;
    
    /**
     * Log out.
     * Invalidates the provided session
     * 
     * @param session current session
     * @throws RemoteException - rmi thrown exception
     * @throws SessionException - something wrong with session handle
     */
    public void logout(Session session) throws RemoteException, SessionException;
    
    /**
     * Attempt to create a new file on the server.
     * Should then be followed by the file transaction, as the rmi does not transfer the file.
     * 
     * @param session current session id
     * @param name the name of the file
     * @param isPublic should the public be listed to other accounts?
     * @param isReadOnly should other accounts only be able to read this file? Only necessary for public files
     * @throws RemoteException - rmi thrown exception
     * @throws SessionException - something wrong with session handle
     * @throws FileException - something is wrong with the file
     */
    public void createFile(Session session, String name, boolean isPublic, boolean isReadOnly) throws RemoteException, SessionException, FileException;
    
    /**
     * Attempt to flag a file for an update
     * Should be followed by a file transaction
     * 
     * @param session current session id
     * @param name the name of the file to re-upload
     * @throws RemoteException - rmi thrown exception
     * @throws SessionException - something wrong with session handle
     * @throws FileException - file with the provided name could not be found / current session doesn't have correct rights
     */
    public void updateFile(Session session, String name) throws RemoteException, SessionException, FileException;
    
    /**
     * Update file's access details
     * Can only be done by the file's owner even if the file is public
     * 
     * @param session current session
     * @param name the name of the file
     * @param isPublic should the public be listed to other accounts?
     * @param isReadOnly should other accounts only be able to read this file? Only necessary for public files
     * @throws RemoteException - rmi thrown exception
     * @throws SessionException - something wrong with session handle
     * @throws FileException - file with the provided name could not be found / current session doesn't have correct rights
     */
    public void updateFileDetails(Session session, String name, boolean isPublic, boolean isReadOnly) throws RemoteException, SessionException, FileException;
    
    /**
     * Rename a file
     * 
     * @param session current session
     * @param originalName the name of the file to rename
     * @param newName the new name to rename to
     * @throws RemoteException - rmi thrown exception
     * @throws SessionException - something wrong with session handle
     * @throws FileException - file with the original name could not be found / current session doesn't have correct rights
     */
    public void renameFile(Session session, String originalName, String newName) throws RemoteException, SessionException, FileException;
    
    /**
     * Delete a file
     * 
     * @param session current session
     * @param name the name of the file to delete
     * @throws RemoteException - rmi thrown exception
     * @throws SessionException - something wrong with session handle
     * @throws FileException - file with the provided name could not be found / current session doesn't have correct rights
     */
    public void deleteFile(Session session, String name) throws RemoteException, SessionException, FileException;
    
    /**
     * Retreive a list of files that can be accessed by current acount
     * 
     * @param session current session
     * @return a list of FileDTO with details of files that the account has access to
     * @throws RemoteException - rmi thrown exception
     * @throws SessionException - something wrong with session handle
     */
    public List<FileDTO> getFiles(Session session) throws RemoteException, SessionException;
}
