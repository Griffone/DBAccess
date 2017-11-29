/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.Serializable;

/**
 * An account Data Transfer Object
 *
 * @author Griffone
 */
public class AccountDTO implements Serializable {
    
    public final String username;
    
    public AccountDTO(String name) {
        this.username = name;
    }
}
