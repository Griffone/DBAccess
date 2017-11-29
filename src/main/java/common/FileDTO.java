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
public interface FileDTO extends Serializable {
    
    public String getName();
    public long getSize();
    
    public boolean isPublic();
    
    /**
     * Only needs to make sense if isPublic() is true
     * 
     * @return true if the file is read-only publically, false if other users can override this file
     */
    public boolean isReadOnly();
}
