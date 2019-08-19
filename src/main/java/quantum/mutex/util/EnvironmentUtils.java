/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.util;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.Tenant;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.domain.entity.User;
import quantum.mutex.util.functional.Result;

/**
 *
 * @author Florent
 */
@Stateless
public class EnvironmentUtils {
    
    @Resource SessionContext ctx;
    
    @Inject UserDAO userDAO;
    @Inject UserGroupDAO userGroupDAO;
    
    protected Result<String> getAuthenticatedUserLogin(){
        return Result.of(ctx.getCallerPrincipal().getName(), 
                Constants.ANONYMOUS_USER_PRINCIPAL_NAME);
    }
    
    public String getUserlogin(){
        return getAuthenticatedUserLogin().getOrElse(() -> "");
    }
    
     public Result<User> getUser(){
        return getAuthenticatedUserLogin()
                .flatMap(userDAO::findByLogin);
    }
    
    public Result<Tenant> getUserTenant(){
        return getAuthenticatedUserLogin().flatMap(userDAO::findByLogin)
                    .map(u -> u.getTenant())
                    .orElse(() ->  Result.empty());
    }
    
    public String getUserTenantName(){
       return getAuthenticatedUserLogin().flatMap(userDAO::findByLogin)
                    .map(u -> u.getTenant().getName())
                    .getOrElse(() -> "");
    }
    
    public Result<Group> getUserPrimaryGroup(){
        return getAuthenticatedUserLogin().flatMap(userDAO::findByLogin)
                    .flatMap(u -> userGroupDAO.findUserPrimaryGroup(u))
                    .map(ug -> ug.getGroup());
    }
    
    public String getUserPrimaryGroupName(){
        return getAuthenticatedUserLogin().flatMap(userDAO::findByLogin)
                    .flatMap(u -> userGroupDAO.findUserPrimaryGroup(u))
                    .map(ug -> ug.getGroup().getName())
                    .getOrElse(() -> "");
    }
}
