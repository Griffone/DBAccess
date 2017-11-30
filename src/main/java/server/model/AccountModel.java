/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import common.AccountDTO;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@NamedQueries({
    
    @NamedQuery(
            name = "deleteAccountByName",
            query = "DELETE FROM Account act WHERE act.username LIKE :name"
    ),
    
    @NamedQuery(
            name = "findAccountByName",
            query = "SELECT act FROM Account act WHERE act.username LIKE :name",
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
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long accountId;
    
    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;     // If I understand correctly this is essentially a semaphore

    @Column(name = "USERNAME", nullable = false)
    public String username;
    
    @Column(name = "PASSWORD", nullable = false)
    public String password;
    
    @Column(name = "LAST_SESSION_ID", nullable = false)
    public long lastSessionID;
    
    @OneToMany(cascade=CascadeType.REMOVE, mappedBy="owner")
    public List<FileModel> files;
    
    /**
     * A default 0-arg constructor that is required by JPA
     */
    public AccountModel() {
        this(null, null);
    }
    
    public AccountModel(String username, String password) {
        this.username = username;
        this.password = password;
        this.files = new LinkedList();
    }
    
    public AccountDTO toDTO() {
        return new AccountDTO(username);
    }
    
    public void appendFile(FileModel file) {
        files.add(file);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o.getClass() == this.getClass()) {
            AccountModel other = (AccountModel) o;
            return other.username.compareTo(this.username) == 0;
        }
        return false;
    }
}
