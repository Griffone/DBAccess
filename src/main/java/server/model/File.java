/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import common.FileDTO;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * A file meta info
 *
 * @author Griffone
 */
@Entity(name = "File")
public class File implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long fileId;
    
    @ManyToOne
    @JoinColumn(name="FILE_ID", nullable = false)
    private Account owner;
    
    @Column(name = "NAME", nullable = false)
    private String name;
    
    @Column(name = "SIZE", nullable = false)
    private long size;
    
    @Column(name = "IS_PUBLIC", nullable = false)
    private boolean isPublic;
    
    @Column(name = "IS_READ_ONLY", nullable = false)
    private boolean isReadOnly;
    
    /**
     * A 0-argument constructor is required by JPA
     */
    public File() {
        this(null, null, 0, false, false);
    }
    
    public File(Account owner, String name, long size, boolean isPublic, boolean isReadOnly) {
        this.owner = owner;
        this.name = name;
        this.size = size;
        this.isPublic = isPublic;
        this.isReadOnly = isReadOnly;
    }
    
    public FileDTO toDTO() {
        return new FileDTO(owner.toDTO(), name, size, isPublic, isReadOnly);
    }
}
