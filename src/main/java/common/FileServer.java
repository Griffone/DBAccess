/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.net.SocketAddress;
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
     * @throws RemoteException 
     */
    public SocketAddress getServerAddress() throws RemoteException;
    
    /**
     * Create a new acccount with provided name and password.
     * Does not log in, the client must explicitly call login instead
     * 
     * @param name the username
     * @param password password
     * @return true if a new account was sucessfully created
     * @throws RemoteException - rmi thrown exception
     */
    public boolean createAccount(String name, String password) throws RemoteException;
    
    /**
     * Attempt to login
     * 
     * @param client the client-side remote method definitions reference
     * @param name the username of the account to login into
     * @param password the password of the account to login into
     * @return session id for a connection on successful login, 0 otherwise
     * @throws RemoteException - rmi thrown exception
     */
    public Session login(NotificationClient client, String name, String password) throws RemoteException;
    
    /**
     * Log out.
     * Invalidates the provided sessionID
     * 
     * @param session
     * @throws RemoteException 
     */
    public void logout(Session session) throws RemoteException;
    
    /**
     * Attempt to create a new file on the server.
     * Should then be followed by the file transaction, as the rmi does not transfer the file.
     * 
     * @param session current session id
     * @param name the name of the file
     * @param isPublic should the public be listed to other accounts?
     * @param isReadOnly should other accounts only be able to read this file? Only necessary for public files
     * @return true if a file can be created and a transaction should follow. False if a file with given name cannot be created
     * @throws RemoteException - rmi thrown exception
     */
    public boolean createFile(Session session, String name, boolean isPublic, boolean isReadOnly) throws RemoteException;
    
    /**
     * Attempt to flag a file for an update
     * Should be followed by a file transaction
     * 
     * @param session current session id
     * @param name the name of the file to re-upload
     * @return true if a file with provided name can be uploaded and a transaction should follow. False if a file with the given name cannot be updated by
     * @throws RemoteException - rmi thrown exception
     */
    public boolean updateFile(Session session, String name) throws RemoteException;
    
    /**
     * Update file's access details
     * Can only be done by the file's owner even if the file is public
     * 
     * @param session current session
     * @param name the name of the file
     * @param isPublic should the public be listed to other accounts?
     * @param isReadOnly should other accounts only be able to read this file? Only necessary for public files
     * @return true if the file details were updated successfully
     * @throws RemoteException - rmi thrown exception
     */
    public boolean updateFileDetails(Session session, String name, boolean isPublic, boolean isReadOnly) throws RemoteException;
    
    /**
     * Rename a file
     * 
     * @param session current session
     * @param originalName the name of the file to rename
     * @param newName the new name to rename to
     * @return true if the file was successfully renamed
     * @throws RemoteException - rmi thrown exception
     */
    public boolean renameFile(Session session, String originalName, String newName) throws RemoteException;
    
    /**
     * Delete a file
     * 
     * @param session current session
     * @param name the name of the file to delete
     * @return true if the file was successfully deleted
     * @throws RemoteException - rmi thrown exception
     */
    public boolean deleteFile(Session session, String name) throws RemoteException;
    
    /**
     * Retreive a list of files that can be accessed by current acount
     * 
     * @param session current session
     * @return a list of FileDTO with details of files that the account has access to
     * @throws RemoteException - rmi thrown exception
     */
    public List<FileDTO> getFiles(Session session) throws RemoteException;
}
