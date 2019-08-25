/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.config;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.annotation.FacesConfig;
import javax.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

/**
 *
 * @author Florent
 */
@CustomFormAuthenticationMechanismDefinition(
    loginToContinue = @LoginToContinue(
        loginPage = "/login.xhtml",
        errorPage = ""
    )
)
@DatabaseIdentityStoreDefinition(
    dataSourceLookup = "java:/mutex",
    callerQuery = "SELECT password FROM mx_user WHERE login = ? AND status= 'ENABLED'",
    groupsQuery = "SELECT role_name, 'Roles'  FROM mx_user_role WHERE login = ?"
    
)
//@FacesConfig(version = FacesConfig.Version.JSF_2_3)
@FacesConfig
@ApplicationScoped
public class JSFApplicationConfig {
    
}
