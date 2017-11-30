/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import client.view.Command.NamedCommand;
import common.*;
import common.exceptions.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Griffone
 */
public class Client implements Runnable {
    
    private static final String FILES_DIR = "D:\\Projects\\Java\\HW3\\client_files\\";
    private static final String PROMPT = "> ";
    private static final int MS_TIMEOUT = 2000;
    
    private final InetSocketAddress serverAddress;
    private final Scanner console = new Scanner(System.in);
    private final ThreadSafeOut out = new ThreadSafeOut();
    private final NotificationPusher client;
    private final FileServer server;
    private Session currentSession;

    private boolean running = false;
    
    public Client(String serverAddress, int serverPort) throws RemoteException, NotBoundException, MalformedURLException, UnknownHostException {
        this.client = new NotificationPusher();
        this.serverAddress = new InetSocketAddress(InetAddress.getByName(serverAddress), serverPort);
        server = (FileServer) Naming.lookup("//" + serverAddress + "/" + FileServer.NAME_IN_REGISTRY);
    }
    
    public void start() {
        if (running)
            return;
        
        running = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Command cmd = Command.parseLine(readLine());
                if (validateParams(cmd))
                    switch(cmd.type) {
                        case CMD_QUIT:
                            try {
                                quit();
                            } catch (NoSuchObjectException ex) {
                                
                            }
                            break;
                        case CMD_HELP:
                            printHelp();
                            break;
                        
                        case CMD_ACCOUNT_CREATE:
                            createAccount(cmd.params[0], cmd.params[1]);
                            break;
                        case CMD_ACCOUNT_LOGIN:
                            if (currentSession == null)
                                login(cmd.params[0], cmd.params[1]);
                            else
                                out.println("Please log out first!");
                            break;
                        case CMD_ACCOUNT_LOGOUT:
                            try {
                                if (validateSession())
                                    logout();
                            } catch (NoSuchObjectException ex) {
                                // Don't worry, this happens because client doesn't export itself
                            }
                            break;
                        case CMD_ACCOUNT_DELETE:
                            if (validateSession())
                                deleteAccount();
                            break;

                        case CMD_FILE_LIST:
                            if (validateSession())
                                listFiles();
                            break;
                        case CMD_FILE_UPLOAD:
                            if (validateSession())
                                uploadFile(cmd.params);
                            break;
                        case CMD_FILE_REUPLOAD:
                            if (validateSession())
                                reuploadFile(cmd.params);
                            break;
                        case CMD_FILE_DOWNLOAD:
                            if (validateSession())
                                downloadFile(cmd.params);
                            break;
                        case CMD_FILE_UPDATE_DETAILS:
                            if (validateSession())
                                updateFileDetails(cmd.params);
                            break;
                        case CMD_FILE_RENAME:
                            if (validateSession())
                                renameFile(cmd.params);
                            break;
                        case CMD_FILE_DELETE:
                            if (validateSession())
                                deleteFile(cmd.params);
                            break;
                        case CMD_FILE_NOTIFY:
                            if (validateSession())
                                enableNotifications(cmd.params);
                            break;

                        default:
                            out.println("Unknown command!\nPlease use \"help\" to get a list of commands!");
                            break;
                    }
            } catch (SessionException | RemoteException ex) {
                out.exception(ex);
            }
        }
    }
    
    private boolean validateAccountName(String string) {
        if (!string.contains(";") && !string.contains("\"") && !string.contains("*"))
            return true;
        
        out.println("Please enter a valid username! (no \";\", \"\"\" or \"*\")");
        return false;
    }
    
    private boolean validateAccountPassword(String string) {
        if (!string.contains(";") && !string.contains("\"") && !string.contains("*"))
            return true;
        
        out.println("Please enter a valid password! (no \";\", \"\"\" or \"*\")");
        return false;
    }
    
    private boolean validateFileName(String string) {
        if (!string.contains("\\") && !string.contains(":") && !string.contains(";") && !string.contains("\"") && !string.contains("*"))
            return true;
        
        out.println("Invalid file name! Please use one without any of: \"\\\", \":\", \";\", \"\"\" or \"*\"");
        return false;
    }

    private String readLine() {
        out.print(PROMPT);
        return console.nextLine();
    }
    
    private boolean validateParams(Command command) {
        switch (command.type) {
            case CMD_ACCOUNT_LOGIN:
            case CMD_ACCOUNT_CREATE:
                if (command.params.length >= 2) {
                    if (validateAccountName(command.params[0]))
                        if (validateAccountPassword(command.params[1]))
                            return true;
                } else
                  out.println("Please enter account username and password!");
                return false;
                
            case CMD_FILE_UPLOAD:
                if (command.params.length >= 2) {
                    if (validateFileName(command.params[0]) && validateFileName(command.params[1]))
                        if (command.params.length >= 3)
                            return validateFileDetails(command.params, 2);
                        else
                            return true;
                } else
                    out.println("Please specify local file name and the server file name create.");
                return false;
            case CMD_FILE_REUPLOAD:
                if (command.params.length >= 2)
                    return validateFileName(command.params[0]) && validateFileName(command.params[1]);
                else
                    out.println("Please specify local file name and the server file name to update");
                return false;
            case CMD_FILE_DOWNLOAD:
                if(command.params.length >= 1)
                    return validateFileName(command.params[0]);
                else
                    out.println("Plese specify a file you want to download!");
                return false;
            case CMD_FILE_UPDATE_DETAILS:
                if (command.params.length >= 2) {
                    return validateFileName(command.params[0]) && validateFileDetails(command.params, 1);
                } else
                    out.println("Please specify file name and its details!");
                return false;
            case CMD_FILE_RENAME:
                if (command.params.length >= 2) {
                    return validateFileName(command.params[0]) && validateFileName(command.params[1]);
                } else
                    out.println("Please specify the original file name and a new one!");
                return false;
            case CMD_FILE_DELETE:
                if (command.params.length >= 1)
                    return validateFileName(command.params[0]);
                else
                    out.println("Please specify the file to delete!");
                return false;
            case CMD_FILE_NOTIFY:
                if (command.params.length > 0) {
                    int invalidCount = 0;
                    for (String name : command.params)
                        if (!validateFileName(name))
                            invalidCount++;
                    if (invalidCount == 0)
                        return true;
                } else
                    out.println("Please specify at least one file name!");
                return false;
                
            default:
                return true;
        }
    }
    
    private boolean validateFileDetails(String[] params, int offset) {
        if (params[offset].compareToIgnoreCase("public") == 0)
            if (params.length >= offset + 2 && (params[offset + 1].compareToIgnoreCase("r") == 0 || params[offset + 1].compareToIgnoreCase("rw") == 0))
                return true;
            else
                out.println("Please specify if the file is \"r\" - readonly or \"rw\" - readwrite!");
        else if (params[offset].compareToIgnoreCase("private") == 0)
            return true;
        else
            out.println("Please specify if the file is \"public\" or \"private\"!");
        return false;
    }
    
    private void quit() throws RemoteException, SessionException {
        running = false;
        logout();
    }
    
    private void logout() throws RemoteException, SessionException {
        server.logout(currentSession);
        currentSession = null;
        UnicastRemoteObject.unexportObject(client, false);
        out.println("Logged out!");
    }
    
    private void login(String name, String password) throws RemoteException, SessionException {
        try {
            currentSession = server.login(client, name, password);
            out.println("Succesfully logged in! (" + String.valueOf(currentSession.id) + ")");
        } catch (LoginException ex) {
            out.println("Wrong username-password combo!");
        }
    }
    
    private void createAccount(String name, String password) throws RemoteException {
        try {
            server.createAccount(name, password);
            out.println("Successfully created an account!");
        } catch (AccountException ex) {
            out.println("Account with provided username could not be created. Try a different one!");
        }
    }
    
    private void deleteAccount() throws RemoteException, SessionException {
        server.deleteAccount(currentSession);
        currentSession = null;
        UnicastRemoteObject.unexportObject(client, false);
    }
    
    private void printHelp() {
        StringBuilder sb = new StringBuilder();
        for (NamedCommand command : Command.COMMANDS)
            sb.append(command.name).append(" - ").append(command.help).append('\n');
        out.print(sb.toString());
    }
    
    private void listFiles() throws RemoteException, SessionException {
        List<FileDTO> files = server.getFiles(currentSession);
        if (files.isEmpty())
            out.println("No files were found!");
        else
            for (FileDTO file : files)
                out.println(file.toString());
    }
    
    private void uploadFile(String[] params) throws RemoteException, SessionException {
        try {
            byte[] file = readFile(params[0]);
            
            boolean isPublic = false;
            if (params.length >= 3)
                isPublic = (params[2].compareToIgnoreCase("public") == 0);
            boolean isReadOnly = true;
            if (params.length >= 4)
                isReadOnly = (params[3].compareToIgnoreCase("rw") != 0);
            
            long transactionID = server.createFile(currentSession, params[1], isPublic, isReadOnly);
            
            sendFile(transactionID, file);
            out.println("Successfully sent " + params[0] + " as " + params[1]);
        } catch (FileNotFoundException ex) {
            out.println("Could not find file! Please make sure you use FILES_DIR!");
        } catch (IOException ex) {
            out.println("Error reding the file!");
        } catch (FileException ex) {
            out.println("The server denied the file name! Try using a different name.");
        }
    }
    
    private void reuploadFile(String[] params) throws RemoteException, SessionException {
        try {
            byte[] file = readFile(params[0]);
            
            long transactionID = server.updateFile(currentSession, params[1]);
            sendFile(transactionID, file);
            out.println("Successfully sent " + params[0] + " as " + params[1]);
        } catch (FileNotFoundException ex) {
            out.println("Could not find file! Please make sure you use FILES_DIR!");
        } catch (IOException ex) {
            out.println("Error reding the file!");
        } catch (FileException ex) {
            out.println("File was not found on the server!");
        } catch (PermissionException ex) {
            out.println("Permission denied!");
        }
    }
    
    private void updateFileDetails(String[] params) throws RemoteException, SessionException {
        try {
            boolean isPublic = false;
            if (params.length >= 2)
                isPublic = (params[1].compareToIgnoreCase("public") == 0);
            boolean isReadOnly = true;
            if (params.length >= 3)
                isReadOnly = (params[2].compareToIgnoreCase("rw") != 0);
            
            server.updateFileDetails(currentSession, params[0], isPublic, isReadOnly);
            out.println("Successfully updated file details!");
        } catch (IOException ex) {
            out.println("Error reding the file!");
        } catch (FileException ex) {
            out.println("Could not find file with provided name!");
        } catch (PermissionException ex) {
            out.println("You are not authorised to modify given file!");
        }
    }
    
    private void renameFile(String[] params) throws RemoteException, SessionException {
        try {
            server.renameFile(currentSession, params[0], params[1]);
            out.println("Successfully renamed " + params[0] + " to " + params[1] + " file!");
        } catch (IOException ex) {
            out.println("Error reding the file!");
        } catch (FileException ex) {
            out.println("Could not find file with provided name!");
        } catch (PermissionException ex) {
            out.println("You are not authorised to modify given file!");
        }
    }
    
    private void deleteFile(String[] params) throws RemoteException, SessionException {
        try {
            server.deleteFile(currentSession, params[0]);
            out.println("Successfully deleted " + params[0] + "!");
        } catch (IOException ex) {
            out.println("Error reding the file!");
        } catch (FileException ex) {
            out.println("Could not find file with provided name!");
        } catch (PermissionException ex) {
            out.println("You are not authorised to modify given file!");
        }
    }
    
    private void enableNotifications(String[] params) throws RemoteException, SessionException {
        List<String> enabled = new LinkedList();
        for (String name : params)
            try {
                server.enableNotification(currentSession, name);
                enabled.add(name);
            } catch (FileException ex) {
                out.println("File " + name + "could not be found!");
            } catch (PermissionException ex) {
                out.println("You are not authorised to be notified about " + name);
            }
        if (enabled.isEmpty())
            out.println("No new notifications will be sent");
        else
            for (String name : enabled)
                out.println("You will be notified about " + name);
    }
    
    private void downloadFile(String[] params) throws RemoteException, SessionException {
        try {
            long transactionID = server.downloadFile(currentSession, params[0]);
            byte[] data = recvFile(transactionID);
            writeFile(params[0], data);
            out.println("Successfully downloaded " + params[0]);
        } catch (FileException ex) {
            out.println("File " + params[0] + " could not be found!");
        } catch (PermissionException ex) {
            out.println("You are not authorised to access the file!");
        } catch (IOException ex) {
            out.println("Error receiving file!");
        }
    }
    
    private boolean validateSession() {
        if (currentSession != null)
            return true;
        out.println("Please log in first!");
        return false;
    }
    
    private byte[] readFile(String fileName) throws FileNotFoundException, IOException {
        File file = new File(FILES_DIR, fileName);
        
        if (file.length() > Integer.MAX_VALUE) {
            out.println("Unsopported file size!");
            throw new IOException();
        }
        
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        int readBytes = fis.read(bytes);
        if (readBytes != file.length()) {
            out.println("Couldn't read the whole file!");
            throw new IOException();
        }
        return bytes;
    }
    
    private void writeFile(String fileName, byte[] data) throws NullPointerException, IOException, IllegalArgumentException {
        if (fileName == null || data == null)
            throw new NullPointerException();
        
        if (data.length == 0)
            throw new IllegalArgumentException("The specified bytes are empty!");
        
        if (!validateFileName(fileName))
            throw new IllegalArgumentException("Illegal file name!");
        
        File file = new File(FILES_DIR, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.flush();
    }
    
    private void sendFile(long transactionID, byte[] file) {
        try {
            Socket socket = new Socket();
            socket.connect(serverAddress, MS_TIMEOUT);
            byte[] bytes = new byte[8];
            for (int i = 0; i < 8; i++)
                bytes[i] = (byte) (transactionID >> (i * 8));
            OutputStream socketOut = socket.getOutputStream();
            socketOut.write(bytes);
            
            bytes = new byte[4];
            for (int i = 0; i < 4; i++)
                bytes[i] = (byte) (file.length >> (i * 8));
            socketOut.write(bytes);
            
            socketOut.write(file);
            socketOut.flush();
        } catch (IOException ex) {
            out.println("Error sending the file!");
        }
    }
    
    private byte[] recvFile(long transactionID) throws IOException {
        Socket socket = new Socket();
        socket.connect(serverAddress, MS_TIMEOUT);
        
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++)
            bytes[i] = (byte) (transactionID >> (i * 8));
        OutputStream socketOut = socket.getOutputStream();
        socketOut.write(bytes);
        socketOut.flush();
        
        InputStream socketIn = socket.getInputStream();
        bytes = new byte[4];
        int readBytes = socketIn.read(bytes);
        if (readBytes != 4)
            throw new IOException("Unexpected eos!");
            
        int fileSize = 0;
        for (int i = 0; i < bytes.length; i++)
            fileSize |= bytes[i] << (i * 8);
        
        if (fileSize == 0)
            throw new IOException("No file was sent!");
        
        bytes = new byte[fileSize];
        readBytes = socketIn.read(bytes);
        if (readBytes != fileSize)
            throw new IOException("Did not get complete file!");
        return bytes;
    }

    private class NotificationPusher extends UnicastRemoteObject implements NotificationClient {

        public NotificationPusher() throws RemoteException {
        }
        
        @Override
        public void pushNotification(FileDTO file, String action, AccountDTO performer) throws RemoteException {
            out.println("File " + file.name + " was " + action + " by " + performer.username);
        }
        
    }
}