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
import quantum.mutex.common.Result;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;

/**
 *
 * @author Florent
 */
@Stateless
public class EnvironmentUtils {
    
    @Resource SessionContext ctx;
    
    @Inject UserDAO userDAO;
    @Inject UserGroupDAO userGroupDAO;
    
    protected Result<String> getAuthenticatedUser(){
        return Result.of(ctx.getCallerPrincipal().getName(), 
                Constants.ANONYMOUS_USER_PRINCIPAL_NAME);
    }
    
    public String getUserlogin(){
        return getAuthenticatedUser().getOrElse(() -> "");
    }
    
    public Result<Tenant> getUserTenant(){
        return getAuthenticatedUser().flatMap(userDAO::findByLogin)
                    .map(u -> u.getTenant())
                    .orElse(() ->  Result.empty());
    }
    
    public String getUserTenantName(){
       return getAuthenticatedUser().flatMap(userDAO::findByLogin)
                    .map(u -> u.getTenant().getName())
                    .getOrElse(() -> "");
    }
    
    public String getUserPrimaryGroupName(){
        return getAuthenticatedUser().flatMap(userDAO::findByLogin)
                    .flatMap(u -> userGroupDAO.findUserPrimaryGroup(u))
                    .map(ug -> ug.getGroup().getName())
                    .getOrElse(() -> "");
    }
}
