/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import io.mutex.search.valueobject.DateRangeCriterion;


/**
 *
 * @author Florent
 */
@Named("dateCriteriaBacking")
@ViewScoped
public class DateCriteriaBacking implements Serializable{

    private static final long serialVersionUID = 1L;
        
    private Date startDate; 
    private Date endDate;
    
    public void validate(){
        Optional<DateRangeCriterion> drc = 
                DateRangeCriterion.of(convert(startDate),convert(endDate));
        returnToCaller(drc);
   }
    
   private LocalDateTime convert(Date date){
       return Optional.of(date).map(d -> d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
               .orElseGet(() -> null);
   }
     
    private void returnToCaller(Optional<DateRangeCriterion> dateRangeCriteria){
        PrimeFaces.current().dialog().closeDynamic(dateRangeCriteria);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    
    
}
