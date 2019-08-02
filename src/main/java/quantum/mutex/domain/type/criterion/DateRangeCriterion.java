/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.type.criterion;



import java.time.LocalDateTime;
import java.time.ZoneOffset;
import quantum.functional.api.Result;
import quantum.mutex.domain.type.criterion.SearchCriterion;

/**
 *
 * @author Florent
 */
public class DateRangeCriterion implements SearchCriterion{
    private final long startDate ;
    private final long endDate ;

    private DateRangeCriterion(long startEpochSecond, long endEpochSecond) {
        this.startDate = startEpochSecond;
        this.endDate = endEpochSecond;
    }
    
    public static DateRangeCriterion of(LocalDateTime start, LocalDateTime end){
        long st = Result.of(start).map(s -> s.toEpochSecond(ZoneOffset.UTC)).getOrElse(() -> Long.valueOf(0));
        long ed = Result.of(end).map(e -> e.toEpochSecond(ZoneOffset.UTC)).getOrElse(() -> Long.valueOf(0));
        return new DateRangeCriterion(st, ed);
    }
    
    public static DateRangeCriterion getDefault(){
        return new DateRangeCriterion(0, 0);
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
