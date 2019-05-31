/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;

import java.util.Collections;
import java.util.List;
import javax.validation.constraints.NotEmpty;

/**
 *
 * @author Florent
 */
public class OwnerCreteria implements SearchCriteria{
    
    private final List<String> owners;

    public OwnerCreteria(List<String> owners) {
        this.owners = owners;
    }

    @Override
    public boolean isValid() {
        return (owners != null) && (!owners.isEmpty());
    }

    public List<String> owners() {
        return owners != null ? owners : Collections.EMPTY_LIST;
    }
    
    
    
}
