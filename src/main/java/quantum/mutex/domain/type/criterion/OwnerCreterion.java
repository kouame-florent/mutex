/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.type.criterion;

import java.util.Collections;
import java.util.List;
import quantum.functional.api.Result;

/**
 *
 * @author Florent
 */
public class OwnerCreterion implements SearchCriterion{
    
    private final List<String> owners;

    private OwnerCreterion(List<String> owners) {
        this.owners = owners;
    }
    
    public static Result<OwnerCreterion> of(List<String> owners){
        return isValid(owners) ? Result.success(new OwnerCreterion(owners)) : 
                Result.empty();
        
    }
 
    private static boolean isValid(List<String> users) {
        return (users != null) && (!users.isEmpty());
    }

    public List<String> owners() {
        return owners != null ? owners : Collections.EMPTY_LIST;
    }
    
    
    
}
