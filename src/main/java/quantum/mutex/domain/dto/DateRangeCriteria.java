/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;


import java.time.LocalDateTime;
import quantum.functional.api.Result;

/**
 *
 * @author Florent
 */
public class DateRangeCriteria implements SearchCriteria{
    
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public DateRangeCriteria(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    @Override
    public boolean isValid() {
        return (startDate != null) 
                && (endDate != null) 
                && ((startDate.isBefore(endDate)) || (startDate.isEqual(endDate)));
    }
    
    public Result<LocalDateTime> startDate(){
        return Result.of(startDate);
    }
    
    public Result<LocalDateTime> endDate(){
        return Result.of(endDate);
    }
    
}
