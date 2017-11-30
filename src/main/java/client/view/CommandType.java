/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

/**
 *
 * @author Griffone
 */
public enum CommandType {
    CMD_NULL,   // could not parse
    CMD_HELP,
    CMD_QUIT,

    CMD_ACCOUNT_LOGOUT,
    CMD_ACCOUNT_LOGIN,
    CMD_ACCOUNT_CREATE,
    CMD_ACCOUNT_DELETE,

    CMD_FILE_LIST,
    CMD_FILE_UPLOAD,
    CMD_FILE_DOWNLOAD,
    CMD_FILE_REUPLOAD,
    CMD_FILE_UPDATE_DETAILS,
    CMD_FILE_RENAME,
    CMD_FILE_DELETE,
    CMD_FILE_NOTIFY
}