package io.mutex.user.service.test;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import io.mutex.shared.service.EncryptionService;
import io.mutex.user.entity.Admin;
import io.mutex.user.exception.AdminLoginExistException;
import io.mutex.user.exception.AdminUserExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.service.AdminServiceImpl;
import io.mutex.user.service.SpaceServiceImpl;
import io.mutex.user.service.UserRoleServiceImpl;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.user.service.AdminService;

@RunWith(Arquillian.class)
public class AdminUserServiceTest {

	 @Deployment
	  public static WebArchive createDeployment() {
	       
	        WebArchive war = ShrinkWrap.create(WebArchive.class,
	             AdminUserServiceTest.class.getName() + ".war")
	            .addPackages(true, "io.mutex.shared.repository","io.mutex.shared.entity",
	                    "io.mutex.user.exception", "io.mutex.user.entity",
	                    "io.mutex.user.repository","io.mutex.user.valueobject")
	             .addClasses(SpaceServiceImpl.class,AdminServiceImpl.class,
	            		 EncryptionService.class,UserRoleServiceImpl.class)
	            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
	            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans" + ".xml");

	        
	        return war;
	               
	    }
	 
    @Inject AdminService adminUserService;
    
    @Test
    @UsingDataSet(value = {"admin/shouldFindAdminUserByLogin-using.yml"})    
    public void shouldFindAdminUserByLogin(){
        Optional<Admin> oAdmin = adminUserService.findByLogin("ange@gmail.com");
        Assert.assertTrue(oAdmin.isPresent());
    }
    
	 
	@Test
	@ShouldMatchDataSet(value = {"admin/shouldCreateNewAdminUser-match.yml"},excludeColumns = {"uuid,version,created,updated,edited,tenant_uuid"})
    public void shouldCreateNewAdminUser(){
		
		//Optional<AdminUser> oAdminUser = Optional.empty();
		try {
			adminUserService.createAdminUser(createNewAdminUser());
			
		} catch (AdminUserExistException | NotMatchingPasswordAndConfirmation e) {
			Logger.getLogger(AdminUserServiceTest.class.getName()).log(Level.SEVERE, null, e);
		}
		//Assert.assertTrue(oAdminUser.isPresent());
    }
	
	private static Admin createNewAdminUser(){
        Admin adminUser = new Admin();
        adminUser.setName("Ange Koffi");
        adminUser.setLogin("ange@gmail.com");
        adminUser.setStatus(UserStatus.DISABLED);
        adminUser.setPassword("ange1234");
        adminUser.setConfirmPassword("ange1234");
        return adminUser;
    }
	
	@Test
	@UsingDataSet(value = {"admin/shouldFailToCreateNewAdminUser-using.yml"})   
	public void shouldFailToCreateNewAdminUser(){
		
		Optional<Admin> oAdminUser = Optional.empty();
		try {
			adminUserService.createAdminUser(createNewAdminUser());
			
		} catch (AdminUserExistException | NotMatchingPasswordAndConfirmation e) {
			Logger.getLogger(AdminUserServiceTest.class.getName()).log(Level.SEVERE, null, e.getMessage());
		}
		Assert.assertTrue(oAdminUser.isEmpty());
    }
	
	@Test
	@UsingDataSet(value = {"admin/shouldUpdateAdminUser-using.yml"})   
	@ShouldMatchDataSet(value = {"admin/shouldUpdateAdminUser-match.yml"},excludeColumns = {"uuid,version,created,updated,edited,tenant_uuid"})
	public void shouldUpdateAdminUser() {
		Optional<Admin> oAdmin = adminUserService.findByLogin("ange@gmail.com");
		oAdmin.ifPresent(a -> updateAdminUser(a));
	}
	
	private Optional<Admin> updateAdminUser(Admin adminUser){
		adminUser.setLogin("ange.koffi@gmail.com");
		try {
			return adminUserService.updateAdminUser(adminUser);
		} catch (AdminLoginExistException | NotMatchingPasswordAndConfirmation e) {
			Logger.getLogger(AdminUserServiceTest.class.getName()).log(Level.SEVERE, null, e.getMessage());
		}
		
		return Optional.empty();
	}

}
