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
    private long fileId;
    
    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;     // If I understand correctly this is essentially a semaphore
    
    @ManyToOne
    @JoinColumn(name="FILE_ID", nullable = false)
    public AccountModel owner;
    
    @Column(name = "NAME", nullable = false)
    public String name;
    
    @Column(name = "SIZE", nullable = false)
    public int size;
    
    @Column(name = "IS_PUBLIC", nullable = false)
    public boolean isPublic;
    
    @Column(name = "IS_READ_ONLY", nullable = false)
    public boolean isReadOnly;
    
    @Column(name = "NOTIFICATION_ENABLED", nullable = false)
    public boolean notificationEnabled = false;
    
    /**
     * A 0-argument constructor is required by JPA
     */
    public FileModel() {
        this(null, null, 0, false, false);
    }
    
    public FileModel(AccountModel owner, String name, int size, boolean isPublic, boolean isReadOnly) {
        this.owner = owner;
        this.name = name;
        this.size = size;
        this.isPublic = isPublic;
        this.isReadOnly = isReadOnly;
    }
    
    public FileDTO toDTO() {
        return new FileDTO(owner.toDTO(), name, size, isPublic, isReadOnly);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o.getClass() == this.getClass())
            return this.name.compareTo(((FileModel)o).name) == 0;
        else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.name);
        return hash;
    }
}
