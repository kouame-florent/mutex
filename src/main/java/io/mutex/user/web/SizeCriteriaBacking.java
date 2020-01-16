/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import io.mutex.search.valueobject.SizeRangeCriterion;


/**
 *
 * @author Florent
 */
@Named("sizeCriteriaBacking")
@ViewScoped
public class SizeCriteriaBacking implements Serializable{

   
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(SizeCriteriaBacking.class.getName());

    private Integer minSize = 0;
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

    public Integer getMinSize() {
        return minSize;
    }

    public void setMinSize(Integer minSize) {
        this.minSize = minSize;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }
    
    
    
}
