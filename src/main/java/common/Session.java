/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.Serializable;

/**
 * An identifier of a session.
 * 
 * In the real world example should be more complex and with additional security measures than just a long.
 *
 * @author Griffone
 */
public class Session implements Serializable {
    
    public final long id;
    
    public Session(long id) {
        this.id = id;
    }
}
