/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.api;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import quantum.mutex.domain.Group;
import quantum.mutex.util.EnvironmentUtils;

/**
 *
 * @author Florent
 */
@Stateless
public class ElasticApiUtils {
    
    @Inject EnvironmentUtils envUtils;
    
    public String getMetadataIndexName(@NotNull Group group){
        return envUtils.getUserTenantName().replaceAll(" ", "_").toLowerCase()
                + "$" 
                + group.getName().replaceAll(" ", "_").toLowerCase()
                + "$" + 
                "metadata";
    }
    
    public String getVirtualPageIndexName(@NotNull Group group){
        return envUtils.getUserTenantName().replaceAll(" ", "_").toLowerCase()
                + "$" 
                + group.getName().replaceAll(" ", "_").toLowerCase()
                + "$" + 
                "virtual_page";
    }
}
