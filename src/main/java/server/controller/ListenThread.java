/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import server.model.FileModel;
import server.startup.Server;

/**
 *
 * @author Griffone
 */
public class ListenThread implements Runnable {
    
    private final ServerSocket listenSocket;
    public final Map<Long, Transaction> transactions;
    public boolean running;
    
    public ListenThread(int port) throws IOException {
        this.transactions = Collections.synchronizedMap(new HashMap());
        listenSocket = new ServerSocket(port);
        running = true;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Socket sock = listenSocket.accept();
                InputStream in = sock.getInputStream();
                OutputStream out = sock.getOutputStream();
                byte[] bytes = new byte[8];
                int bytesRead = in.read(bytes);
                if (bytesRead != 8)
                    continue;
                
                long transactionID = 0;
                for (int i = 0; i < bytes.length; i++)
                    transactionID |= (bytes[i] << (i * 8));
                
                Transaction transaction = transactions.get(transactionID);
                if (transaction != null) {
                    transactions.remove(transactionID);
                    switch (transaction.type) {
                        case TT_CLIENT_TO_SERVER:
                            uploadFile(in, out, transaction);
                            break;
                            
                        case TT_SERVER_TO_CLIENT:
                            downloadFile(in, out, transaction);
                            break;
                    }
                }
                
                // The socket gets closed automatically as soon as we leave this context
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
    
    /**
     * Upload file from client to the server
     * 
     * @param in
     * @param out 
     */
    private void uploadFile(InputStream in, OutputStream out, Transaction transaction) throws IOException {
        byte[] bytes = new byte[4];
        int count = in.read(bytes);
        if (count != 4)
            throw new IOException("Unexpected eos!");
        
        int fileSize = 0;
        for (int i = 0; i < 4; i++)
            fileSize |= (bytes[i] << (i * 8));
        bytes = new byte[fileSize];
        count = in.read(bytes);
        if (count != fileSize)
            throw new IOException("Wrong file size!");
        
        File file = new File(Server.FILES_DIR, transaction.file.getName());
        if (file.exists())
            file.delete();
        FileOutputStream fs = new FileOutputStream(file);
        fs.write(bytes);
        fs.flush();
        if (transaction.alreadyExists) {
            FileModel workFile = transaction.dao.findFile(transaction.file.getName(), false);
            workFile.setSize(fileSize);
            transaction.dao.updateEntity();
        } else {
            transaction.file.setSize(fileSize);
            transaction.dao.createFile(transaction.file);
        }
    }
    
    /**
     * Download file from server to the client
     * 
     * @param in
     * @param out 
     */
    private void downloadFile(InputStream in, OutputStream out, Transaction transaction) throws IOException {
        File file = new File(Server.FILES_DIR, transaction.file.getName());
        int fileSize;
        if (file.exists())
            fileSize = (int) file.length();
        else
            fileSize = 0;
        byte[] bytes = new byte[4];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) (fileSize >> (8 * i));
        out.write(bytes);
        if (fileSize == 0) {
            out.flush();
            return;
        }
        FileInputStream fis = new FileInputStream(file);
        bytes = new byte[fileSize];
        fis.read(bytes);
        out.write(bytes);
        out.flush();
    }
}
