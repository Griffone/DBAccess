/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

/**
 * A sunchronized version of System.out
 *
 * @author Griffone
 */
public class ThreadSafeOut {
    
    synchronized void print(String output) {
        System.out.print(output);
    }
    
    synchronized void println(String output) {
        System.out.println(output);
    }

    void exception(Exception ex) {
        System.err.println(ex);
    }
}