/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service.test;

import java.util.Locale;
import java.util.Optional;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.mutex.user.entity.Tenant;
import io.mutex.user.exception.TenantNameExistException;
import io.mutex.user.service.AdminUserService;
import io.mutex.user.service.TenantService;
import io.mutex.user.service.UserRoleService;
import io.mutex.user.valueobject.TenantStatus;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.junit.Assert;

/**
 *
 * @author root
 */
@RunWith(Arquillian.class)
public class TenantServiceTest {
    
    @Deployment
    public static WebArchive createDeployment() {
       
        WebArchive war = ShrinkWrap.create(WebArchive.class,
            TenantServiceTest.class.getName() + ".war")
            .addPackages(true, "io.mutex.shared.repository","io.mutex.shared.entity",
                    "io.mutex.user.exception", "io.mutex.user.entity",
                    "io.mutex.user.repository","io.mutex.user.valueobject")
            .addClasses(TenantService.class,AdminUserService.class,
                    UserRoleService.class)
            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans" + ".xml");

        
        return war;
               
    }
    
    @Inject
    TenantService tenantService;
    
    @Test
    @UsingDataSet(value = {"tenant/shouldFindTenantByName-using.yml"})    
    public void shouldFindTenantByName(){
        Optional<Tenant> oTenant = tenantService.findByName("ibm".toUpperCase(Locale.getDefault()));
        Assert.assertTrue(oTenant.isPresent());
    }
    
    @Test
    @UsingDataSet(value = {"tenant/shouldFindTenantByName-using.yml"})    
    public void shouldFailToFindTenantByName(){
        Optional<Tenant> oTenant = tenantService.findByName("ibm");
        Assert.assertTrue(oTenant.isPresent());
    }
    
    @Test
    @ShouldMatchDataSet(value = {"tenant/shouldCreateNewTenant-match.yml"},excludeColumns = {"uuid,version,created,updated,edited"})
    public void shouldCreateNewTenant(){
        Optional<Tenant> oTenant = Optional.empty();
        try {
            oTenant = tenantService.createTenant(CreateNewTenant());
        } catch (TenantNameExistException ex) {
            Logger.getLogger(TenantServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Assert.assertTrue(oTenant.isPresent());
                
    }
    
    private static Tenant CreateNewTenant(){
        Tenant tenant = new Tenant("RED HAT", "distributions GNU/Linux");
        tenant.setStatus(TenantStatus.ENABLED);
        return tenant;
    }
        
    @Test
    @UsingDataSet(value = {"tenant/shouldFailToCreateTenantWithExistingName-using.yml"})
    public void shouldFailToCreateTenantWithExistingName(){
        Optional<Tenant> oTenant = Optional.empty();
        try {
            oTenant = tenantService.createTenant(CreateAlreadyExistingTenant());
        } catch (TenantNameExistException ex) {
            Logger.getLogger(TenantServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Assert.assertTrue(oTenant.isEmpty());
    }
            
    private static Tenant CreateAlreadyExistingTenant(){
        Tenant tenant = new Tenant("ibm", "International Business Machines Corporation");
        tenant.setStatus(TenantStatus.ENABLED);
        return tenant;
    }
          
    @Test
    @UsingDataSet(value = {"tenant/shouldUpdateTenant-using.yml"})
    @ShouldMatchDataSet(value = {"tenant/shouldUpdateTenant-match.yml"},excludeColumns = {"uuid,version,created,updated,edited"})
    public void shouldUpdateTenant(){
        Optional<Tenant> oTenant = tenantService.findByName("ibm");
        oTenant.ifPresent(t -> { 
            t.setName("HTC");
            t.setDescription("High Tech Computer Corporation");
        });
    }
    
    @Test
    @UsingDataSet(value = {"tenant/shouldDeleteTenant-using.yml"})
    @ShouldMatchDataSet(value = {"tenant/shouldDeleteTenant-match.yml"},excludeColumns = {"uuid,version,created,updated,edited"})
    public void shouldDeleteTenant(){
        Optional<Tenant> oTenant = tenantService.findByUuid("b97d6945-18ee-44a7-aec1-0017cf077c52");
        oTenant.ifPresent(tenantService::deleteTenant);
    }
    
    

}
