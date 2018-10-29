/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.security.Principal;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import quantum.mutex.common.Result;
import quantum.mutex.domain.Tenant;
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
   
    
    protected Result<String> getAuthenticatedUser(){
        Principal principal = ctx.getCallerPrincipal();
        return Result.of(principal.getName(),Constants.ANONYMOUS_USER_PRINCIPAL_NAME);
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
