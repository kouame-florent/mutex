/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.util;



import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.domain.entity.Group;



/**
 *
 * @author Florent
 */
@Stateless
public class QueryUtils {

    private static final Logger LOG = Logger.getLogger(QueryUtils.class.getName());
    
    @Inject EnvironmentUtils envUtils;
  
    public Optional<String> indexName( Group group,String suffix){
        String target = envUtils.getUserTenantName().replaceAll(" ", "_").toLowerCase()
                + "$" 
                + group.getName().replaceAll(" ", "_").toLowerCase()
                + "$" + suffix;
        LOG.log(Level.INFO, "-||->|> INDEX NAME: {0}", target);
        return Optional.of(target); 
    }
 
}
