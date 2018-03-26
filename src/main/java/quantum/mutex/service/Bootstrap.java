/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 *
 * @author Florent
 */
@Singleton
@Startup
public class Bootstrap {
    
    @Inject FileIOService fileSservice;
    
    @PostConstruct
    public void init(){
        fileSservice.createHomeDir();
        fileSservice.createSpoolDir();
        fileSservice.createStoreDir();
        fileSservice.createIndexDir();
    }
    
}
