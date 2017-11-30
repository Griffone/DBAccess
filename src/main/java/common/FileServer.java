/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import common.exceptions.*;
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
    public static final int FILE_TRANSFER_PORT = 4269;
    
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
     * @return the transaction id that is to be used for uploading the file
     * @throws RemoteException - rmi thrown exception
     * @throws SessionException - something wrong with session handle
     * @throws FileException - something is wrong with the file
     */
    public long createFile(Session session, String name, boolean isPublic, boolean isReadOnly) throws RemoteException, SessionException, FileException;
    
    /**
     * Attempt to flag a file for an update
     * Should be followed by a file transaction
     * 
     * @param session current session id
     * @param name the name of the file to re-upload
     * @return the transaction id that is to be used for uploading the file
     * @throws RemoteException - rmi thrown exception
     * @throws SessionException - something wrong with session handle
     * @throws FileException - file with the provided name could not be found
     * @throws PermissionException - the currently logged in user has no rights to update the file
     */
    public long updateFile(Session session, String name) throws RemoteException, SessionException, FileException, PermissionException;
    
    /**
     * Attempt to download a file.
     * Should be followed by server->client file transaction
     * 
     * @param session current session id
     * @param name the name of the file to download
     * @return the transaction id that is to be used for downloading the file
     * @throws RemoteException - rmi thrown exception
     * @throws SessionException - something wrong with session handle
     * @throws FileException - file with provided name could not be found
     * @throws PermissionException - the currently logged in user has no rights to download the file
     */
    public long downloadFile(Session session, String name) throws RemoteException, SessionException, FileException, PermissionException;
    
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
     * @throws FileException - file with the provided name could not be found
     * @throws PermissionException - the currently logged in user has no rights to update the file
     */
    public void updateFileDetails(Session session, String name, boolean isPublic, boolean isReadOnly) throws RemoteException, SessionException, FileException, PermissionException;
    
    /**
     * Rename a file
     * 
     * @param session current session
     * @param originalName the name of the file to rename
     * @param newName the new name to rename to
     * @throws RemoteException - rmi thrown exception
     * @throws SessionException - something wrong with session handle
     * @throws FileException - file with the original name could not be found
     * @throws PermissionException - the currently logged in user has no rights to update the file
     */
    public void renameFile(Session session, String originalName, String newName) throws RemoteException, SessionException, FileException, PermissionException;
    
    /**
     * Delete a file
     * 
     * @param session current session
     * @param name the name of the file to delete
     * @throws RemoteException - rmi thrown exception
     * @throws SessionException - something wrong with session handle
     * @throws FileException - file with the provided name could not be found
     * @throws PermissionException - the currently logged in user has no rights to update the file
     */
    public void deleteFile(Session session, String name) throws RemoteException, SessionException, FileException, PermissionException;
    
    /**
     * Retreive a list of files that can be accessed by current acount
     * 
     * @param session current session
     * @return a list of FileDTO with details of files that the account has access to
     * @throws RemoteException - rmi thrown exception
     * @throws SessionException - something wrong with session handle
     */
    public List<FileDTO> getFiles(Session session) throws RemoteException, SessionException;
    
    /**
     * Enable notification when a given file is modified.
     * 
     * @param session current session id
     * @param name the name of the file to be notified about
     * @throws RemoteException - rmi thrown exception
     * @throws SessionException - something wrong with session handle
     * @throws FileException - file with the provided name could not be found
     * @throws PermissionException - the currently logged in user has no rights to update the file
     */
    public void enableNotification(Session session, String name) throws RemoteException, SessionException, FileException, PermissionException;
}
