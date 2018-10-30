/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.view;

import java.util.Objects;
import quantum.mutex.dto.VirtualPageDTO;

/**
 *
 * @author Florent
 */
public class VirtualPageSearchView {
    
    private final VirtualPageDTO virtualPage;

    public VirtualPageSearchView(VirtualPageDTO virtualPage) {
        this.virtualPage = virtualPage;
    }

    public VirtualPageDTO getVirtualPage() {
        return virtualPage;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.virtualPage.getMutexFileUUID());
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
        final VirtualPageSearchView other = (VirtualPageSearchView) obj;
        if (!Objects.equals(this.virtualPage.getMutexFileUUID(), other.virtualPage.getMutexFileUUID())) {
            return false;
        }
        return true;
    }
    
    
    
}
