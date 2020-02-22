/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;



import io.mutex.shared.service.EnvironmentUtils;
import java.util.Optional;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.user.entity.Group;



/**
 *
 * @author Florent
 */
@Stateless
public class IndexNameUtils {

    private static final Logger LOG = Logger.getLogger(IndexNameUtils.class.getName());
    
    @Inject EnvironmentUtils envUtils;
  
    public Optional<String> getName(Group group,String suffix){
        return Optional.of(group.getUuid() + "$" + suffix);
    }
 
}
