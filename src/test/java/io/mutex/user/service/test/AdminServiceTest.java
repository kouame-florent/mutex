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
import io.mutex.user.exception.AdminExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.service.AdminServiceImpl;
import io.mutex.user.service.SpaceServiceImpl;
import io.mutex.user.service.UserRoleServiceImpl;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.user.service.AdminService;

@RunWith(Arquillian.class)
public class AdminServiceTest {

	 @Deployment
	  public static WebArchive createDeployment() {
	       
	        WebArchive war = ShrinkWrap.create(WebArchive.class,
	             AdminServiceTest.class.getName() + ".war")
	            .addPackages(true, "io.mutex.shared.repository","io.mutex.shared.entity",
	                    "io.mutex.user.exception", "io.mutex.user.entity",
	                    "io.mutex.user.repository","io.mutex.user.valueobject")
	             .addClasses(SpaceServiceImpl.class,AdminServiceImpl.class,
	            		 EncryptionService.class,UserRoleServiceImpl.class)
	            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
	            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans" + ".xml");

	        
	        return war;
	               
	    }
	 
    @Inject AdminService adminService;
    
    @Test
    @UsingDataSet(value = {"admin/shouldFindAdminByLogin-using.yml"})    
    public void shouldFindAdminByLogin(){
        Optional<Admin> oAdmin = adminService.findByLogin("ange@gmail.com");
        Assert.assertTrue(oAdmin.isPresent());
    }
    
	 
	@Test
	@ShouldMatchDataSet(value = {"admin/shouldCreateNewAdmin-match.yml"},excludeColumns = {"uuid,version,created,updated,edited,space_uuid"})
    public void shouldCreateNewAdmin(){
		
		//Optional<Admin> oAdmin = Optional.empty();
		try {
			adminService.createAdmin(createNewAdmin());
			
		} catch (AdminExistException | NotMatchingPasswordAndConfirmation e) {
			Logger.getLogger(AdminServiceTest.class.getName()).log(Level.SEVERE, null, e);
		}
		//Assert.assertTrue(oAdmin.isPresent());
    }
	
	private static Admin createNewAdmin(){
        Admin admin = new Admin();
        admin.setName("Ange Koffi");
        admin.setLogin("ange@gmail.com");
        admin.setStatus(UserStatus.DISABLED);
        admin.setPassword("ange1234");
        admin.setConfirmPassword("ange1234");
        return admin;
    }
	
	@Test
	@UsingDataSet(value = {"admin/shouldFailToCreateNewAdmin-using.yml"})   
	public void shouldFailToCreateNewAdmin(){
		
		Optional<Admin> oAdmin = Optional.empty();
		try {
			adminService.createAdmin(createNewAdmin());
			
		} catch (AdminExistException | NotMatchingPasswordAndConfirmation e) {
			Logger.getLogger(AdminServiceTest.class.getName()).log(Level.SEVERE, null, e.getMessage());
		}
		Assert.assertTrue(oAdmin.isEmpty());
    }
	
	@Test
	@UsingDataSet(value = {"admin/shouldUpdateAdmin-using.yml"})   
	@ShouldMatchDataSet(value = {"admin/shouldUpdateAdmin-match.yml"},excludeColumns = {"uuid,version,created,updated,edited,space_uuid"})
	public void shouldUpdateAdmin() {
		Optional<Admin> oAdmin = adminService.findByLogin("ange@gmail.com");
		oAdmin.ifPresent(a -> updateAdmin(a));
	}
	
	private Optional<Admin> updateAdmin(Admin admin){
		admin.setLogin("ange.koffi@gmail.com");
		try {
			return adminService.updateAdmin(admin);
		} catch (AdminLoginExistException | NotMatchingPasswordAndConfirmation e) {
			Logger.getLogger(AdminServiceTest.class.getName()).log(Level.SEVERE, null, e.getMessage());
		}
		
		return Optional.empty();
	}

}
