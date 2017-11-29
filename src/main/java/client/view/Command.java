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
        new NamedCommand("login", CommandType.CMD_LOGIN, "attempts to log in, must be followed by <username> <password>!"),
        new NamedCommand("logout", CommandType.CMD_LOGOUT, "log out."),
        new NamedCommand("createaccount", CommandType.CMD_CREATE_ACCOUNT, "attempts to create a new account, must be followed by <username> <password>!")
    };
    
    
    public enum CommandType {
        CMD_NULL,   // could not parse
        CMD_HELP,
        CMD_QUIT,
        CMD_LOGOUT,
        CMD_LOGIN,
        CMD_CREATE_ACCOUNT
    }
    
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
        
        if (isParamsValid(type, params))
            return new Command(type, params);
        else
            return new Command(CommandType.CMD_NULL, null);
    }
    
    private Command(CommandType type, String[] params) {
        this.type = type;
        this.params = params;
    }
    
    private static boolean isParamsValid(CommandType type, String[] params) {
        switch (type) {
            case CMD_LOGIN:
            case CMD_CREATE_ACCOUNT:
                return params.length >= 2 && params[0].length() > 0 && params[1].length() > 0;
            
            default:
                return true;
        }
    }
}
