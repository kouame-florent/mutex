/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.util;



import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.util.EnvironmentUtils;

/**
 *
 * @author Florent
 */
@Stateless
public class QueryUtils {

    private static final Logger LOG = Logger.getLogger(QueryUtils.class.getName());
    
    @Inject EnvironmentUtils envUtils;
//    @Inject UserGroupService userGroupService;
//    
//    public String getMetadataIndexName(@NotNull Group group){
//        return envUtils.getUserTenantName().replaceAll(" ", "_").toLowerCase()
//                + "$" 
//                + group.getName().replaceAll(" ", "_").toLowerCase()
//                + "$" + 
//                "metadata";
//    }
    
//    public String getVirtualPageIndexName(@NotNull Group group){
//        return envUtils.getUserTenantName().replaceAll(" ", "_").toLowerCase()
//                + "$" 
//                + group.getName().replaceAll(" ", "_").toLowerCase()
//                + "$" + 
//                "virtual_page";
//    }
    
//    public String getCompletionIndexName(@NotNull Group group){
//        return envUtils.getUserTenantName().replaceAll(" ", "_").toLowerCase()
//                + "$" 
//                + group.getName().replaceAll(" ", "_").toLowerCase()
//                + "$" + 
//                "completion";
//    }
    
    public Result<String> indexName(@NotNull Group group,String suffix){
        String target = envUtils.getUserTenantName().replaceAll(" ", "_").toLowerCase()
                + "$" 
                + group.getName().replaceAll(" ", "_").toLowerCase()
                + "$" + suffix;
        return Result.of(target);
    }
    
//    public Result<String> buildCompletionIndex(Group group,String suffix){
//        String target = indexName(group,suffix);
//        LOG.log(Level.INFO, "--> INDEX NAME: {0}", target);
//        return Result.of(target);
//    }
    
//    public String getMetadataIndicesString(@NotNull List<Group> groups){
//        return groups.stream()
//                .map(this::getMetadataIndexName)
//                .collect(joining(","));
//    }
    
//    public String getVirtualPageIndicesString(@NotNull List<Group> groups){
//        return groups.stream()
//                .map(this::getVirtualPageIndexName)
//                .collect(joining(","));
//    }
//    
//    public List<String> getVirtualPageIndices(@NotNull List<Group> groups){
//        return groups.stream()
//                .map(this::getVirtualPageIndexName)
//                .collect(Collectors.toList());
//    }
  
}
