/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.type.criterion;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Florent
 */
public class OwnerCreterion implements SearchCriterion{
    
    private final List<String> owners;

    private OwnerCreterion(List<String> owners) {
        this.owners = owners;
    }
    
    public static OwnerCreterion of(List<String> owners){
        return new OwnerCreterion(owners);
    }
    
    public static OwnerCreterion getDefault(){
        return new OwnerCreterion(Collections.EMPTY_LIST);
    }

    @Override
    public boolean isValid() {
        return (owners != null) && (!owners.isEmpty());
    }

    public List<String> owners() {
        return owners != null ? owners : Collections.EMPTY_LIST;
    }
    
    
    
}
