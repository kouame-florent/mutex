/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.search.interfaces.web;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import mutex.search.valueobject.DateRangeCriterion;


/**
 *
 * @author Florent
 */
@Named("dateCriteriaBacking")
@ViewScoped
public class DateCriteriaBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(DateCriteriaBacking.class.getName());
      
    @Getter @Setter
    private Date startDate;
    @Getter @Setter
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
    
}
