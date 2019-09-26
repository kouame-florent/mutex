/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.security.Principal;
import java.util.Optional;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import quantum.mutex.domain.entity.Tenant;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.util.Constants;


/**
 *
 * @author Florent
 */
@Dependent
public class AuthenticationService {
    
    @Resource SessionContext ctx;
    
    private @Inject UserDAO userDAO;
    private @Inject UserGroupDAO userGroupDAO;
   
    
    protected Optional<String> getAuthenticatedUser(){
        Principal principal = ctx.getCallerPrincipal();
        return Optional.ofNullable(principal)
                .map(p -> p.getName())
                .or(() -> Optional.empty());
        
//        return Optional.of(principal.getName(),Constants.ANONYMOUS_USER_PRINCIPAL_NAME);
    }
    
    public String getUserlogin(){
        return getAuthenticatedUser().orElseGet(() -> "");
    }
    
    public Optional<Tenant> getUserTenant(){
        return getAuthenticatedUser().flatMap(userDAO::findByLogin)
                    .map(u -> u.getTenant())
                    .or(() ->  Optional.empty());  
    }
    
    public String getUserTenantName(){
       return getAuthenticatedUser().flatMap(userDAO::findByLogin)
                    .map(u -> u.getTenant().getName())
                    .orElseGet(() -> "");
    }
    
    public String getUserPrimaryGroupName(){
        return getAuthenticatedUser().flatMap(userDAO::findByLogin)
                    .flatMap(u -> userGroupDAO.findUserPrimaryGroup(u))
                    .map(ug -> ug.getGroup().getName())
                    .orElseGet(() -> "");
    }
}
