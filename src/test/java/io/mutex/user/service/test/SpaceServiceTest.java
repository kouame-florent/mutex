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
import io.mutex.user.entity.Space;
import io.mutex.user.exception.SpaceNameExistException;
import io.mutex.user.service.AdminServiceImpl;
import io.mutex.user.service.SpaceServiceImpl;
import io.mutex.user.service.UserRoleServiceImpl;
import io.mutex.user.valueobject.SpaceStatus;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.junit.Assert;

/**
 *
 * @author root
 */
@RunWith(Arquillian.class)
public class SpaceServiceTest {
    
    @Deployment
    public static WebArchive createDeployment() {
       
        WebArchive war = ShrinkWrap.create(WebArchive.class,
            SpaceServiceTest.class.getName() + ".war")
            .addPackages(true, "io.mutex.shared.repository","io.mutex.shared.entity",
                    "io.mutex.user.exception", "io.mutex.user.entity",
                    "io.mutex.user.repository","io.mutex.user.valueobject")
            .addClasses(SpaceServiceImpl.class,AdminServiceImpl.class,
                    UserRoleServiceImpl.class)
            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans" + ".xml");

        
        return war;
               
    }
    
    @Inject
    SpaceServiceImpl spaceService;
    
    @Test
    @UsingDataSet(value = {"space/shouldFindSpaceByName-using.yml"})    
    public void shouldFindSpaceByName(){
        Optional<Space> oSpace = spaceService.getSpaceByName("ibm".toUpperCase(Locale.getDefault()));
        Assert.assertTrue(oSpace.isPresent());
    }
    
    @Test
    @UsingDataSet(value = {"space/shouldFindSpaceByName-using.yml"})    
    public void shouldFailToFindSpaceByName(){
        Optional<Space> oSpace = spaceService.getSpaceByName("ibm");
        Assert.assertTrue(oSpace.isPresent());
    }
    
    @Test
    @ShouldMatchDataSet(value = {"space/shouldCreateNewSpace-match.yml"},excludeColumns = {"uuid,version,created,updated,edited"})
    public void shouldCreateNewSpace(){
        Optional<Space> oSpace = Optional.empty();
        try {
            oSpace = spaceService.create(CreateNewSpace());
        } catch (SpaceNameExistException ex) {
            Logger.getLogger(SpaceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Assert.assertTrue(oSpace.isPresent());
                
    }
    
    private static Space CreateNewSpace(){
        Space space = new Space("RED HAT", "distributions GNU/Linux");
        space.setStatus(SpaceStatus.ENABLED);
        return space;
    }
        
    @Test
    @UsingDataSet(value = {"space/shouldFailToCreateSpaceWithExistingName-using.yml"})
    public void shouldFailToCreateSpaceWithExistingName(){
        Optional<Space> oSpace = Optional.empty();
        try {
            oSpace = spaceService.create(CreateAlreadyExistingSpace());
        } catch (SpaceNameExistException ex) {
            Logger.getLogger(SpaceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Assert.assertTrue(oSpace.isEmpty());
    }
            
    private static Space CreateAlreadyExistingSpace(){
        Space space = new Space("ibm", "International Business Machines Corporation");
        space.setStatus(SpaceStatus.ENABLED);
        return space;
    }
          
    @Test
    @UsingDataSet(value = {"space/shouldUpdateSpace-using.yml"})
    @ShouldMatchDataSet(value = {"space/shouldUpdateSpace-match.yml"},excludeColumns = {"uuid,version,created,updated,edited"})
    public void shouldUpdateSpace(){
        Optional<Space> oSpace = spaceService.getSpaceByName("ibm");
        oSpace.ifPresent(t -> { 
            t.setName("HTC");
            t.setDescription("High Tech Computer Corporation");
        });
    }
    
    @Test
    @UsingDataSet(value = {"space/shouldDeleteSpace-using.yml"})
    @ShouldMatchDataSet(value = {"space/shouldDeleteSpace-match.yml"},excludeColumns = {"uuid,version,created,updated,edited"})
    public void shouldDeleteSpace(){
        Optional<Space> oSpace = spaceService.getSpaceByUuid("b97d6945-18ee-44a7-aec1-0017cf077c52");
        oSpace.ifPresent(spaceService::delete);
    }
    
    

}
