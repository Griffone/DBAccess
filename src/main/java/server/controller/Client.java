/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import common.NotificationClient;
import server.model.Account;

/**
 *
 * @author Griffone
 */
public class Client {
    
    public Account account;
    public NotificationClient client;
    
    public Client(Account account, NotificationClient client) {
        this.account = account;
        this.client = client;
    }
}
