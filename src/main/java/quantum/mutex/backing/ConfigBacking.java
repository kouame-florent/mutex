/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import quantum.mutex.service.ConfigService;

/**
 *
 * @author Florent
 */
@Named(value = "configBacking")
@ViewScoped
public class ConfigBacking extends BaseBacking{
    
    @Inject ConfigService configService;
    
    public void buildIndex(){
        configService.buildIndex();
    }
}
