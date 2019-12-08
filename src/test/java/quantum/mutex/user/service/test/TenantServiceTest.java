/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.user.service.test;

import java.util.Locale;
import java.util.Optional;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.mutex.domain.entity.BaseEntity;
import io.mutex.repository.GenericDAO;
import io.mutex.repository.GenericDAOImpl;
import io.mutex.domain.entity.AdminUser;
import io.mutex.domain.entity.BusinessEntity;
import io.mutex.domain.entity.Tenant;
import io.mutex.domain.entity.User;
import io.mutex.domain.valueobject.TenantStatus;
import io.mutex.domain.valueobject.UserStatus;
import io.mutex.repository.AdminUserDAO;
import io.mutex.repository.AdminUserDAOImpl;
import io.mutex.repository.TenantDAO;
import io.mutex.repository.TenantDAOImpl;
import io.mutex.service.user.TenantService;

/**
 *
 * @author root
 */
//@RunWith(Arquillian.class)
public class TenantServiceTest {
    
//    @Deployment
//    public static WebArchive createDeployment() {
//        WebArchive war = ShrinkWrap.create(WebArchive.class,
//            TenantServiceTest.class.getName() + ".war")
//            .addPackages(true, "io.ncl.user.domain.interfaces")
//            .addClasses(GenericDAO.class,GenericDAOImpl.class,
//                        AdminUserDAO.class,AdminUserDAOImpl.class,
//                        TenantDAO.class,TenantDAOImpl.class,
//                        AdminUser.class,Tenant.class,BusinessEntity.class,
//                        User.class,BaseEntity.class,TenantStatus.class,
//                        UserStatus.class,TenantService.class)
//            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
//            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans" + ".xml");
//        
////        System.out.println(war.toString(true));
//        
//        return war;
//               
//    }
//    
//    @Inject
//    TenantService tenantService;
//    
//    @Test
//    @UsingDataSet(value = {"user/tenants.yml"})    
//    public void shouldFindTenantByName(){
//        Optional<Tenant> oTenant = tenantService.findByName("ibm".toUpperCase(Locale.getDefault()));
//        Assert.assertTrue(oTenant.isPresent());
//    }
//    
//    @Test
//    @ShouldMatchDataSet(value = {"user/tenants-created.yml"},excludeColumns = {"uuid,version,created,updated,edited"})
//    public void shouldCreateNewTenant(){
//        Optional<Tenant> oTenant = tenantService.createTenant(CreateNewTenant());
//        Assert.assertTrue(oTenant.isPresent());
//                
//    }
//    
//    @Test
//    @UsingDataSet(value = {"user/tenants.yml"})
//    public void shouldFailToCreateAlreadyExistingTenantWithSameName(){
//        Optional<Tenant> oTenant = tenantService.createTenant(CreateAlreadyExistingTenant());
//        Assert.assertTrue(oTenant.isEmpty());
//    }
//        
//    @Test
//    @UsingDataSet(value = {"user/tenants.yml"})
//    @ShouldMatchDataSet(value = {"user/tenants-updated.yml"},excludeColumns = {"uuid,version,created,updated,edited"})
//    public void shouldUpdateTenant(){
//        Optional<Tenant> oTenant = tenantService.findByName("ibm");
//        oTenant.ifPresent(t -> { 
//            t.setName("HTC");
//            t.setDescription("High Tech Computer Corporation");
//        });
//    }
//    
//    
//    private static Tenant CreateNewTenant(){
//        Tenant tenant = new Tenant("RED HAT", "distributions GNU/Linux");
//        tenant.setStatus(TenantStatus.ENABLED);
//        return tenant;
//    }
//    
//    private static Tenant CreateAlreadyExistingTenant(){
//        Tenant tenant = new Tenant("ibm", "International Business Machines Corporation");
//        tenant.setStatus(TenantStatus.ENABLED);
//        return tenant;
//    }
}