/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;



import java.time.LocalDateTime;
import java.time.ZoneOffset;
import quantum.functional.api.Result;

/**
 *
 * @author Florent
 */
public class DateRangeCriteria implements SearchCriteria{
    private final long startDate ;
    private final long endDate ;

    private DateRangeCriteria(long startEpochSecond, long endEpochSecond) {
        this.startDate = startEpochSecond;
        this.endDate = endEpochSecond;
    }
    
    public static DateRangeCriteria of(LocalDateTime start, LocalDateTime end){
        long st = Result.of(start).map(s -> s.toEpochSecond(ZoneOffset.UTC)).getOrElse(() -> Long.valueOf(0));
        long ed = Result.of(end).map(e -> e.toEpochSecond(ZoneOffset.UTC)).getOrElse(() -> Long.valueOf(0));
        return new DateRangeCriteria(st, ed);
    }
    
    public static DateRangeCriteria getDefault(){
        return new DateRangeCriteria(0, 0);
    }
      
    @Override
    public boolean isValid() {
        return (startDate != 0) 
                && (endDate != 0) 
                && ((startDate <= endDate));
    }
    
    public long startDate(){
        return startDate;
    }
    
    public long endDate(){
        return endDate;
    }
}
