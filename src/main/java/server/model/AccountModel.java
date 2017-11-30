/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import common.AccountDTO;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

@NamedQueries({
    
    @NamedQuery(
            name = "deleteAccountByName",
            query = "DELETE FROM Account act WHERE act.name LIKE :name"
    ),
    
    @NamedQuery(
            name = "findAccountByName",
            query = "SELECT act FROM Account act WHERE act.name LIKE :name",
            lockMode = LockModeType.OPTIMISTIC
    )
})

/**
 * A user account
 * 
 * @author Griffone
 */
@Entity(name = "Account")
public class AccountModel implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    
    @Version
    private int optlock;     // If I understand correctly this is essentially a semaphore

    private String name;
    private String password;
    private long lastsessionid;
    
    public String getName() {
        return name;
    }
    
    public boolean isPassword(String password) {
        return (password.compareTo(this.password) == 0);
    }
    
    public long getLastSessionID() {
        return lastsessionid;
    }
    
    public void updateSessionID(long sessionID) {
        this.lastsessionid = sessionID;
    }
    
    /**
     * A default 0-arg constructor that is required by JPA
     */
    public AccountModel() {
        this(null, null);
    }
    
    public AccountModel(String name, String password) {
        this.name = name;
        this.password = password;
    }
    
    public AccountDTO toDTO() {
        return new AccountDTO(name);
    }
}
