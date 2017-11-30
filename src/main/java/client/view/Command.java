/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Griffone
 */
public class Command {
    
    public final CommandType type;
    public final String[] params;
    
    private static final HashMap<String, CommandType> COMMAND_MAP = new HashMap();
    public static final NamedCommand[] COMMANDS = {
        new NamedCommand("quit", CommandType.CMD_QUIT, "stops and exits the program."),
        new NamedCommand("help", CommandType.CMD_HELP, "prints this message"),
        
        new NamedCommand("login", CommandType.CMD_ACCOUNT_LOGIN, "attempts to log in, must be followed by <username> <password>!"),
        new NamedCommand("logout", CommandType.CMD_ACCOUNT_LOGOUT, "log out."),
        new NamedCommand("createuser", CommandType.CMD_ACCOUNT_CREATE, "attempts to create a new account, must be followed by <username> <password>!"),
        new NamedCommand("deleteuser", CommandType.CMD_ACCOUNT_DELETE, "deletes the currently logged in account."),
        
        new NamedCommand("list", CommandType.CMD_FILE_LIST, "lists available files."),
        new NamedCommand("upload", CommandType.CMD_FILE_UPLOAD, "attempt to upload a file. Should be followed by <local file name> <server file name> [public] [is-read-only]!"),
        new NamedCommand("update", CommandType.CMD_FILE_REUPLOAD, "attempt to update a file. Should be followed by <local file name> <server file name>."),
        new NamedCommand("download", CommandType.CMD_FILE_DOWNLOAD, "attempt to download a file. Should be followed by <server file name>."),
        new NamedCommand("details", CommandType.CMD_FILE_UPDATE_DETAILS, "update a file's details. Should be followed by <server file name> [public] [is-read-only]!"),
        new NamedCommand("rename", CommandType.CMD_FILE_RENAME, "update a file's name. Should be followed by <server original file name> <new file name>."),
        new NamedCommand("delete", CommandType.CMD_FILE_DELETE, "attempt to delete a given file. Should be followed by <server file name>!"),
        
        new NamedCommand("notify", CommandType.CMD_FILE_NOTIFY, "the server should notify if a given file is modified. Should be followed by {<server file name>}.")
    };
    
    public static class NamedCommand {
        public final String name;
        public final CommandType type;
        public final String help;
        
        public NamedCommand(String name, CommandType type, String help) {
            this.name = name;
            this.type = type;
            this.help = help;
        }
    }
    
    public static void initializeCommands() {
        COMMAND_MAP.clear();
        for (NamedCommand cmd : COMMANDS)
            COMMAND_MAP.put(cmd.name, cmd.type);
    }
    
    public static Command parseLine(String line) {
        String[] words = line.split(" ");
        String[] params = new String[words.length - 1];
        System.arraycopy(words, 1, params, 0, params.length);
        
        CommandType type = COMMAND_MAP.get(words[0].toLowerCase());
        if (type == null)
            type = CommandType.CMD_NULL;
        
        return new Command(type, params);
    }
    
    private Command(CommandType type, String[] params) {
        this.type = type;
        this.params = params;
    }
    
}