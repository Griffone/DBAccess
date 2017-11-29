/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.Serializable;

/**
 * A file Data Transfer Object
 * Does not contain the actual file, only the meta info.
 *
 * @author Griffone
 */
public class FileDTO implements Serializable {
    
    /**
     * The name of the file
     */
    public final String name;
    
    /**
     * The size of the file
     */
    public final long size;
    
    /**
     * Can the file be viewed by non-owner accounts?
     */
    public final boolean isPublic;
    
    /**
     * Can non-owner account only read the file? Only used when isPublic is true
     */
    public final boolean isReadOnly;

    /**
     * The owner account
     */
    public final AccountDTO owner;
    
    public FileDTO(AccountDTO owner, String name, long size, boolean isPublic, boolean isReadOnly) {
        this.owner = owner;
        this.name = name;
        this.size = size;
        this.isPublic = isPublic;
        this.isReadOnly = isReadOnly;
    }
}
