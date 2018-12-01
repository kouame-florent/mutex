/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Florent
 */
@Getter @Setter
public class Fragment {
    
    private String uuid;
    private String mutexFileUUID;
    private String content;

    public Fragment() {
    }
    
    public Fragment(String uuid,String mutexFileUUID, String content) {
        this.uuid = uuid;
        this.mutexFileUUID = mutexFileUUID;
        this.content = content;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.uuid);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Fragment other = (Fragment) obj;
        if (!Objects.equals(this.uuid, other.uuid)) {
            return false;
        }
        return true;
    }
    
    
    
}
