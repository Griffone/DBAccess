/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import common.FileDTO;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.LockModeType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

@NamedQueries({
    
    @NamedQuery(
            name = "deleteFileByName",
            query = "DELETE FROM File f WHERE f.name LIKE :name"
    ),
    
    @NamedQuery(
            name = "deleteFileByOwnerName",
            query = "DELETE FROM File f WHERE f.owner.name LIKE :name"
    ),
    
    @NamedQuery(
            name = "findFileByName",
            query = "SELECT f FROM File f WHERE f.name LIKE :name",
            lockMode = LockModeType.OPTIMISTIC
    ),
    
    @NamedQuery(
            name = "getFiles",
            query = "SELECT f FROM File f",
            lockMode = LockModeType.OPTIMISTIC
    )
})

/**
 * A file meta info
 *
 * @author Griffone
 */
@Entity(name = "File")
public class FileModel implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @Version
    private int optlock;     // If I understand correctly this is essentially a semaphore
 
    @ManyToOne
    @JoinColumn(name="account_id")
    private AccountModel owner;
    
    private String name;
    private int filesize;
    private boolean ispublic;
    private boolean isreadonly;
    private boolean notificationenabled = false;
    
    public String getName() {
        return name;
    }
    
    public String getOwnerName() {
        return owner.getName();
    }
    
    public long getOwnerSessionID() {
        return owner.getLastSessionID();
    }
    
    public int getSize() {
        return filesize;
    }
    
    public boolean isPublic() {
        return ispublic;
    }
    
    public boolean isReadOnly() {
        return isreadonly;
    }
    
    public boolean isNotificationEnabled() {
        return notificationenabled;
    }
    
    public void setSize(int size) {
        this.filesize = size;
    }
    
    public void setPublic(boolean isPublic) {
        this.ispublic = isPublic;
    }
    
    public void setReadOnly(boolean readOnly) {
        this.isreadonly = readOnly;
    }
    
    public void setNotifications(boolean on) {
        this.notificationenabled = on;
    }
    
    public void rename(String name) {
        this.name = name;
    }
    
    /**
     * A 0-argument constructor is required by JPA
     */
    public FileModel() {
        this(null, null, 0, false, false);
    }
    
    public FileModel(AccountModel owner, String name, int size, boolean isPublic, boolean isReadOnly) {
        this.owner = owner;
        this.name = name;
        this.filesize = size;
        this.ispublic = isPublic;
        this.isreadonly = isReadOnly;
    }
    
    public FileDTO toDTO() {
        return new FileDTO(owner.toDTO(), name, filesize, ispublic, isreadonly);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o.getClass() != this.getClass())
            return false;
        FileModel obj = (FileModel) o;
        return obj.name.compareTo(this.name) == 0 && obj.filesize == this.filesize;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.name);
        hash = 41 * hash + this.filesize;
        return hash;
    }
}
