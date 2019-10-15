/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.util;

import java.util.Optional;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.user.domain.entity.Group;
import quantum.mutex.user.domain.entity.Tenant;
import quantum.mutex.user.repository.UserDAO;
import quantum.mutex.user.repository.UserGroupDAO;
import quantum.mutex.user.domain.entity.User;


/**
 *
 * @author Florent
 */
@Stateless
public class EnvironmentUtils {
    
    @Resource SessionContext ctx;
    
    @Inject UserDAO userDAO;
    @Inject UserGroupDAO userGroupDAO;
    
    protected Optional<String> getAuthenticatedUserLogin(){
        return Optional.of(ctx.getCallerPrincipal().getName())
                .or(() -> Optional.of(Constants.ANONYMOUS_USER_PRINCIPAL_NAME));
//        return Optional.of(ctx.getCallerPrincipal().getName(), 
//                Constants.ANONYMOUS_USER_PRINCIPAL_NAME);
    }
    
    public String getUserlogin(){
        return getAuthenticatedUserLogin().orElseGet(() -> "");
    }
    
     public Optional<User> getUser(){
        return getAuthenticatedUserLogin()
                .flatMap(userDAO::findByLogin);
    }
    
    public Optional<Tenant> getUserTenant(){
        return getAuthenticatedUserLogin().flatMap(userDAO::findByLogin)
                    .map(u -> u.getTenant())
                    .or(() ->  Optional.empty());
    }
    
    public String getUserTenantName(){
       return getAuthenticatedUserLogin().flatMap(userDAO::findByLogin)
                    .map(u -> u.getTenant().getName())
                    .orElseGet(() -> "");
    }
    
    public Optional<Group> getUserPrimaryGroup(){
        return getAuthenticatedUserLogin().flatMap(userDAO::findByLogin)
                    .flatMap(u -> userGroupDAO.findUserPrimaryGroup(u))
                    .map(ug -> ug.getGroup());
    }
    
    public String getUserPrimaryGroupName(){
        return getAuthenticatedUserLogin().flatMap(userDAO::findByLogin)
                    .flatMap(u -> userGroupDAO.findUserPrimaryGroup(u))
                    .map(ug -> ug.getGroup().getName())
                    .orElseGet(() -> "");
    }
}
