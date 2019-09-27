/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.user;

import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import quantum.mutex.domain.type.criterion.SizeRangeCriterion;


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
    private Integer maxSize = 250;
    
    public void validate(){
        Optional<SizeRangeCriterion> src = 
                SizeRangeCriterion.of(minSize * 1024 * 1024 ,
                        maxSize * 1024 * 1024); //to get mega bytes
        returnToCaller(src);
   }
     
    private void returnToCaller(Optional<SizeRangeCriterion> sizeRangeCriteria){
        PrimeFaces.current().dialog().closeDynamic(sizeRangeCriteria);
    }
    
}
