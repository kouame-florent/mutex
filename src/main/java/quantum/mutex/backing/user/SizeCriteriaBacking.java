/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.user;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import quantum.mutex.domain.dto.SizeRangeCriteria;

/**
 *
 * @author Florent
 */
@Named("sizeCriteriaBacking")
@ViewScoped
public class SizeCriteriaBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(SizeCriteriaBacking.class.getName());
    
    @Getter @Setter
    private Integer minSize = 0;
    @Getter @Setter
    private int maxSize = 250;
    
    public void validate(){
        LOG.log(Level.INFO, "--> MIN SIZE: {0}", minSize);
        SizeRangeCriteria src = 
                SizeRangeCriteria.of(minSize,maxSize);
        returnToCaller(src);
   }
     
    private void returnToCaller(SizeRangeCriteria sizeRangeCriteria){
        PrimeFaces.current().dialog().closeDynamic(sizeRangeCriteria);
    }
    
}
