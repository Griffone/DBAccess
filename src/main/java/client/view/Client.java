/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import client.view.Command.NamedCommand;
import common.FileServer;
import common.Session;
import common.exceptions.AccountException;
import common.exceptions.LoginException;
import common.exceptions.SessionException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/**
 *
 * @author Griffone
 */
public class Client implements Runnable {
    private static final String FILES_DIR = "D:\\Projects\\Java\\HW3\\client_files\\";
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private final ThreadSafeOut out = new ThreadSafeOut();
    private final NotificationPusher client;
    private final FileServer server;
    private Session currentSession;

    private boolean running = false;
    
    public Client(String serverAddress) throws RemoteException, NotBoundException, MalformedURLException {
        this.client = new NotificationPusher(out);
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
                switch(cmd.type) {
                    case CMD_QUIT:
                        running = false;
                        // Important fall-through!
                    case CMD_LOGOUT:
                        if (currentSession != null) {
                            server.logout(currentSession);
                            currentSession = null;
                            UnicastRemoteObject.unexportObject(client, false);
                            out.println("Logged out!");
                        } else
                            out.println("Not logged in!");
                        break;
                        
                    case CMD_HELP: {
                            StringBuilder sb = new StringBuilder();
                            for (NamedCommand command : Command.COMMANDS)
                                sb.append(command.name).append(" - ").append(command.help).append('\n');
                            out.print(sb.toString());
                        }
                        break;
                        
                    case CMD_LOGIN:
                        if (!isValidString(cmd.params[0]))
                            out.println("Illegal username! (cannot contain ';', '\"' or '*')");
                        else if (!isValidString(cmd.params[0]))
                            out.println("Illegal password! (cannot contain ';', '\"' or '*')");
                        else
                            try {
                                currentSession = server.login(client, cmd.params[0], cmd.params[1]);
                                out.println("Succesfully logged in! (" + String.valueOf(currentSession.id) + ")");
                            } catch (LoginException ex) {
                                out.println("Wrong username-password combo!");
                            } 
                        break;
                        
                    case CMD_CREATE_ACCOUNT:
                        if (!isValidString(cmd.params[0]))
                            out.println("Illegal username! (cannot contain ';', '\"' or '*')");
                        else if (!isValidString(cmd.params[0]))
                            out.println("Illegal password! (cannot contain ';', '\"' or '*')");
                        else
                            try {
                                server.createAccount(cmd.params[0], cmd.params[1]);
                                out.println("Successfully created an account!");
                            } catch (AccountException ex) {
                                out.println("Account with provided username could not be created. Try a different one!");
                            }
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
    
    private static boolean isValidString(String string) {
        return !string.contains(";") && !string.contains("\"") && !string.contains("*");
    }
    
    private String readLine() {
        out.print(PROMPT);
        return console.nextLine();
    }
}