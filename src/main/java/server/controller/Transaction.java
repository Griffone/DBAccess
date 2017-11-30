/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import server.integration.DataAccessObject;
import server.model.FileModel;

/**
 *
 * @author Griffone
 */
public class Transaction {
    
    public final TransactionType type;
    public final Client client;
    public final FileModel file;
    public final DataAccessObject dao;
    
    public Transaction(TransactionType type, Client client, FileModel file, DataAccessObject dao) {
        this.type = type;
        this.client = client;
        this.file = file;
        this.dao = dao;
    }
    
    public enum TransactionType {
        TT_CLIENT_TO_SERVER,
        TT_SERVER_TO_CLIENT;
    }
}
