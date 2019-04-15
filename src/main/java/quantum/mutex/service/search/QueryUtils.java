/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;


import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.service.domain.UserGroupService;
import quantum.mutex.util.EnvironmentUtils;

/**
 *
 * @author Florent
 */
@Stateless
public class QueryUtils {

    private static final Logger LOG = Logger.getLogger(QueryUtils.class.getName());
    
    @Inject EnvironmentUtils envUtils;
    @Inject UserGroupService userGroupService;
    
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
    
    public String getMetadataIndicesString(@NotNull List<Group> groups){
        return groups.stream()
                .map(this::getMetadataIndexName)
                .collect(joining(","));
    }
    
    public String getVirtualPageIndicesString(@NotNull List<Group> groups){
        return groups.stream()
                .map(this::getVirtualPageIndexName)
                .collect(joining(","));
    }
    
    public List<String> getVirtualPageIndices(@NotNull List<Group> groups){
        return groups.stream()
                .map(this::getVirtualPageIndexName)
                .collect(Collectors.toList());
    }
  
}
